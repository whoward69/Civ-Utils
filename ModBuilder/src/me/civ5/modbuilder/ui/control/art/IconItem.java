package me.civ5.modbuilder.ui.control.art;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import me.civ5.dds.DdsRGBA8File;
import me.civ5.modbuilder.ui.control.ComplexControl;
import me.civ5.modutils.log.LogInfo;
import me.civ5.modutils.log.ModReporter;
import me.civ5.modutils.utils.ModUtilsImages;
import me.civ5.ui.filter.IconFilter;
import me.civ5.xml.XmlHelper;
import me.civ5.xpath.XpathHelper;

import org.jdom.Element;

public abstract class IconItem extends ComplexControl implements ActionListener {
	protected static File lastDir = null;
	private File iconFile = null;
	
	private int size;
	private int diameter;
	
	public IconItem(String id, int size, int diameter) {
		super(id, new JButton("Choose image"), "notag");
		this.size = size;
		this.diameter = diameter;

		JButton c = (JButton) getControl();
		c.addActionListener(this);
	}
	
	@Override
	public String getText() {
		return (iconFile == null) ? "" : iconFile.getAbsolutePath();
	}

	@Override
	public boolean isDefault() {
		return false;
	}
	
	@Override
	public void setLanguage(Element parentLanguage) {
		super.setLanguage(parentLanguage);
		
		if (iconFile == null) {
			((JButton) getControl()).setText(XpathHelper.getString(language, "ancestor::language/ui/icons/text"));
		} else {
			((JButton) getControl()).setToolTipText(XpathHelper.getString(language, "ancestor::language/ui/icons/tip"));
		}
	}

	@Override
	protected void serialiseImpl(Element me) {
		if (lastDir != null) {
			XmlHelper.newTextElement(me, "lastDir", lastDir.getAbsolutePath());
		}
		
		if (iconFile != null) {
			XmlHelper.newTextElement(me, "iconFile", iconFile.getAbsolutePath());
		}
	}

	@Override
	protected void deserialiseImpl(Element me) {
		String dir = XpathHelper.getString(me, "./lastDir");
		if (dir != null) {
			lastDir = new File(dir);
		}
		
		String file = XpathHelper.getString(me, "./iconFile");
		if (file != null) {
			iconFile = new File(file);
			updateButton();
		}
	}

	protected boolean canSave() {
		return (iconFile != null && getControl().isEnabled());
	}

	protected void saveIcon(int size, int diameter, File file, ModReporter reporter) throws IOException {
		if (canSave()) {
			if (reporter !=null) {
				reporter.log(new LogInfo("Save to " + file.getName() + " at " + size + " from " + getText()));
			}
	
			DdsRGBA8File ddsFile = new DdsRGBA8File(size, size, diameter, ImageIO.read(iconFile));
			
			OutputStream os = null;
			try {
				os = new FileOutputStream(file);
				os.write(ddsFile.getBytes());
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (Exception e) {}
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser(lastDir);

		chooser.addChoosableFileFilter(new IconFilter());
		chooser.setAcceptAllFileFilterUsed(false);
		
		chooser.setAccessory(new ImagePreview(chooser));

		if (chooser.showDialog(getControl(), XpathHelper.getString(language, "ancestor::language/ui/icons/select")) == JFileChooser.APPROVE_OPTION) {
			iconFile = chooser.getSelectedFile();
			lastDir = iconFile.getParentFile();

			updateButton();
		}
	}
	
	protected void updateButton() {
		JButton c = (JButton) getControl();
		c.setText(null);
		c.setToolTipText(XpathHelper.getString(language, "ancestor::language/ui/icons/tip"));

		ImageIcon icon;
		try {
			BufferedImage image = ImageIO.read(iconFile);
			if (diameter > 0) {
				image = ModUtilsImages.getScaledImage(image, diameter, diameter);
				image = ModUtilsImages.getClippedImage(image, diameter);
			} else {
				image = ModUtilsImages.getScaledImage(image, size, size);
				
			}
			
			icon = new ImageIcon(image);
		} catch (IOException e) {
			icon = new ImageIcon(iconFile.getAbsolutePath());
		}

		c.setIcon(icon);
		c.setMargin(new Insets(0, 0, 0, 0));
		c.setBorderPainted(false);
		c.setContentAreaFilled(false);
		c.setIconTextGap(0);
	}

	public class ImagePreview extends JPanel implements PropertyChangeListener {
		JLabel thumbnail = new JLabel();
		JLabel caption = new JLabel("");
		
		File file = null;

		public ImagePreview(JFileChooser fc) {
			super(new BorderLayout());
			
			add(thumbnail, BorderLayout.CENTER);
			add(caption, BorderLayout.SOUTH);
			
			setPreferredSize(new Dimension(100, 100));
			fc.addPropertyChangeListener(this);
		}

		public void loadImage() {
			if (file == null) {
				thumbnail.setIcon(null);
				caption.setText("");
				return;
			}

			ImageIcon tmpIcon = new ImageIcon(file.getPath());
			if (tmpIcon != null) {
				if (tmpIcon.getIconWidth() > 90) {
					thumbnail.setIcon(new ImageIcon(tmpIcon.getImage().getScaledInstance(90, -1, Image.SCALE_DEFAULT)));
				} else {
					thumbnail.setIcon(tmpIcon);
				}
				
				caption.setText("" + tmpIcon.getIconWidth() + "x" + tmpIcon.getIconHeight());
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent e) {
			boolean update = false;
			String prop = e.getPropertyName();

			if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
				file = null;
				update = true;
			} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
				file = (File) e.getNewValue();
				update = true;
			}

			// Update the preview accordingly.
			if (update) {
				thumbnail.setIcon(null);
				caption.setText("");
				
				if (isShowing()) {
					loadImage();
				}
			}
		}
	}
}