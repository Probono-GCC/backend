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
import probono.gcc.school.model.enums.NoticeType;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "notice")
public class Notice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long noticeId;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

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

  @Column(nullable = false)
  private int views;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NoticeType type;

  @ManyToOne
  @JoinColumn(name = "classId")
  private Classes classId;

  @ManyToOne
  @JoinColumn(name = "loginId")
  private Users loginId;

  @ManyToOne
  @JoinColumn(name = "courseId")
  private Course courseId;

  // Getters and Setters
}
