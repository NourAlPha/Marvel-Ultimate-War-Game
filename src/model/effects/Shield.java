package model.effects;

import model.world.Champion;

public class Shield extends Effect {

	public Shield(int duration) {
		super("Shield", duration, EffectType.BUFF);
	}

	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		c.setSpeed((int)(c.getSpeed() * (102.0 / 100)));
	}

	@Override
	public void remove(Champion c) {
		c.setSpeed((int)(c.getSpeed() * (100.0 / 102)));
	}
	
}
