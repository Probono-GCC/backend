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

  private String studentName = "testStudent";
  private String studenPassword = "1234";

  private String teacherName = "testTeacher";
  private String teacherPassword = "1234";

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

    if (userRepository.findByUsername(studentName).isEmpty()) {
      Users student = new Users();
      student.setUsername(studentName);
      student.setPassword(bCryptPasswordEncoder.encode(studenPassword));
      student.setRole(Role.ROLE_STUDENT);
      student.setName("testStudent");
      student.setStatus(Status.ACTIVE);
      LocalDateTime now = LocalDateTime.now();
      Timestamp timestamp = Timestamp.valueOf(now);
      student.setCreatedAt(timestamp);
      student.setUpdatedAt(timestamp);
      student.setCreatedChargeId(1l);
      userRepository.save(student);
    }

    if (userRepository.findByUsername(teacherName).isEmpty()) {
      Users teacher = new Users();
      teacher.setUsername(teacherName);
      teacher.setPassword(bCryptPasswordEncoder.encode(teacherPassword));
      teacher.setRole(Role.ROLE_TEACHER);
      teacher.setName("testTeacher");
      teacher.setStatus(Status.ACTIVE);
      LocalDateTime now = LocalDateTime.now();
      Timestamp timestamp = Timestamp.valueOf(now);
      teacher.setCreatedAt(timestamp);
      teacher.setUpdatedAt(timestamp);
      teacher.setCreatedChargeId(1l);
      userRepository.save(teacher);
    }
  }
}
