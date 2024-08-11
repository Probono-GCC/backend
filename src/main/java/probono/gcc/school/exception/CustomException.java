package probono.gcc.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class CustomException extends RuntimeException{

  private final HttpStatus status;

  public CustomException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }

  public CustomException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }

}
