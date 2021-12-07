package model.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class AppExceptionHandler {

    private static final Logger logger = LogManager.getLogger(AppExceptionHandler.class);
    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<Object> handleApiRequestException(RequestException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Exception exception =new Exception(
              e.getMessage(),
              HttpStatus.BAD_REQUEST,
              ZonedDateTime.now(ZoneId.of("Z"))
      );
        logger.error("Error caused by " + e);
      return  new ResponseEntity<>(exception, badRequest);
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException (NotFoundException e) {
        HttpStatus badRequest = HttpStatus.NOT_FOUND;
        Clock cl = Clock.systemUTC();
        Exception exception = new Exception(
                e.getMessage(),
                HttpStatus.NOT_FOUND,
                ZonedDateTime.now(cl)
        );
        logger.error("Error caused by " + e);
        return  new ResponseEntity<>(exception, badRequest);
    }

    @ExceptionHandler(value = {AlreadyExistsException.class})
    public ResponseEntity<Object> handleAlreadyExistsException (AlreadyExistsException e) {
        HttpStatus badRequest = HttpStatus.CONFLICT;
        Clock cl = Clock.systemUTC();
        Exception exception = new Exception(
                e.getMessage(),
                HttpStatus.CONFLICT,
                ZonedDateTime.now(cl)
        );
        logger.error("Error caused by " + e);
        return  new ResponseEntity<>(exception, badRequest);
    }

}
