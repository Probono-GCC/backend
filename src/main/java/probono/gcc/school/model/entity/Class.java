package probono.gcc.school.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sections;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//mysql의 auto increment
    private Long id;

    @NotNull
    private Year year;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Grades grade;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Sections section;

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

    @PrePersist
    protected void onCreate() {
        this.created_at = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_at = LocalDateTime.now();
    }


}
