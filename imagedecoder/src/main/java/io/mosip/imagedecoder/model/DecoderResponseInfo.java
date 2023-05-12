package io.mosip.imagedecoder.model;

import java.awt.image.BufferedImage;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * The DecoderRequestInfo
 *
 * @author Janardhan B S
 */
@Getter
@Setter
@Data
public class DecoderResponseInfo {	
	// JPEG2000 or WSQ
	private String imageType;
	private String imageWidth;
	private String imageHeight;
	// [0 = lossy, 1 = lossless]
	private String imageLossless;
	// [8 = Gray, 24 = RGB]
	private String imageDepth;
	private String imageDpiHorizontal;
	private String imageDpiVertical;
	private String imageBitRate;
	private String imageSize;
	//base 64 urlencoded image data
	private String imageData;
	private BufferedImage bufferedImage;
	//GRAY or RGB
	private String imageColorSpace;
	private String imageAspectRatio;
	//Lossy should be 15 : 1, for lossless should be 1 : 1
	private String imageCompressionRatio;
}