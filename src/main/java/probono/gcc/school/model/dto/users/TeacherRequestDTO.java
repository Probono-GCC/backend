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
public class TeacherRequestDTO {
  private String loginId;
  private String loginPw;
  private String name;//
  private LocalDate birth;//최초 1회 입력
  private Sex sex;//최초 1회 입력
  private String phoneNum;//
  private String pwAnswer;//최초 1회 입력
  private Classes classId;
  private Long imageId;


}
