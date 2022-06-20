package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Root extends Effect {

	public Root(int duration) {
		super("Root", duration, EffectType.DEBUFF);
	}
	
	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		if(c.getCondition() == Condition.ACTIVE)
			c.setCondition(Condition.ROOTED);
	}

	@Override
	public void remove(Champion c) {	
		//Counting Root
		int root = 0;		
		for(Effect e : c.getAppliedEffects())
			if(e instanceof Root)
				root++;
		if(root == 0 && c.getCondition() == Condition.ROOTED)c.setCondition(Condition.ACTIVE);
	}
	
}
