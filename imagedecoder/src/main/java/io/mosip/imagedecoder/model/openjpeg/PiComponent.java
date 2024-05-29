package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * pi Component
 */
public class PiComponent {
	private int dX;
	private int dY;
	  /** number of resolution levels */
	private int noOfResolutions;
	private PiResolution[] resolutions;
}
