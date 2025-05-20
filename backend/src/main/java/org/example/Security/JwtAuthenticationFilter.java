package org.example.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.Service.CustomUserDetailsService;
import org.example.Service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(TokenService tokenService, CustomUserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    )   throws ServletException, IOException {

        // Логирование входящего запроса
        logger.debug("Incoming request: {} {} | Headers: {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getHeaderNames());

        // Пропускаем аутентификацию для публичных эндпоинтов
        if (isPublicEndpoint(request)) {
            logger.debug("Skipping authentication for public endpoint: {}", request.getServletPath());
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.warn("Missing or invalid Authorization header. Expected format: 'Bearer <token>'");
            sendErrorResponse(response, "Authorization header is missing or invalid", HttpServletResponse.SC_UNAUTHORIZED);
            filterChain.doFilter(request, response);
            return;
        }
        processJwtToken(request, response, filterChain, authHeader);

    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/v1/auth/");
    }

    private void processJwtToken(HttpServletRequest request, HttpServletResponse response,
                                 FilterChain filterChain, String authHeader) throws IOException, ServletException {
        try {
            String token = authHeader.substring(BEARER_PREFIX.length());
            logger.debug("Extracted JWT token: {}... ({} chars)",
                    token.substring(0, Math.min(10, token.length())),
                    token.length());

            // Валидация токена
            if (!tokenService.isTokenValid(token)) {
                String error = tokenService.getTokenValidationError(token);
                logger.warn("Token validation failed: {} | Token: {}...", error, token.substring(0, 10));
                sendErrorResponse(response, error, HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            authenticateUser(request, token);
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("JWT processing failed: {} | Stacktrace: {}", e.getMessage(), e);
            sendErrorResponse(response, "Authentication failed: " + e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void authenticateUser(HttpServletRequest request, String token) {
        String userPhone = tokenService.extractNumberPhone(token);
        logger.debug("Extracted phone from token: {}", userPhone);

        if (userPhone != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.debug("Loading user details for phone: {}", userPhone);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userPhone);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("Successfully authenticated user: {} | Roles: {}",
                    userPhone,
                    userDetails.getAuthorities());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        logger.debug("Sending error response: {} - {}", status, message);
        response.setContentType("application/json");
        response.setStatus(status);
        response.getWriter().write(String.format("{\"error\": \"%s\", \"status\": %d}", message, status));
    }
}
