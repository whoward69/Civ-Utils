package me.civ5.modtools.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import me.civ5.modtools.ModTools;
import me.civ5.modtools.mod.Mod;
import me.civ5.modtools.ui.model.ModListModel;
import me.civ5.modtools.ui.model.OrderedModListModel;
import me.civ5.modtools.ui.model.SortedModListModel;
import me.civ5.modtools.ui.model.handler.ModItemTransferHandler;
import me.civ5.modtools.ui.view.ModListView;
import me.civ5.modtools.ui.view.bar.ButtonBar;
import me.civ5.modtools.ui.view.bar.MenuBar;
import me.civ5.modtools.ui.view.dialog.AboutDialog;
import me.civ5.modtools.ui.view.dialog.OptionsDialog;
import me.civ5.modtools.ui.view.dialog.SaveDialog;
import me.civ5.modutils.log.LogError;
import me.civ5.modutils.log.LogInfo;
import me.civ5.modutils.log.LogSeparator;
import me.civ5.modutils.log.LogWarn;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.action.ModAction;
import me.civ5.modutils.ui.view.dialog.LogDialog;
import me.civ5.modutils.ui.worker.ModWorker;
import me.civ5.modutils.utils.ModUtilsOptions;

public class ModToolsFrame extends ModFrame implements ListDataListener {
	private ModListModel selectedModel;

