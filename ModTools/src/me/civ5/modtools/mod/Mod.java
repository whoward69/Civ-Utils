package me.civ5.modtools.mod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.UUID;

import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.ModReporter;

import org.xml.sax.SAXException;

public class Mod implements Comparable<Mod> {
	private ModReporter reporter;
	
	private String modsDir;
	private String modDir;
	
	private ModInfo modInfo;
	private ModCivFiles modFiles;
	
	private boolean isMerged = false;
	
	public Mod(ModReporter reporter, String modsDir, String modName, String modVersion, String modDescription) throws SAXException, IOException {
		this.reporter = reporter;
		this.modsDir = modsDir;
		this.modDir = makeVersionedName(modName, modVersion);
		
		File modPath = new File(modsDir, modDir);
		modInfo = new ModInfo(reporter, modPath, modName, modVersion, modDescription);
		modFiles = new ModCivFiles(reporter, modPath);
	}
	
	public Mod(ModReporter reporter, String modsDir, String modDir) throws SAXException, IOException {
		this.reporter = reporter;
		this.modsDir = modsDir;
		this.modDir = modDir;

		File modPath = new File(modsDir, modDir);
		modInfo = new ModInfo(reporter, modPath);
		modFiles = new ModCivFiles(reporter, modPath, modInfo);
	}
	
	public Mod(ModReporter reporter, File modDir) throws SAXException, IOException {
		this(reporter, modDir.getParent(), modDir.getName());
	}
	
	public void setReporter(ModReporter reporter) {
		this.reporter = reporter;
		modInfo.setReporter(reporter);
		modFiles.setReporter(reporter);
	}
	
	public void generateNewGUID() {
		modInfo.generateNewGUID();
	}
	
	public void setNameAndVersion(String modName, String modVersion) {
		modInfo.setNameAndVersion(modName, modVersion);
	}
	
	public String makeVersionedName(String modName, String modVersion) {
		return modName + " (v " + modVersion + ")";
	}
	
	public String getNameWithVersion() {
		return modDir;
	}
	
	public String getName() {
		return modInfo.getName();
	}
	
	public String getVersion() {
		return modInfo.getVersion();
	}
	
	public String getDescription() {
		return modInfo.getDescription();
	}
	
	public boolean verify(boolean fixImports, boolean removeFiles) {
		// Never, never, ever removeFiles in a merged mod!
		removeFiles = (removeFiles && !isMerged);
		
		// Verify the .modinfo file, then the actual files
		if (modInfo.verify(fixImports, removeFiles)) {
			if ( removeFiles && !isMerged ) {
				// Files may have been removed, in which case update the mod file list
				modFiles = new ModCivFiles(reporter, new File(modsDir, modDir), modInfo);
			}
			
			if ( modFiles.verify() ) {
				return true;
			}
		}
		
		return false;
	}
	
	public void merge(Mod mergeMod) {
		// Once we have merged another mod into this one, we can never remove files from it!
		isMerged = true;
		
		modInfo.merge(mergeMod.modInfo);
		modFiles.merge(mergeMod.modFiles);
	}
	
	private void delete(File dir) {
		for ( File entry : dir.listFiles() ) {
			if ( entry.isDirectory() ) {
				delete(entry);
			} else if ( entry.isFile() ) {
				entry.delete();
			}
		}
		
		dir.delete();
	}
	
	public void saveAsMod(String subDir, String newModName, String newModVersion) {
		saveAsMod(new File(new File(modsDir, subDir), makeVersionedName(newModName, newModVersion)));
	}
	
	public void saveAsMod(String newModName, String newModVersion) {
		saveAsMod(new File(modsDir, makeVersionedName(newModName, newModVersion)));
	}

	public void saveAsMod() {
		if ( modInfo.getName() != null) {
			saveAsMod(modInfo.getName(), modInfo.getVersion());
		} else {
			reporter.log(new LogError("Unable to save as mod name is null!"));
		}
	}
	
	private void saveAsMod(File newModPath) {
		if ( newModPath.exists() ) {
			delete(newModPath);
		}
		
		modInfo.saveAsMod(newModPath);
		modFiles.copyTo(newModPath);
	}
	
	public void saveAsProject(String subDir, String projectsDir, String projectName) {
		saveAsProject(new File(new File(projectsDir, subDir), projectName), projectName);
	}
	
	public void saveAsProject(String projectsDir, String projectName) {
		saveAsProject(new File(projectsDir, projectName), projectName);
	}
	
	private void saveAsProject(File projectDir, String projectName) {
		String projectGUID = UUID.randomUUID().toString();
		
		if ( projectDir.exists() ) {
			delete(projectDir);
		}

		saveVsSolutionFile(projectDir, projectName, projectGUID);
		saveVsProjectFile(projectDir, projectName, projectGUID);
		
		modFiles.copyTo(new File(projectDir, projectName));
	}
	
	private void saveVsSolutionFile(File solutionPath, String projectName, String projectGUID) {
		File solutionFile = new File(solutionPath, projectName + ".civ5sln");
		String solutionGUID = UUID.randomUUID().toString();

		try {
			solutionPath.mkdirs();
			
			if ( solutionFile.createNewFile() ) {
				PrintStream out = null;
				
				try {
					out = new PrintStream(solutionFile);
					writeVsSolutionFile(out, solutionGUID, projectName, projectGUID);
				} catch (FileNotFoundException e) {
					reporter.log(new LogError("ModProject Error: " + solutionFile.getAbsolutePath() + " unable to open"));
				} finally {
					if ( out != null ) {
						out.close();
					}
				}
				
			} else {
				reporter.log(new LogError("ModProject Error: " + solutionFile.getAbsolutePath() + " unable to create"));
			}
		} catch (IOException e) {
			reporter.log(new LogError("ModProject Error: " + solutionFile.getAbsolutePath() + " unable to create"));
		}
	}
	
