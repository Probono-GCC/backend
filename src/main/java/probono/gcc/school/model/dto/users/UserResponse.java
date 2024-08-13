package probono.gcc.school.model.dto.users;

import java.time.LocalDate;
import lombok.Data;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Sex;

@Data
public class UserResponse {

  private String loginId;
  private String name;
  private int serialNumber;
  private Grades grade;
  private LocalDate birth;
  private Sex sex;
  private String phoneNum;
  private String fatherPhoneNum;
  private String motherPhoneNum;
  private String guardiansPhoneNum;
  private Role role;
  private Image imageId;
}
