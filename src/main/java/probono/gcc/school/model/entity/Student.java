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
    private String login_id;

    @NotNull
    @Column(length = 20)
    private String login_pw;

    @NotNull
    @Column(length = 40)
    private String name;

    @NotNull
    @Column
    private Integer serial_number;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Grades grade;

    @Column
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(length = 20)
    private String phone_num;

    @Column(length = 20)
    private String father_phone_num;

    @Column(length = 20)
    private String mother_phone_num;

    @Column(length = 20)
    private String guardians_phone_num;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @Column
    private LocalDateTime created_at;

    @Column
    private LocalDateTime updated_at;

    @NotNull
    @Column
    private Long created_charged_id;

    @Column
    private Long updated_charged_id;

    public void promoteToNextGrade() {
        if (grade != null) {
            this.grade = this.grade.getNextGrade();
        }
    }
}
