package probono.gcc.school.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;
import probono.gcc.school.model.enums.Role;
import probono.gcc.school.model.enums.Sex;
import probono.gcc.school.model.enums.Status;

@Entity
@Table(name = "users")
public class Users {

  @Id
  @Column(length = 20)
  private String loginId;

  @Column(nullable = false, length = 255)
  private String loginPw;

  @Column(nullable = false, length = 40)
  private String name;

  @Column(nullable = false, unique = true)
  private int serialNumber;

  @Enumerated(EnumType.STRING)
  private Sex sex;

  @Column(length = 20)
  private String phoneNum;

  @Column(length = 50)
  private String pwAnswer;

  @Column(columnDefinition = "DATE")
  private Date birth;

  @Column(length = 20)
  private String fatherPhoneNum;

  @Column(length = 20)
  private String motherPhoneNum;

  @Column(length = 20)
  private String guardiansPhoneNum;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

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

  @ManyToOne
  @JoinColumn(name = "classId")
  private Classes classId;

  @ManyToOne
  @JoinColumn(name = "imageId")
  private Image imageId;

  // Getters and Setters
}
