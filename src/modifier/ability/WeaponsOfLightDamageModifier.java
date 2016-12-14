package modifier.ability;

import modifier.AbilityRelatedDamageModifier;
import crucible_number_crunching.DestinySubclass;
import crucible_number_crunching.WeaponArchetype;

public class WeaponsOfLightDamageModifier extends AbilityRelatedDamageModifier {
	
	public enum Strength {
		REGULAR(1.25),
		ILLUMINATED(1.35);
		
		public double strengthMultiplier;
		
		Strength(double strengthMultiplier) {
			this.strengthMultiplier = strengthMultiplier;
		}
	}
	
	protected Strength bubbleStrength;
	
	public WeaponsOfLightDamageModifier(Strength bubbleStrength) {
		this.bubbleStrength = bubbleStrength;
	}
	
	public WeaponsOfLightDamageModifier(String inputText) {
		if(inputText.contains("Regular")) {
			this.bubbleStrength = Strength.REGULAR;
		} else if(inputText.contains("Illuminated")) {
			this.bubbleStrength = Strength.ILLUMINATED;
		} else {
			throw new RuntimeException("Invalid input text for weapons of light: " + inputText);
		}
	}

	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value * bubbleStrength.strengthMultiplier;
	}

	@Override
	public double unmodifyValue(double modifiedValue, WeaponArchetype weaponArchetype) {
		return modifiedValue / bubbleStrength.strengthMultiplier;
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName() + "|" + bubbleStrength.name();
	}
	
	@Override
	public DestinySubclass getRequiredSubclass() {
		return DestinySubclass.DEFENDER;
	}
	
}
