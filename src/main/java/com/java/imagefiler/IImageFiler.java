package com.java.imagefiler;

import ij.ImagePlus;

import java.io.IOException;

public interface IImageFiler {

	void makeBackgroundTransparent(ImagePlus imagePlus, String targetPath) throws IOException;
}
