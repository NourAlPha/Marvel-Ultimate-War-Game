package exceptions;

public class NotEnoughResourcesException extends GameActionException {
	public NotEnoughResourcesException() {

	}

	public NotEnoughResourcesException(String s) {
		super(s);
	}
}
