package crucible_number_crunching;

import java.util.List;

public class DamageConstraint {
	public double minDamage = Double.MIN_VALUE;
	public double maxDamage = Double.MAX_VALUE;
	
	public String explanationOfMaxDamageSource;
	public String explanationOfMinDamageSource;
	
	public static DamageConstraint combineConstraints(List<DamageConstraint> damageConstraints) {
		DamageConstraint newConstraint = new DamageConstraint();
		for(DamageConstraint damageConstraint: damageConstraints) {
			if(damageConstraint.maxDamage < newConstraint.maxDamage) {
				newConstraint.maxDamage = damageConstraint.maxDamage;
				newConstraint.explanationOfMaxDamageSource = damageConstraint.explanationOfMaxDamageSource;
			}
			if(damageConstraint.minDamage > newConstraint.minDamage) {
				newConstraint.minDamage = damageConstraint.minDamage;
				newConstraint.explanationOfMinDamageSource = damageConstraint.explanationOfMinDamageSource;
			}
		}
		return newConstraint;
	}
}
