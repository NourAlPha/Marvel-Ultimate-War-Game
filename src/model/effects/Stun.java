package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Stun extends Effect {

	public Stun(int duration) {
		super("Stun", duration, EffectType.DEBUFF);
	}

	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		c.setCondition(Condition.INACTIVE);
	}

	@Override
	public void remove(Champion c) {
		//Counting Stun and Root
		int stun = 0, root = 0;
		for(Effect e : c.getAppliedEffects()) {
			if(e instanceof Stun)
				stun++;
			else if(e instanceof Root)
				root++;
		}
		if(stun == 0 && root > 0)c.setCondition(Condition.ROOTED);
		else if(stun == 0)c.setCondition(Condition.ACTIVE);
	}

}
