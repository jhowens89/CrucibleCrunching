package modifier.weapon_perk;

import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;
import modifier.DamageModifier;
import modifier.weapon_perk.GlassHalfFullDamageModifier.BulletCategory;

public class SpecialBulletDamageModifier implements DamageModifier {

	public enum BulletType {
		LUCK_IN_THE_CHAMBER(1.3),
		FINAL_ROUND(1.33);
		
		public double damageMultiplier;
		
		BulletType(double damageMultiplier) {
			this.damageMultiplier = damageMultiplier;
		}
	}
	
	public BulletType bulletType;
	
	public SpecialBulletDamageModifier(BulletType bulletType) {
		this.bulletType = bulletType;
	}
	
	public SpecialBulletDamageModifier(String inputText) {
		if(inputText.contains("LuckInTheChamber")) {
			this.bulletType = BulletType.LUCK_IN_THE_CHAMBER;
		} else if(inputText.contains("FinalRound")) {
			this.bulletType = BulletType.FINAL_ROUND;
		} else {
			throw new RuntimeException("Invalid input text for special bullet: " + inputText);
		}
	}
	
	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value * bulletType.damageMultiplier;
	}

	@Override
	public double unmodifyValue(double modifiedValue, WeaponArchetype weaponArchetype) {
		return modifiedValue / bulletType.damageMultiplier;
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName() + "|" + bulletType.name();
	}

	@Override
	public boolean isPossible(WeaponCategory weaponCategory) {
		return weaponCategory == WeaponCategory.SCOUT_RIFLE ||weaponCategory == WeaponCategory.HAND_CANNON || weaponCategory == WeaponCategory.SHOTGUN || weaponCategory == WeaponCategory.SNIPER;
	}

}
