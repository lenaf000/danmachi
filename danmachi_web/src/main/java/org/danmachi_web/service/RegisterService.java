package org.danmachi_web.service;

import org.danmachi_web.model.User;
import org.danmachi_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegisterService {
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String signup(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "L'adresse mail donné est déjà utilisé";
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        return "signup";
    }

    public Map<String, Boolean> checkUnique(String username, String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("usernameExists", userRepository.existsByUsername(username));
        response.put("emailExists", userRepository.existsByEmail(email));

        return response;
    }

    public boolean confirmEmail(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.isEnabled()) {
            String hashedPassword = user.getPassword();
            if (passwordEncoder.matches(password, hashedPassword)) {
                return user;
            }
        }
        return null;
    }
}
