package me.civ5.modddsconverter.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import me.civ5.modddsconverter.ui.panel.AbstractPanel;
import me.civ5.modddsconverter.ui.panel.AtlasPanel;
import me.civ5.modddsconverter.ui.panel.IconsPanel;
import me.civ5.modddsconverter.ui.panel.ImagesPanel;
import me.civ5.modddsconverter.ui.view.bar.MenuBar;
import me.civ5.modddsconverter.ui.view.dialog.AboutDialog;
import me.civ5.modddsconverter.ui.view.dialog.ImageDialogs;
import me.civ5.modddsconverter.ui.view.dialog.OptionsDialog;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.ui.ModFrame;
import me.civ5.modutils.ui.action.ModAction;
import me.civ5.modutils.ui.worker.ModWorker;
import me.civ5.modutils.utils.ModUtilsOptions;
import me.civ5.modutils.utils.ModUtilsSession;
import me.civ5.ui.filter.XmlFilter;
import me.civ5.xml.XmlBuilder;
import me.civ5.xml.XmlHelper;
import me.civ5.xml.XmlOutputHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Document;
import org.jdom.Element;

public class DdsConverterFrame extends ModFrame implements ModUtilsSession {
	String sessionFilename = "DdsConverter.xml";
	String sessionAutoFilename = "DdsConverterAuto.xml";
	
	private DdsConverterPane content;

	private Element language, configs;

	protected File lastSessionDir = null;

	public DdsConverterFrame(ModReporter sysReporter, ModUtilsOptions options, Element configs, Element language) throws SQLException {
		super("DDS Converter by whoward69", sysReporter, options);

		this.configs = configs;
		
		ImageDialogs.setSession(this);
		
		if (!isWebStartSession()) {
			// Auto-load the saved session state
			lastSessionDir = options.getToolsDir();
			autoLoadSession();
		}

		content = new DdsConverterPane(this, "ddsconverter", configs);

		setLanguage(language);

		addImageTab(false);
		addIconTab(false);
		content.setSelectedIndex(0);

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.getContentPane().add(content);
		this.setJMenuBar(new MenuBar(this, isWebStartSession()));

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	protected void addImageTab(boolean canClose) {
		ImagesPanel panel = new ImagesPanel(this, XpathHelper.getElement(configs, "./config[@type='images']"), canClose); 
		addTab(panel, canClose);
	}

	protected void addIconTab(boolean canClose) {
		IconsPanel panel = new IconsPanel(this, XpathHelper.getElement(configs, "./config[@type='icons']"), canClose); 
		addTab(panel, canClose);
	}
	
	protected void addAtlasTab(boolean canClose) {
		AtlasPanel panel = new AtlasPanel(this, XpathHelper.getElement(configs, "./config[@type='atlas']"), canClose); 
		addTab(panel, false);
	}
	
	private void addTab(AbstractPanel panel, boolean autoOpen) {
		content.add(panel);

		if (autoOpen) {
			panel.open();
		}
	}
	
	public void removeTab(AbstractPanel panel) {
		content.remove(panel);
	}

	public void exit() {
		System.exit(0);
	}

	protected void setLanguage(Element language) {
		this.language = language;

		getExitAction().setName(XpathHelper.getString(language, "./ui/actions/exit"));
		getAboutAction().setName(XpathHelper.getString(language, "./ui/actions/about"));

		getAddImageAction().setName(XpathHelper.getString(language, "./ui/actions/image"));
		getAddIconAction().setName(XpathHelper.getString(language, "./ui/actions/icon"));
		getAddAtlasAction().setName(XpathHelper.getString(language, "./ui/actions/atlas"));

		getPreferencesAction().setName(XpathHelper.getString(language, "./ui/actions/session/prefs"));
		getLoadSessionAction().setName(XpathHelper.getString(language, "./ui/actions/session/load"));
		getSaveSessionAction().setName(XpathHelper.getString(language, "./ui/actions/session/save"));
		
		content.setLanguage(language);
	}

	public Element serialise(Element container, boolean auto) {
		Element me = XmlHelper.newIdElement(container, "converter", "dds");

		XmlHelper.newTextElement(me, "lastSaveDir", ImageDialogs.getLastSaveDir());
		XmlHelper.newTextElement(me, "lastOpenDir", ImageDialogs.getLastOpenDir());

		if (!auto) {
			content.serialise(me);
		}
		
		return me;
	}

	public void deserialise(Element container, boolean auto) {
		Element me = XpathHelper.getElement(container, "./converter[@id='dds']");

		ImageDialogs.setLastSaveDir(XpathHelper.getString(me, "./lastSaveDir"));
		ImageDialogs.setLastOpenDir(XpathHelper.getString(me, "./lastOpenDir"));

		if (!auto) {
			content.deserialise(me);
		}
	}
	
	public class SaveSessionAction extends ModAction {
		public SaveSessionAction(DdsConverterFrame owner) {
			super(owner, "Save Session ...");
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			File outFile = null;

			if (e == null) {
				outFile = new File(options.getToolsDir(), sessionAutoFilename);
			} else {
				JFileChooser chooser = new JFileChooser(lastSessionDir);
				FileFilter xmlFilter = new XmlFilter();
				
				chooser.setSelectedFile(new File(lastSessionDir, sessionFilename));
	
				chooser.addChoosableFileFilter(xmlFilter);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(xmlFilter);
	
				if (chooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
					outFile = chooser.getSelectedFile();
					lastSessionDir = outFile.getParentFile();
	
					if (outFile.exists()) {
						String title = XpathHelper.getString(language, "./ui/confirm/title");
						String prompt = XpathHelper.getString(language, "./ui/confirm/prompt") + outFile.getName() + "?";
	
						if (JOptionPane.YES_OPTION != JOptionPane.showOptionDialog(owner, prompt, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null)) {
							outFile = null;
						}
					}
				}

				if (!xmlFilter.accept(outFile)) {
					outFile = null;
				}
			}

			if (outFile != null) {
				final File saveFile = outFile;

				class SaveSessionWorker extends ModWorker {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							OutputStream os = null;

							try {
								os = new FileOutputStream(saveFile);

								Element session = XmlHelper.newElement(new Document(), "session");
								serialise(session, false);

								os.write(XmlOutputHelper.prettyOutput(session.getDocument(), XmlBuilder.defaultEncoding).getBytes(XmlBuilder.defaultEncoding));
							} catch (Exception ex) {
								System.err.println(ex.getMessage());
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
						}

						return null;
					}
				}

				(new SaveSessionWorker()).doWork(null);
			}
		}
	}

