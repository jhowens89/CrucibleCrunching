package crucible_number_crunching;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import modifier.DamageModifier;
import modifier.ModifierUtil;
import extractor.LocalDataExtractor;

public class WeaponDamageCalculator {

	public static final boolean USE_BARREL_MODS = false;
	public static final boolean USE_HEADSEEKER_DAMAGE = false;
	public static final boolean USE_GLASS_HALF_FULL_DAMAGE = false;

	public static void main(String[] args) throws FileNotFoundException {
		
		
		Map<String, WeaponDamageResult> finalConstrainedResultMap = new TreeMap<String, WeaponDamageCalculator.WeaponDamageResult>();
		List<WeaponDamageResult >sortedDamageResults = generateWeaponDamageResults(finalConstrainedResultMap);

		for (WeaponDamageResult damageResult : sortedDamageResults) {

			System.out.println(damageResult.toString());
			
			List<TestCase> allPossibleTestCases = ModifierUtil.getAllAllowedTestCases(damageResult.weaponArchetype);
			List<TestCase> testsThatNarrowConstraints = new LinkedList<TestCase>();
			for (TestCase testCase : allPossibleTestCases) {
				if(testCase.willNarrowCurrentContraint(damageResult.finalBodyConstraint)) {
					testsThatNarrowConstraints.add(testCase);
				}
			}

			Collections.sort(testsThatNarrowConstraints, new Comparator<TestCase>() {
				@Override
				public int compare(TestCase o1, TestCase o2) {
					int o1Value = (o1.determineRequirements().requiresAtLeastOneTeammate ? 100 : 0) + (o1.determineRequirements().requiresSpecificBarrelMod ? 10 : 0) + o1.damageModifiers.size();
					int o2Value = (o2.determineRequirements().requiresAtLeastOneTeammate ? 100 : 0) + (o2.determineRequirements().requiresSpecificBarrelMod ? 10 : 0) + o2.damageModifiers.size();
					return o1Value - o2Value;
				}
			});

			System.out.println("\t\tNarrowing Tests:");
			for (TestCase testCase : testsThatNarrowConstraints) {
				DamageConstraint matchingConstraint = finalConstrainedResultMap.get(testCase.weaponArchetype.generateArchetypeHash()).finalBodyConstraint;
				if (testCase.damageModifiers.size() <= SettingsUtil.MODIFIER_COUNT_LIMIT && (!testCase.determineRequirements().requiresAtLeastOneTeammate || SettingsUtil.INCLUDE_TESTS_NEEDING_EXTRA_TESTING_TEAMMATES)) {
					System.out.println("\t\t" + ModifierUtil.generateModifierString(testCase.damageModifiers) + " / narrowFactor=" + testCase.getNarrowFactor(matchingConstraint));
				} else {
					break;
				}

			}

		}
		
		
		
		//printRedditTable(sortedDamageResults);

	}

	private static void printRedditTable(List<WeaponDamageResult> sortedDamageResults) {
		Collections.sort(sortedDamageResults, new Comparator<WeaponDamageResult>() {

			@Override
			public int compare(WeaponDamageResult o1, WeaponDamageResult o2) {
				
				if(!o1.weaponArchetype.weaponCategory.name().equals(o2.weaponArchetype.weaponCategory.name())) {
					return o1.weaponArchetype.weaponCategory.name().compareTo(o2.weaponArchetype.weaponCategory.name());
				} else {
					return o1.weaponArchetype.archetypeLabel.compareTo(o2.weaponArchetype.archetypeLabel);
				}
			}
		});
		
		for (WeaponDamageResult damageResult : sortedDamageResults) {
			String weaponType = damageResult.weaponArchetype.weaponCategory.name();
			String archetypeLabel = damageResult.weaponArchetype.archetypeLabel;
			String example = damageResult.weaponArchetype.exampleName;
			
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(3);
			double bodyMin = damageResult.finalBodyConstraint.minDamage;
			double bodyMax = damageResult.finalBodyConstraint.maxDamage;
			double range = bodyMax-bodyMin;
			double critMin = damageResult.weaponArchetype.critMult* bodyMin;
			double critMax = damageResult.weaponArchetype.critMult* bodyMax;
			
			String outputString = weaponType + "|" + archetypeLabel  + "|" + example + "| (" +  df.format(bodyMin) + ", " + df.format(bodyMax) + ") |" + df.format(range) + "| (" + df.format(critMin) + ", " + df.format(critMax) + ")";
			System.out.println(outputString);
			
		}
	}
	
