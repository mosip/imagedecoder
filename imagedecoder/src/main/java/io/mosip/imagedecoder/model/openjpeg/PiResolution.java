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
	private int pDX, pDY;
	private int pWidth, pHeight;
}
