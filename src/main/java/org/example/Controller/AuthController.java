package org.example.Controller;

import org.example.Model.User;
import org.example.Service.JwtService;
import org.example.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private UserService userService;
    private JwtService jwtService;

    @PostMapping("/registration")
    public ResponseEntity<String> registration(@RequestBody User user)
    {
        userService.registration(user);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
