package me.civ5.modddsconverter.ui.panel;

import gov.nasa.worldwind.formats.dds.DDSCompressor;
import gov.nasa.worldwind.formats.dds.DDSConstants;
import gov.nasa.worldwind.formats.dds.DXTCompressionAttributes;
import gov.nasa.worldwind.util.WWMath;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import me.civ5.dds.DdsRGBA8File;
import me.civ5.image.ModImage;
import me.civ5.modddsconverter.ui.DdsConverterFrame;
import me.civ5.modddsconverter.ui.model.grid.AtlasCellModelFactory;
import me.civ5.modddsconverter.ui.model.icon.IconType;
import me.civ5.modddsconverter.ui.model.image.ImageData;
import me.civ5.modddsconverter.ui.view.dialog.ImageDialogs;
import me.civ5.modddsconverter.ui.view.icon.IconCheck;
import me.civ5.modddsconverter.ui.view.icon.IconPanel;
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

public class IconsPanel extends AbstractPanel implements ActionListener {
	private ModImage originalImage = null;
	private ModImage targetImage = null;

	private IconPanel preview = new IconPanel(256, 256);

	private TitledBorder iconBorder;
	private ModComboBox<IconType> iconList;
	private JCheckBox iconMipMaps;

	private TitledBorder resizeBorder;
	private JRadioButton processResizeAndClip;
	private JRadioButton processClipCentre;

	private Map<Integer, IconCheck> iconChecks = new TreeMap<Integer, IconCheck>();
	
	private int selections = 0;

	public IconsPanel(DdsConverterFrame owner, Element config, boolean canClose) {
		super(owner, new BorderLayout(), "icons", config);

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

		preview.setAlignmentX(Component.LEFT_ALIGNMENT);

		openAction = new OpenAction(owner);
		JButton openButton = new JButton(openAction);
		openButton.setAlignmentX(Component.LEFT_ALIGNMENT);

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

		iconList = new ModComboBox<IconType>();
		iconList.addActionListener(this);
		iconList.setAlignmentX(Component.LEFT_ALIGNMENT);

		iconMipMaps = new JCheckBox("MipMaps");
		iconMipMaps.addActionListener(this);

		processResizeAndClip = new JRadioButton("Resize and Clip", true);
		processResizeAndClip.addActionListener(this);
		processClipCentre = new JRadioButton("Clip Only");
		processClipCentre.addActionListener(this);

		ButtonGroup resizeGroup = new ButtonGroup();
		resizeGroup.add(processResizeAndClip);
		resizeGroup.add(processClipCentre);

		JPanel resizePanel = new JPanel();
		resizeBorder = new TitledBorder("Image Manipulation"); 
		resizePanel.setBorder(resizeBorder);
		resizePanel.setLayout(new BoxLayout(resizePanel, BoxLayout.Y_AXIS));
		resizePanel.add(processResizeAndClip);
		resizePanel.add(Box.createVerticalStrut(5));
		resizePanel.add(processClipCentre);
		resizePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

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

		iconLeftCol.addComponent(new IconCheck(256, 172, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(128, 86, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(64, 43, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(45, 31, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(32, 22, this, iconChecks));
		iconLeftCol.addComponent(new IconCheck(24, 17, this, iconChecks));

		iconRightCol.addComponent(new IconCheck(214, 144, this, iconChecks));
		iconRightCol.addComponent(new IconCheck(80, 52, this, iconChecks));
		iconRightCol.addComponent(new IconCheck(48, 33, this, iconChecks));
		iconRightCol.addComponent(new IconCheck(22, -18, this, iconChecks)); // This icon is always square
		iconRightCol.addComponent(new IconCheck(16, 11, this, iconChecks));
		iconRightCol.addComponent(iconMipMaps);

		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(256)).addComponent(iconChecks.get(214)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(128)).addComponent(iconChecks.get(80)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(64)).addComponent(iconChecks.get(48)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(45)).addComponent(iconChecks.get(22)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(32)).addComponent(iconChecks.get(16)));
		iconRows.addGroup(iconLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(iconChecks.get(24)).addComponent(iconMipMaps));

		controlPanel.add(Box.createVerticalStrut(5));
		controlPanel.add(openButton);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(iconList);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(resizePanel);
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
		previewPanel.add(preview);
		previewPanel.add(Box.createVerticalStrut(10));
		previewPanel.add(getCommentsScroller());
		previewPanel.add(Box.createVerticalStrut(5));

		add(mainPanel, BorderLayout.CENTER);

		deselect();
		updateControls((IconType) iconList.getSelectedItem());
	}
	
	@Override
	public void open() {
		openAction.actionPerformed(null);
	}
	
	public void select() {
		++selections;
		
		closeButton.setEnabled(false);
	}
	
	public void deselect() {
		selections = Math.max(0, selections-1);

		closeButton.setEnabled(selections == 0);
	}
	
	public BufferedImage getIconImage(int size) {
		if (targetImage != null) {
			return ModUtilsImages.getIconImage(targetImage.getData(), size, size, iconChecks.get(size).getDiameter());
		} else {
			return null;
		}
	}

	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);

		openAction.setName(getLangStr("./ui/actions/open"));
		saveAction.setName(getLangStr("./ui/actions/save"));
		closeButton.setName(getLangStr("./ui/actions/close"));
		
		resizeBorder.setTitle(getLangStr("./ui/resize/title"));
		processResizeAndClip.setText(getLangStr("./ui/resize/resizeAndClip"));
		processClipCentre.setText(getLangStr("./ui/resize/resizeClip"));
		
		iconBorder.setTitle(getLangStr("./ui/icon/title"));
		iconMipMaps.setText(getLangStr("./ui/icon/mipmaps"));

		for (Element type : XpathHelper.getElements(config, "./types/type")) {
			iconList.addItem(new IconType(getLangStr("./types/type[@id='" + XpathHelper.getString(type, "./@id") + "']"), type));
		}
	}

