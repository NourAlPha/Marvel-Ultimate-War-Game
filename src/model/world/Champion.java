package model.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import model.abilities.Ability;
import model.effects.Disarm;
import model.effects.Dodge;
import model.effects.Effect;
import model.effects.Shield;
import model.effects.Silence;

public abstract class Champion implements Damageable, Comparable<Champion> {

	private String name;
	private int maxHP;
	private int currentHP;
	private int mana;
	private int maxActionPointsPerTurn;
	private int currentActionPoints;
	private int attackRange;
	private int attackDamage;
	private int speed;
	private ArrayList<Ability> abilities;
	private ArrayList<Effect> appliedEffects;
	private Condition condition;
	private Point location;

	public static HashMap<String, Ability> stringToAbility = new HashMap<>();

	public Champion(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
		this.name = name;
		this.maxHP = maxHP;
		currentHP = maxHP;
		this.mana = mana;
		this.speed = speed;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.maxActionPointsPerTurn = maxActions;
		currentActionPoints = maxActions;
		this.condition = Condition.ACTIVE;
		abilities = new ArrayList<>();
		appliedEffects = new ArrayList<>();
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public void setCurrentActionPoints(int currentActionPoints) {
		this.currentActionPoints = (currentActionPoints > maxActionPointsPerTurn ? maxActionPointsPerTurn
				: currentActionPoints < 0 ? 0 : currentActionPoints);
	}

	public int getCurrentHP() {
		return currentHP;
	}

	public void setCurrentHP(int currentHP) throws CloneNotSupportedException {

		int dodge = 0, shield = 0;
		
		// Counting Dodge and Shield
		for(Effect e : appliedEffects) {
			if(e instanceof Dodge)
				dodge++;
			else if(e instanceof Shield)
				shield++;
		}
		
		// Doing Dodge
		boolean dodgeAttack = false;
		for (int i = 0; i < dodge; i++) {
			if ((int) (Math.random() * 2) == 1) {
				dodgeAttack = true;
				break;
			}
		}

		boolean blockAttack = false;
		if (currentHP < this.currentHP && !dodgeAttack) {
			if (shield > 0) {
				blockAttack = true;
				for (Effect a : appliedEffects) {
					if (a instanceof Shield) {
						getAppliedEffects().remove(a);
						a.remove(this);
						break;
					}
				}
			}
		}
		
		this.currentHP = (currentHP > maxHP ? maxHP
				: currentHP >= this.currentHP ? currentHP
						: dodgeAttack || blockAttack ? this.currentHP : currentHP < 0 ? 0 : currentHP);
	}

	public int getMaxActionPointsPerTurn() {
		return maxActionPointsPerTurn;
	}

	public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
		this.maxActionPointsPerTurn = maxActionPointsPerTurn;
	}

	public int getAttackDamage() {
		int disarm = 0;
		// Counting Disarm
		for(Effect e : appliedEffects)
			if(e instanceof Disarm)
				disarm++;
		if (disarm > 0)
			return 0;
		return attackDamage;
	}

	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public int getMana() {
		return mana;
	}

	public int getCurrentActionPoints() {
		return currentActionPoints;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public ArrayList<Ability> getAbilities() {
		
		int silence = 0;
		
		//Counting Disarm
		for(Effect e : appliedEffects)
			if(e instanceof Silence)
				silence++;
		// Doing Silence
		if (silence > 0)
			return new ArrayList<Ability>();
		return abilities;
	}

	public ArrayList<Effect> getAppliedEffects() {
		return appliedEffects;
	}

	public int compareTo(Champion c) {
		if (c.getSpeed() != this.getSpeed()) 
			return c.getSpeed() - this.getSpeed();
		return this.getName().compareTo(c.getName());
	}

	public abstract void useLeaderAbility(ArrayList<Champion> targets) throws CloneNotSupportedException;
	
}
