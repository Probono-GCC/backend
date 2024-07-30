package probono.gcc.school.model.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentUpdateRequestDto {

  @NotEmpty(message = "로그인 아이디는 필수 입니다.")
  private String loginId;

  @NotEmpty(message = "유저 이름은 필수 입니다.")
  private String name;

  @NotNull(message = "serial number는 필수 입니다.")
  @Min(value = 1, message = "serial number는 1 이상이어야 합니다.")
  private Integer serialNumber;

  @NotNull(message = "학년은 필수 입니다.")
  private Grades grade;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birth;

  private Sex sex;

  private String phoneNum;

  private String fatherPhoneNum;

  private String motherPhoneNum;

  private String guardiansPhoneNum;
}
