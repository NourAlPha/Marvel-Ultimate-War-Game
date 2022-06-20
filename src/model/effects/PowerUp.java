package model.effects;

import model.abilities.Ability;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;

public class PowerUp extends Effect {

	public PowerUp(int duration) {
		super("PowerUp", duration, EffectType.BUFF);
	}

	@Override
	public void apply(Champion c) throws CloneNotSupportedException {
		for (Ability a : c.getAbilities()) {
			if (a instanceof DamagingAbility) {
				DamagingAbility ad = (DamagingAbility) a;
				ad.setDamageAmount((int) (ad.getDamageAmount() * (120.0 / 100)));
			} else if (a instanceof HealingAbility) {
				HealingAbility ah = (HealingAbility) a;
				ah.setHealAmount((int) (ah.getHealAmount() * (120.0 / 100)));
			}
		}
	}

	@Override
	public void remove(Champion c) {
		for (Ability a : c.getAbilities()) {
			if (a instanceof DamagingAbility) {
				DamagingAbility ad = (DamagingAbility) a;
				ad.setDamageAmount((int) (ad.getDamageAmount() * (100.0 / 120)));
			} else if (a instanceof HealingAbility) {
				HealingAbility ah = (HealingAbility) a;
				ah.setHealAmount((int) (ah.getHealAmount() * (100.0 / 120)));
			}
		}
	}

}
