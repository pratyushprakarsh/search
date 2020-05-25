package com.pratyush.search.error;

import com.pratyush.search.dto.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<?> handleIllegalArgumentException(Exception ex) {
        log.error("Illegal Argument Exception Occured ", ex);
        return new ResponseEntity<>(new ResponseWrapper<>(false, ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateEntityException.class})
    public ResponseEntity<?> handleDuplicateEntityException(Exception ex) {
        log.error("Illegal Argument Exception Occured ", ex);
        return new ResponseEntity<>(new ResponseWrapper<>(false, ex.getMessage(), null), HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity Not Found Exception Occured  ", ex);
        return new ResponseEntity<>(new ResponseWrapper<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleException(Exception ex) {
        log.error("handleServerException", ex);
        return new ResponseEntity<>(new ResponseWrapper<>(false, ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

}