package probono.gcc.school.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.sql.Timestamp;

import java.util.List;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "subject")
@Data
@DynamicInsert
@DynamicUpdate
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long subjectId;

  @Column(nullable = false, unique = true, length = 20)
  private String name;

  @Column(nullable = false)
  private boolean isElective;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column
  private Timestamp updatedAt;

  @Column(nullable = false)
  private String createdChargeId;

  @Column
  private String updatedChargeId;

  @OneToMany(mappedBy = "subjectId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Course> courseList;

//  @PreUpdate
//  protected void onUpdate() {
//    updatedAt = new Timestamp(System.currentTimeMillis());
//  }


}
