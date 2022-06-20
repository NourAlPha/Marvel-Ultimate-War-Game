package model.effects;

import model.abilities.AreaOfEffect;
import model.abilities.DamagingAbility;
import model.world.Champion;

public class Disarm extends Effect {

	public Disarm(int duration) {
		super("Disarm", duration, EffectType.DEBUFF);
	}

	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		c.getAbilities().add(new DamagingAbility("Punch", 0, 1, 1, AreaOfEffect.SINGLETARGET, 1, 50));
	}

	@Override
	public void remove(Champion c) {
		int i = 0;
		for(; i < c.getAbilities().size(); i++)
			if(c.getAbilities().get(i).getName().equals("Punch"))
				break;
		if(i < c.getAbilities().size())
			c.getAbilities().remove(i);
	}

}
