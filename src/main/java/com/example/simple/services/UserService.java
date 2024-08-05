package com.example.simple.services;
import com.example.simple.models.User;
import com.example.simple.models.enums.Role;
import com.example.simple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void createUser(User user) {
        String username = user.getUsername();
        if(userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("User with username " + user.getUsername() + " already exists");
        }
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(Role.ROLE_USER);
        userRepository.save(user);
    }
}
