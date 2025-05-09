package org.example.Service;

import org.example.DTO.AuthData;
import org.example.Model.Bicycle;
import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private static final int DEFAULT_LENGTH_USERNAME = 15;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleService userRoleService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User registration(AuthData registrationData)
    {
        try {
            if (userRepository.findByNumberPhone(registrationData.getNumberPhone()).isPresent()) {
                throw new IllegalArgumentException("User already registered");
            }

            User newUser = new User();
            newUser.setNumberPhone(registrationData.getNumberPhone());
            newUser.setPassword(passwordEncoder.encode(registrationData.getPassword()));
            newUser.setUsername(generateNickname());
            newUser.setUserRole(userRoleService.getUserRole());

            return userRepository.save(newUser);
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage(), e);
            throw e;
        }
    }

    public static String generateNickname() {
        StringBuilder nickname = new StringBuilder("User_");
        SecureRandom random = new SecureRandom();
        String pattern = "0123456789";
        for (int i = 0; i < DEFAULT_LENGTH_USERNAME; i++) {
            nickname.append(pattern.charAt(random.nextInt(pattern.length())));
        }
        return nickname.toString();
    }

    public User loadUserByNumberPhone(String numberPhone){
        return userRepository.findByNumberPhone(numberPhone)
                .orElseThrow(() -> new NoSuchElementException("User with numberPhone: '" + numberPhone + "' not found"));
    }

    public Optional<User> getUser(Integer id_user) {
        try {
            return userRepository.findById(id_user);
        } catch (Exception e) {
            logger.error("Error during user found: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findUsersWithRoles() {
        return userRepository.findAllWithUserRoles();
    }


    public void deleteUserById(Integer id){
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Not found user with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public void deleteAllUser() {
        userRepository.deleteAll();
    }
}
