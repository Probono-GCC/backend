package probono.gcc.school.model.dto.users;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.entity.Image;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sex;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StudentCreateRequestDTO {
  @NotNull
  private String loginId;
  @NotNull
  private String loginPw;
  @NotNull
  private String name;
  @NotNull
  private int serialNumber;
  @NotNull
  private Grades grade;
  private Date birth;
  private Sex sex;
  private String phoneNum;
  private String fatherPhoneNum;
  private String motherPhoneNum;
  private String guardiansPhoneNum;
  private String pwAnswer;
  private Image imageId;
}
