package probono.gcc.school.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import probono.gcc.school.model.enums.Grades;

@Data
public class StudentCreateRequestDto {

  @NotEmpty(message = "로그인 아이디는 필수 입니다.")
  private String loginId;

  @NotEmpty(message = "로그인 패스워드는 필수 입니다.")
  private String loginPw;

  @NotEmpty(message = "유저 이름은 필수 입니다.")
  private String name;

  @NotNull(message = "serial number는 필수 입니다.")
  @Min(value = 1, message = "serial number는 1 이상이어야 합니다.")
  private Integer serialNumber;

  @NotNull(message = "학년은 필수 입니다.")
  private Grades grade;
}
