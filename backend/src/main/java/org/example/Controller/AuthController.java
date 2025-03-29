package org.example.Controller;

import org.example.DTO.AuthData;
import org.example.DTO.Response.AuthResponse;
import org.example.DTO.Response.ErrorResponse;
import org.example.Model.User;
import org.example.Service.JwtService;
import org.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          JwtService jwtService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody AuthData registrationData)
    {
        try {
            User newUser = userService.registration(registrationData);
            return ResponseEntity.ok(newUser.getId().toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.CONFLICT).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthData authData)
    {
        final User testUser = userService.loadUserByNumberPhone(authData.getNumberPhone());
        if (testUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found"));
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authData.getNumberPhone(),
                    authData.getPassword()
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Inappropriate accounting data"));
        }

        String token = jwtService.generateToken(testUser);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage()));
    }
}
