package probono.gcc.school.model.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.entity.Teacher;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class TeacherListResponseDto {

    private Long id;

    private String login_id;

    private String login_pw;

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


    public TeacherListResponseDto(Teacher teacher){
        this.id = teacher.getId();
        this.login_id = teacher.getLogin_id();
        this.login_pw = teacher.getLogin_pw();
        this.name = teacher.getName();
        this.sex = teacher.getSex();
        this.phone_num = teacher.getPhone_num();
        this.status = teacher.getStatus();
        this.birth = teacher.getBirth();
        this.pw_question = teacher.getPw_question();
        this.pw_answer = teacher.getPw_answer();


    }
}
