package extractor;

import crucible_number_crunching.WeaponCategory;

public class LocalExtractorWeaponCategoryInfo {

	public WeaponCategory weaponCategory;
	public double criticalMultiplier;
	public String fileName;
	
	public LocalExtractorWeaponCategoryInfo(WeaponCategory weaponCategory, double precisionMultiplier, String fileName) {
		this.weaponCategory = weaponCategory;
		this.criticalMultiplier = precisionMultiplier;
		this.fileName = fileName;
	}

	public WeaponCategory getWeaponCategory() {
		return weaponCategory;
	}

	public double getPrecisionMultiplier() {
		return criticalMultiplier;
	}

	public String getFileName() {
		return fileName;
	}
	
}
