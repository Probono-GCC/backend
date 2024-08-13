package probono.gcc.school.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler2 extends ResponseEntityExceptionHandler {
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<String> handleCustomException(CustomException ex) {

    return ResponseEntity
        .status(ex.getStatus()) // 400 Bad Request
        .body(ex.getMessage()); // 예외 메시지 반환
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGlobalException(Exception ex) {

    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR) // 400 Bad Request
        .body(ex.getMessage()); // 예외 메시지 반환
  }


}
