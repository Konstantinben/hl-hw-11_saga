package otus.ms.auth.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import otus.ms.auth.model.entity.User;
import otus.ms.auth.repository.UserRepository;

@Service("userDetailsServiceImpol")
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new BadCredentialsException("Username cannot be empty");
        }
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found by name "+ username));
        return SecurityUser.fromUser(user);
    }
}
