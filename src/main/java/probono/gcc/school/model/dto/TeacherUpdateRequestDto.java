package probono.gcc.school.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class TeacherUpdateRequestDto {

    private String name;
    private LocalDate birth;
    private String phone_num;
    private String pw_answer;

    private String previous_pw;
    private String new_pw;

    private LocalDateTime updated_at;
    private Long updated_charged_id;

}
