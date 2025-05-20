package org.example.Controller;

import org.example.DTO.AuthData;
import org.example.DTO.Response.AuthResponse;
import org.example.DTO.Response.ErrorResponse;
import org.example.DTO.ErrorResponse.UserNotFoundException;
import org.example.DTO.Response.UserResponse;
import org.example.Model.User;
import org.example.Service.TokenService;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserDetailsService userDetailsService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthController.class);
    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          TokenService tokenService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/registration")
    public ResponseEntity<Object> registration(@RequestBody AuthData registrationData)
    {
        logger.info("Registration attempt for phone: {}", registrationData.getNumberPhone());
        try {
            User newUser = userService.registration(registrationData);
            UserResponse userResponse = new UserResponse(newUser);
            return ResponseEntity.ok(userResponse);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthData authData)
    {
        User user = userService.loadUserByNumberPhone(authData.getNumberPhone());

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authData.getNumberPhone(),
                    authData.getPassword()
                )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Inappropriate accounting data"));
        }

        return ResponseEntity.ok(
            new AuthResponse( tokenService.generateToken(user) )
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid refresh token format");
            }

            String token = refreshToken.substring(7);
            if (!tokenService.isTokenValid(token)) {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }

            String phoneNumber = tokenService.extractNumberPhone(token);
            User user = userService.loadUserByNumberPhone(phoneNumber);

            return ResponseEntity.ok(
                    new AuthResponse( tokenService.generateToken(user) )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Failed to refresh token: " + e.getMessage()));
        }
    }

    @ExceptionHandler({AuthenticationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleAuthException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(ex.getMessage(), "AUTH_ERROR", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Пользователь не найден", "USER_NOT_FOUND", 404));
    }

}