	public static LinkedList<WeaponDamageResult> generateWeaponDamageResults(Map<String, WeaponDamageResult> finalConstrainedResultMap) {
		List<ConcreteData> concreteDataPoints = new LinkedList<ConcreteData>();

		/*
		 * Extract Data Points
		 */

		LocalDataExtractor localDataExtractor = new LocalDataExtractor();
		List<ConcreteData> localDataPoints = localDataExtractor.extractData();
		concreteDataPoints.addAll(filterDataPoints(localDataPoints));

		/*
		 * Assemble end result map
		 */

		Map<String, List<ConcreteData>> dataPointsOrganizedByWeaponArchetype = new TreeMap<String, List<ConcreteData>>();
		for (ConcreteData dataPoint : concreteDataPoints) {
			String weaponArchetypeHash = dataPoint.weaponArchetype.generateArchetypeHash();
			if (dataPointsOrganizedByWeaponArchetype.containsKey(weaponArchetypeHash)) {
				dataPointsOrganizedByWeaponArchetype.get(weaponArchetypeHash).add(dataPoint);
			} else {
				List<ConcreteData> newList = new LinkedList<ConcreteData>();
				newList.add(dataPoint);
				dataPointsOrganizedByWeaponArchetype.put(weaponArchetypeHash, newList);
			}
		}


		for (List<ConcreteData> archetypeDataPointList : dataPointsOrganizedByWeaponArchetype.values()) {
			List<DamageConstraint> allDamageConstraints = new LinkedList<DamageConstraint>();
			WeaponArchetype weaponArchetype = null;
			for (ConcreteData dataPoint : archetypeDataPointList) {
				allDamageConstraints.add(dataPoint.generateDamageConstraint());
				weaponArchetype = dataPoint.weaponArchetype;
			}
			DamageConstraint overallConstraint = DamageConstraint.combineConstraints(allDamageConstraints);

			WeaponDamageResult finalResult = new WeaponDamageResult(overallConstraint, weaponArchetype);
			finalConstrainedResultMap.put(weaponArchetype.generateArchetypeHash(), finalResult);

		}

		/*
		 * Print out sorted result list
		 */
		LinkedList<WeaponDamageResult> sortedDamageResults = new LinkedList<WeaponDamageResult>(finalConstrainedResultMap.values());
		Collections.sort(sortedDamageResults, new Comparator<WeaponDamageResult>() {

			@Override
			public int compare(WeaponDamageResult o1, WeaponDamageResult o2) {
				double val1 = o1.finalBodyConstraint.maxDamage - o1.finalBodyConstraint.minDamage;
				double val2 = o2.finalBodyConstraint.maxDamage - o2.finalBodyConstraint.minDamage;
				return (int) (100000 * (val1 - val2));
			}

		});
		return sortedDamageResults;
	}

	private static Collection<? extends ConcreteData> filterDataPoints(List<ConcreteData> dataPoints) {
		/*
		 * This method will be expanded in the future to account for other
		 * options and possibly to focus in on certain weapon archetypes /
		 * categories
		 */
		Iterator<ConcreteData> dataPointsIter = dataPoints.iterator();
		while (dataPointsIter.hasNext()) {
			ConcreteData dataPoint = dataPointsIter.next();
			for (DamageModifier damageModfier : dataPoint.activeModifiers) {
				String modifierClassName = damageModfier.getClass().getName();
				boolean rejectDataPoint = false;
				if (!Arrays.asList(SettingsUtil.MODIFIER_CLASSES_TO_USE_IN_CALCULATION).contains(modifierClassName)) {
					rejectDataPoint = true;
				} else {
					for(DamageModifier damageModifierToReject: SettingsUtil.SPECIFIC_MODIFIERS_TO_REJECT_IN_CALCULATION) {
						if(damageModfier.modifierHash().equals(damageModifierToReject.modifierHash())) {
							rejectDataPoint = true;
							break;
						}
					}
				}
				
				if(rejectDataPoint) {
					dataPointsIter.remove();
					break;
				}
				
			}
		}
		return dataPoints;
	}

