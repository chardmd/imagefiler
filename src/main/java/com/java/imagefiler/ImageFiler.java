package com.java.imagefiler;

import ij.ImagePlus;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageFiler implements IImageFiler {
	
	/**
	 * Will transform the background of an image into transparent. It will create a new file with prefix "transparent-[filename]".
	 * Newly created image will be in PNG format - since it supports transperancy
	 * 
	 * @param imagePlus - Actual image source
	 * @param bgHexColorList - List of colors that will be turned into transparent.
	 * @param targetFilePath - Directory of the newly created image.
	 */
	public void makeBackgroundTransparent(ImagePlus imagePlus, String targetFilePath) throws IOException {

		final String PREFIX = "transparent-";
		
		BufferedImage source = imagePlus.getBufferedImage();
		int rgbSource = source.getRGB(0, 0);
		Color color = new Color(rgbSource);

		Image imageWithTransparency = makeColorTransparent(source, color);
		
		BufferedImage transparentImage = imageToBufferedImage(imageWithTransparency);
		try {
			String imagePath = targetFilePath + File.separator + PREFIX + imagePlus.getTitle();
			File output = new File(imagePath);
			ImageIO.write(transparentImage, "PNG", output);

		} catch (IOException ex) {
			throw ex;
		}
		
	}

	private BufferedImage imageToBufferedImage(Image image) {
		
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), 
																								image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();

		return bufferedImage;
	}

	/**
	 * Make provided image transparent wherever color matches the provided
	 * match color.
	 */
	private Image makeColorTransparent(final BufferedImage im, final Color color) {
		
		final ArrayList<String> rgbWhiteList = getRGBWhiteList();
		
		ImageFilter filter = new RGBImageFilter() {

			public int filterRGB(int x, int y, int rgb) {
				
				Color c = new Color(rgb);
				int red  = c.getRed();
				int blue = c.getBlue();
				int green = c.getGreen();
				
				String newRGB = red + "-" + blue + "-" + green;
								
				if (rgbWhiteList.contains(newRGB)) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
					
				} else {
					// nothing to do
					return rgb;
				}
			}
		};

		ImageProducer imageProducer = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(imageProducer);
	}
	
	/**
	 * List of RGB WHITE - Combinations from 250 - 255 (RGB)
	 * @return
	 */
	private ArrayList<String> getRGBWhiteList() {
		ArrayList<String> whiteList = new ArrayList<String>();
		for (int r = 250; r <= 255; r++) {
			for ( int g = 250; g <= 255; g++) {		
				for ( int b = 250; b <= 255; b++) {
					whiteList.add(r + "-" + g + "-" + b);
				}
			}	
		}
		
		return whiteList;
	}
	
}
