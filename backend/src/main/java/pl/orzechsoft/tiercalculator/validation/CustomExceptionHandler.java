package pl.orzechsoft.tiercalculator.validation;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.orzechsoft.tiercalculator.model.exception.CustomerDoesNotExistException;
import pl.orzechsoft.tiercalculator.model.exception.InvalidCustomerException;

@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<List<String>> handleException(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(List.of(e.getMessage()));
  }

  @ExceptionHandler(CustomerDoesNotExistException.class)
  public ResponseEntity<?> handleException(CustomerDoesNotExistException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of(e.getMessage()));
  }

  @ExceptionHandler(InvalidCustomerException.class)
  public ResponseEntity<?> handleException(InvalidCustomerException e) {
    return ResponseEntity.badRequest().body(List.of(e.getMessage()));
  }
}