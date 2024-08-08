package probono.gcc.school.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class S3Exception extends RuntimeException {
  private final ErrorCode errorCode;

  public S3Exception(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
