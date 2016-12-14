package crucible_number_crunching;

public class WeaponArchetype {
	public WeaponCategory weaponCategory;
	public String exampleName;
	public String archetypeLabel;
	public double critMult;
	
	
	public WeaponArchetype(WeaponCategory weaponCategory, String exampleName, String archetypeLabel, double critMult) {
		this.weaponCategory = weaponCategory;
		this.exampleName = exampleName;
		this.archetypeLabel = archetypeLabel;
		this.critMult = critMult;
	}
	
	public String generateArchetypeHash() {
		return weaponCategory.name() + "/" + critMult + "/" + archetypeLabel;
	}
}
