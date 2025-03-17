package org.example.Service;

import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //Шифрование пароля
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User registration(User user)
    {
        try {
            if (userRepository.findByNumberPhone(user.getNumberPhone()).isPresent()) {
                throw new IllegalArgumentException("User already registered");
            }

            User newUser = new User();
            newUser.setNumberPhone(user.getNumberPhone());
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
            //newUser.setPassword(user.getPassword());
            newUser.setUsername(user.getUsername());
            newUser.setTypeOrder(1L);

            return userRepository.save(newUser);
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage(), e);
            throw e;
        }
    }

    public User loadUserByNumberPhone(String numberPhone){
        return userRepository.findByNumberPhone(numberPhone)
                .orElseThrow(() -> new IllegalArgumentException("User with numberPhone: '" + numberPhone + "' not found"));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUserById(Long id){
        if (userRepository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Not found user with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    public void deleteAllUser() {
        userRepository.deleteAll();
    }

}