	public ModToolsFrame(ModReporter sysReporter, ModUtilsOptions options) {
		super(ModTools.NAME, sysReporter, options);
		
		TransferHandler h = new ModItemTransferHandler();

		ModListModel availableModel = new SortedModListModel();
		availableModel.load(sysReporter, options.getModDir());
		ModListView availableList = new ModListView("Available Mods", availableModel, h);

		selectedModel = new OrderedModListModel();
		selectedModel.addListDataListener(this);
		ModListView selectedList = new ModListView("Selected Mods", selectedModel, h);

		JPanel p = new JPanel(new GridLayout(1, 2, 10, 0));
		p.setBorder(BorderFactory.createTitledBorder(null, "Drag to select/deselect mods and to change their order", TitledBorder.LEFT, TitledBorder.BELOW_TOP));
		p.add(availableList.getComponent());
		p.add(selectedList.getComponent());

		JPanel content = new JPanel(new BorderLayout());
		content.add(p, BorderLayout.CENTER);
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.setPreferredSize(new Dimension(800, 500));
		
		content.add(new ButtonBar(this), BorderLayout.LINE_END);

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.getContentPane().add(content);
		this.setJMenuBar(new MenuBar(this));

		enableListActions(true);
		
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void exit() {
		System.exit(0);
	}

	
	public class VerifyAction extends ModAction {
		public VerifyAction(ModFrame owner) {
			super(owner, "Verify ...", "Verify all selected mods");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LogDialog logDialog = new LogDialog(owner, "Verify Mods", sysReporter.getLevel(), null);
			
			class VerifyWorker extends ModWorker {
				@Override
				protected Void doInBackground() throws Exception {
					for ( int i = 0; i < selectedModel.getSize(); i++ ) {
						Mod mod = (Mod) selectedModel.getElementAt(i);
						mod.setReporter(this);
						publish(new LogInfo("Verifing '" + mod + "'"));

						if ( mod.verify(options.isFixMinorErrors(), options.isRemoveRedundantFiles()) ) {
							publish(new LogInfo("--- Done"));
						} else {
							publish(new LogWarn("--- Issues!"));
						}

						publish(new LogSeparator());
					}

					return null;
				}
			}
			
			(new VerifyWorker()).doWork(logDialog);
		}
	}
	
	public class SaveModAction extends ModAction {
		public SaveModAction(ModFrame owner) {
			super(owner, "Save as Mod ...", "Merge all selected mods and save as a new mod");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final boolean single = (selectedModel.getSize() == 1);
			
			SaveDialog saveDialog = new SaveDialog(owner, "New Mod name: ", null, getExitAction());
			final String modName = saveDialog.getModName();
			final String modVersion = "1";
			
			if (modName != null) {
				LogDialog logDialog = new LogDialog(owner, (single ? "Save Mod" : "Merge Mods and Save"), sysReporter.getLevel(), null);
	
				class SaveModWorker extends ModWorker {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							Mod newMod;
							
							if (single) {
								newMod = (Mod) selectedModel.getElementAt(0);
								newMod.setNameAndVersion(modName, modVersion);
								newMod.setReporter(logDialog);
							} else {
								newMod = new Mod(logDialog, options.getModDir().getAbsolutePath(), modName, modVersion, "Comprising the following mods:");
								newMod.setReporter(logDialog);
			
								for ( int i = 0; i < selectedModel.getSize(); i++ ) {
									Mod mod = (Mod) selectedModel.getElementAt(i);
									mod.setReporter(logDialog);
									publish(new LogInfo("Processing '" + mod + "'"));
				
									if ( mod.verify(options.isFixMinorErrors(), options.isRemoveRedundantFiles()) ) {
										newMod.merge(mod);
									}
									
									publish(new LogInfo("---"));
								}
							}
			
							publish(new LogInfo("Saving mod '" + newMod.getNameWithVersion() + "'"));
							if ( newMod.verify(false, false) ) {
								newMod.saveAsMod();
								publish(new LogInfo("--- Done"));
							} else {
								publish(new LogWarn("--- Issues!"));
							}
						} catch (Exception ex) {
							publish(new LogError(ex.getMessage()));
						}
						return null;
					}
				}
				
				(new SaveModWorker()).doWork(logDialog);
			}
		}
	}
	
	public class SaveProjectAction extends ModAction {
		public SaveProjectAction(ModFrame owner) {
			super(owner, "Save as Project ...", "Merge all selected mods and save as a new ModBuddy project");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			final boolean single = (selectedModel.getSize() == 1);
			
			SaveDialog saveDialog = new SaveDialog(owner, "New Project name: ", (single ? ((Mod) selectedModel.getElementAt(0)).getName() : null), getExitAction());
			final String modName = saveDialog.getModName();
			final String modVersion = "1";
			
			if (modName != null) {
				LogDialog logDialog = new LogDialog(owner, (single ? "Save Mod as a Project" : "Merge Mods and Save as a Project"), sysReporter.getLevel(), null);
	
				class SaveProjectWorker extends ModWorker {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							Mod newMod;
							
							if (single) {
								newMod = (Mod) selectedModel.getElementAt(0);
								newMod.setNameAndVersion(modName, newMod.getVersion());
								newMod.setReporter(logDialog);
							} else {
								newMod = new Mod(logDialog, options.getModDir().getAbsolutePath(), modName, modVersion, "Comprising the following mods:");
								newMod.setReporter(logDialog);
			
								for ( int i = 0; i < selectedModel.getSize(); i++ ) {
									Mod mod = (Mod) selectedModel.getElementAt(i);
									mod.setReporter(logDialog);
									publish(new LogInfo("Processing '" + mod + "'"));
				
									if ( mod.verify(options.isFixMinorErrors(), options.isRemoveRedundantFiles()) ) {
										newMod.merge(mod);
									}
									
									publish(new LogInfo("---"));
								}
							}
			
							publish(new LogInfo("Saving project '" + newMod.getName() + "'"));
							if ( newMod.verify(false, false) ) {
								newMod.saveAsProject(options.getProjectDir().getAbsolutePath(), modName);
								publish(new LogInfo("--- Done"));
							} else {
								publish(new LogWarn("--- Issues!"));
							}
						} catch (Exception ex) {
							publish(new LogError(ex.getMessage()));
						}
						return null;
					}
				}
				
				(new SaveProjectWorker()).doWork(logDialog);
			}
		}
	}
	
	public class SaveSessionAction extends ModAction {
		public SaveSessionAction(ModFrame owner) {
			super(owner, "Save Session ...");
			// TODO - session save/load
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	public class LoadSessionAction extends ModAction {
		public LoadSessionAction(ModFrame owner) {
			super(owner, "Load Session ...");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
	
	public class PreferencesAction extends ModAction {
		public PreferencesAction(ModFrame owner) {
			super(owner, "Preferences ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new OptionsDialog(owner, getExitAction(), options);
		}
	}
	
	public class AboutAction extends ModAction {
		public AboutAction(ModFrame owner) {
			super(owner, "About ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new AboutDialog(owner, getExitAction());
		}
	}
	
	public class ExitAction extends ModAction {
		public ExitAction(ModFrame owner) {
			super(owner, "Exit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			exit();
		}
	}
	
	private Action actionVerify = new VerifyAction(this);
	private Action actionSaveMod = new SaveModAction(this);
	private Action actionSaveProject = new SaveProjectAction(this);
	private Action actionSaveSession = new SaveSessionAction(this);
	private Action actionLoadSession = new LoadSessionAction(this);
	private Action actionPreferences = new PreferencesAction(this);
	private Action actionAbout = new AboutAction(this);
	private Action actionExit = new ExitAction(this);

	public Action getVerifyAction() {
		return actionVerify;
	}

	public Action getSaveModAction() {
		return actionSaveMod;
	}

	public Action getSaveProjectAction() {
		return actionSaveProject;
	}

	public Action getSaveSessionAction() {
		return actionSaveSession;
	}

	public Action getLoadSessionAction() {
		return actionLoadSession;
	}

	@Override
	public Action getPreferencesAction() {
		return actionPreferences;
	}

	@Override
	public Action getAboutAction() {
		return actionAbout;
	}

	@Override
	public Action getExitAction() {
		return actionExit;
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
	}
	
	@Override
	public void intervalAdded(ListDataEvent e) {
		if (e.getSource() == selectedModel) {
			enableListActions(true);
		}
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		if (e.getSource() == selectedModel) {
			enableListActions(true);
		}
	}
	
	@Override
	public void enableListActions(boolean enabled) {
		enabled = enabled && (!selectedModel.isEmpty());
		
		getVerifyAction().setEnabled(enabled);
		getSaveModAction().setEnabled(enabled);
		getSaveProjectAction().setEnabled(enabled);
	}
}
