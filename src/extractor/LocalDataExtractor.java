package extractor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import modifier.DamageModifier;
import modifier.ability.MeltingPointDamageModifier;
import modifier.ability.VikingFuneralDamageModifier;
import modifier.ability.WeaponsOfLightDamageModifier;
import modifier.damage_resistance.SuperDamageResistanceDamageModifier;
import modifier.weapon_perk.BarrelUpgradeDamageModifier;
import modifier.weapon_perk.CriticalDamageModifier;
import modifier.weapon_perk.GlassHalfFullDamageModifier;
import modifier.weapon_perk.GlassHalfFullDamageModifier.BulletCategory;
import modifier.weapon_perk.SpecialBulletDamageModifier;
import crucible_number_crunching.ConcreteData;
import crucible_number_crunching.DamageConstraint;
import crucible_number_crunching.WeaponArchetype;
import crucible_number_crunching.WeaponCategory;

public class LocalDataExtractor implements DataExtractor {

	public static final String NAME_COLUMN_HEADER = "Name";
	public static final String ARCHETYPE_COLUMN_HEADER = "Archetype";
	public static final String DAMAGE_COLUMN_HEADER = "Damage";
	public static final String WEAPONS_OF_LIGHT_COLUMN_HEADER = "WoL";
	public static final String GLASS_HALF_FULL_COLUMN_HEADER = "GlassHalfFull";
	public static final String CRIT_TYPE_COLUMN_HEADER = "CritType";
	public static final String BARREL_UPGRADE_COLUMN_HEADER = "Barrel";
	public static final String MELTING_POINT_COLUMN_HEADER = "Melting Point";
	public static final String VIKING_FUNERAL_COLUMN_HEADER = "Viking Funeral";
	public static final String SUPER_DAMAGE_RESISTANCE_COLUMN_HEADER = "SuperDamageResistance";
	public static final String SPECIAL_BULLET_COLUMN_HEADER = "SpecialBullet";

	@Override
	public List<ConcreteData> extractData() {

		List<ConcreteData> dataPoints = new LinkedList<ConcreteData>();

		final LocalExtractorWeaponCategoryInfo[] EXTRACTOR_INFOS = new LocalExtractorWeaponCategoryInfo[] { 
				new LocalExtractorWeaponCategoryInfo(WeaponCategory.PULSE_RIFLE, 1.5, "pulse.txt"),
				new LocalExtractorWeaponCategoryInfo(WeaponCategory.AUTO_RIFLE, 1.25, "auto.txt"),
				new LocalExtractorWeaponCategoryInfo(WeaponCategory.MACHINE_GUN, 1.25, "machine_gun.txt"),
				new LocalExtractorWeaponCategoryInfo(WeaponCategory.HAND_CANNON, 1.5, "hand_cannon.txt"),
				new LocalExtractorWeaponCategoryInfo(WeaponCategory.SCOUT_RIFLE, 1.5, "scout.txt")};

		for (LocalExtractorWeaponCategoryInfo extractorInfo : EXTRACTOR_INFOS) {
			String[] linesOfFiles = getLinesOfFile(extractorInfo.fileName);
			String[] columnHeaders = linesOfFiles[0].split(",");
			// Required
			int nameIndex = Arrays.asList(columnHeaders).indexOf(NAME_COLUMN_HEADER);
			int archetypeIndex =  Arrays.asList(columnHeaders).indexOf(ARCHETYPE_COLUMN_HEADER);
			int damageIndex = Arrays.asList(columnHeaders).indexOf(DAMAGE_COLUMN_HEADER);

			for (int i = 1; i < linesOfFiles.length; i++) {
				String[] data = linesOfFiles[i].split(",");
				int shownNumber = Integer.valueOf(data[damageIndex]);
				WeaponArchetype weaponArchetype = new WeaponArchetype(extractorInfo.weaponCategory, data[nameIndex], data[archetypeIndex], extractorInfo.criticalMultiplier);
				List<DamageModifier> activeModifiersList = new LinkedList<DamageModifier>();

				for (int j = 0; j < data.length; j++) {
					if (j == nameIndex || j == archetypeIndex || j == damageIndex) {
						continue;
					} else {
						String value = data[j];
						if (columnHeaders[j].equals(WEAPONS_OF_LIGHT_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new WeaponsOfLightDamageModifier(value));
							}
						} else if (columnHeaders[j].equals(GLASS_HALF_FULL_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new GlassHalfFullDamageModifier(value));
							}
						} else if (columnHeaders[j].equals(CRIT_TYPE_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new CriticalDamageModifier(value));
							}
						} else if (columnHeaders[j].equals(BARREL_UPGRADE_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new BarrelUpgradeDamageModifier(value));
							}
						} else if (columnHeaders[j].equals(MELTING_POINT_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new MeltingPointDamageModifier());
							}
						} else if (columnHeaders[j].equals(VIKING_FUNERAL_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new VikingFuneralDamageModifier(value));
							}
						} else if (columnHeaders[j].equals(SUPER_DAMAGE_RESISTANCE_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new SuperDamageResistanceDamageModifier(value));
							}
						} else if (columnHeaders[j].equals(SPECIAL_BULLET_COLUMN_HEADER)) {
							if (!value.isEmpty()) {
								activeModifiersList.add(new SpecialBulletDamageModifier(value));
							}
						} else {
							System.out.println("Unknown identifiable column header:" + columnHeaders[j] + " and value=" + value);
						}
					}	
				}
				ConcreteData dataPoint = new ConcreteData(shownNumber, activeModifiersList, weaponArchetype);
				dataPoints.add(dataPoint);
			}
		}

		return dataPoints;
	}

	protected String[] getLinesOfFile(String fileName) {
		Scanner in;
		try {
			in = new Scanner(new FileReader(fileName));

			List<String> lines = new LinkedList<String>();
			while (in.hasNext()) {
				String line = in.nextLine();
				if (!line.isEmpty() && line.charAt(0) != '#') {
					lines.add(line);
				}
			}
			in.close();

			String[] lineArray = new String[lines.size()];
			return lines.toArray(lineArray);

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e1);
		}
	}

}
