package probono.gcc.school.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  EMPTY_FILE_EXCEPTION("The provided file is empty."),
  IO_EXCEPTION_ON_IMAGE_UPLOAD("An I/O exception occurred during image upload."),
  NO_FILE_EXTENTION("The provided file does not have an extension."),
  INVALID_FILE_EXTENTION("The provided file extension is not allowed."),
  PUT_OBJECT_EXCEPTION("An error occurred while putting the object to S3."),
  IO_EXCEPTION_ON_IMAGE_DELETE("An I/O exception occurred during image deletion.");

  private final String message;
}