	public class LoadSessionAction extends ModAction {
		protected File lastDir = null;

		public LoadSessionAction(DdsConverterFrame owner) {
			super(owner, "Load Session ...");
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			File inFile = null;
			
			if (e == null) {
				inFile = new File(options.getToolsDir(), sessionAutoFilename);
			} else {
				JFileChooser chooser = new JFileChooser(lastSessionDir);
				FileFilter xmlFilter = new XmlFilter();
	
				chooser.addChoosableFileFilter(xmlFilter);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(xmlFilter);

				if (chooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					inFile = chooser.getSelectedFile();
					lastSessionDir = inFile.getParentFile();
				}
			}

			if (inFile != null) {
				final File loadFile = inFile;

				class LoadSessionWorker extends ModWorker {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							Document session = XmlBuilder.parse(new FileInputStream(loadFile));
							deserialise(session.getRootElement(), (e == null));
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

	public class PreferencesAction extends ModAction {
		public PreferencesAction(DdsConverterFrame owner) {
			super(owner, "Preferences ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new OptionsDialog(owner, getExitAction(), language, options);
		}
	}

	public class AboutAction extends ModAction {
		public AboutAction(DdsConverterFrame owner) {
			super(owner, "About ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new AboutDialog(owner, getExitAction(), language);
		}
	}

	public class ExitAction extends ModAction {
		public ExitAction(DdsConverterFrame owner) {
			super(owner, "Exit");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			exit();
		}
	}

	public class AddImageAction extends ModAction {
		public AddImageAction(DdsConverterFrame owner) {
			super(owner, "Add Image");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			addImageTab(true);
		}
	}

	public class AddIconAction extends ModAction {
		public AddIconAction(DdsConverterFrame owner) {
			super(owner, "Add Icon");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			addIconTab(true);
		}
	}

	public class AddAtlasAction extends ModAction {
		public AddAtlasAction(DdsConverterFrame owner) {
			super(owner, "Add Atlas");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			addAtlasTab(true);
		}
	}

	private ModAction actionAddImage = new AddImageAction(this);
	private ModAction actionAddIcon = new AddIconAction(this);
	private ModAction actionAddAtlas = new AddAtlasAction(this);
	private ModAction actionPreferences = new PreferencesAction(this);
	private ModAction actionSaveSession = new SaveSessionAction(this);
	private ModAction actionLoadSession = new LoadSessionAction(this);
	private ModAction actionAbout = new AboutAction(this);
	private ModAction actionExit = new ExitAction(this);

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

	public ModAction getAddImageAction() {
		return actionAddImage;
	}

	public ModAction getAddIconAction() {
		return actionAddIcon;
	}

	public ModAction getAddAtlasAction() {
		return actionAddAtlas;
	}

	// ModUtilsSession interface methods
	static boolean webStartSession = false;
	static {
		try {
			ServiceManager.lookup("javax.jnlp.FileOpenService");
			ServiceManager.lookup("javax.jnlp.FileSaveService");
			webStartSession = true;
		} catch (UnavailableServiceException e) {
			webStartSession = false;
		}
	}

	@Override
	public boolean isWebStartSession() {
		return webStartSession;
	}

	@Override
	public boolean autoSaveSession() {
		getSaveSessionAction().actionPerformed(null);

		return true;
	}

	@Override
	public boolean autoLoadSession() {
		getLoadSessionAction().actionPerformed(null);

		return true;
	}
}
