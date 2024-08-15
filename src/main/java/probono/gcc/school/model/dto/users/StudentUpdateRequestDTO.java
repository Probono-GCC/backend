package probono.gcc.school.model.dto.users;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class StudentUpdateRequestDTO {


  private String password;//항상 바꿀 수 있음
  private String name;//항상 바꿀 수 있음
  private int serialNumber;//항상 바꿀 수 있음
  private Grades grade; //항상 바꿀 수 있음
  private LocalDate birth;//항상 바꿀 수 있음 //최초 1회 접속 시 입력
  private Sex sex;//항상 바꿀 수 있음 //최초 1회 접속 시 입력
  private String phoneNum;//항상 바꿀 수 있음
  private String fatherPhoneNum;//항상 바꿀 수 있음
  private String motherPhoneNum;//항상 바꿀 수 있음
  private String guardiansPhoneNum;//항상 바꿀 수 있음
  private String pwAnswer;//최초 1회 접속 시에만 바꿀 수 있음 //최초 1회 접속 시 입력
  private Long imageId;//항상 바꿀 수 있음 //최초 1회 접속 시 입력

}
