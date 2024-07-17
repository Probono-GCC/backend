package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TeacherCreateResponseDto {

    private String login_id;
    private String name;
    private String login_pw;
    //private String re_type_pw;
}
