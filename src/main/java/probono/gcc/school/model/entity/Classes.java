package probono.gcc.school.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sections;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "classes")
public class Classes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long classId;

  @Column(nullable = false)
  private int year;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Grades grade;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Sections section;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(nullable = false, updatable = false)
  private Timestamp createdAt;

  @Column
  private Timestamp updatedAt;

  @Column(nullable = false)
  private Long createdChargeId;

  @Column
  private Long updatedChargeId;

  // Getters and Setters
}
