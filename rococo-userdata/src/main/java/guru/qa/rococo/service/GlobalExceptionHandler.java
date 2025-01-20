package guru.qa.rococo.service;

import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.ex.SameUsernameException;
import guru.qa.rococo.model.ErrorJson;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${spring.application.name}")
    private String appName;

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorJson> handleNotFoundException(@Nonnull RuntimeException ex,
                                                             @Nonnull HttpServletRequest request) {
        LOG.warn("Resolve NotFoundException: ", ex);
        return buildResponse("Resource not found", HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(SameUsernameException.class)
    public ResponseEntity<ErrorJson> handleSameUsernameException(@Nonnull RuntimeException ex,
                                                                 @Nonnull HttpServletRequest request) {
        LOG.warn("Resolve SameUsernameException: ", ex);
        return buildResponse("Validation error", HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorJson> handleGenericException(@Nonnull Exception ex,
                                                            @Nonnull HttpServletRequest request) {
        LOG.error("Unexpected error occurred: ", ex);
        return buildResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    private @Nonnull ResponseEntity<ErrorJson> buildResponse(@Nonnull String type,
                                                             @Nonnull HttpStatus status,
                                                             @Nonnull String message,
                                                             @Nonnull HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(new ErrorJson(
                        appName + ": " + type,
                        status.getReasonPhrase(),
                        status.value(),
                        message,
                        request.getRequestURI()
                ));
    }
}