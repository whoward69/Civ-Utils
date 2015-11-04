package me.civ5.modddsconverter.ui.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import me.civ5.dds.DdsRGBA8File;
import me.civ5.modddsconverter.ui.DdsConverterFrame;
import me.civ5.modddsconverter.ui.model.icon.IconType;
import me.civ5.modddsconverter.ui.model.image.ImageData;
import me.civ5.modddsconverter.ui.view.dialog.ImageDialogs;
import me.civ5.modddsconverter.ui.view.grid.AtlasGrid;
import me.civ5.modddsconverter.ui.view.icon.IconCheck;
import me.civ5.modutils.log.LogInfo;
import me.civ5.modutils.ui.action.ModAction;
import me.civ5.modutils.ui.view.dialog.LogDialog;
import me.civ5.modutils.ui.view.dialog.ProgressDialog;
import me.civ5.modutils.ui.worker.ModWorker;
import me.civ5.modutils.utils.ModUtilsImages;
import me.civ5.ui.component.ModComboBox;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class AtlasPanel extends AbstractPanel implements ActionListener {
	private String atlasBase = "Atlas.jpg";
	private AtlasGrid atlasGrid;
	
	private TitledBorder gridBorder;
	private JLabel gridLabelRows, gridLabelCols;

	private TitledBorder iconBorder;
	private ModComboBox<IconType> iconList;

	private Map<Integer, IconCheck> iconChecks = new TreeMap<Integer, IconCheck>();

	public AtlasPanel(DdsConverterFrame owner, Element config, boolean canClose) {
		super(owner, new BorderLayout(), "atlas", config);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

		JPanel previewPanel = new JPanel();
		previewPanel.setLayout(new BoxLayout(previewPanel, BoxLayout.Y_AXIS));

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		mainPanel.add(Box.createHorizontalStrut(5));
		mainPanel.add(previewPanel);
		mainPanel.add(Box.createHorizontalStrut(10));
		mainPanel.add(controlPanel);
		mainPanel.add(Box.createHorizontalStrut(5));

		atlasGrid = new AtlasGrid(2, 2);
		atlasGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

		saveAction = new SaveAction(owner, this);
		JButton saveButton = new JButton(saveAction);
		saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);

		closeButton = new JButton("Close");
		closeButton.setVisible(canClose); // Used to store the canClose state
		closeButton.addActionListener(this);
		closeButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		JPanel closePanel = new JPanel();
		closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.X_AXIS));
		closePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		closePanel.add(Box.createHorizontalGlue());
		closePanel.add(closeButton);

		JPanel innerGridPanel = new JPanel();
		GroupLayout gridLayout = new GroupLayout(innerGridPanel);
		innerGridPanel.setLayout(gridLayout);

		JPanel gridPanel = new JPanel();
		gridBorder = new TitledBorder("Grid Size"); 
		gridPanel.setBorder(gridBorder);
		gridPanel.setLayout(new BoxLayout(gridPanel, BoxLayout.X_AXIS));
		gridPanel.add(innerGridPanel);
		gridPanel.add(Box.createHorizontalGlue());
		gridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		gridLayout.setAutoCreateGaps(true);
		gridLayout.setAutoCreateContainerGaps(true);

		SequentialGroup gridRows = gridLayout.createSequentialGroup();
		ParallelGroup gridLeftCol = gridLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
		ParallelGroup gridRightCol = gridLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

		gridLabelRows = new JLabel("Rows");
		gridLabelCols = new JLabel("Cols");

		gridLayout.setHorizontalGroup(gridLayout.createSequentialGroup().addGroup(gridLeftCol).addGroup(gridRightCol));
		gridLayout.setVerticalGroup(gridRows);

		gridLeftCol.addComponent(gridLabelRows);
		gridLeftCol.addComponent(gridLabelCols);

		gridRightCol.addComponent(atlasGrid.getRowControl());
		gridRightCol.addComponent(atlasGrid.getColControl());

		gridRows.addGroup(gridLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(gridLabelRows).addComponent(atlasGrid.getRowControl()));
		gridRows.addGroup(gridLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(gridLabelCols).addComponent(atlasGrid.getColControl()));

		iconList = new ModComboBox<IconType>();
		iconList.addActionListener(this);
		iconList.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel innerIconPanel = new JPanel();
		GroupLayout iconLayout = new GroupLayout(innerIconPanel);
		innerIconPanel.setLayout(iconLayout);

		JPanel iconPanel = new JPanel();
		iconBorder = new TitledBorder("Icon Sizes"); 
		iconPanel.setBorder(iconBorder);
		iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
		iconPanel.add(innerIconPanel);
		iconPanel.add(Box.createHorizontalGlue());
		iconPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		iconLayout.setAutoCreateGaps(true);
		iconLayout.setAutoCreateContainerGaps(true);

		SequentialGroup iconRows = iconLayout.createSequentialGroup();
		ParallelGroup iconLeftCol = iconLayout.createParallelGroup(GroupLayout.Alignment.LEADING);
		ParallelGroup iconRightCol = iconLayout.createParallelGroup(GroupLayout.Alignment.LEADING);

		iconLayout.setHorizontalGroup(iconLayout.createSequentialGroup().addGroup(iconLeftCol).addGroup(iconRightCol));
		iconLayout.setVerticalGroup(iconRows);

		iconLeftCol.addComponent(new IconCheck(256, 176, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(128, 88, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(64, 44, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(45, 31, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(32, 22, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(24, 17, this, iconChecks));

		iconRightCol.addComponent(new IconCheck(214, 144, this, iconChecks));
		iconRightCol.addComponent(new IconCheck(80, 52, this, iconChecks));
		iconRightCol.addComponent(new IconCheck(48, 33, this, iconChecks));
		iconRightCol.addComponent(new IconCheck(22, 18, this, iconChecks)); // This icon is always square
		iconRightCol.addComponent(new IconCheck(16, 11, this, iconChecks));

		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(256)).addComponent(iconChecks.get(214)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(128)).addComponent(iconChecks.get(80)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(64)).addComponent(iconChecks.get(48)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(45)).addComponent(iconChecks.get(22)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(32)).addComponent(iconChecks.get(16)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(24)));

		controlPanel.add(Box.createVerticalStrut(5));
		controlPanel.add(gridPanel);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(iconList);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(iconPanel);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(saveButton);
		controlPanel.add(Box.createVerticalGlue());
		if (canClose) {
			controlPanel.add(closePanel);
			controlPanel.add(Box.createVerticalStrut(5));
		}

		previewPanel.add(Box.createVerticalStrut(5));
		previewPanel.add(getDescription());
		previewPanel.add(Box.createVerticalStrut(10));
		previewPanel.add(atlasGrid);
		previewPanel.add(Box.createVerticalStrut(10));
		previewPanel.add(getCommentsScroller());
		previewPanel.add(Box.createVerticalStrut(5));

		add(mainPanel, BorderLayout.CENTER);

		updateControls((IconType) iconList.getSelectedItem());
	}

	@Override
	public void open() {
	}
	
	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);

		saveAction.setName(getLangStr("./ui/actions/save"));
		closeButton.setName(getLangStr("./ui/actions/close"));
		
		iconBorder.setTitle(getLangStr("./ui/icon/title"));
		
		gridBorder.setTitle(getLangStr("./ui/grid/title"));
		gridLabelRows.setText(getLangStr("./ui/grid/rows"));
		gridLabelCols.setText(getLangStr("./ui/grid/cols"));

		for (Element type : XpathHelper.getElements(config, "./types/type")) {
			iconList.addItem(new IconType(getLangStr("./types/type[@id='" + XpathHelper.getString(type, "./@id") + "']"), type));
		}
	}

	public Element serialise(Element container) {
		Element me = super.serialise(container);
		
		Element image = XmlHelper.newElement(me, "image");
		image.setAttribute("target", Integer.toString(iconList.getSelectedIndex()));

		Element icons = XmlHelper.newElement(me, "icons");
		for (Map.Entry<Integer, IconCheck> entries : iconChecks.entrySet()) {
			XmlHelper.newNamedElement(icons, "icon", entries.getKey().toString()).setText(Boolean.toString(entries.getValue().isSelected()));
		}

		atlasGrid.serialise(me);
		
		return me;
	}
	
	public Element deserialise(Element container) {
		Element me = super.deserialise(container);

		iconList.setSelectedIndex(XpathHelper.getInt(me, "./image/@target", 0));

		for (Map.Entry<Integer, IconCheck> entries : iconChecks.entrySet()) {
			entries.getValue().setSelected(XpathHelper.getBoolean(me, "./icons/icon[@name='" + entries.getKey().toString() + "']", false));
		}

		atlasGrid.deserialise(me);
		
		return me;
	}

	private void updateControls(IconType type) {
		if (type == null || type.getSizes()[0] == null) {
			// No icon type selected
			for (JCheckBox iconCheck : iconChecks.values()) {
				iconCheck.setEnabled(false);
			}

			atlasBase = getLangStr("./file/base");
		} else {
			for (JCheckBox iconCheck : iconChecks.values()) {
				iconCheck.setEnabled(true);
			}

			atlasBase = type.getName().replaceAll("[^-a-zA-Z0-9_]", "") + getLangStr("./file/base");
		}
		
		for (Map.Entry<Integer, IconCheck> entry : iconChecks.entrySet()) {
			entry.getValue().setEnabled(true);
		}

		saveAction.setEnabled(false);
		for (JCheckBox iconCheck : iconChecks.values()) {
			if (iconCheck.isSelected()) {
				saveAction.setEnabled(true);
				break;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		IconType targetType = (IconType) iconList.getSelectedItem();

		if (e.getSource().equals(closeButton)) {
			owner.removeTab(this);
		} else if (e.getSource().equals(iconList)) {
			for (JCheckBox iconCheck : iconChecks.values()) {
				iconCheck.setSelected(false);
			}

			for (Integer size : targetType.getSizes()) {
				if (size != null && iconChecks.containsKey(size)) {
					iconChecks.get(size).setSelected(true);
				}
			}
		}

		updateControls(targetType);
	}

	public class SaveAction extends ModAction {
		private AbstractPanel panel;

		public SaveAction(DdsConverterFrame owner, AbstractPanel panel) {
			super(owner, "Save ...");
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LogDialog logDialog = new ProgressDialog(owner, getLangStr("./ui/progress/title"), owner.getLevel(), null);

			class AtlasWorker extends ModWorker {
				private List<ImageData> ddsIcons = new ArrayList<ImageData>();

				private Date start, end;

				List<ImageData> getDdsIcons() {
					return ddsIcons;
				}

				double getElapsed() {
					return (((end.getTime() - start.getTime() + 50) / 100) / 10.0);
				}

				@Override
				protected Void doInBackground() throws Exception {
					start = new Date();

					try {
						publish(new LogInfo(getLangStr("./ui/progress/generating")));
						
						for (Map.Entry<Integer, IconCheck> entry : iconChecks.entrySet()) {
							if (entry.getValue().isSelected()) {
								Integer iconSize = entry.getKey();
								publish(new LogInfo(getLangStr("./ui/progress/size", new Object[] {iconSize})));

								int atlasWidth = iconSize * atlasGrid.getCols();
								int atlasHeight = iconSize * atlasGrid.getRows();

								BufferedImage atlasImage = ModUtilsImages.getColouredImage(atlasWidth, atlasHeight, new Color(0, true));
								
								for (int row = 0; row < atlasGrid.getRows(); row++) {
									for (int col = 0; col < atlasGrid.getCols(); col++) {
										BufferedImage iconImage = atlasGrid.getIconImage(row, col, iconSize);
										
										if (iconImage != null) {
											ModUtilsImages.getInsetImage(atlasImage, iconImage, iconSize * col, iconSize * row);
										}
									}
								}

								DdsRGBA8File rgba8 = new DdsRGBA8File(atlasWidth, atlasHeight, 0, atlasImage);
								ddsIcons.add(new ImageData(atlasBase, iconSize.toString(), ".dds", rgba8.getBytes()));
								
								if (!logDialog.isVisible()) {
									// The user closed/cancelled the dialog
									break;
								}
							}
						}
					} catch (Exception e) {
						appendComment(e.getMessage());
					}

					end = new Date();
					
					if (logDialog.isVisible()) {
						logDialog.close();
	
						appendComment("converted", new Object[] {atlasBase, atlasBase, getElapsed()});
	
						ImageDialogs.saveImage(panel, getDdsIcons());
					} else {
						appendComment("cancelled", new Object[] {atlasBase, atlasBase});
					}
					
					return null;
				}
			}

			appendComment("processing", new Object[] {atlasBase});
			(new AtlasWorker()).doWork(logDialog);
		}
	}
}
