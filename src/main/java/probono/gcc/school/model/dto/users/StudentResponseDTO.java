package probono.gcc.school.model.dto.users;

import java.sql.Timestamp;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StudentResponseDTO {

  private String username;
  private String name;
  private int serialNumber;
  private Grades grade;
  private LocalDate birth;
  private Sex sex;
  private String phoneNum;
  private String fatherPhoneNum;
  private String motherPhoneNum;
  private String guardiansPhoneNum;
  private String pwAnswer;
  private Role role;
  private Status status;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private String createdChargeId;
  private String updatedChargeId;
  private CreateImageResponseDTO imageResponseDTO;
  private ClassResponse classResponse;


}
