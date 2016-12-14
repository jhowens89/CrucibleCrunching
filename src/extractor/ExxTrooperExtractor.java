package extractor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import modifier.weapon_perk.BarrelUpgradeDamageModifier;
import modifier.weapon_perk.CriticalDamageModifier;
import modifier.weapon_perk.BarrelUpgradeDamageModifier.BarrelType;
import modifier.weapon_perk.CriticalDamageModifier.CritType;
import modifier.DamageModifier;
import crucible_number_crunching.ConcreteData;
import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;

public class ExxTrooperExtractor implements DataExtractor {

	@Override
	public List<ConcreteData> extractData() {

		final String critMatchText = "Crit Shot Damage";
		final String bodyMatchText = "Body Shot Damage";
		final String critMultText = "Crit Multiplier";
		final String roundsPerMinuteText = "Rounds Per Minute";
		final String barrelUpgradeText = "Barrel Upgrade";
		final String specialModifierText = "Special Modifier";

		final int nameIndex = 0;
		int barrelUpgradeIndex = 0;
		int roundsPerMinuteIndex = 0;
		int critIndex = 0;
		int bodyIndex = 0;
		int critMultIndex = 0;
		int specialModifierIndex = 0;

		/*
		 * Define archetype by: RPM, crit damage, and no barrel mods and roll
		 * over after headers added"
		 */
		TreeMap<String, ConcreteData> dataMap = new TreeMap();
		WeaponCategory weaponCategory = null;

		Scanner in;
		try {
			in = new Scanner(new FileReader("exo.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e1);
		}

		while (in.hasNext()) {
			boolean waitingForSupportedWeaponCategory = weaponCategory == null;

			String line = in.nextLine();
			if (line.contains(critMatchText)) {

				String[] columns = line.split(",");

				weaponCategory = determineCategory(columns[nameIndex]);
				//Currently not trusting exo
				if(weaponCategory == WeaponCategory.AUTO_RIFLE) {
					continue;
				}
				
				if (WeaponCategory.EXXTROOPER_SUPPORTED_CATEGORIES.contains(weaponCategory)) {
					for (int i = 1; i < columns.length; i++) {
						if (columns[i].contains(critMatchText)) {
							critIndex = i;
						} else if (columns[i].contains(bodyMatchText)) {
							bodyIndex = i;
						} else if (columns[i].contains(barrelUpgradeText)) {
							barrelUpgradeIndex = i;
						} else if (columns[i].contains(roundsPerMinuteText)) {
							roundsPerMinuteIndex = i;
						} else if (columns[i].contains(critMultText)) {
							critMultIndex = i;
						} else if (columns[i].contains(specialModifierText)) {
							specialModifierIndex = i;
						}
					}
				} else {
					weaponCategory = null;
				}
			} else if (!waitingForSupportedWeaponCategory) {
				try {
					String[] data = line.split(",");
					String name = data[nameIndex];
					String barrelUpgradeTextValue = data[barrelUpgradeIndex];
					int crit = Integer.parseInt(data[critIndex]);
					int body = Integer.parseInt(data[bodyIndex]);
					double critMult = Double.parseDouble(data[critMultIndex].replace("x", ""));
					String roundsPerMinute = data[roundsPerMinuteIndex];
					String specialModifier = data[specialModifierIndex];

//					if (!specialModifier.contains("None")) {
//						continue;
//					}

					BarrelType barrelType;
					try {
						barrelType = BarrelUpgradeDamageModifier.determineBarrelType(barrelUpgradeTextValue);
					} catch (RuntimeException e) {
						System.out.println("Could not determine recognize barrel type. Exception: " + e.getMessage());
						continue;
					}
					
					String archetypeLabel = convertToArchetypeLabel(roundsPerMinute, specialModifier, weaponCategory);
					
					WeaponArchetype weaponArchetype = new WeaponArchetype(weaponCategory, name, archetypeLabel, critMult);
					List<DamageModifier> damageModifiers = new LinkedList<>();
					
					if(barrelType != null) {
						BarrelUpgradeDamageModifier barrelUpgradeDamageModifier = new BarrelUpgradeDamageModifier(barrelType);
						damageModifiers.add(barrelUpgradeDamageModifier);
					}
					
					ConcreteData dataPoint = new ConcreteData(body, damageModifiers, weaponArchetype);
					dataMap.put(dataPoint.generateIdentifier(), dataPoint);
					
					damageModifiers.add(new CriticalDamageModifier(CritType.REGULAR));
					dataPoint = new ConcreteData(crit, damageModifiers, weaponArchetype);
					dataMap.put(dataPoint.generateIdentifier(), dataPoint);
					
				} catch (NumberFormatException e) {
					/*
					 *  I'm okay with this at the moment, because I will be running into columns that don't follow the same formatting like Thorn
					 */
					continue;
				}
			}

		}
		in.close();
		
		return new LinkedList<>(dataMap.values());
	}

	public static String convertToArchetypeLabel(String rpmText, String specialModifier, WeaponCategory weaponCategory) {
		if(weaponCategory == WeaponCategory.PULSE_RIFLE) {
			if(rpmText.contains("450/900")) {
				return "77/4";
			} else if(rpmText.contains("415/900")) {
				return "73/7";
			} else if(rpmText.contains("360/900")) {
				if(specialModifier.contains("4-burst")) {
					return "66/14 Hakke";
				} else {
					return "66/14";
				}
			} else if(rpmText.contains("318/900")) {
				if(specialModifier.contains("4-burst")) {
					return "59/30 Hakke";
				} else {
					return "59/30";
				}
			}
		}
		
		/*
		 * Default until more archetypes are clearly defined
		 */
		if(specialModifier != null && !specialModifier.isEmpty() && !specialModifier.equals("None")) {
			return rpmText + "#" +specialModifier;
		} else {
			return rpmText;
		}
		
	}

	public static WeaponCategory determineCategory(String columnHeaderText) {
		if (columnHeaderText.contains("Auto Rifles")) {
			return WeaponCategory.AUTO_RIFLE;
		} else if (columnHeaderText.contains("Scout Rifles")) {
			return WeaponCategory.SCOUT_RIFLE;
		} else if (columnHeaderText.contains("Hand Cannons")) {
			return WeaponCategory.HAND_CANNON;
		} else if (columnHeaderText.contains("Pulse Rifles")) {
			return WeaponCategory.PULSE_RIFLE;
		} else if (columnHeaderText.contains("Sidearms")) {
			return WeaponCategory.SIDEARM;
		} else if (columnHeaderText.contains("Sniper Rifles")) {
			return WeaponCategory.SNIPER;
		} else if (columnHeaderText.contains("Shotgun")) {
			return WeaponCategory.SHOTGUN;
		} else if (columnHeaderText.contains("Fusion Rifles")) {
			return WeaponCategory.FUSION;
		} else if (columnHeaderText.contains("Machine Guns")) {
			return WeaponCategory.MACHINE_GUN;
		} else if (columnHeaderText.contains("Rocket Launcher")) {
			return WeaponCategory.ROCKET_LAUNCHER;
		} else {
			throw new RuntimeException("Could not match WeaponCategory from columnHeaderText: " + columnHeaderText);
		}
	}

}
