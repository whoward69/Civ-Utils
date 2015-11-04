package me.civ5.modutils.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.civ5.modutils.log.LogMessage;
import me.civ5.modutils.log.ModReporter;

public class ModUtilsOptions {
	private static final String MODTOOLS_SUBDIR = "Documents/ModTools";
	private static final String MODBUDDY_SUBDIR = "Documents/Firaxis ModBuddy";
	private static final String CIV5_SUBDIR     = "Documents/My Games/Sid Meier's Civilization 5";
	private static final String CIV5MODS_SUBDIR = CIV5_SUBDIR + "/MODS";
	private static final String CIV5SQL_SUBDIR  = CIV5_SUBDIR + "/cache";
	
	// Spares agjklnoxyz
	private static String helpMsg = "" + 
	 " -h            help \n" + 
	 " -m mod (mod)  mod(s) to process \n" +
	 " -ma           all mods \n" +
	 " -v            verify \n" +
	 " -s name       save as mod \n" +
	 " -p name       save as project \n" +
	 " -b dir        ModBuddy dir (default is '" + MODBUDDY_SUBDIR + "' \n" +
	 " -c dir        Civ5 MODS dir (default is '" + CIV5MODS_SUBDIR + "' \n" +
	 " -q dir        Civ5 SQL dir (default is '" + CIV5SQL_SUBDIR + "' \n" +
	 " -u dir        UserHome dir (-b, -c and -t will be derived from this if omitted) \n" +
	 " -t dir        ModTools dir (default is '" + MODTOOLS_SUBDIR + "' \n" +
	 " -f            Fix minor errors \n" +
	 " -r            Remove redundant files \n" +
	 " -e -e4        Show only errors \n" +
	 " -w -e3        Show warnings \n" +
	 " -i -e2        Show informational messages \n" +
	 " -d -e1        Show debug messages \n" +
	 "";
	
	private ModReporter reporter;
	
	private boolean help = false;
	private boolean console = false;
	private boolean fixMinorErrors = false;
	private boolean removeRedundantFiles = false;
	private List<String> modList = new ArrayList<String>();
	private boolean verify = false;
	private String saveAsMod = null;
	private String saveAsProject = null;
	private File modDir = null;
	private File projectDir = null;
	private File sqlDir = null;
	private File toolsDir = null;
	private File userDir = null;
	
	public ModUtilsOptions(ModReporter reporter, String[] args) {
		this.reporter = reporter;
		
		if (!parse(args)) {
			help = true;
		}
	}
	
	public boolean parse(String[] args) {
		for ( int i = 0; i < args.length; ) {
			String arg = args[i++];
			
			if (arg.startsWith("--")) {
				arg = arg.substring(1);
			}
			
			if (arg.startsWith("-h")) {
				help = true;
				console = true;
			} else if (arg.startsWith("-d") || arg.equals("-e1")) {
				reporter.setLevel(LogMessage.DEBUG);
			} else if (arg.startsWith("-i") || arg.equals("-e2")) {
				reporter.setLevel(LogMessage.INFO);
			} else if (arg.startsWith("-w") || arg.equals("-e3")) {
				reporter.setLevel(LogMessage.WARN);
			} else if (arg.startsWith("-e")) {
				reporter.setLevel(LogMessage.ERROR);
			} else if (arg.startsWith("-f")) {
				fixMinorErrors = true;
			} else if (arg.startsWith("-r")) {
				removeRedundantFiles = true;
			} else if (arg.startsWith("-v")) {
				verify = true;
			} else if ("-ma".equals(arg)) {
				modList.add("*");
				console = true;
			} else {
				if (i < args.length) {
					if (arg.startsWith("-m")) {
						while (i < args.length && !args[i].startsWith("-")) {
							modList.add(args[i++]);
						}
						console = true;
					} else if (arg.startsWith("-s")) {
						saveAsMod = args[i++];
						console = true;
					} else if (arg.startsWith("-p")) {
						saveAsProject = args[i++];
						console = true;
					} else if (arg.startsWith("-c")) {
						modDir = new File(args[i++]);
					} else if (arg.startsWith("-b")) {
						projectDir = new File(args[i++]);
					} else if (arg.startsWith("-q")) {
						sqlDir = new File(args[i++]);
					} else if (arg.startsWith("-t")) {
						toolsDir = new File(args[i++]);
					} else if (arg.startsWith("-u")) {
						userDir = new File(args[i++]);
					}
				} else {
					return false;
				}
			}
		}
		
		return true;
	}

