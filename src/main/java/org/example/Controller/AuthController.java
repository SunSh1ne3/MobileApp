package org.example.Controller;

import org.example.Model.User;
import org.example.Service.JwtService;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;


    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
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
    public ResponseEntity<String> login(@RequestBody User user)
    {
        final User testUser = userService.loadUserByNumberPhone(user.getNumberPhone());
        if (testUser == null) {
            throw new IllegalArgumentException("Authentication object cannot be null");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getNumberPhone(),
                user.getPassword()));


        logger.debug("ID: {}",testUser.getId());
        String token = jwtService.generateToken(testUser);
        return ResponseEntity.ok(token);
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestParam String numberPhone, @RequestParam String password)
//    {
//        if (authenticationManager == null) {
//            throw new IllegalArgumentException("Authentication object cannot be null");
//        }
//
//        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(numberPhone, password));
//
//        final User testUser = userService.loadUserByNumberPhone(numberPhone);
//        logger.debug("ID: {}",testUser.getId());
//        //String token = jwtService.generateToken(testUser);
//        String token = jwtService.generateToken(numberPhone, testUser.getId());
//        return ResponseEntity.ok(token);
//    }

}
