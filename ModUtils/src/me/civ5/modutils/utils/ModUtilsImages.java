package me.civ5.modutils.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class ModUtilsImages {
	public static BufferedImage getBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage) image;
	    }

	    BufferedImage ret = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2 = ret.createGraphics();
	    g2.drawImage(image, 0, 0, null);
	    g2.dispose();

	    return ret;
	}
	
	/*
	 * See https://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
	 *
	 * Convenience method that returns a scaled instance of the provided BufferedImage.
	 */
	public static BufferedImage getScaledImage(BufferedImage image, int targetWidth, int targetHeight) {
		BufferedImage ret = image;
		
		int width = image.getWidth();
		int	height = image.getHeight();
	        
		do {
			if (width > targetWidth) {
				width /= 2;
				if (width < targetWidth) {
					width = targetWidth;
				}
			} else {
				width = targetWidth;
			}

			if (height > targetHeight) {
				height /= 2;
				if (height < targetHeight) {
					height = targetHeight;
				}
			} else {
				height = targetHeight;
			}

			BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(ret, 0, 0, width, height, null);
			g2.dispose();

			ret = tmp;
		} while (width != targetWidth && height != targetHeight);

		return ret;
	}

	public static BufferedImage getCenteredImage(BufferedImage image, int targetWidth, int targetHeight) {
		BufferedImage ret = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		
		int width = image.getWidth();
		int height = image.getHeight();

		Graphics2D g2 = ret.createGraphics();
		g2.setColor(new Color(0, true));
		g2.fillRect(0,  0, targetWidth, targetHeight);
		g2.drawImage(image, (targetWidth-width)/2, (targetHeight-height)/2, width, height, null);
		g2.dispose();

		return ret;
	}
	
	public static BufferedImage getOffsetImage(BufferedImage image, int offsetX, int offsetY, int targetWidth, int targetHeight) {
		BufferedImage ret = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		
		int width = image.getWidth();
		int height = image.getHeight();

		Graphics2D g2 = ret.createGraphics();
		g2.setColor(new Color(0, true));
		g2.fillRect(0,  0, targetWidth, targetHeight);
		g2.drawImage(image, -offsetX, -offsetY, width, height, null);
		g2.dispose();

		return ret;
	}
	
	public static BufferedImage getInsetImage(BufferedImage image, BufferedImage insetImage, int insetX, int insetY) {
		BufferedImage ret = image;
		
		Graphics2D g2 = ret.createGraphics();
		g2.drawImage(insetImage, insetX, insetY, insetImage.getWidth(), insetImage.getHeight(), null);
		g2.dispose();

		return ret;
	}
	
	public static BufferedImage getClippedImage(BufferedImage image, int diameter) {
		int width = image.getWidth();
		int	height = image.getHeight();
	        
		Ellipse2D porthole = new Ellipse2D.Float((width-diameter)/2, (height-diameter)/2, diameter, diameter);
		BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = ret.createGraphics();
		g2.setColor(new Color(0, true));
		g2.fillRect(0,  0, width, height);
		g2.setClip(porthole);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(image, 0, 0, width, height, null);
		g2.dispose();

		return ret;
	}

	public static BufferedImage getColouredImage(int width, int height, Color colour) {
		BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = ret.createGraphics();
		g2.setColor(colour);
		g2.fillRect(0,  0, width, height);
		g2.dispose();

		return ret;
	}
	
	public static BufferedImage getIconImage(BufferedImage image, int width, int height, int diameter) {
		BufferedImage ret = image;
		
		if (diameter > 0) {
			// Through the round window
			ret = ModUtilsImages.getScaledImage(ret, diameter, diameter);
			ret = ModUtilsImages.getCenteredImage(ret, width, height);
			ret = ModUtilsImages.getClippedImage(ret, diameter);
		} else if (diameter == 0) {
			// Through the square window
			ret = ModUtilsImages.getScaledImage(ret, width, height);
		} else {
			// Through the font icon window
			ret = ModUtilsImages.getScaledImage(ret, -diameter, -diameter);
			ret = ModUtilsImages.getCenteredImage(ret, width, height);
		}
		
		return ret;
	}
}
