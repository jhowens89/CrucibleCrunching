package modifier.weapon_perk;

import modifier.DamageModifier;
import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;

public class CriticalDamageModifier implements DamageModifier {

	public enum CritType {
		REGULAR, HEADSEEKER;
	}

	protected CritType critType;

	public CriticalDamageModifier(CritType critType) {
		this.critType = critType;
	}

	public CriticalDamageModifier(String inputText) {
		if (inputText.contains("Regular")) {
			this.critType = CritType.REGULAR;
		} else if (inputText.contains("Headseeker")) {
			this.critType = CritType.HEADSEEKER;
		} else {
			throw new RuntimeException("Invalid input text for critType: " + inputText);
		}

	}

	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		if (critType == CritType.REGULAR) {
			return value * weaponArchetype.critMult;
		} else if (critType == CritType.HEADSEEKER) {
			/*
			 * h = 1.25*c - 0.25*b h = 1.25*cm*b - 0.25*b h = b*(1.25*cm - 0.25)
			 */
			return value * (1.25 * weaponArchetype.critMult - 0.25);
		} else {
			throw new RuntimeException("Unable to determine modifyValue case for crit type: " + critType);
		}
	}

	@Override
	public double unmodifyValue(double modifiedValue, WeaponArchetype weaponArchetype) {
		if (critType == CritType.REGULAR) {
			return modifiedValue / weaponArchetype.critMult;
		} else if (critType == CritType.HEADSEEKER) {
			/*
			 * b = h / (1.25*cm - 0.25)
			 */
			return modifiedValue / (1.25 * weaponArchetype.critMult - 0.25);
		} else {
			throw new RuntimeException("Unable to determine unmodifyValue case for crit type: " + critType);
		}
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName() + "|" + critType.name();
	}

	@Override
	public boolean isPossible(WeaponCategory weaponCategory) {
		if (critType == CritType.REGULAR) {
			return weaponCategory != WeaponCategory.FUSION;
		} else if (critType == CritType.HEADSEEKER) {
			return weaponCategory == WeaponCategory.PULSE_RIFLE;
		} else {
			throw new RuntimeException("Unrecognized critType: " + critType);
		}
	}
}
