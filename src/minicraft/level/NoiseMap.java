package minicraft.level;

import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class NoiseMap {
	private static final Random random = new Random();
	public double[] values;
	private int w, h;

	public NoiseMap(int w, int h, int featureSize) {
		this.w = w;
		this.h = h;

		values = new double[w * h];

		for (int y = 0; y < w; y += featureSize) {
			for (int x = 0; x < w; x += featureSize) {
				setSample(x, y, random.nextFloat() * 2 - 1);
			}
		}

		int stepSize = featureSize;
		double scale = 1.0 / w;
		double scaleMod = 1;
		do {
			int halfStep = stepSize / 2;
			for (int y = 0; y < w; y += stepSize) {
				for (int x = 0; x < w; x += stepSize) {
					double a = sample(x, y);
					double b = sample(x + stepSize, y);
					double c = sample(x, y + stepSize);
					double d = sample(x + stepSize, y + stepSize);

					double e = (a + b + c + d) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale;
					setSample(x + halfStep, y + halfStep, e);
				}
			}
			for (int y = 0; y < w; y += stepSize) {
				for (int x = 0; x < w; x += stepSize) {
					double a = sample(x, y);
					double b = sample(x + stepSize, y);
					double c = sample(x, y + stepSize);
					double d = sample(x + halfStep, y + halfStep);
					double e = sample(x + halfStep, y - halfStep);
					double f = sample(x - halfStep, y + halfStep);

					double H = (a + b + d + e) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5;
					double g = (a + c + d + f) / 4.0 + (random.nextFloat() * 2 - 1) * stepSize * scale * 0.5;
					setSample(x + halfStep, y, H);
					setSample(x, y + halfStep, g);
				}
			}
			stepSize /= 2;
			scale *= (scaleMod + 1);
			scaleMod *= 0.8;
		} while (stepSize > 1);
	}

	private double sample(int x, int y) {
		return values[(x & (w - 1)) + (y & (h - 1)) * w];
	}

	private void setSample(int x, int y, double value) {
		values[(x & (w - 1)) + (y & (h - 1)) * w] = value;
	}

	/*
	 * public static void main(String[] args) { int w = 256; int h = 256;
	 * 
	 * NoiseMap noise = new NoiseMap(w, h); BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB); int[] pixels = new int[w * h]; for (int i = 0; i < w * h; i++) { int br = (int)(noise.values[i] * 120 + 128); pixels[i] = br << 16 | br << 8 | br; } img.setRGB(0, 0, w, h, pixels, 0, w);
	 * JOptionPane.showMessageDialog(null, null, "The image", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(img)); }
	 */

	public static void main(String[] args) {
		for (int j = 0; j < 10; j++) {
			int w = 512;
			int h = 512;

			NoiseMap noise1 = new NoiseMap(w, h, 32);
			NoiseMap noise2 = new NoiseMap(w, h, 16);
			
			BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			int[] pixels = new int[w * h];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					int i = x + y * w;

					double val = Math.abs(noise1.values[i] - noise2.values[i]) * 2 - 1;

					double xd = x / (w - 1.0) * 2 - 1;
					double yd = y / (h - 1.0) * 2 - 1;
					val = val + 1 - (xd * xd + yd * yd) * 4;

					int br = val < 0 ? 0 : 255;
					pixels[i] = br << 16 | br << 8 | br;
					if (val > 1) {
						pixels[i] = 0xff00ff;
					}
				}
			}
			img.setRGB(0, 0, w, h, pixels, 0, w);
			JOptionPane.showMessageDialog(null, null, "Another", JOptionPane.YES_NO_OPTION, new ImageIcon(img));
		}
	}
}