	private void writeVsSolutionFile(PrintStream out, String solutionGUID, String projectName, String projectGUID) {
		out.println();
		out.println("Microsoft Visual Studio Solution File, Format Version 11.00");
		out.println("# ModBuddy Solution File, Format Version 11.00");
		out.println("Project(\"{" + solutionGUID + "}\") = \"" + projectName + "\", \"" + projectName + "\\" + projectName + ".civ5proj\", \"{" + projectGUID + "}\"");
		out.println("EndProject");
		out.println("Global");
		out.println("\tGlobalSection(SolutionConfigurationPlatforms) = preSolution");
		out.println("\t\tDefault|x86 = Default|x86");
		out.println("\t\tDeploy Only|x86 = Deploy Only|x86");
		out.println("\t\tPackage Only|x86 = Package Only|x86");
		out.println("\tEndGlobalSection");
		out.println("\tGlobalSection(ProjectConfigurationPlatforms) = postSolution");
		out.println("\t\t{" + projectGUID + "}.Default|x86.ActiveCfg = Default|x86");
		out.println("\t\t{" + projectGUID + "}.Default|x86.Build.0 = Default|x86");
		out.println("\t\t{" + projectGUID + "}.Deploy Only|x86.ActiveCfg = Deploy Only|x86");
		out.println("\t\t{" + projectGUID + "}.Deploy Only|x86.Build.0 = Deploy Only|x86");
		out.println("\t\t{" + projectGUID + "}.Package Only|x86.ActiveCfg = Package Only|x86");
		out.println("\t\t{" + projectGUID + "}.Package Only|x86.Build.0 = Package Only|x86");
		out.println("\tEndGlobalSection");
		out.println("\tGlobalSection(SolutionProperties) = preSolution");
		out.println("\t\tHideSolutionNode = FALSE");
		out.println("\tEndGlobalSection");
		out.println("EndGlobal");
	}
	
	private void saveVsProjectFile(File projectPath, String projectName, String projectGUID) {
		File projectDir = new File(projectPath, projectName);
		File projectFile = new File(projectDir, projectName + ".civ5proj");

		try {
			projectDir.mkdirs();
			
			if ( projectFile.createNewFile() ) {
				PrintStream out = null;
				
				try {
					out = new PrintStream(projectFile);
					writeVsProjectFile(out, projectGUID);
				} catch (FileNotFoundException e) {
					reporter.log(new LogError("ModProject Error: " + projectFile.getAbsolutePath() + " unable to open"));
				} finally {
					if ( out != null ) {
						out.close();
					}
				}
				
			} else {
				reporter.log(new LogError("ModProject Error: " + projectFile.getAbsolutePath() + " unable to create"));
			}
		} catch (IOException e) {
			reporter.log(new LogError("ModProject Error: " + projectFile.getAbsolutePath() + " unable to create"));
		}
	}
	
	private void writeVsProjectFile(PrintStream out, String projectGUID) {
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<Project DefaultTargets=\"Deploy\" ToolsVersion=\"4.0\" xmlns=\"http://schemas.microsoft.com/developer/msbuild/2003\">");
		out.println("  <PropertyGroup>");
		out.println("    <Configuration Condition=\" '$(Configuration)' == '' \">Default</Configuration>");
		out.println("    <ProjectGuid>{" + projectGUID + "}</ProjectGuid>");
		
		modInfo.writeVsProjectProperties(out, "    ");

		modInfo.writeVsProjectAssociations(out, "    ");
		modInfo.writeVsProjectActions(out, "    ");
		modInfo.writeVsProjectContent(out, "    ");

		out.println("  </PropertyGroup>");
		
		out.println("  <PropertyGroup Condition=\" '$(Configuration)' == 'Default' \">");
		out.println("    <OutputPath>.</OutputPath>");
		out.println("  </PropertyGroup>");
		out.println("  <PropertyGroup Condition=\" '$(Configuration)' == 'Package Only' \">");
		out.println("    <PackageMod>true</PackageMod>");
		out.println("    <DeployMod>false</DeployMod>");
		out.println("  </PropertyGroup>");
		out.println("  <PropertyGroup Condition=\" '$(Configuration)' == 'Deploy Only' \">");
		out.println("    <PackageMod>false</PackageMod>");
		out.println("    <DeployMod>true</DeployMod>");
		out.println("  </PropertyGroup>");

		modInfo.writeVsProjectDirsAndFiles(out, "  ");
		
		out.println("  <Import Project=\"$(MSBuildExtensionsPath)\\Firaxis\\ModBuddy\\Civ5Mod.targets\" />");
		out.println("</Project>");
	}
	
	@Override
	public int compareTo(Mod that) {
		return this.modDir.compareTo(that.modDir);
	}

	@Override
	public boolean equals(Object that) {
		return (that instanceof Mod && modDir.equals(((Mod) that).modDir));
	}

	@Override
	public int hashCode() {
		return modDir.hashCode();
	}

	@Override
	public String toString() {
		return modDir;
	}
}
