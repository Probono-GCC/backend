package probono.gcc.school.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "notice")
@Getter
@Setter
@DynamicInsert
@DynamicUpdate
public class Notice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long noticeId;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

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
  private String createdChargeId;

  @Column
  private String updatedChargeId;

  @Column(nullable = false)
  private int views;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NoticeType type;

  @ManyToOne
  @JoinColumn(name = "classId")
  private Classes classId;

  @ManyToOne
  @JoinColumn(name = "username")
  private Users username;

  @ManyToOne
  @JoinColumn(name = "courseId")
  private Course courseId;

  /**
   * Image완성 이후 추가 로직 필요
   */
  @OneToMany(mappedBy = "noticeId", cascade = CascadeType.ALL , orphanRemoval = true)
  private List<Image> imageList;

  // Getters and Setters
}
