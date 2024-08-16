package probono.gcc.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.CustomUserDetails;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Users userData = userRepository.findByUsername(username).get();

    if (userData != null) {
      return new CustomUserDetails(userData);
    }
    return null;
  }
}
