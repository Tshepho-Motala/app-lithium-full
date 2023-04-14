package lithium.security;

public class JwtTokenMissingException extends Exception {
	private static final long serialVersionUID = 9102395768590679789L;
	
	public JwtTokenMissingException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public JwtTokenMissingException(String message) {
		super(message);
	}
	
	public JwtTokenMissingException(Throwable cause) {
		super(cause);
	}
}