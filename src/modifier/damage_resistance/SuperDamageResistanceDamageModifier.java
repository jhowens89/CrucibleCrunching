package modifier.damage_resistance;

import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;
import modifier.DamageModifier;

public class SuperDamageResistanceDamageModifier implements DamageModifier {

	public enum SuperType {
//		BLADE(.50),
//		SUNBREAKER(.50),
//		STORMCALLER(.50),
		SUNSINGER(0.50),
		RADIANT_SKIN_SUNSINGER(0.55);
		
		public double damageResistance;
	
		SuperType(double damageResistance) {
			this.damageResistance = damageResistance;
		}
	}
	
	protected SuperType superType;
	
	public SuperDamageResistanceDamageModifier(SuperType superType) {
		this.superType = superType;
	}
	
	public SuperDamageResistanceDamageModifier(String inputText) {
		this.superType = determineSuperType(inputText);
	}
	
	public static SuperType determineSuperType(String superTypeText) {
		if (superTypeText.contains("RadiantSkinSunsinger")) {
			return SuperType.RADIANT_SKIN_SUNSINGER;
		} else if (superTypeText.contains("Sunsinger")) {
			return SuperType.SUNSINGER;
		}
		 else {
			throw new RuntimeException("Could not match SuperType from superTypeText: " + superTypeText);
		}
	}

	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value * (1 - superType.damageResistance);
	}

	@Override
	public double unmodifyValue(double modifiedValue,
			WeaponArchetype weaponArchetype) {
		return modifiedValue / (1 - superType.damageResistance);
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName() + "|" + superType.name() + "(" + superType.damageResistance +")";
	}

	@Override
	public boolean isPossible(WeaponCategory weaponCategory) {
		return true;
	}

}
