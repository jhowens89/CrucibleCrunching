package crucible_number_crunching;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import crucible_number_crunching.WeaponDamageCalculator.WeaponDamageResult;
import modifier.DamageModifier;
import modifier.ModifierUtil;
import modifier.ability.WeaponsOfLightDamageModifier;
import modifier.ability.WeaponsOfLightDamageModifier.Strength;
import modifier.damage_resistance.SuperDamageResistanceDamageModifier;
import modifier.damage_resistance.SuperDamageResistanceDamageModifier.SuperType;
import modifier.weapon_perk.BarrelUpgradeDamageModifier.BarrelType;

public class HandcannonMachineGunTestCreator {

	public static final double JON_CONSTANT = 200.0/165;
	
	public static class GunInfo {
		public String gunName;
		public BarrelType barrelUpgrade;
		public double baseDamage;
		public double criticalMultiplier;

		public GunInfo(String gunName, BarrelType barrelUpgrade, double baseDamage, double criticalMultiplier) {
			this.gunName = gunName;
			this.barrelUpgrade = barrelUpgrade;
			this.baseDamage = baseDamage;
			this.criticalMultiplier = criticalMultiplier;
		}
		
		
		public double calculateCrucibleDamage(int crits, int bodies) {
			double bodyDamage = baseDamage * JON_CONSTANT * (barrelUpgrade == null ? 1 : barrelUpgrade.damageMultiplier);
			return bodies * bodyDamage + crits * criticalMultiplier * bodyDamage;
		}
		
		public double withAFinalRound(int crits, int bodies, boolean finalIsBody) {
			double bodyDamage = baseDamage * JON_CONSTANT * (barrelUpgrade == null ? 1 : barrelUpgrade.damageMultiplier);
			if(finalIsBody) {
				return (bodies - 1) * bodyDamage + (1.33 * bodyDamage) + crits * criticalMultiplier * bodyDamage;
			} else {
				return bodies * bodyDamage + (crits - 1) * criticalMultiplier * bodyDamage + (1.33* bodyDamage * criticalMultiplier);
			}
		}
		
		
		@Override
		public String toString() {
			return gunName + (barrelUpgrade == null ? "" : " " + barrelUpgrade.name());
		}
		
	}
	
	
	public static double[] HP_THEORY_VALUES = new double[]{
		150 * JON_CONSTANT,
		151.5 * JON_CONSTANT,
		153 * JON_CONSTANT,
		154.5 * JON_CONSTANT,
		156 * JON_CONSTANT,
		157.5 * JON_CONSTANT,
		159 * JON_CONSTANT,
		160.5 * JON_CONSTANT,
		162 * JON_CONSTANT,
		163.5 * JON_CONSTANT,
		165 * JON_CONSTANT,
		167.5 * JON_CONSTANT,
		172.5 * JON_CONSTANT,
		180 * JON_CONSTANT,
		190 * JON_CONSTANT,
		202.5 * JON_CONSTANT,
		217.5 * JON_CONSTANT
	};
	
	public static GunInfo[] AVAILABLE_GUNS = new GunInfo[] {
		new GunInfo("The Last Word", BarrelType.SOFT_BALLISTICS, 42, 1.5),
		new GunInfo("The Last Word", BarrelType.SMART_DRIFT_CONTROL, 42, 1.5),
		new GunInfo("The Last Word", BarrelType.AGGRESSIVE_BALLISTICS, 42, 1.5),
		new GunInfo("Eyasluna", null, 47, 1.5),
		new GunInfo("Hawkmoon", BarrelType.FIELD_CHOKE, 47, 1.5),
		new GunInfo("Hawkmoon", BarrelType.AGGRESSIVE_BALLISTICS, 47, 1.5),
		new GunInfo("Ill Will", null, 52, 1.5),
		new GunInfo("Unending Deluge III", BarrelType.SMART_DRIFT_CONTROL, 23.8, 1.25),
		new GunInfo("Unending Deluge III", BarrelType.SOFT_BALLISTICS, 23.8, 1.25),
		new GunInfo("Unending Deluge III", BarrelType.ACCURIZED_BALLISTICS, 23.8, 1.25),
		new GunInfo("Unending Deluge III", BarrelType.AGGRESSIVE_BALLISTICS, 23.8, 1.25),
//		new GunInfo("The Silvered Dread", BarrelType.ACCURIZED_BALLISTICS, 31.7, 1.25),
//		new GunInfo("The Silvered Dread", BarrelType.AGGRESSIVE_BALLISTICS, 31.7, 1.25),
		new GunInfo("Chaotic Neutral", BarrelType.SMOOTH_BALLISTICS, 39.7, 1.25),
		new GunInfo("Chaotic Neutral", BarrelType.FIELD_CHOKE, 39.7, 1.25),
		new GunInfo("The Unseeing Eye", BarrelType.AGGRESSIVE_BALLISTICS, 39.7, 1.25),
		new GunInfo("Bane of the Taken", BarrelType.SMOOTH_BALLISTICS, 47.6, 1.25),
		new GunInfo("Bane of the Taken", BarrelType.SOFT_BALLISTICS, 47.6, 1.25),
		new GunInfo("Bonekruscher", BarrelType.FIELD_CHOKE, 47.6, 1.25),
		new GunInfo("Bonekruscher", BarrelType.AGGRESSIVE_BALLISTICS, 47.6, 1.25),
		new GunInfo("**Exile's Student", BarrelType.ACCURIZED_BALLISTICS, 42, 1.5),
		new GunInfo("**Ace of Spades", BarrelType.SOFT_BALLISTICS, 52, 1.5),
//		new GunInfo("**SilverDreadedLike", BarrelType.SOFT_BALLISTICS, 31.7, 1.25),
//		new GunInfo("**SilverDreadedLike", BarrelType.CQB_BALLISTICS, 31.7, 1.25),
		new GunInfo("**Chaotic NeutralLike", BarrelType.SOFT_BALLISTICS, 39.7, 1.25),
	};
	
