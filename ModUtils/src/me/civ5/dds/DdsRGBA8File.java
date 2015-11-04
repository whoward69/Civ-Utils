package me.civ5.dds;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import me.civ5.dds.structures.DdsRGBA8Header;
import me.civ5.modutils.utils.ModUtilsImages;


public class DdsRGBA8File {
	private int width;
	private int height;

	private DdsRGBA8Header header;
	private byte[] data;

	public DdsRGBA8File(int width, int height, int diameter, BufferedImage image) {
		this.width = width;
		this.height = height;

		header = new DdsRGBA8Header(width, height);
		data = new byte[width * height * 4];
		
		processImage(ModUtilsImages.getIconImage(image, width, height, diameter));
	}
	
	public byte[] getBytes() {
		byte[] b = new byte[DdsRGBA8Header.SIZE + data.length];
		
		int offset = 0;
		offset = header.getBytes(b, offset);
		
		for (int i = 0; i < data.length; ++i) {
			b[offset++] = data[i];
		}
		
		assert (offset == b.length) : "DDS RGBA8 file is incorrect size";
		return b;
	}
	

	private void processImage(BufferedImage image) {
		ColorModel cm = image.getColorModel();
		
		if (cm instanceof DirectColorModel) {
			processImageImpl(image, (DirectColorModel) cm);
		} else if (cm instanceof ComponentColorModel) {
			processImageImpl(image, (ComponentColorModel) cm);
		} else {
			System.err.println("Unsupported ColorModel: " + cm.getClass().getName());
		}
	}
	
	private void processImageImpl(BufferedImage image, ComponentColorModel cm) {
		int offset = 0;
		
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				int[] pixel = image.getData().getPixel(x, y, (int[]) null);

				data[offset+3] = (byte) (cm.getAlpha(pixel[0]) & 0xff);
				data[offset+2] = (byte) (cm.getRed(pixel[0]) & 0xff);
				data[offset+1] = (byte) (cm.getGreen(pixel[0]) & 0xff);
				data[offset+0] = (byte) (cm.getBlue(pixel[0]) & 0xff);
				
				offset = offset + 4;
			}
		}
	}
	
	private void processImageImpl(BufferedImage image, DirectColorModel cm) {
		int offset = 0;

		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				try {
					int[] pixel = image.getData().getPixel(x, y, (int[]) null);

					data[offset+0] = (byte) (pixel[2] & 0xff);
					data[offset+1] = (byte) (pixel[1] & 0xff);
					data[offset+2] = (byte) (pixel[0] & 0xff);
					data[offset+3] = (byte) (pixel[3] & 0xff);
				} catch (Exception e) {
					System.err.println("Can't read pixel at (" + x + ", " + y + ")");
				}
				
				offset = offset + 4;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		BufferedImage pngImage = ImageIO.read(new File("C:/Users/William/Desktop/RGBA.png"));
		DdsRGBA8File ddsFile = new DdsRGBA8File(pngImage.getWidth(), pngImage.getHeight(), 60, pngImage);
		
		OutputStream os = new FileOutputStream("c:/temp/rgba8.dds");
		os.write(ddsFile.getBytes());
		os.close();
	}
}
