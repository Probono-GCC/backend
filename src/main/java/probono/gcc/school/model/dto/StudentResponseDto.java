package probono.gcc.school.model.dto;

import lombok.Data;
import probono.gcc.school.model.entity.Logs;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class StudentResponseDto {

  private Long id;
  private String login_id;
  private String name;
  private Integer serial_number;
  private Grades grade;
  private LocalDate birth;
  private Sex sex;
  private String phone_num;
  private String father_phone_num;
  private String mother_phone_num;
  private String guardians_phone_num;
}
