package probono.gcc.school.model.dto.users;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TeacherResponseDTO {

  private String username;
  private String password;
  private Role role;
  private String name;
  private LocalDate birth;
  private Sex sex;
  private String phoneNum;
  private String pwAnswer;
  private Classes classId;
  private Image imageId;
  private Status status;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private Long createdChargeId;
  private Long updatedChargeId;
}
