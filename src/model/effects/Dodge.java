package model.effects;

import model.world.Champion;

public class Dodge extends Effect {

	public Dodge(int duration) {
		super("Dodge", duration, EffectType.BUFF);
	}

	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		c.setSpeed((int)(c.getSpeed() * (105.0 / 100)));
	}

	@Override
	public void remove(Champion c) {
		c.setSpeed((int)(c.getSpeed() * (100.0 / 105)));
	}

}
