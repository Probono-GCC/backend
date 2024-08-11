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
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "image")
@Data
@DynamicInsert
@DynamicUpdate
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long imageId;

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
  private long createdChargeId;

  @Column(nullable = false)
  private Long updatedChargeId;

  @ManyToOne
  @JoinColumn(name = "noticeId")
  private Notice noticeId;

  @Column(length = 2048, nullable = false)
  private String imagePath;


  // Getters and Setters

}
