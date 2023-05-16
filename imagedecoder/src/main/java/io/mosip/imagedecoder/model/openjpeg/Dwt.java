package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
 * Coding parameters Discrete Wavelet Transform
 */
public class Dwt {
	private int memIndex;
	private int[] mem;
	private int dn;
	private int sn;
	private int cas;
}