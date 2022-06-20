package model.world;

import java.awt.Point;

public class Cover implements Damageable {
	private int currentHP;
	private Point location;

	public Cover(int x, int y) {
		currentHP = 100 + (int) (Math.random() * (900));
		location = new Point(x, y);
	}

	public int getCurrentHP() {
		return currentHP;
	}

	public void setCurrentHP(int currentHP) {
		this.currentHP = currentHP > 0 ? currentHP : 0;
	}

	public Point getLocation() {
		return location;
	}

}
