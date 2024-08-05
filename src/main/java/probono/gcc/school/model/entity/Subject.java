package probono.gcc.school.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Timestamp;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "subject")
@Data
@DynamicInsert
@DynamicUpdate
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long subjectId;

  @Column(nullable = false, unique = true, length = 20)
  private String name;

  @Column(nullable = false)
  private boolean isElective;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

//  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Timestamp createdAt;

  @Column
  private Timestamp updatedAt;

  @Column(nullable = false)
  private Long createdChargeId;

  @Column
  private Long updatedChargeId;

//  @PreUpdate
//  protected void onUpdate() {
//    updatedAt = new Timestamp(System.currentTimeMillis());
//  }


}
