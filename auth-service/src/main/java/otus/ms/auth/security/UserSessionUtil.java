package otus.ms.auth.security;

import otus.ms.auth.model.entity.User;
import otus.ms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSessionUtil {

    private final UserRepository userRepository;

    public User getAuthorizedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = ((org.springframework.security.core.userdetails.User)auth.getPrincipal()).getUsername();
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user by email " + email));
    }
}
