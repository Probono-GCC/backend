package probono.gcc.school.config;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import probono.gcc.school.model.entity.Users;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Status;
import probono.gcc.school.repository.UserRepository;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  @Value("${admin.username}")
  private String adminUsername;

  @Value("${admin.password}")
  private String adminPassword;

  public AdminAccountInitializer(UserRepository userRepository,
      BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("here!!!");
    if (userRepository.findByUsername(adminUsername).isEmpty()) {
      Users admin = new Users();
      admin.setUsername(adminUsername);
      admin.setPassword(bCryptPasswordEncoder.encode(adminPassword));
      admin.setRole(Role.ROLE_ADMIN);
      admin.setName("ADMIN");
      admin.setStatus(Status.ACTIVE);
      LocalDateTime now = LocalDateTime.now();
      Timestamp timestamp = Timestamp.valueOf(now);
      admin.setCreatedAt(timestamp);
      admin.setUpdatedAt(timestamp);
      admin.setCreatedChargeId(1l);
      userRepository.save(admin);
    }
  }
}
