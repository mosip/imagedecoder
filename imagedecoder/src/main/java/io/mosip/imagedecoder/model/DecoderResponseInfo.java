package io.mosip.imagedecoder.model;

import java.awt.image.BufferedImage;
import java.util.Objects;

import lombok.Data;

/**
 * The DecoderRequestInfo
 *
 * @author Janardhan B S
 */
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
	// base 64 urlencoded image data
	private String imageData;
	private BufferedImage bufferedImage;
	// GRAY or RGB
	private String imageColorSpace;
	private String imageAspectRatio;
	// Lossy should be 15 : 1, for lossless should be 1 : 1
	private String imageCompressionRatio;
	private boolean isAllInfo = true;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DecoderResponseInfo))
			return false;
		DecoderResponseInfo that = (DecoderResponseInfo) o;
		return isAllInfo == that.isAllInfo && Objects.equals(imageType, that.imageType)
				&& Objects.equals(imageWidth, that.imageWidth) && Objects.equals(imageHeight, that.imageHeight)
				&& Objects.equals(imageLossless, that.imageLossless) && Objects.equals(imageDepth, that.imageDepth)
				&& Objects.equals(imageDpiHorizontal, that.imageDpiHorizontal)
				&& Objects.equals(imageDpiVertical, that.imageDpiVertical)
				&& Objects.equals(imageBitRate, that.imageBitRate) && Objects.equals(imageSize, that.imageSize)
				&& Objects.equals(imageData, that.imageData) && Objects.equals(bufferedImage, that.bufferedImage)
				&& Objects.equals(imageColorSpace, that.imageColorSpace)
				&& Objects.equals(imageAspectRatio, that.imageAspectRatio)
				&& Objects.equals(imageCompressionRatio, that.imageCompressionRatio);
	}

	@Override
	public int hashCode() {
		return Objects.hash(imageType, imageWidth, imageHeight, imageLossless, imageDepth, imageDpiHorizontal,
				imageDpiVertical, imageBitRate, imageSize, imageData, bufferedImage, imageColorSpace, imageAspectRatio,
				imageCompressionRatio, isAllInfo);
	}

	public boolean canEqual(Object other) {
		return other instanceof DecoderResponseInfo;
	}
}