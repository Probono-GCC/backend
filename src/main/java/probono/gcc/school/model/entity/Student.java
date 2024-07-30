package probono.gcc.school.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)//mysql의 auto increment
  private Long id;

  @NotNull
  @Column(length = 20)
  private String loginId;

  @NotNull
  @Column(length = 20)
  private String loginPw;

  @NotNull
  @Column(length = 40)
  private String name;

  @NotNull
  @Column
  private Integer serialNumber;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Grades grade;

  @Column
  private LocalDate birth;

  @Enumerated(EnumType.STRING)
  private Sex sex;

  @Column(length = 20)
  private String phoneNum;

  @Column(length = 20)
  private String fatherPhoneNum;

  @Column(length = 20)
  private String motherPhoneNum;

  @Column(length = 20)
  private String guardiansPhoneNum;

  @Embedded
  private Logs logs;

  @Column(length = 50)
  private String pwAnswer; // 추가된 필드

  public void promoteToNextGrade() {
    if (grade != null) {
      this.grade = this.grade.getNextGrade();
    }
  }
}
