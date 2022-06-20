package exceptions;

public class LeaderNotCurrentException extends GameActionException{
    public LeaderNotCurrentException() {
    	
    }
    public LeaderNotCurrentException(String s) {
        super(s);
    }
}
