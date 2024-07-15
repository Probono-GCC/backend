package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class TeacherUpdateRequestDto {

    private String name;
    private LocalDate birth;
    private String phone_num;
    private String pw_answer;
    private String login_previous_pw; //Previous PW
    private String login_new_pw; //New PW
    private LocalDateTime updated_at;
    private Long updated_charged_id;

}
