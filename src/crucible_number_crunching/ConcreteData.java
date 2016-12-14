package crucible_number_crunching;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;

import modifier.DamageModifier;
import modifier.ModifierUtil;

public class ConcreteData {

	public int shownNumber;
	public List<DamageModifier> activeModifiers;
	public WeaponArchetype weaponArchetype;
	
	public ConcreteData(int shownNumber, List<DamageModifier> activeModifiersListToCopy, WeaponArchetype weaponArchetype) {
		this.shownNumber = shownNumber;
		this.activeModifiers = new LinkedList<DamageModifier>();
		activeModifiers.addAll(activeModifiersListToCopy);
		this.weaponArchetype = weaponArchetype;
	}
	
	public String generateIdentifier() {
		List<String> modifierHashList = new LinkedList<String>();
		for(DamageModifier damageModifier: activeModifiers) {
			modifierHashList.add(damageModifier.modifierHash());
		}
		/*
		 *  In the future it may be necessary to account for modifier order
		 */
		Collections.sort(modifierHashList);
		String modifierListHash = "";
		for(String hash: modifierHashList) {
			modifierListHash += hash + ",";
		}
		return weaponArchetype.generateArchetypeHash() + "/" + shownNumber + "/" + modifierListHash;
	}
	
	public DamageConstraint generateDamageConstraint() {
		DamageConstraint damageConstraint = new DamageConstraint();
		damageConstraint.explanationOfMaxDamageSource = damageConstraint.explanationOfMinDamageSource = generateExplanation();
		
		damageConstraint.maxDamage = shownNumber;
		damageConstraint.minDamage = shownNumber - 1;
		
		
		ListIterator<DamageModifier> modifierIterator = activeModifiers.listIterator(activeModifiers.size());
		while(modifierIterator.hasPrevious()) {
			DamageModifier modifier = modifierIterator.previous();
			damageConstraint.maxDamage = modifier.unmodifyValue(damageConstraint.maxDamage, weaponArchetype);
			damageConstraint.minDamage = modifier.unmodifyValue(damageConstraint.minDamage, weaponArchetype);
		}
		
		return damageConstraint;
	}
	

	protected String generateExplanation() {
		return "shown number= " + shownNumber + " from a " + weaponArchetype.exampleName + "-like " + weaponArchetype.weaponCategory.name() + " with the following modifiers: " + ModifierUtil.generateModifierString(activeModifiers);
	}
	
	
}
