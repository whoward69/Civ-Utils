package me.civ5.modbuilder.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import me.civ5.modbuilder.db.ModDb;
import me.civ5.modbuilder.ui.panel.AbilitiesPanel;
import me.civ5.modbuilder.ui.panel.ArtPanel;
import me.civ5.modbuilder.ui.panel.CombatPanel;
import me.civ5.modbuilder.ui.panel.DisplayPanel;
import me.civ5.modbuilder.ui.panel.ReligionPanel;
import me.civ5.modbuilder.ui.panel.RequirementsPanel;
import me.civ5.modbuilder.ui.panel.TextPanel;
import me.civ5.modbuilder.ui.panel.UnitPanel;
import me.civ5.modbuilder.ui.panel.UpgradesPanel;
import me.civ5.modbuilder.ui.panel.XmlPanel;
import me.civ5.modbuilder.ui.view.bar.MenuBar;
import me.civ5.modbuilder.ui.view.dialog.AboutDialog;
import me.civ5.modbuilder.ui.view.dialog.OptionsDialog;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogInfo;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.action.ModAction;
import me.civ5.modutils.ui.view.dialog.LogDialog;
import me.civ5.modutils.ui.worker.ModWorker;
import me.civ5.modutils.utils.ModUtilsOptions;
import me.civ5.ui.filter.XmlFilter;
import me.civ5.xml.XmlBuilder;
import me.civ5.xml.XmlHelper;
import me.civ5.xml.XmlOutputHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Document;
import org.jdom.Element;

public class CombatUnitBuilderFrame extends ModFrame implements ChangeListener, ValidityListener {
	private ModBuilderPane content;
	private UnitPanel unitPanel;

	private Element language;

	protected File lastSaveDir = null;
	protected File lastSessionDir = null;

	public CombatUnitBuilderFrame(ModReporter sysReporter, ModUtilsOptions options, Element language) throws SQLException {
		super("CombatUnitBuilder by whoward69", sysReporter, options);

		lastSessionDir = options.getToolsDir();
		lastSaveDir = new File(options.getToolsDir(), "Units");
		if (!lastSaveDir.exists()) {
			lastSaveDir.mkdirs();
		}

		ModDb modDb = new ModDb(sysReporter, options);
		setLanguage(language);

		content = new ModBuilderPane("combatunit");
		content.setLanguage(language);

		unitPanel = (UnitPanel) content.add(new UnitPanel(modDb));
		content.add(new CombatPanel(modDb));
		content.add(new TextPanel(modDb));
		content.add(new UpgradesPanel(modDb));
		content.add(new RequirementsPanel(modDb));
		content.add(new AbilitiesPanel(modDb));
		content.add(new ReligionPanel(modDb));
		content.add(new ArtPanel(modDb));

		// content.add(new SqlPanel(modDb));
		content.add(new XmlPanel(modDb));

		content.addChangeListener(this);
		content.addValidityListener(this);
		content.verify(false);

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.getContentPane().add(content);
		this.setJMenuBar(new MenuBar(this));

		this.pack();
		this.setSize(800, 700);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void exit() {
		System.exit(0);
	}

	protected void setLanguage(Element language) {
		this.language = language;

		getExitAction().setName(XpathHelper.getString(language, "./ui/actions/exit"));
		getAboutAction().setName(XpathHelper.getString(language, "./ui/actions/about"));

		getSaveAction().setName(XpathHelper.getString(language, "./ui/actions/save"));

		getPreferencesAction().setName(XpathHelper.getString(language, "./ui/actions/session/prefs"));
		getLoadSessionAction().setName(XpathHelper.getString(language, "./ui/actions/session/load"));
		getSaveSessionAction().setName(XpathHelper.getString(language, "./ui/actions/session/save"));
	}

	public void serialise(Element container) {
		Element me = XmlHelper.newIdElement(container, "builder", "unitcombat");

		if (lastSaveDir != null) {
			XmlHelper.newTextElement(me, "lastSaveDir", lastSaveDir.getAbsolutePath());
		}

		content.serialise(me);
	}

	public void deserialise(Element container) {
		Element me = XpathHelper.getElement(container, "./builder[@id='unitcombat']");

		String saveDir = XpathHelper.getString(me, "./lastSaveDir");
		if (saveDir != null) {
			lastSaveDir = new File(saveDir);
		}

		content.deserialise(me);
		content.verify(true);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == content) {
			if (content.getSelectedComponent() instanceof DisplayPanel) {
				((DisplayPanel) content.getSelectedComponent()).display(content.buildXml(new Document(new Element("GameData"))));
			}
		}
	}

