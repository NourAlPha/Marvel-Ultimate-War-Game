package model.effects;

import model.world.Champion;

public class Embrace extends Effect {

	public Embrace(int duration) {
		super("Embrace", duration, EffectType.BUFF);
	}
	
	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		c.setCurrentHP((int)(c.getMaxHP() * (20.0 / 100)) + c.getCurrentHP());
		c.setMana((int)(c.getMana() * (120.0 / 100)));
		c.setAttackDamage((int)(c.getAttackDamage() * (120.0 / 100)));
		c.setSpeed((int)(c.getSpeed() * (120.0 / 100)));
	}

	@Override
	public void remove(Champion c) {
		c.setAttackDamage((int)(c.getAttackDamage() * (100.0 / 120)));
		c.setSpeed((int)(c.getSpeed() * (100.0 / 120)));
	}
	
}
