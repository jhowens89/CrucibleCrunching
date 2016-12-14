package modifier.ability;

import modifier.AbilityRelatedDamageModifier;
import crucible_number_crunching.DestinySubclass;
import crucible_number_crunching.WeaponArchetype;

public class TetherDamageModifier extends AbilityRelatedDamageModifier {

	public static final double TETHER_DAMAGE_MULTIPLIER = 1.35;
	
	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value*TETHER_DAMAGE_MULTIPLIER;
	}

	@Override
	public double unmodifyValue(double modifiedValue, WeaponArchetype weaponArchetype) {
		return modifiedValue / TETHER_DAMAGE_MULTIPLIER;
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName();
	}

	@Override
	public DestinySubclass getRequiredSubclass() {
		return DestinySubclass.NIGHTSTALKER;
	}
}
