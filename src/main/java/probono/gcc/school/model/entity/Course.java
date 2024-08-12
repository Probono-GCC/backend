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
import jakarta.persistence.TemporalType;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "course")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long courseId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private Timestamp createdAt;

  @Column
  @UpdateTimestamp
  private Timestamp updatedAt;

  @Column(nullable = false)
  private long createdChargeId;

  private long updatedChargeId;

  @ManyToOne
  @JoinColumn(name = "subjectId")
  private Subject subjectId;

  @ManyToOne
  @JoinColumn(name = "classId")
  private Classes classId;

  // Getters and Setters
}
