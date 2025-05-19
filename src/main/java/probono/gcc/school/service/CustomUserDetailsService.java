package probono.gcc.school.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import probono.gcc.school.model.dto.CustomUserDetails;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final MeterRegistry meterRegistry;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Timer.Sample sample = Timer.start(meterRegistry);
    /*DB 조회 - 시작*/
    Users userData = userRepository.findByUsername(username).get();
    /*끝*/
    sample.stop(meterRegistry.timer("login.step", "phase", "DB_user_lookup"));
    if (userData != null) {
      return new CustomUserDetails(userData);
    }
    return null;
  }
}
