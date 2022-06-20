package exceptions;

public class ChampionDisarmedException extends GameActionException {
	public ChampionDisarmedException() {

	}

	public ChampionDisarmedException(String s) {
		super(s);
	}
}
