package modifier;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import crucible_number_crunching.SettingsUtil;
import crucible_number_crunching.TestCase;
import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;
import modifier.ability.MeltingPointDamageModifier;
import modifier.ability.TetherDamageModifier;
import modifier.ability.VikingFuneralDamageModifier;
import modifier.ability.WeaponsOfLightDamageModifier;
import modifier.ability.VikingFuneralDamageModifier.StackCount;
import modifier.ability.WeaponsOfLightDamageModifier.Strength;
import modifier.damage_resistance.SuperDamageResistanceDamageModifier;
import modifier.damage_resistance.SuperDamageResistanceDamageModifier.SuperType;
import modifier.weapon_perk.BarrelUpgradeDamageModifier;
import modifier.weapon_perk.CriticalDamageModifier;
import modifier.weapon_perk.GlassHalfFullDamageModifier;
import modifier.weapon_perk.CriticalDamageModifier.CritType;
import modifier.weapon_perk.GlassHalfFullDamageModifier.BulletCategory;
import modifier.weapon_perk.SpecialBulletDamageModifier;
import modifier.weapon_perk.SpecialBulletDamageModifier.BulletType;

public class ModifierUtil {


	
	public static final ModifierPair[] SPECIAL_CASE_INCOMPATIBLE_MODIFIERS = new ModifierPair[]{
			new ModifierPair(new TetherDamageModifier(), new SuperDamageResistanceDamageModifier(SuperType.SUNSINGER)),
			new ModifierPair(new TetherDamageModifier(), new SuperDamageResistanceDamageModifier(SuperType.RADIANT_SKIN_SUNSINGER)),
			new ModifierPair(new CriticalDamageModifier(CritType.HEADSEEKER),new VikingFuneralDamageModifier(StackCount.ONE))
	};
	
	/*
	 * Note this is not going to include barrels with identical damage
	 * multipliers
	 */
	public static List<TestCase> getAllAllowedTestCases(WeaponArchetype weaponArchetype) {
		List<TestCase> testCases = new LinkedList<TestCase>();

		DamageModifier[][] modifierStackArray = new DamageModifier[][] 
			{ 
				{ 
					new BarrelUpgradeDamageModifier(BarrelUpgradeDamageModifier.BarrelType.SOFT_BALLISTICS), 
					new BarrelUpgradeDamageModifier(BarrelUpgradeDamageModifier.BarrelType.LINEAR_COMPENSATOR), 
					new BarrelUpgradeDamageModifier(BarrelUpgradeDamageModifier.BarrelType.AGGRESSIVE_BALLISTICS) 
				}, 
				{ 
					new CriticalDamageModifier(CritType.REGULAR), 
					new CriticalDamageModifier(CritType.HEADSEEKER) 
				}, 
				{ 
					new GlassHalfFullDamageModifier(BulletCategory.FIRST_GHF_BULLET), 
					new GlassHalfFullDamageModifier(BulletCategory.LAST_GHF_BULLET) 
				}, 
				{ 
					new SpecialBulletDamageModifier(BulletType.LUCK_IN_THE_CHAMBER),
					new SpecialBulletDamageModifier(BulletType.FINAL_ROUND),
				}, 
				{ 
					new MeltingPointDamageModifier(), 
					new TetherDamageModifier() 
				}, 
				{ 
					new VikingFuneralDamageModifier(StackCount.ONE) 
				},
				{ 
					new WeaponsOfLightDamageModifier(Strength.REGULAR), 
					new WeaponsOfLightDamageModifier(Strength.ILLUMINATED) 
				}, 
				{ 
					new SuperDamageResistanceDamageModifier(SuperType.SUNSINGER), 
					new SuperDamageResistanceDamageModifier(SuperType.RADIANT_SKIN_SUNSINGER) 
				} 
			};
		
		

		List<DamageModifier> currentDamageModifierList = new LinkedList<DamageModifier>();
		// No modifiers first
		testCases.add(new TestCase(weaponArchetype, currentDamageModifierList));

		combineModifiersIntoLists(weaponArchetype, testCases, currentDamageModifierList, modifierStackArray, 0);
		return testCases;
	}

	public static void combineModifiersIntoLists(WeaponArchetype weaponArchetype, List<TestCase> testCases, List<DamageModifier> currentDamageModifierList, DamageModifier[][] modifierStackArray, int currentStackIndex) {
		if (currentStackIndex < modifierStackArray.length) {
			for (int i = 0; i < modifierStackArray[currentStackIndex].length; i++) {
				DamageModifier damageModifier = modifierStackArray[currentStackIndex][i];
				boolean classAllowed = Arrays.asList(SettingsUtil.MODIFIER_CLASSES_TO_USE_IN_CALCULATION).contains(damageModifier.getClass().getName());
				if (!classAllowed) {
					continue;
				}

				boolean specificModifierAllowed = true;
				for (DamageModifier specificallyExcludedModifier : SettingsUtil.SPECIFIC_MODIFIERS_TO_REJECT_IN_CALCULATION) {
					if (specificallyExcludedModifier.modifierHash().equals(damageModifier.modifierHash())) {
						specificModifierAllowed = false;
						break;
					}
				}
				if (!specificModifierAllowed) {
					continue;
				}

				if (weaponArchetype != null) {
					boolean possibleForWeaponCategory = damageModifier.isPossible(weaponArchetype.weaponCategory);
					if (!possibleForWeaponCategory) {
						continue;
					}
				}

				/*
				 * Check for special cases of incompatibility
				 */
				boolean isCompatible = true;
				for(ModifierPair incompatiblePair: SPECIAL_CASE_INCOMPATIBLE_MODIFIERS) {
					if(incompatiblePair.contains(damageModifier)) {
						DamageModifier incompatibleHalf = incompatiblePair.getOtherHalf(damageModifier);
						for(DamageModifier alreadyInsertedModifier: currentDamageModifierList) {
							if(incompatibleHalf.modifierHash().equals(alreadyInsertedModifier.modifierHash())) {
								isCompatible = false;
								break;
							}
						}
					}
					if(!isCompatible) {
						break;
					}
				}
				if(!isCompatible) {
					continue;
				}
				
				List<DamageModifier> newCombination = new LinkedList<DamageModifier>(currentDamageModifierList);
				newCombination.add(damageModifier);
				testCases.add(new TestCase(weaponArchetype, newCombination));
				combineModifiersIntoLists(weaponArchetype, testCases, newCombination, modifierStackArray, currentStackIndex + 1);
			}

			/*
			 * This represents the option of choosing none of the damage
			 * modifiers at this index of the array
			 */
			combineModifiersIntoLists(weaponArchetype, testCases, currentDamageModifierList, modifierStackArray, currentStackIndex + 1);
		}
	}

	public static double applyModifiers(List<DamageModifier> damageModifiers, WeaponArchetype weaponArchetype, double initialValue) {
		for (DamageModifier damageModifier : damageModifiers) {
			initialValue = damageModifier.modifyValue(initialValue, weaponArchetype);
		}
		return initialValue;
	}

	public static String generateModifierString(List<DamageModifier> damageModifiers) {
		if (damageModifiers.isEmpty()) {
			return "none";
		} else {
			Iterator<DamageModifier> iter = damageModifiers.iterator();
			String modifierListString = iter.next().modifierHash();
			while (iter.hasNext()) {
				modifierListString += ", " + iter.next().modifierHash();
			}
			return modifierListString;
		}
	}
}
