package me.civ5.modtools.mod.modinfo.related;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

public abstract class Related {
	private static Map<String, String> knownDlc = new HashMap<String, String>();
	static {
		knownDlc.put("0E3751A1-F840-4e1b-9706-519BF484E59D", "Expansion - Gods and Kings");
		knownDlc.put("293C1EE3-1176-44f6-AC1F-59663826DE74", "Genghis Khan's Mongolia");
		knownDlc.put("B685D5DE-7CCA-4e75-81B4-2F60754E6330", "Isabella's Spain and Pachacuti's Incan Empire");
		knownDlc.put("ECF7C605-BA11-4CAC-8D80-D71306AAC471", "Kamehameha's Polynesian Empire");
		knownDlc.put("B3030D39-C0D8-4bc7-91B1-7AD1CAF585AB", "Harald Bluetooth's Denmark");
		knownDlc.put("112C22B2-5308-42b6-B734-171CCAB3037B", "Sejong The Great's Korea");
		knownDlc.put("7459BA32-5764-44ae-8E95-01AD0E0EFD48", "Nebuchadnezzar's Babylon");
		knownDlc.put("BBB0D085-A0B1-4475-B007-3E549CF3ADC3", "Wonders of the Ancient World");
		knownDlc.put("8871E748-29A4-4910-8C57-8C99E32D0167", "Upgrade Data 1");
	}
	
	private static Map<String, String> knownMods = new HashMap<String, String>();
	static {
		knownMods.put("d1b6328c-ff44-4b0d-aad7-c657f83610cd", "DLL - Various Mod Components");
		knownMods.put("603c3504-d224-4101-a5da-cd712fefd49d", "DLL - Various Mod Components (GK)");
		knownMods.put("7a1dd394-09eb-41dd-85ae-2f01fbcbf57a", "Maps - Rotate Start Position");
		knownMods.put("a6e2d8b9-4675-4409-aa06-b9a9469d3cef", "Promotions - AntiAir Specialisations");
		knownMods.put("609a05b4-0231-4eaa-9ad1-a2d79e1fc16d", "Promotions - Subs Silent Running");
		knownMods.put("4f184876-e029-4aad-98a4-10e5334ce52e", "Promotions - Terrain Crossing");
		knownMods.put("69eeec02-c97e-42ac-b40c-1f38673c7af8", "Units - Herdsmen");
		knownMods.put("24d89bd3-1904-442c-8a2c-e5a002df8e33", "Units - Mounted Units");
		knownMods.put("057224c2-b380-433c-9f64-8bbc30816688", "UI - CS View City (GK)");
		knownMods.put("1d335490-033b-4bd8-b762-75a7b8be85f0", "UI - Found Pantheon Button");
		knownMods.put("7cc1b8f8-f3be-46c1-96b4-dbcd424fda93", "UI - Next Turn Clock (GK)");
		knownMods.put("66743884-90cd-4de4-9ea2-dfbbe608c3d1", "UI - Notification Options (GK)");
		knownMods.put("4ad8de1c-a52a-4752-8e61-e73edcecf8dd", "UI - Religion Spread");
		knownMods.put("21e78f1f-06f9-4147-ac61-75aa7c403d18", "Utils - Modular DiploCorner");
		knownMods.put("53df104c-e650-4962-bc4f-2b94a9fc7402", "Utils - Modular UnitPanel (GK)");
		knownMods.put("2ddb65d1-deb8-4202-a28b-911f3da8aab8", "Utils - Multiple Wonder Splashes");
	}
	
	protected ModReporter reporter;
	
	private String id;
	private String name = null;
	private int minVersion;
	private int maxVersion;
	
	public Related(ModReporter reporter, String id, String minVersion, String maxVersion) {
		this.reporter = reporter;
		this.id = id;
		this.name = getName();
		
		try {
			this.minVersion = Integer.parseInt(minVersion);
		} catch (NumberFormatException e) {
			this.minVersion = 0;
		}

		try {
			this.maxVersion = Integer.parseInt(maxVersion);
		} catch (NumberFormatException e) {
			this.maxVersion = 999;
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		if (name == null) {
			if (knownDlc.containsKey(id)) {
				name =  knownDlc.get(id);
			} else if (knownMods.containsKey(id)) {
				name =  knownMods.get(id);
			} else {
				name = "UNKNOWN!!!";
			}
		}
		
		return name;
	}
	public int getMinVersion() {
		return minVersion;
	}

	public int getMaxVersion() {
		return maxVersion;
	}
	
	public void merge(Related related) {
		if ( id.equals(related.id) ) {
			this.minVersion = Math.max(this.minVersion, related.minVersion);
			this.maxVersion = Math.min(this.maxVersion, related.maxVersion);
		} else {
			// Should never happen!
			reporter.log(new LogError("Trying to merge related " + id + " with " + related.id));
		}
	}
	
	public abstract void writeAsMod(PrintStream out, String indent);

	protected void writeAsMod(PrintStream out, String indent, String name) {
		out.print(indent);
		
		out.print("<");
		out.print(name);
		out.print(" id=\"");
		out.print(id);
		out.print("\" minversion=\"");
		out.print(minVersion);
		out.print("\" maxversion=\"");
		out.print(maxVersion);
		out.println("\" />");
	}

	public abstract void writeAsProject(PrintStream out, String indent);

	protected void writeAsProject(PrintStream out, String indent, String type) {
		out.println(indent + "<Association>");
		out.println(indent + "  <Type>" + type + "</Type>");
		out.println(indent + "  <Name>" + getName() + "</Name>");
		out.println(indent + "  <Id>" + id + "</Id>");
		out.println(indent + "  <MinVersion>" + minVersion + "</MinVersion>");
		out.println(indent + "  <MaxVersion>" + maxVersion + "</MaxVersion>");
		out.println(indent + "</Association>");
	}
}
