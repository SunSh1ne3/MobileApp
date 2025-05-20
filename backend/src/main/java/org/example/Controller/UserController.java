package org.example.Controller;

import org.example.DTO.Response.UserResponse;
import org.example.Model.User;
import org.example.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json")
public class UserController {
    @Autowired
    private UserService userService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/get/id/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        User user = userService.loadUserById(userId);
        UserResponse userResponse = new UserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/get/{numberPhone}")
    public ResponseEntity<UserResponse> getUserByNumberPhone(@PathVariable String numberPhone) {
        User user = userService.loadUserByNumberPhone(numberPhone);
        UserResponse userResponse = new UserResponse(user);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<User>> getUsersWithRoles() {
        List<User> users = userService.findUsersWithRoles();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllUser() {
        userService.deleteAllUser();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id){
        try{
            userService.deleteUserById(id);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
