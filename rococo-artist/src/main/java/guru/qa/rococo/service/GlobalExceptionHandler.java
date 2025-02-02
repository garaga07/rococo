package guru.qa.rococo.service;

import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ErrorJson;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorJson> handleNotFoundException(@Nonnull NotFoundException ex,
                                                             @Nonnull HttpServletRequest request) {
        LOG.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorJson(
                        "Not Found",
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        HttpStatus.NOT_FOUND.value(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorJson> handleBadRequestException(@Nonnull BadRequestException ex,
                                                               @Nonnull HttpServletRequest request) {
        LOG.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorJson(
                        "Bad Request",
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        request.getRequestURI()
                ));
    }
}