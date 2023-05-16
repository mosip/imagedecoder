package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data

public class DwtV4 {
	private V4[] wavelet;
	private int	dn;
	private int	sn;
	private int	cas;
}