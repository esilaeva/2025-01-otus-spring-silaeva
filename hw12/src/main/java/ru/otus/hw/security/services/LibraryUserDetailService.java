package ru.otus.hw.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.security.models.User;
import ru.otus.hw.security.repositories.UserRepository;

/*
When a user tries to authenticate, Spring Security calls the loadUserByUsername method.
This method fetches a User from the UserRepository by their username.
If found, it wraps the User object into a LibraryPrincipal
(which implements UserDetails) and returns it. If the user is not found, it throws a UsernameNotFoundException.
* */
@Service
@RequiredArgsConstructor
public class LibraryUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User: %s not found".formatted(username)));

        return new LibraryPrincipal(user);
    }
}
