package modifier.ability;

import modifier.AbilityRelatedDamageModifier;
import crucible_number_crunching.DestinySubclass;
import crucible_number_crunching.WeaponArchetype;

public class MeltingPointDamageModifier extends AbilityRelatedDamageModifier {

	public static final double MELTING_POINT_DAMAGE_MULTIPLIER = 1.5;
	
	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value*MELTING_POINT_DAMAGE_MULTIPLIER;
	}

	@Override
	public double unmodifyValue(double modifiedValue, WeaponArchetype weaponArchetype) {
		return modifiedValue / MELTING_POINT_DAMAGE_MULTIPLIER;
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName();
	}

	@Override
	public DestinySubclass getRequiredSubclass() {
		return DestinySubclass.SUNBREAKER;
	}
}
