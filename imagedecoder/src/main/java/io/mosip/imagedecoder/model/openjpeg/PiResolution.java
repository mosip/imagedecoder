package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * pi resolution
 */
public class PiResolution {
	private int pDX;
	private int pDY;
	private int pWidth;
	private int pHeight;
}
