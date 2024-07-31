package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class TeacherResponseDto {

    private Long id;
    private String loginId;
    private String loginPw;
    private String name;
    private Sex sex;
    private String phone_num;
    private Status status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private Long created_charged_id;
    private Long updated_charged_id;
    private LocalDate birth;
    private String pw_question;
    private String pw_answer;


}
