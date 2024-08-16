package probono.gcc.school.model.dto.users;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.entity.Classes;
import probono.gcc.school.model.enums.Sex;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TeacherCreateRequestDTO {
  @NotNull
  private String loginId;
  @NotNull
  private String loginPw;
  @NotNull
  private String name;

}
