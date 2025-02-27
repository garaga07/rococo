package guru.qa.rococo.service;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ErrorJson;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected @Nonnull ResponseEntity<Object> handleMethodArgumentNotValid(
            @Nonnull MethodArgumentNotValidException ex,
            @Nonnull HttpHeaders headers,
            @Nonnull HttpStatusCode status,
            @Nonnull WebRequest request) {
        return ResponseEntity
                .status(status)
                .body(new ErrorJson(
                        "Ошибка валидации данных",
                        Objects.requireNonNull(HttpStatus.resolve(status.value())).getReasonPhrase(),
                        status.value(),
                        ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", ")),
                        ((ServletWebRequest) request).getRequest().getRequestURI()
                ));
    }

    @ExceptionHandler({
            HttpClientErrorException.NotFound.class,
            HttpClientErrorException.BadRequest.class,
            HttpClientErrorException.Conflict.class,
            HttpClientErrorException.NotAcceptable.class,
            HttpServerErrorException.InternalServerError.class,
            HttpServerErrorException.ServiceUnavailable.class
    })
    public ResponseEntity<ErrorJson> handleRestTemplateExceptions(@Nonnull HttpClientErrorException ex,
                                                                  @Nonnull HttpServletRequest request) {
        LOG.warn("### REST Exception caught in Gateway: {}", ex.getMessage());
        return handleForwardedException(ex, request);
    }

    @ExceptionHandler(NoRestResponseException.class)
    public ResponseEntity<ErrorJson> handleApiNoResponseException(@Nonnull RuntimeException ex,
                                                                  @Nonnull HttpServletRequest request) {
        LOG.warn("### No REST Response ", ex);
        return withStatus("Ошибка API", HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorJson> handleUnexpectedException(@Nonnull Exception ex,
                                                               @Nonnull HttpServletRequest request) {
        LOG.error("### Internal Server Error ", ex);
        return withStatus("Внутренняя ошибка", HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    private @Nonnull ResponseEntity<ErrorJson> withStatus(@Nonnull String type,
                                                          @Nonnull HttpStatus status,
                                                          @Nonnull String message,
                                                          @Nonnull HttpServletRequest request) {
        return ResponseEntity
                .status(status)
                .body(new ErrorJson(
                        type,
                        status.getReasonPhrase(),
                        status.value(),
                        message,
                        request.getRequestURI()
                ));
    }

    @Nonnull
    private ResponseEntity<ErrorJson> handleForwardedException(@Nonnull HttpClientErrorException ex,
                                                               @Nonnull HttpServletRequest request) {
        ErrorJson originalError = ex.getResponseBodyAs(ErrorJson.class);
        assert originalError != null;
        return ResponseEntity
                .status(originalError.status())
                .body(new ErrorJson(
                        originalError.type(),
                        originalError.title(),
                        originalError.status(),
                        originalError.detail(),
                        request.getRequestURI()
                ));
    }
}