	public Element serialise(Element container) {
		Element me = super.serialise(container);
		
		Element image = XmlHelper.newElement(me, "image");
		if (originalImage != null) {
			image.setAttribute("path", originalImage.getPath());
			image.setAttribute("name", originalImage.getName());
		}
		image.setAttribute("target", Integer.toString(iconList.getSelectedIndex()));

		Element icons = XmlHelper.newElement(me, "icons");
		for (Map.Entry<Integer, IconCheck> entries : iconChecks.entrySet()) {
			XmlHelper.newNamedElement(icons, "icon", entries.getKey().toString()).setText(Boolean.toString(entries.getValue().isSelected()));
		}
		XmlHelper.newNamedElement(icons, "icon", "mipmaps").setText(Boolean.toString(iconMipMaps.isSelected()));
		
		Element processes = XmlHelper.newElement(me, "processes");
		XmlHelper.newNamedElement(processes, "process", "resizeAndClip").setText(Boolean.toString(processResizeAndClip.isSelected()));
		XmlHelper.newNamedElement(processes, "process", "clipCentre").setText(Boolean.toString(processClipCentre.isSelected()));
		
		return me;
	}
	
	public Element deserialise(Element container) {
		Element me = super.deserialise(container);
		
		originalImage = null;
		iconList.setSelectedIndex(XpathHelper.getInt(me, "./image/@target", 0));

		for (Map.Entry<Integer, IconCheck> entries : iconChecks.entrySet()) {
			entries.getValue().setSelected(XpathHelper.getBoolean(me, "./icons/icon[@name='" + entries.getKey().toString() + "']", false));
		}
		iconMipMaps.setSelected(XpathHelper.getBoolean(me, "./icons/icon[@name='mipmaps']", false));

		processResizeAndClip.setSelected(true);
		processClipCentre.setSelected(XpathHelper.getBoolean(me, "./processes/process[@name='clipCentre']", false));

		try {
			String imagePath = XpathHelper.getString(me, "./image/@path");
			String imageName = XpathHelper.getString(me, "./image/@name");
			if (imageName != null) {
				File imageFile = new File(imagePath, imageName);
				if (imageFile.exists()) {
					originalImage = new ModImage(imageFile);
				}
			}

			validateImage((IconType) iconList.getSelectedItem());
		} catch (IOException ex) {
			clearComments();
			appendComment(ex.getLocalizedMessage());
		}
		
		return me;
	}

	private void validateImage(IconType targetType) throws IOException {
		updateDescription(targetType);
		clearComments();

		if (originalImage != null) {
			int imageWidth = originalImage.getWidth();
			int imageHeight = originalImage.getHeight();

			appendComment("imageSize", new Object[] {imageWidth, imageHeight});

			if ((imageWidth % 4) != 0) {
				appendComment("badWidth", new Object[] {imageWidth});
			}

			if ((imageHeight % 4) != 0) {
				appendComment("badHeight", new Object[] {imageHeight});
			}

			if (imageWidth != imageHeight) {
				appendComment("mismatch", null);
			}

			if (targetType.getSizes()[0] == null) {
				appendComment("selectFormat", null);
			}

			updateTarget(targetType);
		} else {
			appendComment("selectImage", null);
		}
	}

	private void updateTarget(IconType type) throws IOException {
		ModImage previewImage = null;

		if (originalImage != null) {
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			int targetSize, diameter;

			if (type.getSizes()[0] == null || type.getSizes()[0] == 0) {
				targetSize = 256;
				diameter = 176;
			} else {
				targetSize = type.getSizes()[0];
				diameter = Math.abs(iconChecks.get(type.getSizes()[0]).getDiameter());
			}
			
			float scale = Math.max(diameter / (width * 1.0f), diameter / (height * 1.0f));
			int scaleWidth = (int) (width * scale);
			int scaleHeight = (int) (height * scale);

			BufferedImage scaledImage = processClipCentre.isSelected() ? originalImage.getData() : ModUtilsImages.getScaledImage(originalImage.getData(), scaleWidth, scaleHeight);
			BufferedImage clippedImage = type.isSquare() ? scaledImage : ModUtilsImages.getClippedImage(scaledImage, diameter);
			BufferedImage centeredImage = ModUtilsImages.getCenteredImage(clippedImage, targetSize, targetSize);

			targetImage = new ModImage(originalImage.getPath(), originalImage.getName(), scaledImage);
			previewImage = new ModImage(originalImage.getPath(), originalImage.getName(), centeredImage);
		} else {
			targetImage = null;
		}

		preview.setImage(previewImage);

		saveAction.setEnabled(false);
		for (JCheckBox iconCheck : iconChecks.values()) {
			if (iconCheck.isSelected()) {
				saveAction.setEnabled(true);
				break;
			}
		}
		
		AtlasCellModelFactory.updatePanel(this);
	}

