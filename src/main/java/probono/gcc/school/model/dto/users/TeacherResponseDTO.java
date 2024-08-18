package probono.gcc.school.model.dto.users;

import java.sql.Timestamp;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.dto.classes.ClassResponse;
import probono.gcc.school.model.dto.image.CreateImageResponseDTO;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TeacherResponseDTO {


  private String username;
  private Role role;
  private String name;
  private LocalDate birth;
  private Sex sex;
  private String phoneNum;
  private String pwAnswer;
  private ClassResponse classId; //ClassResponse로 type 바꿈
  private CreateImageResponseDTO imageId;
  private Status status;
  private Timestamp createdAt;
  private Timestamp updatedAt;
  private String createdChargeId;
  private String updatedChargeId;
}
