package modifier;

import crucible_number_crunching.DestinySubclass;
import crucible_number_crunching.WeaponCategory;

public abstract class AbilityRelatedDamageModifier implements DamageModifier {
	
	@Override
	public boolean isPossible(WeaponCategory weaponCategory) {
		return true;
	}
	
	public abstract DestinySubclass getRequiredSubclass();

}
