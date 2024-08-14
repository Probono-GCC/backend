package probono.gcc.school.exception;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
        .body(ex.getMessage()); // 예외 메시지 반환
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND) // 404 Not Found
        .body(ex.getMessage()); // 예외 메시지 반환
  }

  @ExceptionHandler(DuplicateEntityException.class)
  public ResponseEntity<String> handleDuplicateEntityException(DuplicateEntityException ex) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT) // 409 Conflict
        .body(ex.getMessage()); // 예외 메시지 반환
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
        .body(ex.getMessage()); // 예외 메시지 반환
  }
//
//  @ExceptionHandler(EntityNotFoundException.class)
//  public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
//    return ResponseEntity
//        .status(HttpStatus.NOT_FOUND) // 404 Not Found
//        .body(ex.getMessage()); // 예외 메시지 반환
//  }
//
//  @ExceptionHandler(EntityInactiveException.class)
//  public ResponseEntity<String> handleEntityInactiveException(EntityInactiveException ex) {
//    return ResponseEntity
//        .status(HttpStatus.GONE) // 410 Gone
//        .body(ex.getMessage()); // 예외 메시지 반환
//  }
//
//  @ExceptionHandler(IllegalStateException.class)
//  public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
//    return ResponseEntity
//        .status(HttpStatus.BAD_REQUEST) // 400 Bad Request
//        .body(ex.getMessage()); // 예외 메시지 반환
//  }
//
//  @ExceptionHandler(Exception.class)
//  public ResponseEntity<String> handleGenericException(Exception ex) {
//    return ResponseEntity
//        .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500 Internal Server Error
//        .body("An unexpected error occurred: " + ex.getMessage());
//  }
}
