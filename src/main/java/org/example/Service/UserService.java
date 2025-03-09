package org.example.Service;

import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    //Шифрование пароля
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registration(User user)
    {
        if (userRepository.findByNumberPhone(user.getNumberPhone()).isPresent())
        {
            throw new IllegalArgumentException("User already registered");
        }
        User newUser = new User(
                user.getUsername(),
                passwordEncoder.encode(user.getPassword())
        );

        return userRepository.save(newUser);
    }

}
