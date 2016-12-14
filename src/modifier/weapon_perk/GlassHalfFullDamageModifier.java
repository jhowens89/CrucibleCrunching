package modifier.weapon_perk;

import modifier.DamageModifier;
import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;

public class GlassHalfFullDamageModifier implements DamageModifier{
	public enum BulletCategory {
		FIRST_GHF_BULLET(1.03),
		LAST_GHF_BULLET(1.06);
		
		public double damageModifier;
		
		BulletCategory(double damageModifier) {
			this.damageModifier = damageModifier;
		}
	}
	
	protected  BulletCategory bulletCategory;
	
	public GlassHalfFullDamageModifier(BulletCategory bulletCategory) {
		this.bulletCategory = bulletCategory;
	}
	
	public GlassHalfFullDamageModifier(String inputText) {
		if(inputText.contains("1stBullet")) {
			this.bulletCategory = BulletCategory.FIRST_GHF_BULLET;
		} else if(inputText.contains("FinalBullet")) {
			this.bulletCategory = BulletCategory.LAST_GHF_BULLET;
		} else {
			throw new RuntimeException("Invalid input text for glass half full: " + inputText);
		}
	}

	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value * bulletCategory.damageModifier;
	}

	@Override
	public double unmodifyValue(double modifiedValue,
			WeaponArchetype weaponArchetype) {
		return modifiedValue / bulletCategory.damageModifier;
	}
	
	@Override
	public String modifierHash() {
		return getClass().getSimpleName() + "|" + bulletCategory.name();
	}

	@Override
	public boolean isPossible(WeaponCategory weaponCategory) {
		return weaponCategory == WeaponCategory.PULSE_RIFLE || weaponCategory == WeaponCategory.AUTO_RIFLE;
	}
}
