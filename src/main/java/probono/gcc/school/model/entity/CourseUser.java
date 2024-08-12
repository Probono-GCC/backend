package probono.gcc.school.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "course_user")
public class CourseUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long cuId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(nullable = false, updatable = false)
  private Timestamp createdAt;

  @Column
  private Timestamp updatedAt;

  @Column(nullable = false)
  private long createdChargeId;

  private long updatedChargeId;

  @ManyToOne
  @JoinColumn(name = "courseId")
  private Course courseId;

  @ManyToOne
  @JoinColumn(name = "loginId")
  private Users loginId;

  // Getters and Setters

}