	@Override
	public void validityUpdate(boolean valid) {
		getSaveAction().setEnabled(valid);
		getSaveSessionAction().setEnabled(valid);
	}

	public class SaveAction extends ModAction {
		public SaveAction(CombatUnitBuilderFrame owner) {
			super(owner, "Save As ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String unitName = unitPanel.getUnitName();
			String fileName = "Unit" + unitName.replaceAll("[^a-zA-Z0-9_]", "") + ".xml";

			JFileChooser chooser = new JFileChooser(lastSaveDir);
			FileFilter xmlFilter = new XmlFilter();

			chooser.setSelectedFile(new File(lastSessionDir, fileName));

			// chooser.addChoosableFileFilter(new SqlFilter());
			chooser.addChoosableFileFilter(xmlFilter);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(xmlFilter);

			if (chooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
				File outFile = chooser.getSelectedFile();
				lastSaveDir = outFile.getParentFile();

				if (outFile.exists()) {
					String title = XpathHelper.getString(language, "./ui/confirm/title");
					String prompt = XpathHelper.getString(language, "./ui/confirm/prompt") + outFile.getName() + "?";

					if (JOptionPane.YES_OPTION != JOptionPane.showOptionDialog(owner, prompt, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) {
						outFile = null;
					}
				}

				if (xmlFilter.accept(outFile)) {
					final File saveFile = outFile;
					LogDialog logDialog = new LogDialog(owner, "Save as XML", sysReporter.getLevel(), null);

					class SaveWorker extends ModWorker {
						@Override
						protected Void doInBackground() throws Exception {
							try {
								OutputStream os = null;

								try {
									os = new FileOutputStream(saveFile);

									String text;
									if (content.getSelectedComponent() instanceof DisplayPanel) {
										text = ((DisplayPanel) content.getSelectedComponent()).getText();
									} else {
										publish(new LogInfo("Generating XML"));
										text = content.buildXml(new Document(new Element("GameData")));
									}

									publish(new LogInfo("Writing XML"));
									os.write(text.getBytes("UTF-8"));

									publish(new LogInfo("Saving DDS files"));
									content.saveFiles(saveFile.getParentFile(), this);
								} catch (Exception ex) {
									publish(new LogError(ex.getMessage()));
								} finally {
									if (os != null) {
										try {
											os.close();
											publish(new LogInfo("Finished"));
										} catch (IOException ex) {
										}
									}
								}
							} catch (Exception ex) {
								publish(new LogError(ex.getMessage()));
							}

							return null;
						}
					}

					(new SaveWorker()).doWork(logDialog);
				}
			}
		}
	}

	public class SaveSessionAction extends ModAction {
		public SaveSessionAction(CombatUnitBuilderFrame owner) {
			super(owner, "Save Session ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String unitName = unitPanel.getUnitName();
			String fileName = "UnitBuilder_" + unitName.replaceAll("[^a-zA-Z0-9_]", "") + ".xml";

			JFileChooser chooser = new JFileChooser(lastSessionDir);
			FileFilter xmlFilter = new XmlFilter();
			
			chooser.setSelectedFile(new File(lastSessionDir, fileName));

			chooser.addChoosableFileFilter(xmlFilter);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(xmlFilter);

			if (chooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
				File outFile = chooser.getSelectedFile();
				lastSessionDir = outFile.getParentFile();

				if (outFile.exists()) {
					String title = XpathHelper.getString(language, "./ui/confirm/title");
					String prompt = XpathHelper.getString(language, "./ui/confirm/prompt") + outFile.getName() + "?";

					if (JOptionPane.YES_OPTION != JOptionPane.showOptionDialog(owner, prompt, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) {
						outFile = null;
					}
				}

				if (xmlFilter.accept(outFile)) {
					final File saveFile = outFile;

					class SaveSessionWorker extends ModWorker {
						@Override
						protected Void doInBackground() throws Exception {
							try {
								OutputStream os = null;

								try {
									os = new FileOutputStream(saveFile);

									Element session = XmlHelper.newElement(new Document(), "session");
									serialise(session);

									os.write(XmlOutputHelper.prettyOutput(session.getDocument(), XmlBuilder.defaultEncoding).getBytes(XmlBuilder.defaultEncoding));
								} catch (Exception ex) {
									System.err.println(ex.getMessage());
									ex.printStackTrace(System.err);
								} finally {
									if (os != null) {
										try {
											os.close();
										} catch (IOException ex) {
										}
									}
								}
							} catch (Exception ex) {
								System.err.println(ex.getMessage());
								ex.printStackTrace(System.err);
							}

							return null;
						}
					}

					(new SaveSessionWorker()).doWork(null);
				}
			}
		}
	}

	public class LoadSessionAction extends ModAction {
		protected File lastDir = null;

		public LoadSessionAction(CombatUnitBuilderFrame owner) {
			super(owner, "Load Session ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(lastSessionDir);
			FileFilter xmlFilter = new XmlFilter();

			chooser.addChoosableFileFilter(xmlFilter);
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(xmlFilter);

			if (chooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
				File inFile = chooser.getSelectedFile();
				lastSessionDir = inFile.getParentFile();

				if (xmlFilter.accept(inFile)) {
					final File loadFile = inFile;

					class LoadSessionWorker extends ModWorker {
						@Override
						protected Void doInBackground() throws Exception {
							try {
								Document session = XmlBuilder.parse(new FileInputStream(loadFile));
								deserialise(session.getRootElement());
							} catch (Exception ex) {
								System.err.println(ex.getMessage());
							}

							return null;
						}
					}

					(new LoadSessionWorker()).doWork(null);
				}
			}
		}
	}

	public class PreferencesAction extends ModAction {
		public PreferencesAction(CombatUnitBuilderFrame owner) {
			super(owner, "Preferences ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new OptionsDialog(owner, getExitAction(), language, options);
		}
	}

	public class AboutAction extends ModAction {
		public AboutAction(CombatUnitBuilderFrame owner) {
			super(owner, "About ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new AboutDialog(owner, getExitAction(), language);
		}
	}

	public class ExitAction extends ModAction {
		public ExitAction(CombatUnitBuilderFrame owner) {
			super(owner, "Exit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			exit();
		}
	}

	private ModAction actionSave = new SaveAction(this);
	private ModAction actionSaveSession = new SaveSessionAction(this);
	private ModAction actionLoadSession = new LoadSessionAction(this);
	private ModAction actionPreferences = new PreferencesAction(this);
	private ModAction actionAbout = new AboutAction(this);
	private ModAction actionExit = new ExitAction(this);

	public ModAction getSaveAction() {
		return actionSave;
	}

	public ModAction getSaveSessionAction() {
		return actionSaveSession;
	}

	public ModAction getLoadSessionAction() {
		return actionLoadSession;
	}

	@Override
	public ModAction getPreferencesAction() {
		return actionPreferences;
	}

	@Override
	public ModAction getAboutAction() {
		return actionAbout;
	}

	@Override
	public ModAction getExitAction() {
		return actionExit;
	}

	@Override
	public void enableListActions(boolean enabled) {
	}
}
