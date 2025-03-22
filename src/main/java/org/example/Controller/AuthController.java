package org.example.Controller;

import org.example.DTO.AuthData;
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
    public ResponseEntity<String> registration(@RequestBody User user)
    {
        try {
            User newUser = userService.registration(user);
            return ResponseEntity.ok(newUser.getId().toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthData authData)
    {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authData.getNumberPhone(),
                    authData.getPassword()
            ));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Inappropriate accounting data");
        }

        final User testUser = userService.loadUserByNumberPhone(authData.getNumberPhone());
        if (testUser == null) {
            throw new IllegalArgumentException("Authentication object cannot be null");
        }

        String token = jwtService.generateToken(testUser);
        return ResponseEntity.ok(token);
    }
}
