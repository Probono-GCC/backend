package probono.gcc.school.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import probono.gcc.school.model.enums.Status;

@Embeddable
@NoArgsConstructor
@Data
public class Logs {

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

  @NotNull
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  @NotNull
  private Long createdChargedId;

  private Long updatedChargedId;

  public Logs(LocalDateTime createdAt, Long createdChargedId) {
    this.status = Status.ACTIVE;
    this.createdAt = createdAt;
    this.updatedAt = null;
    this.createdChargedId = createdChargedId;
    this.updatedChargedId = null;
  }

  public void updateLogs(Status status, LocalDateTime createdAt, Long createdChargedId,
      LocalDateTime updatedAt, Long updatedChargedId) {
    this.status = status;
    this.updatedAt = updatedAt;
    this.updatedChargedId = updatedChargedId;
  }

}
