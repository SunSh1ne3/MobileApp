package org.example.Service;

import org.example.DTO.AuthData;
import org.example.DTO.ErrorResponse.UserNotFoundException;
import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
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
        userRepository.findByNumberPhone(registrationData.getNumberPhone()).ifPresent(u -> {
            logger.error("User already exists with phone: {}, ID: {}", u.getNumberPhone(), u.getId());
            throw new IllegalArgumentException("Пользователь с номером " + registrationData.getNumberPhone() + " уже зарегистрирован");
        });
        User newUser = new User();
        newUser.setNumberPhone(registrationData.getNumberPhone());
        newUser.setPassword(passwordEncoder.encode(registrationData.getPassword()));
        newUser.setUsername(generateNickname());
        newUser.setUserRole(userRoleService.getUserRole());

        try {
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

    public User loadUserById(Integer userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID: '" + userId + "' not found"));
    }

    public User loadUserByNumberPhone(String numberPhone){
        return userRepository.findByNumberPhone(numberPhone)
                .orElseThrow(() -> new UserNotFoundException("User with numberPhone: '" + numberPhone + "' not found"));
    }

    public Optional<User> getUser(Integer id_user) {
        try {
            return userRepository.findById(id_user);
        } catch (Exception e) {
            logger.error("Error during user found: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<User> getAllUsers() {
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
