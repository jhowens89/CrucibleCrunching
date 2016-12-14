package modifier.weapon_perk;

import modifier.DamageModifier;
import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;

public class BarrelUpgradeDamageModifier implements DamageModifier {
	public enum BarrelType {
		SMOOTH_BALLISTICS(1.0),
		SOFT_BALLISTICS(0.975),
		ACCURIZED_BALLISTICS(1.025),
		CQB_BALLISTICS(1.0),
		LINEAR_COMPENSATOR(1.025),
		FIELD_CHOKE(1.025),
		SMART_DRIFT_CONTROL(1),
		AGGRESSIVE_BALLISTICS(1.05);
		
		public double damageMultiplier;
	
		BarrelType(double damageMultiplier) {
			this.damageMultiplier = damageMultiplier;
		}
	}
	
	protected BarrelType barrelType;
	
	public BarrelUpgradeDamageModifier(BarrelType barrelType) {
		this.barrelType = barrelType;
	}
	
	public BarrelUpgradeDamageModifier(String inputText) {
		this.barrelType = determineBarrelType(inputText);
	}
	
	public static BarrelType determineBarrelType(String barrelUpgradeText) {
		if (barrelUpgradeText.contains("Smooth Ballistics")) {
			return BarrelType.SMOOTH_BALLISTICS;
		} else if (barrelUpgradeText.contains("Soft Ballistics")) {
			return BarrelType.SOFT_BALLISTICS;
		} else if (barrelUpgradeText.contains("Accurized Ballistics")) {
			return BarrelType.ACCURIZED_BALLISTICS;
		} else if (barrelUpgradeText.contains("CQB Ballistics")) {
			return BarrelType.CQB_BALLISTICS;
		} else if (barrelUpgradeText.contains("Linear Compensator")) {
			return BarrelType.LINEAR_COMPENSATOR;
		} else if (barrelUpgradeText.contains("Field Choke")) {
			return BarrelType.FIELD_CHOKE;
		} else if (barrelUpgradeText.contains("Smart Drift Control")) {
			return BarrelType.SMART_DRIFT_CONTROL;
		} else if (barrelUpgradeText.contains("Aggressive Ballistics")) {
			return BarrelType.AGGRESSIVE_BALLISTICS;
		} else if (barrelUpgradeText.contains("N/A")) {
			return null;
		} else {
			throw new RuntimeException("Could not match BarrelType from barrelUpgradeText: " + barrelUpgradeText);
		}
	}

	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value * barrelType.damageMultiplier;
	}

	@Override
	public double unmodifyValue(double modifiedValue,
			WeaponArchetype weaponArchetype) {
		return modifiedValue / barrelType.damageMultiplier;
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName() + "|" + barrelType.name() + "(" + barrelType.damageMultiplier +")";
	}

	@Override
	public boolean isPossible(WeaponCategory weaponCategory) {
		return true;
	}
}
