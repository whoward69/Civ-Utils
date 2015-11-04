package me.civ5.modddsconverter.ui.view.dialog;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.JFileChooser;

import me.civ5.image.ModImage;
import me.civ5.modddsconverter.ui.model.image.ImageData;
import me.civ5.modddsconverter.ui.panel.AbstractPanel;
import me.civ5.modutils.utils.ModUtilsSession;
import me.civ5.ui.filter.DdsFilter;
import me.civ5.ui.filter.ImagesFilter;
import me.civ5.ui.filter.ModFileFilter;

public class ImageDialogs {
	private static FileOpenService fos = null;
	private static FileSaveService fss = null;

	private static String lastOpenDir = null;
	private static String lastSaveDir = null;
	
	private static ModUtilsSession session;
	
	static {
		try {
			fos = (FileOpenService) ServiceManager.lookup("javax.jnlp.FileOpenService");
			fss = (FileSaveService) ServiceManager.lookup("javax.jnlp.FileSaveService");
		} catch (UnavailableServiceException e) {
			fos = null;
			fss = null;
		}
	}
	
	public static String getLastOpenDir() {
		return lastOpenDir;
	}

	public static void setLastOpenDir(String lastOpenDir) {
		ImageDialogs.lastOpenDir = lastOpenDir;
	}

	public static String getLastSaveDir() {
		return lastSaveDir;
	}

	public static void setLastSaveDir(String lastSaveDir) {
		ImageDialogs.lastSaveDir = lastSaveDir;
	}

	public static void setSession(ModUtilsSession session) {
		ImageDialogs.session = session;
	}

	public static ModImage chooseImage(Component owner) throws IOException {
		ModImage image = null;
		ModFileFilter imageFilter = new ImagesFilter();
		
		try {
			if (session.isWebStartSession()) {
				// Use Java Web Start
				FileContents inFile = fos.openFileDialog(lastOpenDir, imageFilter.getExtns());
				if (inFile != null) {
					image = new ModImage(inFile);
				}
			} else {
				// Use Java Swing
				JFileChooser chooser = new JFileChooser(lastOpenDir);
				chooser.addChoosableFileFilter(imageFilter);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(imageFilter);

				if (chooser.showOpenDialog(owner) == JFileChooser.APPROVE_OPTION) {
					File inFile = chooser.getSelectedFile();
					lastOpenDir = inFile.getParentFile().getAbsolutePath();

					image = new ModImage(inFile);
				}
			}
			
			if (lastSaveDir == null) {
				lastSaveDir = lastOpenDir;
			}
			
			if (!session.isWebStartSession()) {
				session.autoSaveSession();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			image = null;
		}

		return image;
	}
	
	public static void saveImage(AbstractPanel owner, List<ImageData> images) throws IOException {
		ModFileFilter ddsFilter = new DdsFilter();

		try {
			if (session.isWebStartSession()) {
				// Use Java Web Start
				for (ImageData image : images) {
					saveImage(owner, image);
				}
			} else {
				// Use Java Swing
				JFileChooser chooser = new JFileChooser(lastSaveDir);
				chooser.addChoosableFileFilter(ddsFilter);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(ddsFilter);
				
				chooser.setSelectedFile(new File(images.get(0).getName()));

				if (chooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
					File baseFile = chooser.getSelectedFile();
					lastSaveDir = baseFile.getParentFile().getAbsolutePath();

					String baseName = baseFile.getName();
					int dot = baseName.lastIndexOf('.');
					if (dot != -1) {
						baseName = baseName.substring(0, dot);
					}
					
					for (ImageData image : images) {
						if (baseName.endsWith(image.getQualifier())) {
							baseName = baseName.substring(0, baseName.length() - image.getQualifier().length());
							break;
						}
					}

					for (ImageData image : images) {
						File imageFile = new File(lastSaveDir, image.makeName(baseName));
						owner.appendComment("writing", new Object[] {imageFile.getAbsolutePath()});
						writeImageData(new FileOutputStream(imageFile), image.getData());
					}
					
					session.autoSaveSession();
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void saveImage(Component owner, ImageData image) throws IOException {
		ModFileFilter ddsFilter = new DdsFilter();

		try {
			if (session.isWebStartSession()) {
				// Use Java Web Start
				ByteArrayOutputStream boas = new ByteArrayOutputStream();
				writeImageData(boas, image.getData());
				
				fss.saveFileDialog(lastSaveDir, ddsFilter.getExtns(), new ByteArrayInputStream(boas.toByteArray()), image.getName());
			} else {
				// Use Java Swing
				JFileChooser chooser = new JFileChooser(lastSaveDir);
				chooser.addChoosableFileFilter(ddsFilter);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(ddsFilter);
				
				chooser.setSelectedFile(new File(image.getName()));

				if (chooser.showSaveDialog(owner) == JFileChooser.APPROVE_OPTION) {
					File outFile = chooser.getSelectedFile();
					lastSaveDir = outFile.getParentFile().getAbsolutePath();

					writeImageData(new FileOutputStream(outFile), image.getData());

					session.autoSaveSession();
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	private static void writeImageData(OutputStream os, ByteBuffer imageData) throws IOException {
		try {
			if (imageData.hasArray()) {
				os.write(imageData.array());
			} else {
				for (int i = 0; i < imageData.limit(); ++i) {
					os.write(imageData.get(i));
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			os.close();
		}
	}
}
