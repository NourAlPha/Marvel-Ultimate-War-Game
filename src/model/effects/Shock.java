package model.effects;

import model.world.Champion;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);
	}

	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		c.setSpeed((int) (c.getSpeed() * (90.0 / 100)));
		c.setAttackDamage((int) (c.getAttackDamage() * (90.0 / 100)));
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 1);
		c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
	}

	@Override
	public void remove(Champion c) {
		c.setSpeed((int) (c.getSpeed() * (100.0 / 90)));
		c.setAttackDamage((int) (c.getAttackDamage() * (100 / 90.0)));
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 1);
		c.setCurrentActionPoints(c.getCurrentActionPoints() + 1);
	}

}
