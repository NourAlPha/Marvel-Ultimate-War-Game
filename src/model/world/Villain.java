package model.world;

import java.util.ArrayList;

public class Villain extends Champion {

	public Villain(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
		this(name, maxHP, mana, maxActions, speed, attackRange, attackDamage, null, null, null);
	}

	public Villain(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage,
			String ability1, String ability2, String ability3) {
		super(name, maxHP, mana, maxActions, speed, attackRange, attackDamage);
		if (ability1 != null && ability2 != null && ability3 != null) {
			getAbilities().add(stringToAbility.getOrDefault(ability1, null));
			getAbilities().add(stringToAbility.getOrDefault(ability2, null));
			getAbilities().add(stringToAbility.getOrDefault(ability3, null));
		}
	}

	@Override
	public void useLeaderAbility(ArrayList<Champion> targets) throws CloneNotSupportedException {
		for (Champion c : targets) {
			if(c.getCurrentHP() / c.getMaxHP() < 0.3) {
				c.setCondition(Condition.KNOCKEDOUT);
				c.setCurrentHP(0);
			}
		}
	}

}