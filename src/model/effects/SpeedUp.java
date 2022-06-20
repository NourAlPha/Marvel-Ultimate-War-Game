package model.effects;

import model.world.Champion;

public class SpeedUp extends Effect {

	public SpeedUp(int duration) {
		super("SpeedUp", duration, EffectType.BUFF);
	}

	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		c.setSpeed((int)(c.getSpeed() * (115.0 / 100)));
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() + 1);
		c.setCurrentActionPoints(c.getCurrentActionPoints() + 1);
	}

	@Override
	public void remove(Champion c) {
		c.setSpeed((int)(c.getSpeed() * (100.0 / 115)));
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn() - 1);
		c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
	}
	
}
