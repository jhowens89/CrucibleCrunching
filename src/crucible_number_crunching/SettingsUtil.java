package crucible_number_crunching;


import modifier.DamageModifier;
import modifier.ability.MeltingPointDamageModifier;
import modifier.ability.TetherDamageModifier;
import modifier.ability.VikingFuneralDamageModifier;
import modifier.ability.WeaponsOfLightDamageModifier;
import modifier.damage_resistance.SuperDamageResistanceDamageModifier;
import modifier.weapon_perk.BarrelUpgradeDamageModifier;
import modifier.weapon_perk.CriticalDamageModifier;
import modifier.weapon_perk.GlassHalfFullDamageModifier;
import modifier.weapon_perk.GlassHalfFullDamageModifier.BulletCategory;
import modifier.weapon_perk.SpecialBulletDamageModifier.BulletType;
import modifier.weapon_perk.SpecialBulletDamageModifier;

public class SettingsUtil {
	
	public static final boolean INCLUDE_TESTS_NEEDING_EXTRA_TESTING_TEAMMATES = true;
	public static final int MODIFIER_COUNT_LIMIT = 5;

	public static final String[] MODIFIER_CLASSES_TO_USE_IN_CALCULATION = new String[] {
		BarrelUpgradeDamageModifier.class.getName(),
		CriticalDamageModifier.class.getName(),
		GlassHalfFullDamageModifier.class.getName(),
		MeltingPointDamageModifier.class.getName(),
		TetherDamageModifier.class.getName(),
		VikingFuneralDamageModifier.class.getName(),
		WeaponsOfLightDamageModifier.class.getName(),
		SpecialBulletDamageModifier.class.getName(),
		SuperDamageResistanceDamageModifier.class.getName()
	};
	
	public static final DamageModifier[] SPECIFIC_MODIFIERS_TO_REJECT_IN_CALCULATION = new DamageModifier[] {
		new GlassHalfFullDamageModifier(BulletCategory.FIRST_GHF_BULLET),
		/*
		 * Rejecting this because I believe it's actually something like 33.333%. All the other calculations go against it being
		 * just a plain 33%
		 */
		new SpecialBulletDamageModifier(BulletType.FINAL_ROUND)
	};
}
