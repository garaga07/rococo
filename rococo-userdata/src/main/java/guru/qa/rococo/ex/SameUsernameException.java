package guru.qa.rococo.ex;

public class SameUsernameException extends RuntimeException {
  public SameUsernameException(String message) {
    super(message);
  }
}
