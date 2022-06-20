package exceptions;

public class LeaderAbilityAlreadyUsedException extends GameActionException{
	public LeaderAbilityAlreadyUsedException(){
		
	}
	public LeaderAbilityAlreadyUsedException(String s){
		super(s);
	}
}
