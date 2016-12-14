package crucible_number_crunching;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.management.modelmbean.RequiredModelMBean;

import modifier.AbilityRelatedDamageModifier;
import modifier.DamageModifier;
import modifier.ModifierUtil;
import modifier.weapon_perk.BarrelUpgradeDamageModifier;

public class TestCase {
	
	public class Requirements {
		public boolean requiresAtLeastOneTeammate = false;
		public boolean requiresSpecificBarrelMod = false;;
	}
	
	protected WeaponArchetype weaponArchetype;
	protected List<DamageModifier> damageModifiers;
	
	public TestCase(WeaponArchetype weaponArchetype, List<DamageModifier> damageModifiers) {
		this.weaponArchetype = weaponArchetype;
		this.damageModifiers = damageModifiers;
	}
	
	public boolean willNarrowCurrentContraint(DamageConstraint damageConstraint) {
		return getNarrowFactor(damageConstraint) != -1;
	}
	
	public List<DamageModifier> getDamageModifiers() {
		return damageModifiers;
	}
	
	/*
	 * Returns number 0...1 indicating if the test is more likely to narrow the min or narrow the max or split it down the middle
	 */
	public double getNarrowFactor(DamageConstraint damageConstraint) {
		double constraintMinAfter = ModifierUtil.applyModifiers(damageModifiers, weaponArchetype, damageConstraint.minDamage);
		double constraintMaxAfter = ModifierUtil.applyModifiers(damageModifiers, weaponArchetype, damageConstraint.maxDamage);
		/*
		 * Very small value is to guard against being matched against the modifier lists that actually created the constraint in the first place
		 */
		
		final double ROUNDING_ERROR_GUARD = .0000001;
		
		int expectedMin;
		if(Math.abs(constraintMinAfter - Math.round(constraintMinAfter)) < ROUNDING_ERROR_GUARD) {
			expectedMin = (int) Math.round(constraintMinAfter);
		} else {
			expectedMin = ((int)constraintMinAfter) + 1;
		}
		
		int expectedMax;
		if(Math.abs(constraintMaxAfter - Math.round(constraintMaxAfter)) < ROUNDING_ERROR_GUARD) {
			expectedMax = (int) Math.round(constraintMaxAfter);
		} else {
			expectedMax = ((int)constraintMaxAfter) + 1;
		}
		
		
		if(expectedMin == expectedMax) {
			//Will not constraint
			return -1;
		} else {
			double minDif = expectedMin - constraintMinAfter;
			double maxDif = 1-(expectedMax - constraintMaxAfter);
			double narrowFactor = minDif/(minDif + maxDif);
	
			if(Math.abs(narrowFactor) < ROUNDING_ERROR_GUARD || Math.abs(1 - narrowFactor) < ROUNDING_ERROR_GUARD) {
				return -1;
			} else {
				return narrowFactor;
			}
			
		}
	}
	
	public Requirements determineRequirements() {
		
		Requirements requirements = new Requirements();
		Set<DestinySubclass> subclassesNeeded = new TreeSet<>();
		for(DamageModifier damageModifier: damageModifiers) {
			if(damageModifier instanceof AbilityRelatedDamageModifier) {
				subclassesNeeded.add(((AbilityRelatedDamageModifier)damageModifier).getRequiredSubclass());
			}
			if(damageModifier instanceof BarrelUpgradeDamageModifier) {
				requirements.requiresSpecificBarrelMod = true;
			}
		}
		requirements.requiresAtLeastOneTeammate = subclassesNeeded.size() > 1;
		return requirements;
	}
	
}
