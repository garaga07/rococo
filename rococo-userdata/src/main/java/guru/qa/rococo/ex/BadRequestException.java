package guru.qa.rococo.ex;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}