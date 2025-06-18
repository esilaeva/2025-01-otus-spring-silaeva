package ru.otus.hw.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.otus.hw.enums.NotFoundMessage;
import ru.otus.hw.security.models.User;
import ru.otus.hw.security.repositories.UserRepository;


@Service
@RequiredArgsConstructor
public class LibraryUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        NotFoundMessage.USER.getMessage().formatted(username)));

        return new LibraryPrincipal(user);
    }
}
