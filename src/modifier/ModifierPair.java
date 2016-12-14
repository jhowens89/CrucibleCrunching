package modifier;

public class ModifierPair {
	public DamageModifier first;
	public DamageModifier second;

	public ModifierPair(DamageModifier first, DamageModifier second) {
		this.first = first;
		this.second = second;
	}

	public boolean contains(DamageModifier damageModifier) {
		return damageModifier.modifierHash().equals(first.modifierHash()) || damageModifier.modifierHash().equals(second.modifierHash());
	}

	public DamageModifier getOtherHalf(DamageModifier damageModifier) {
		if (damageModifier.modifierHash().equals(first.modifierHash())) {
			return second;
		} else if (damageModifier.modifierHash().equals(second.modifierHash())) {
			return first;
		} else {
			return null;
		}
	}

}