	public static void main(String[] args) {
		
		int minTarget = 180;
		int maxTarget = 265;
		TreeMap<Double, String> damageToDescription = new TreeMap<>();
		
		
		
		DamageModifier[][] additionalModifierStackArray = new DamageModifier[][]{
//				{
//					new TetherDamageModifier()
//				},
				{
					new WeaponsOfLightDamageModifier(Strength.REGULAR),
					new WeaponsOfLightDamageModifier(Strength.ILLUMINATED)
				}/*,{
					new SuperDamageResistanceDamageModifier(SuperType.SUNSINGER),
					new SuperDamageResistanceDamageModifier(SuperType.RADIANT_SKIN_SUNSINGER)
				}*/
		};
		
		List<TestCase> possibleTests = new LinkedList<>();
		List<DamageModifier> currentDamageModifierList = new LinkedList<DamageModifier>();
		ModifierUtil.combineModifiersIntoLists(null, possibleTests, currentDamageModifierList, additionalModifierStackArray, 0);
		
		Map<String, WeaponDamageResult> finalConstrainedResultMap = new TreeMap<String, WeaponDamageCalculator.WeaponDamageResult>();
		List<WeaponDamageResult >sortedDamageResults = WeaponDamageCalculator.generateWeaponDamageResults(finalConstrainedResultMap);
		
		for(int critIndex = 0; critIndex < 25; critIndex++) {
			for(int bodyIndex = 0; bodyIndex < 25; bodyIndex++) {
//				for(GunInfo gun: AVAILABLE_GUNS) {
//					double unmodifiedDamage = gun.calculateCrucibleDamage(critIndex, bodyIndex);
//					if(unmodifiedDamage > minTarget && unmodifiedDamage < maxTarget) {
//						damageToDescription.put(unmodifiedDamage, critIndex + " crits, " +bodyIndex + " bodies from " + gun.toString() + " with no modifiers");
//					}
//				
//					for(TestCase possibleTest: possibleTests) {
//						double modifiedDamage = ModifierUtil.applyModifiers(possibleTest.getDamageModifiers(), null, unmodifiedDamage);
//						if(modifiedDamage > minTarget && modifiedDamage < maxTarget) {
//							damageToDescription.put(modifiedDamage, critIndex + " crits, " +bodyIndex + " bodies from " + gun.toString() + " with modifiers: " + ModifierUtil.generateModifierString(possibleTest.getDamageModifiers()));
//						}
//						
////						/*
////						 * Not very clean final round code plopped in
////						 */
////						if(gun.gunName.equals("Eyasluna")) {
////							if(critIndex > 0) {
////								double unmodifiedDamage1 = gun.withAFinalRound(critIndex, bodyIndex, false);
////								double modifiedDamage1 = ModifierUtil.applyModifiers(possibleTest.getDamageModifiers(), null, unmodifiedDamage1);
////								if(modifiedDamage1 > minTarget && modifiedDamage1 < maxTarget) {
////									damageToDescription.put(modifiedDamage1, critIndex + " crits, " +bodyIndex + " bodies from " + gun.toString() + " with a critical final round modifiers: " + ModifierUtil.generateModifierString(possibleTest.getDamageModifiers()));
////								}
////							}
////							
////							if(bodyIndex > 0) {
////								double unmodifiedDamage2 = gun.withAFinalRound(critIndex, bodyIndex, true);
////								double modifiedDamage2 = ModifierUtil.applyModifiers(possibleTest.getDamageModifiers(), null, unmodifiedDamage2);
////								if(modifiedDamage2 > minTarget && modifiedDamage2 < maxTarget) {
////									damageToDescription.put(modifiedDamage2, critIndex + " crits, " +bodyIndex + " bodies from " + gun.toString() + " with a non-critical final round modifiers: " + ModifierUtil.generateModifierString(possibleTest.getDamageModifiers()));
////								}
////							}
////						}
//					}
//				}
					for(WeaponDamageResult weaponDamageResult: sortedDamageResults) {
						double minUnmodifiedDamage = weaponDamageResult.finalBodyConstraint.minDamage * bodyIndex + weaponDamageResult.finalBodyConstraint.minDamage * weaponDamageResult.weaponArchetype.critMult * critIndex;
						double maxUnmodifiedDamage = weaponDamageResult.finalBodyConstraint.maxDamage * bodyIndex + weaponDamageResult.finalBodyConstraint.maxDamage * weaponDamageResult.weaponArchetype.critMult * critIndex;
						
						if(minUnmodifiedDamage > minTarget && minUnmodifiedDamage < maxTarget) {
							damageToDescription.put(minUnmodifiedDamage, critIndex + " crits, " +bodyIndex + " bodies from MINIMIZED" + weaponDamageResult.weaponArchetype.exampleName + " with no modifiers");
						}
						
						if(maxUnmodifiedDamage > minTarget && maxUnmodifiedDamage < maxTarget) {
							damageToDescription.put(maxUnmodifiedDamage, critIndex + " crits, " +bodyIndex + " bodies from MAXIMIZED" + weaponDamageResult.weaponArchetype.exampleName + " with no modifiers");
						}
						
						for(TestCase possibleTest: possibleTests) {
							double modifiedMinDamage = ModifierUtil.applyModifiers(possibleTest.getDamageModifiers(), null, minUnmodifiedDamage);
							if(modifiedMinDamage > minTarget && modifiedMinDamage < maxTarget) {
								damageToDescription.put(modifiedMinDamage, critIndex + " crits, " +bodyIndex + " bodies from MINIMIZED" + weaponDamageResult.weaponArchetype.exampleName + " with modifiers: " + ModifierUtil.generateModifierString(possibleTest.getDamageModifiers()));
							}
							
							double modifiedMaxDamage = ModifierUtil.applyModifiers(possibleTest.getDamageModifiers(), null, maxUnmodifiedDamage);
							if(modifiedMaxDamage > minTarget && modifiedMaxDamage < maxTarget) {
								damageToDescription.put(modifiedMaxDamage, critIndex + " crits, " +bodyIndex + " bodies from MAXIMIZED" + weaponDamageResult.weaponArchetype.exampleName + " with modifiers: " + ModifierUtil.generateModifierString(possibleTest.getDamageModifiers()));
							}
						}
					}
					
					
				
			}
		}
		
//		GunInfo[] LUCKY_GUNS = new GunInfo[] {
//			new GunInfo("How Dare You", null, 42, 1.5),
//			new GunInfo("The Vanity", null, 47, 1.5),
//			new GunInfo("Ill Will", null, 52, 1.5)
//		};
//		
//		for(int index = 1; index < 15; index++) {
//			for(GunInfo luckyGun: LUCKY_GUNS) {
//				double unmodifiedSingularCritDamage = luckyGun.calculateCrucibleDamage(1, 0);
//				double unmodifiedSingularBodyDamage = luckyGun.calculateCrucibleDamage(0, 1);
//				
//				double unmodifiedCritDamage = (unmodifiedSingularCritDamage * 1.30) + (unmodifiedSingularCritDamage * (index - 1));
//				double unmodifiedBodyDamage = (unmodifiedSingularBodyDamage * 1.30) + (unmodifiedSingularBodyDamage * (index - 1));
//				
//				for(TestCase possibleTest: possibleTests) {
//					double modifiedCritDamage = ModifierUtil.applyModifiers(possibleTest.getDamageModifiers(), null, unmodifiedCritDamage);
//					if(modifiedCritDamage > minTarget && modifiedCritDamage < maxTarget) {
//						damageToDescription.put(modifiedCritDamage, index + " crits with one being lucky from " + luckyGun.toString() + " with modifiers: " + ModifierUtil.generateModifierString(possibleTest.getDamageModifiers()));
//					}
//				}
//				
//				for(TestCase possibleTest: possibleTests) {
//					double modifiedBodyDamage = ModifierUtil.applyModifiers(possibleTest.getDamageModifiers(), null, unmodifiedBodyDamage);
//					if(modifiedBodyDamage > minTarget && modifiedBodyDamage < maxTarget) {
//						damageToDescription.put(modifiedBodyDamage, index + " bodies with one being lucky from " + luckyGun.toString() + " with modifiers: " + ModifierUtil.generateModifierString(possibleTest.getDamageModifiers()));
//					}
//				}
//			}
//		}
		
		
		int armorIndex = 1;
		for(Double damage: damageToDescription.keySet()) {
			if(armorIndex < HP_THEORY_VALUES.length && HP_THEORY_VALUES[armorIndex] < damage) {
				System.out.println("  >  Armor " + armorIndex + " with Hp Value: " + HP_THEORY_VALUES[armorIndex++]);
			}
			System.out.println(damage + " " + damageToDescription.get(damage));
		}
		
		
	}

}
