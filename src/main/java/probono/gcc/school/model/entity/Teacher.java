package probono.gcc.school.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teacher {
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

    @Column
    private LocalDate birth; // 추가된 필드

    @Column(length = 50)
    private String pw_answer; // 추가된 필드

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name="CLASS_ID")
    private SchoolClass schoolClass;

    // 연관관계 편의 메서드
    public void setSchoolClass(SchoolClass schoolClass) {

        // 기존 팀과 연관관계를 제거
        if (this.schoolClass != null) {
            this.schoolClass.getTeachers().remove(this);
        }


        // 새로운 연관관계 설정
        this.schoolClass = schoolClass;
        if (schoolClass != null) { // 연관관계 제거 시, team == null
            schoolClass.getTeachers().add(this);
        }
    }



}
