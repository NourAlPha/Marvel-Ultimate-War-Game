package model.world;

import java.util.ArrayList;

import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;

public class Hero extends Champion {

	public Hero(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage) {
		this(name, maxHP, mana, maxActions, speed, attackRange, attackDamage, null, null, null);
	}

	public Hero(String name, int maxHP, int mana, int maxActions, int speed, int attackRange, int attackDamage,
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
			for (Object o : (ArrayList) c.getAppliedEffects().clone()) {
				Effect e = (Effect) o;
				if (e.getType() == EffectType.DEBUFF) {
					c.getAppliedEffects().remove(e);
					e.remove(c);
				}
			}
			Embrace e = new Embrace(2);
			c.getAppliedEffects().add((Effect)e.clone());
			e.apply(c);
		}

	}

}