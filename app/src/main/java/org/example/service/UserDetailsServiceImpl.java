package org.example.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

import org.example.entities.UserInfo;
import org.example.model.UserInfoDto;
import org.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserInfo user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("No such user.");
        }

        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserExists(UserInfoDto userInfoDto) {
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public boolean isValidEmail(String email) {

        String emailRegex =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        return Pattern.matches(emailRegex, email);
    }

    public boolean isValidPassword(String password) {

        String passwordRegex =
                "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$";

        return Pattern.matches(passwordRegex, password);
    }

    public Boolean signupUser(UserInfoDto userInfoDto) {

        if (!isValidEmail(userInfoDto.getUsername())) {
            return false;
        }

        if (!isValidPassword(userInfoDto.getPassword())) {
            return false;
        }

        if (Objects.nonNull(checkIfUserExists(userInfoDto))) {
            return false;
        }

        userInfoDto.setPassword(
                passwordEncoder.encode(userInfoDto.getPassword())
        );

        userRepository.save(
                new UserInfo(
                        null,
                        userInfoDto.getUsername(),
                        userInfoDto.getPassword(),
                        new HashSet<>()
                )
        );

        return true;
    }
}