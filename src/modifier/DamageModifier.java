package modifier;

import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;


public interface DamageModifier {
	double modifyValue(double value, WeaponArchetype weaponArchetype);
	double unmodifyValue(double modifiedValue, WeaponArchetype weaponArchetype);
	
	/*
	 * Used to determine if two modifiers are the same
	 */
	String modifierHash();
	
	
	boolean isPossible(WeaponCategory weaponCategory);
	
	
}
