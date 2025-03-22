package org.example.Service;

import org.example.DTO.CustomUserDetails;
import org.example.Model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.example.Repository.UserRepository;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String numberPhone) throws UsernameNotFoundException {
        User user = userRepository.findByNumberPhone(numberPhone).
                orElseThrow(() -> new IllegalArgumentException("User with numberPhone: '" + numberPhone + "' not found"));
        return new CustomUserDetails(user);
    }


}
