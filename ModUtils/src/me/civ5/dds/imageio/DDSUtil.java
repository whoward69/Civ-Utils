package me.civ5.dds.imageio;

import java.awt.image.BufferedImage;

public class DDSUtil {

	public static void decodeYCoCg(BufferedImage image) {
		final float offset = 0.5f * 256.0f / 255.0f;
		int rgb, r, g, b, alpha;
		float Y, Co, Cg;
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				rgb = image.getRGB(x, y);
				Y = (float) ((rgb >> 24) & 0xff) / 255.0f; // 3 = Alpha
				Co = (float) ((rgb >> 16) & 0xff) / 255.0f; // 0 = Blue
				Cg = (float) ((rgb >> 8) & 0xff) / 255.0f; // 1 = Green
				alpha = (rgb & 0xff); // Blue
				/* convert YCoCg to RGB */
				Co -= offset;
				Cg -= offset;

				r = (int) (saturate(Y + Co - Cg) * 255.0f);
				g = (int) (saturate(Y + Cg) * 255.0f);
				b = (int) (saturate(Y - Co - Cg) * 255.0f);

				rgb = (alpha << 24) | (r << 16) | (g << 8) | (b << 0);
				image.setRGB(x, y, rgb);
			}
		}
	}

	public static void decodeYCoCgScaled(BufferedImage image) {
		final float offset = 0.5f * 256.0f / 255.0f;
		int rgb, r, g, b;
		float Y, Co, Cg, s;
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				rgb = image.getRGB(x, y);
				Y = (float) ((rgb >> 24) & 0xff) / 255.0f; // Alpha
				Co = (float) ((rgb >> 16) & 0xff) / 255.0f; // Read
				Cg = (float) ((rgb >> 8) & 0xff) / 255.0f; // Green
				s = (float) (rgb & 0xff) / 255.0f; // Blue

				s = 1.0f / ((255.0f / 8.0f) * s + 1.0f);

				/* convert YCoCg to RGB */
				Co = (Co - offset) * s;
				Cg = (Cg - offset) * s;

				r = (int) (saturate(Y + Co - Cg) * 255.0f);
				g = (int) (saturate(Y + Cg) * 255.0f);
				b = (int) (saturate(Y - Co - Cg) * 255.0f);

				rgb = (255 << 24) | (r << 16) | (g << 8) | (b << 0);
				image.setRGB(x, y, rgb);
			}
		}
	}

	public static void decodeAlphaExponent(BufferedImage image) {
		int rgb;
		float a, r, g, b;
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				rgb = image.getRGB(x, y);
				a = ((rgb >> 24) & 0xff) / 255.0f;
				r = ((rgb >> 16) & 0xff);
				g = ((rgb >> 8) & 0xff);
				b = (rgb & 0xff);

				r = r * a;
				g = g * a;
				b = b * a;

				r = Math.min(r, 255);
				g = Math.min(g, 255);
				b = Math.min(b, 255);

				rgb = (255 << 24) | ((int) r << 16) | ((int) g << 8) | ((int) b << 0);
				image.setRGB(x, y, rgb);
			}
		}
	}

	public static void showColors(BufferedImage image, boolean alpha, boolean red, boolean green, boolean blue) {
		int a, r, g, b, rgb;
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				rgb = image.getRGB(x, y);

				if (alpha) {
					a = ((rgb >> 24) & 0xff);
				} else {
					a = 255;
				}
				if (red) {
					r = ((rgb >> 16) & 0xff);
				} else {
					r = 0;
				}
				if (green) {
					g = ((rgb >> 8) & 0xff);
				} else {
					g = 0;
				}
				if (blue) {
					b = (rgb & 0xff);
				} else {
					b = 0;
				}

				rgb = (a << 24) | (r << 16) | (g << 8) | (b << 0);
				image.setRGB(x, y, rgb);
			}
		}
	}

	public static float saturate(float a) {
		if (a < 0) {
			a = 0;
		}
		if (a > 1) {
			a = 1;
		}
		return a;
	}
}
