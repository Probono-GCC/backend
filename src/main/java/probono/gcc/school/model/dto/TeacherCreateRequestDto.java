package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class TeacherCreateRequestDto {

    private String name;
    private String login_id;
    private String login_pw;
    //private String re_type_pw;

}
