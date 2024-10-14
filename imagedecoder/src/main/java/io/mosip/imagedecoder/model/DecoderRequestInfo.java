package io.mosip.imagedecoder.model;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;

/**
 * The DecoderRequestInfo
 *
 * @author Janardhan B S
 */
@Data
public class DecoderRequestInfo {
	private byte[] imageData;
	private boolean isBufferedImage;
	private boolean isAllInfo = true;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof DecoderRequestInfo))
			return false;
		DecoderRequestInfo that = (DecoderRequestInfo) o;
		return isBufferedImage == that.isBufferedImage && isAllInfo == that.isAllInfo
				&& Arrays.equals(imageData, that.imageData); // Arrays.equals for array comparison
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(isBufferedImage, isAllInfo);
		result = 31 * result + Arrays.hashCode(imageData); // Arrays.hashCode for array
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof DecoderRequestInfo;
	}
}