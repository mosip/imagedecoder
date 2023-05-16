package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Marker structure
 */
public class MarkerInfo {
	/** marker type */
	private int type;
	/** position in codestream */
	private int position;
	/** length, marker val included */
	private int length;
}