	private void updateDescription(IconType type) {
		StringBuilder sb = new StringBuilder();

		if (originalImage != null) {
			sb.append(originalImage.getName()).append(" (").append(originalImage.getWidth()).append("x").append(originalImage.getHeight()).append(")");

			if (type.getSizes()[0] == null) {
				sb.append(" --> ").append(getLangStr("./comments/comment[@id='selectFormat']"));
			} else {
				sb.append(" --> ").append(type.getName());
			}
		} else {
			sb.append(getLangStr("./comments/comment[@id='selectImage']"));
		}

		setDescription(sb.toString());
	}

	private void updateControls(IconType type) {
		if (type == null || type.getSizes()[0] == null) {
			for (JCheckBox iconCheck : iconChecks.values()) {
				iconCheck.setEnabled(false);
			}
		} else {
			for (JCheckBox iconCheck : iconChecks.values()) {
				iconCheck.setEnabled(true);
			}
		}
		
		for (Map.Entry<Integer, IconCheck> entry : iconChecks.entrySet()) {
			if (iconMipMaps.isSelected()) {
				if (WWMath.isPowerOfTwo(entry.getKey())) {
					entry.getValue().setEnabled(true);
				} else {
					entry.getValue().setSelected(false);
					entry.getValue().setEnabled(false);
				}
			} else {
				entry.getValue().setEnabled(true);
			}
		}

		saveAction.setEnabled(targetImage != null);

		updateDescription(type);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		IconType targetType = (IconType) iconList.getSelectedItem();

		if (e.getSource().equals(closeButton)) {
			owner.removeTab(this);
			AtlasCellModelFactory.removePanel(this);
		} else if (e.getSource().equals(iconList)) {
			for (JCheckBox iconCheck : iconChecks.values()) {
				iconCheck.setSelected(false);
			}

			for (Integer size : targetType.getSizes()) {
				if (size != null && iconChecks.containsKey(size)) {
					iconChecks.get(size).setSelected(true);
				}
			}

			iconMipMaps.setSelected(targetType.isMipMaps());
		}

		try {
			updateControls(targetType);
			validateImage(targetType);
		} catch (IOException ex) {
			clearComments();
			appendComment(ex.getLocalizedMessage());
		}
	}

	public class OpenAction extends ModAction {
		public OpenAction(DdsConverterFrame owner) {
			super(owner, "Open ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				originalImage = ImageDialogs.chooseImage(owner);
				validateImage((IconType) iconList.getSelectedItem());
			} catch (IOException ex) {
				clearComments();
				appendComment(ex.getLocalizedMessage());
			}
		}
	}

	public class SaveAction extends ModAction {
		private AbstractPanel panel;

		public SaveAction(DdsConverterFrame owner, AbstractPanel panel) {
			super(owner, "Save ...");
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String tempName = originalImage.getName();
			int dot = tempName.lastIndexOf('.');
			if (dot != -1) {
				tempName = tempName.substring(0, dot);
			}
			final String imageName = tempName;
			
			LogDialog logDialog = new ProgressDialog(owner, getLangStr("./ui/progress/title"), owner.getLevel(), null);

			class IconWorker extends ModWorker {
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

								IconCheck icon = iconChecks.get(iconSize);

								if (iconMipMaps.isSelected()) {
									DDSCompressor ddsCompressor = new DDSCompressor();
									DXTCompressionAttributes attrs = new DXTCompressionAttributes();
									attrs.setBuildMipmaps(true);
									attrs.setDXTFormat(DDSConstants.D3DFMT_DXT3);

									BufferedImage image = ModUtilsImages.getIconImage(targetImage.getData(), iconSize, iconSize, icon.getDiameter());
									ddsIcons.add(new ImageData(imageName, iconSize.toString(), ".dds", ddsCompressor.compressImage(image, attrs)));
								} else {
									DdsRGBA8File rgba8 = new DdsRGBA8File(iconSize, iconSize, icon.getDiameter(), targetImage.getData());
									ddsIcons.add(new ImageData(imageName, iconSize.toString(), ".dds", rgba8.getBytes()));
								}
								
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
	
						appendComment("converted", new Object[] {originalImage.getName(), imageName, getElapsed()});
	
						ImageDialogs.saveImage(panel, getDdsIcons());
					} else {
						appendComment("cancelled", new Object[] {originalImage.getName(), imageName});
					}
					
					return null;
				}
			}

			appendComment("processing", new Object[] {originalImage.getName()});
			(new IconWorker()).doWork(logDialog);
		}
	}
}
