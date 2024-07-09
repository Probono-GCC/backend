package probono.gcc.school.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.entity.Teacher;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class TeacherResponseDto {

    private String login_id;
    private String name;
    private LocalDateTime createdAt;
    private Long created_charged_id;
    //나머지 column들도 처리

    //Teacher의 정보를 받아 TeacherResponseDto를 생성
    public TeacherResponseDto(Teacher teacher){
        this.login_id=teacher.getLogin_id();
        this.name=teacher.getName();
        this.createdAt=teacher.getCreated_at();
        this.created_charged_id=teacher.getCreated_charged_id();

    }
}
