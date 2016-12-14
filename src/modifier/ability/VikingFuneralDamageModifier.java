package modifier.ability;

import modifier.AbilityRelatedDamageModifier;
import modifier.damage_resistance.SuperDamageResistanceDamageModifier.SuperType;
import crucible_number_crunching.DestinySubclass;
import crucible_number_crunching.WeaponArchetype;

public class VikingFuneralDamageModifier extends AbilityRelatedDamageModifier {

	/*
	 * Currently editing out stacks of two and three because I'm currently unsure if
	 * 3 stacks are represented as a 1.15 multiplier or a (1.05)^3 multiplier
	 */
	
	public enum StackCount {
		ONE(1);
		//TWO(2),
		//THREE(3);
		
		private int stackInt;
		
		StackCount(int stackInt) {
			this.stackInt = stackInt;
		}
		
		public double getDamageMultiplier() {
			return Math.pow(1.05, stackInt);
		}
	}
	
	protected StackCount stackCount;
	
	public VikingFuneralDamageModifier(StackCount stackCount) {
		this.stackCount = stackCount;
	}
	
	public VikingFuneralDamageModifier(String inputText) {
		this.stackCount = determineStackCount(inputText);
	}
	
	private StackCount determineStackCount(String inputText) {
		if (inputText.contains("SINGLE")) {
			return StackCount.ONE;
		} else {
			throw new RuntimeException("Could not match StackCount from inputText: " + inputText);
		}
	}

	@Override
	public double modifyValue(double value, WeaponArchetype weaponArchetype) {
		return value * stackCount.getDamageMultiplier();
	}

	@Override
	public double unmodifyValue(double modifiedValue, WeaponArchetype weaponArchetype) {
		return modifiedValue / stackCount.getDamageMultiplier();
	}

	@Override
	public String modifierHash() {
		return getClass().getSimpleName() + "|" + stackCount.name();
	}
	
	@Override
	public DestinySubclass getRequiredSubclass() {
		return DestinySubclass.SUNSINGER;
	}

}
