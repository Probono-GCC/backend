package probono.gcc.school.model.dto.users;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import probono.gcc.school.model.dto.image.ImageResponseDTO;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Sex;

@Data
@AllArgsConstructor
public class UserResponse {

  private String username;
  private String name;
  private Integer serialNumber;
  private Grades grade;
  private LocalDate birth;
  private Sex sex;
  private String phoneNum;
  private String fatherPhoneNum;
  private String motherPhoneNum;
  private String guardiansPhoneNum;
  private Role role;
//  private Image imageId;

  private ImageResponseDTO imageResponseDTO;
}
