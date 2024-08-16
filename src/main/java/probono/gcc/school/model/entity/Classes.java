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
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import probono.gcc.school.model.enums.Grades;
import probono.gcc.school.model.enums.Sections;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "classes")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class Classes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long classId;

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
  @CreationTimestamp
  private Timestamp createdAt;

  @Column
  @UpdateTimestamp
  private Timestamp updatedAt;

  @Column(nullable = false)
  private long createdChargeId;

  @Column
  private long updatedChargeId;

  @OneToMany(mappedBy = "classId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Notice> notice;

  @OneToMany(mappedBy = "classId")
  private List<Users> users;

  @OneToMany(mappedBy = "classId", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Course> courseList;

}
