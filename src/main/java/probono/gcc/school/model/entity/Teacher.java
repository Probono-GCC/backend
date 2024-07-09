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
public class Teacher {
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

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(length = 20)
    private String phone_num;

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
}
