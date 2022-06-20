package exceptions;

public class UnallowedMovementException extends GameActionException {
	public UnallowedMovementException() {

	}

	public UnallowedMovementException(String s) {
		super(s);
	}
}