	public static class WeaponDamageResult {
		public DamageConstraint finalBodyConstraint;
		public WeaponArchetype weaponArchetype;

		public WeaponDamageResult(DamageConstraint finalBodyConstraint, WeaponArchetype weaponArchetype) {
			this.finalBodyConstraint = finalBodyConstraint;
			this.weaponArchetype = weaponArchetype;
		}

		public String toString() {
			double critMin = finalBodyConstraint.minDamage * weaponArchetype.critMult;
			double critMax = finalBodyConstraint.maxDamage * weaponArchetype.critMult;
			double bodyMin = finalBodyConstraint.minDamage;
			double bodyMax = finalBodyConstraint.maxDamage;
			return weaponArchetype.exampleName + "-like " + weaponArchetype.weaponCategory.name() + " crits in range:(" + critMin + ", " + critMax + ") and bodies in range:(" + bodyMin + ", " + bodyMax + ")\n\tTest constraining min: " + finalBodyConstraint.explanationOfMinDamageSource + "\n\tTest constraining max: " + finalBodyConstraint.explanationOfMaxDamageSource;
		}
	}

	public static class WeaponEvaluationResult {
		public WeaponEvaluationResult(int crits, int bodies, double minTotalDamage, double maxTotalDamage, String weaponDescription) {
			this.crits = crits;
			this.bodies = bodies;
			this.minTotalDamage = minTotalDamage;
			this.maxTotalDamage = maxTotalDamage;
			this.weaponDescription = weaponDescription;
		}

		public int crits;
		public int bodies;
		public double minTotalDamage;
		public double maxTotalDamage;
		public String weaponDescription;

		@Override
		public String toString() {
			return "(" + minTotalDamage + ", " + maxTotalDamage + ") range is: " + (maxTotalDamage - minTotalDamage) + " and (crits, bodies):(" + crits + ", " + bodies + ") - " + weaponDescription;
		}
	}

	public static class WeaponEvaluationContainer {
		public List<WeaponEvaluationResult> belowResults = new LinkedList<>();
		public List<WeaponEvaluationResult> aboveResults = new LinkedList<>();
		public List<WeaponEvaluationResult> overlappingResults = new LinkedList<>();

		public static WeaponEvaluationContainer generateResults(WeaponDamageResult damageResult, double targetDamage, double allowableDifference) {
			WeaponEvaluationContainer evaluationContainer = new WeaponEvaluationContainer();
			DamageConstraint constraint = damageResult.finalBodyConstraint;
			double critMin = constraint.minDamage * damageResult.weaponArchetype.critMult;
			double critMax = constraint.maxDamage * damageResult.weaponArchetype.critMult;
			double bodyMin = constraint.minDamage;
			double bodyMax = constraint.maxDamage;

			int maxCritsNeeded = (int) ((targetDamage + allowableDifference) / critMin) + 1;
			int maxBodiesNeeded = (int) ((targetDamage + allowableDifference) / critMin) + 1;
			for (int critI = 0; critI < maxCritsNeeded; critI++) {
				for (int bodyI = 0; bodyI < maxBodiesNeeded; bodyI++) {
					double minTotalDamage = critMin * critI + bodyMin * bodyI;
					double maxTotalDamage = critMax * critI + bodyMax * bodyI;
					WeaponEvaluationResult weaponEvaluationResult = new WeaponEvaluationResult(critI, bodyI, minTotalDamage, maxTotalDamage, damageResult.weaponArchetype.exampleName);
					if (targetDamage > maxTotalDamage) {
						if (targetDamage - maxTotalDamage <= allowableDifference) {
							evaluationContainer.belowResults.add(weaponEvaluationResult);
						}
					} else if (targetDamage < minTotalDamage) {
						if (minTotalDamage - targetDamage <= allowableDifference) {
							evaluationContainer.aboveResults.add(weaponEvaluationResult);
						}
					} else {
						evaluationContainer.overlappingResults.add(weaponEvaluationResult);
					}
				}
			}
			return evaluationContainer;
		}

	}

}
