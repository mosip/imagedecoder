package io.mosip.imagedecoder.model;

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
public class DecoderRequestInfo {	
	private byte[] imageData;
	private boolean isBufferedImage;
}