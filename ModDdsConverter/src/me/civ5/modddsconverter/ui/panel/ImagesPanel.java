package me.civ5.modddsconverter.ui.panel;

import gov.nasa.worldwind.formats.dds.DDSCompressor;
import gov.nasa.worldwind.formats.dds.DDSConstants;
import gov.nasa.worldwind.formats.dds.DXTCompressionAttributes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import me.civ5.image.ModImage;
import me.civ5.modddsconverter.ui.DdsConverterFrame;
import me.civ5.modddsconverter.ui.model.image.ImageData;
import me.civ5.modddsconverter.ui.model.image.ImageType;
import me.civ5.modddsconverter.ui.view.dialog.ImageDialogs;
import me.civ5.modddsconverter.ui.view.image.ImagePanel;
import me.civ5.modutils.ui.action.ModAction;
import me.civ5.modutils.ui.worker.ModWorker;
import me.civ5.modutils.utils.ModUtilsImages;
import me.civ5.ui.component.ModComboBox;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public class ImagesPanel extends AbstractPanel implements ActionListener {
	private ModImage originalImage = null;
	private ModImage targetImage = null;
	
	private ImagePanel preview = new ImagePanel(560, 360);

	private ModComboBox<ImageType> imageList;
	
	private TitledBorder processBorder;
	private JRadioButton processResizeAndClip;
	private JRadioButton processResizeAuto;
	private JRadioButton processClipCentre;
	private JRadioButton processClipManual;
	private JRadioButton processNone;
	
	private TitledBorder dxtBorder;
	private JRadioButton dxt1;
	private JRadioButton dxt3;
	private JRadioButton dxt5;
	
	public ImagesPanel(DdsConverterFrame owner, Element config, boolean canClose) {
		super(owner, new BorderLayout(), "images", config);
		
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

		saveAction = new SaveAction(owner);
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

		imageList = new ModComboBox<ImageType>();
		imageList.addActionListener(this);
		imageList.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		processResizeAndClip = new JRadioButton("Resize and Clip", true);
		processResizeAndClip.addActionListener(this);
		processResizeAuto = new JRadioButton("Resize Only");
		processResizeAuto.addActionListener(this);
		processClipCentre = new JRadioButton("Centre Clip");
		processClipCentre.addActionListener(this);
		processClipManual = new JRadioButton("Manual Clip");
		processClipManual.addActionListener(this);
		processNone = new JRadioButton("Use As Is");
		processNone.addActionListener(this);
		
		ButtonGroup processGroup = new ButtonGroup();
		processGroup.add(processResizeAndClip);
		processGroup.add(processResizeAuto);
		processGroup.add(processClipCentre);
		processGroup.add(processClipManual);
		processGroup.add(processNone);
		
		JPanel processPanel = new JPanel();
		processBorder = new TitledBorder("Image Manipulation");
		processPanel.setBorder(processBorder);
		processPanel.setLayout(new BoxLayout(processPanel, BoxLayout.Y_AXIS));
		processPanel.add(processResizeAndClip);
		processPanel.add(Box.createVerticalStrut(5));
		processPanel.add(processResizeAuto);
		processPanel.add(Box.createVerticalStrut(5));
		processPanel.add(processClipCentre);
		processPanel.add(Box.createVerticalStrut(5));
		processPanel.add(processClipManual);
		processPanel.add(Box.createVerticalStrut(5));
		processPanel.add(processNone);
		processPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		dxt1 = new JRadioButton("DXT1/BC1");
		dxt1.addActionListener(this);
		dxt3 = new JRadioButton("DXT3/BC2");
		dxt3.addActionListener(this);
		dxt5 = new JRadioButton("DXT5/BC3");
		dxt5.addActionListener(this);
		
		ButtonGroup dxtGroup = new ButtonGroup();
		dxtGroup.add(dxt1);
		dxtGroup.add(dxt3);
		dxtGroup.add(dxt5);
		
		dxt3.setSelected(true);
		
		JPanel dxtPanel = new JPanel();
		dxtBorder = new TitledBorder("Image Compression");
		dxtPanel.setBorder(dxtBorder);
		dxtPanel.setLayout(new BoxLayout(dxtPanel, BoxLayout.Y_AXIS));
		dxtPanel.add(dxt1);
		dxtPanel.add(Box.createVerticalStrut(5));
		dxtPanel.add(dxt3);
		dxtPanel.add(Box.createVerticalStrut(5));
		dxtPanel.add(dxt5);
		dxtPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		controlPanel.add(Box.createVerticalStrut(5));
		controlPanel.add(openButton);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(imageList);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(processPanel);
		controlPanel.add(Box.createVerticalStrut(10));
		controlPanel.add(dxtPanel);
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
		
		updateControls((ImageType) imageList.getSelectedItem());
	}

	@Override
	public void open() {
		openAction.actionPerformed(null);
	}
	
	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);

		openAction.setName(getLangStr("./ui/actions/open"));
		saveAction.setName(getLangStr("./ui/actions/save"));
		closeButton.setName(getLangStr("./ui/actions/close"));
		
		processBorder.setTitle(getLangStr("./ui/resize/title"));
		processResizeAndClip.setText(getLangStr("./ui/resize/resizeAndClip"));
		processResizeAuto.setText(getLangStr("./ui/resize/resizeOnly"));
		processClipCentre.setText(getLangStr("./ui/resize/resizeCentre"));
		processClipManual.setText(getLangStr("./ui/resize/resizeManual"));
		processNone.setText(getLangStr("./ui/resize/resizeNone"));
		
		dxtBorder.setTitle(getLangStr("./ui/dxt/title"));
		dxt1.setText(getLangStr("./ui/dxt/dxt1"));
		dxt3.setText(getLangStr("./ui/dxt/dxt3"));
		dxt5.setText(getLangStr("./ui/dxt/dxt5"));

		for (Element type : XpathHelper.getElements(config, "./types/type")) {
			imageList.addItem(new ImageType(getLangStr("./types/type[@id='" + XpathHelper.getString(type, "./@id") + "']"), type));
		}
	}

	public Element serialise(Element container) {
		Element me = super.serialise(container);
		
		Element image = XmlHelper.newElement(me, "image");
		if (originalImage != null) {
			image.setAttribute("path", originalImage.getPath());
			image.setAttribute("name", originalImage.getName());
		}
		image.setAttribute("target", Integer.toString(imageList.getSelectedIndex()));

		Element processes = XmlHelper.newElement(me, "processes");
		XmlHelper.newNamedElement(processes, "process", "resizeAndClip").setText(Boolean.toString(processResizeAndClip.isSelected()));
		XmlHelper.newNamedElement(processes, "process", "resizeAuto").setText(Boolean.toString(processResizeAuto.isSelected()));
		XmlHelper.newNamedElement(processes, "process", "clipCentre").setText(Boolean.toString(processClipCentre.isSelected()));
		XmlHelper.newNamedElement(processes, "process", "clipManual").setText(Boolean.toString(processClipManual.isSelected()));
		XmlHelper.newNamedElement(processes, "process", "none").setText(Boolean.toString(processNone.isSelected()));

		Element dxts = XmlHelper.newElement(me, "dxts");
		XmlHelper.newNamedElement(dxts, "dxt", "1").setText(Boolean.toString(dxt1.isSelected()));
		XmlHelper.newNamedElement(dxts, "dxt", "3").setText(Boolean.toString(dxt3.isSelected()));
		XmlHelper.newNamedElement(dxts, "dxt", "5").setText(Boolean.toString(dxt5.isSelected()));
		
		return me;
	}
	
	public Element deserialise(Element container) {
		Element me = super.deserialise(container);

		originalImage = null;
		imageList.setSelectedIndex(XpathHelper.getInt(me, "./image/@target", 0));

		processResizeAndClip.setSelected(true);
		processResizeAuto.setSelected(XpathHelper.getBoolean(me, "./processes/process[@name='resizeAuto']", false));
		processClipCentre.setSelected(XpathHelper.getBoolean(me, "./processes/process[@name='clipCentre']", false));
		processClipManual.setSelected(XpathHelper.getBoolean(me, "./processes/process[@name='clipManual']", false));
		processNone.setSelected(XpathHelper.getBoolean(me, "./processes/process[@name='none']", false));

		dxt3.setSelected(true);
		dxt1.setSelected(XpathHelper.getBoolean(me, "./dxts/dxt[@name='1']", false));
		dxt5.setSelected(XpathHelper.getBoolean(me, "./dxts/dxt[@name='5']", false));
		
		try {
			String imagePath = XpathHelper.getString(me, "./image/@path");
			String imageName = XpathHelper.getString(me, "./image/@name");
			if (imageName != null) {
				File imageFile = new File(imagePath, imageName);
				if (imageFile.exists()) {
					originalImage = new ModImage(imageFile);
				}
			}

			validateImage((ImageType) imageList.getSelectedItem());
		} catch (IOException ex) {
			clearComments();
			appendComment(ex.getLocalizedMessage());
		}

		return me;
	}

	private void validateImage(ImageType type) throws IOException {
		updateDescription(type);
		clearComments();
		
		if (originalImage != null) {
			int imageWidth = originalImage.getWidth();
			int imageHeight = originalImage.getHeight();

			int targetWidth = type.getWidth();
			int targetHeight = type.getHeight();
			
			appendComment("imageSize", new Object[] {imageWidth, imageHeight});
			if (targetWidth > 0) {
				appendComment("formatSize", new Object[] {targetWidth, targetHeight});
			}
			
			if ((imageWidth % 4) != 0) {
				if (processNone.isSelected()) processClipCentre.setSelected(true);
				processNone.setEnabled(false);
				
				appendComment("badWidth", new Object[] {imageWidth});
			}
			
			if ((imageHeight %4) != 0) {
				if (processNone.isSelected()) processClipCentre.setSelected(true);
				processNone.setEnabled(false);

				appendComment("badHeight", new Object[] {imageHeight});
			}
			
			if (targetWidth == -1) {
				appendComment("selectFormat", null);
			} else {
				if (targetWidth != 0) {
					if (imageWidth != targetWidth) {
						appendComment("mismatchWidth", new Object[] {imageWidth, targetWidth});
					}

					if (imageHeight != targetHeight) {
						appendComment("mismatchHeight", new Object[] {imageHeight, targetHeight});
					}
				}
			}
			
			updateTarget(type);
		} else {
			appendComment("selectImage", null);
		}
	}

	private void updateTarget(ImageType type) throws IOException {
		if (originalImage != null) {
			if (type.getWidth() == -1) {
				targetImage = null;
			} else {
				int width = originalImage.getWidth();
				int height = originalImage.getHeight();
				
				int targetWidth, targetHeight;
				BufferedImage image;
				
				if (type.getWidth() == 0) {
					targetWidth = (width / 4) * 4;
					targetHeight = (height / 4) * 4;
					
					image = ModUtilsImages.getCenteredImage(originalImage.getData(), targetWidth, targetHeight);
				} else {
					targetWidth = type.getWidth();
					targetHeight = type.getHeight();
					
					int scaleWidth, scaleHeight;
					if (processResizeAndClip.isSelected()) {
						float scale = Math.max(targetWidth / (width * 1.0f), targetHeight / (height * 1.0f));
						scaleWidth = (int) (width * scale);
						scaleHeight = (int) (height * scale);
					} else if (processResizeAuto.isSelected()) {
						scaleWidth = Math.min(targetWidth, width);
						scaleHeight = Math.min(targetHeight, height);
					} else if (processClipCentre.isSelected()) {
						scaleWidth = width;
						scaleHeight = height;
					} else if (processClipManual.isSelected()) {
						preview.setClipping(width, height, targetWidth, targetHeight);
						
						// Reset some variables so as to AVOID scaling and clipping
						targetWidth = width;
						targetHeight = height;
						scaleWidth = targetWidth;
						scaleHeight = targetHeight;
					} else {
						scaleWidth = targetWidth;
						scaleHeight = targetHeight;
					}
					
		        	image = ModUtilsImages.getCenteredImage(ModUtilsImages.getScaledImage(originalImage.getData(), scaleWidth, scaleHeight), targetWidth, targetHeight);
				}

				targetImage = new ModImage(originalImage.getPath(), originalImage.getName(), image);
			}
		} else {
			targetImage = null;
		}
		
		preview.setImage(targetImage);
		saveAction.setEnabled(targetImage != null);
	}
	
	private void updateDescription(ImageType type) {
		StringBuilder sb = new StringBuilder();
		
		if (originalImage != null) {
			sb.append(originalImage.getName()).append(" (").append(originalImage.getWidth()).append("x").append(originalImage.getHeight()).append(")");

			if (type.getWidth() == -1) {
				sb.append(" --> ").append(getLangStr("./comments/comment[@id='selectFormat']"));
			} else if (type.getWidth() == 0) {
				sb.append(" --> ").append(type.getName());
			} else { 
				sb.append(" --> ").append(type.getName()).append(" (").append(type.getWidth()).append("x").append(type.getHeight()).append(")");
			}
		} else {
			sb.append(getLangStr("./comments/comment[@id='selectImage']"));
		}

		setDescription(sb.toString());
	}
	
	private void updateControls(ImageType type) {
		if (type == null || type.getWidth() == -1) {
			// No image type selected
			processNone.setSelected(true);

			processResizeAndClip.setEnabled(false);
			processResizeAuto.setEnabled(false);
			processClipCentre.setEnabled(false);
			processClipManual.setEnabled(false);
			processNone.setEnabled(false);
			
			dxt1.setEnabled(false);
			dxt3.setEnabled(false);
			dxt5.setEnabled(false);
		} else if (type.getWidth() == 0) {
			// Custom image type selected
			if (processResizeAndClip.isSelected() || processResizeAuto.isSelected()) {
				processNone.setSelected(true);
			}

			processResizeAndClip.setEnabled(false);
			processResizeAuto.setEnabled(false);
			processClipCentre.setEnabled(true);
			processClipManual.setEnabled(false);
			processNone.setEnabled(true);
			
			dxt1.setEnabled(true);
			dxt3.setEnabled(true);
			dxt5.setEnabled(true);
		} else {
			// Pre-defined image type selected
			if (processNone.isSelected()) {
				processResizeAndClip.setSelected(true);
			}

			processResizeAndClip.setEnabled(true);
			processResizeAuto.setEnabled(true);
			processClipCentre.setEnabled(true);
			processClipManual.setEnabled (originalImage != null && originalImage.getWidth() > type.getWidth() && originalImage.getHeight() > type.getHeight());
			processNone.setEnabled(false);
			
			dxt1.setEnabled(true);
			dxt3.setEnabled(true);
			dxt5.setEnabled(true);
		}

		preview.setTracking(processClipManual.isSelected());
		saveAction.setEnabled(targetImage != null);

		updateDescription(type);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ImageType targetType = (ImageType) imageList.getSelectedItem();
		
		if (e.getSource().equals(closeButton)) {
			owner.removeTab(this);
		} else if (e.getSource().equals(imageList)) {
			if (targetType.getDefCompression() == DDSConstants.D3DFMT_DXT1) {
				dxt1.setSelected(true);
			} else if (targetType.getDefCompression() == DDSConstants.D3DFMT_DXT3) {
				dxt3.setSelected(true);
			} else {
				dxt5.setSelected(true);
			}
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
				validateImage((ImageType) imageList.getSelectedItem());
			} catch (IOException ex) {
				clearComments();
				appendComment(ex.getLocalizedMessage());
			}
		}
	}

	public class SaveAction extends ModAction {
		public SaveAction(DdsConverterFrame owner) {
			super(owner, "Save ...");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String ddsName = originalImage.getName();
				int dot = ddsName.lastIndexOf('.');
				if (dot != -1) {
					ddsName = ddsName.substring(0, dot);
				}

				DXTCompressionAttributes attrs = new DXTCompressionAttributes();
				attrs.setBuildMipmaps(false);
				attrs.setDXTFormat(dxt1.isSelected() ? DDSConstants.D3DFMT_DXT1 : dxt3.isSelected() ? DDSConstants.D3DFMT_DXT3 : DDSConstants.D3DFMT_DXT5);
				
				if (dxt1.isSelected()) {
					attrs.setEnableDXT1Alpha(true);
				}

				class ImageWorker extends ModWorker {
					private DXTCompressionAttributes attrs;
					private ByteBuffer dds;
					
					private Date start, end;
					
					ImageWorker(DXTCompressionAttributes attrs) {
						this.attrs = attrs;
					}
					
					ByteBuffer getDds() {
						return dds;
					}
					
					double getElapsed() {
						return (((end.getTime() - start.getTime() + 50) / 100) / 10.0);
					}

					@Override
					protected Void doInBackground() throws Exception {
						BufferedImage targetData = targetImage.getData();
						
						DDSCompressor ddsCompressor = new DDSCompressor();
						start = new Date();
						
						if (processClipManual.isSelected()) {
							// Don't use targetImage width/height here as they were reset for manual scaling
							ImageType targetType = (ImageType) imageList.getSelectedItem();
							targetData = ModUtilsImages.getScaledImage(ModUtilsImages.getOffsetImage(targetData, preview.getOffsetX(), preview.getOffsetY(), preview.getTargetWidth(), preview.getTargetHeight()), targetType.getWidth(), targetType.getHeight());
						}

						dds = ddsCompressor.compressImage(targetData, attrs);
						end = new Date();

						return null;
					}
				}
				
				appendComment("processing", new Object[] {originalImage.getName()});

				ImageWorker worker = new ImageWorker(attrs);
				worker.doWork(null);
				while (!worker.isDone()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {}
				}

				appendComment("converted", new Object[] {originalImage.getName(), ddsName, worker.getElapsed()});
				
				ImageDialogs.saveImage(owner, new ImageData(ddsName, null, ".dds", worker.getDds()));
			} catch (IOException ex) {
				clearComments();
				appendComment(ex.getLocalizedMessage());
			}
		}
	}
}