	public boolean isHelp() {
		return help;
	}
	
	public boolean isConsole() {
		return console;
	}
	
	public boolean isFixMinorErrors() {
		return fixMinorErrors;
	}

	public void setFixMinorErrors(boolean fixMinorErrors) {
		this.fixMinorErrors = fixMinorErrors;
	}

	public boolean isRemoveRedundantFiles() {
		return removeRedundantFiles;
	}
	
	public void setRemoveRedundantFiles(boolean removeRedundantFiles) {
		this.removeRedundantFiles = removeRedundantFiles;
	}

	public boolean isMergeMods() {
		return (modList.size() > 1 || modList.get(0).equals("*"));
	}

	public List<String> getModList() {
		return modList;
	}
	
	public List<File> getModDirs() {
		List<File> modFiles = new ArrayList<File>();
		getModDir();
		
		if (modDir != null) {
			for ( String modName : modList ) {
				if (modName.equals("*")) {
					for ( File mod : modDir.listFiles()) {
						if (mod.isDirectory() && !mod.getName().startsWith(".")) {
							modFiles.add(mod);
						}
					}
				} else {
					modFiles.add(new File(modDir, modName));
				}
			}
		}
		
		return modFiles;
	}
	
	public boolean isVerify() {
		return verify;
	}
	
	public boolean isSaveAsMod() {
		return (saveAsMod != null);
	}
	
	public String getSaveAsMod() {
		return saveAsMod;
	}
	
	public boolean isSaveAsProject() {
		return (saveAsProject != null);
	}
	
	public String getSaveAsProject() {
		return saveAsProject;
	}
	
	public File getModDir() {
		if (modDir == null) {
			getUserDir();
			
			if (userDir != null) {
				modDir = new File(userDir, CIV5MODS_SUBDIR);
			}
		}
		
		return modDir;
	}
	
	public void setModDir(String modDir) {
		this.modDir = new File(modDir);
	}

	public File getProjectDir() {
		if (projectDir == null) {
			getUserDir();
			
			if (userDir != null) {
				projectDir = new File(userDir, MODBUDDY_SUBDIR);
			}
		}
		
		return projectDir;
	}
	
	public void setProjectDir(String projectDir) {
		this.projectDir = new File(projectDir);
	}

	public File getSqlDir() {
		if (sqlDir == null) {
			getUserDir();
			
			if (userDir != null) {
				sqlDir = new File(userDir, CIV5SQL_SUBDIR);
			}
		}
		
		return sqlDir;
	}
	
	public void setSqlDir(String sqlDir) {
		this.sqlDir = new File(sqlDir);
	}

	public File getToolsDir() {
		if (toolsDir == null) {
			getUserDir();
			
			if (userDir != null) {
				toolsDir = new File(userDir, MODTOOLS_SUBDIR);
			}
		}
		
		if (toolsDir != null && !toolsDir.exists()) {
			toolsDir.mkdirs();
		}
		
		return toolsDir;
	}
	
	public void setToolsDir(String toolsDir) {
		this.toolsDir = new File(toolsDir);
	}

	public File getUserDir() {
		if (userDir == null) {
			userDir = new File(System.getProperty("user.home"));
		}
		
		return userDir;
	}

	public void setUserDir(String userDir) {
		this.userDir = new File(userDir);
	}

	public String getHelpText() {
		return helpMsg;
	}
}
