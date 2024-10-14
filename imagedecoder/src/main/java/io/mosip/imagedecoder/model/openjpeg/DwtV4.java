package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DwtV4 {
	private V4[] wavelet;
	private int dn;
	private int sn;
	private int cas;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DwtV4))
			return false;
		DwtV4 that = (DwtV4) obj;
		return dn == that.dn && sn == that.sn && cas == that.cas && canEqual(that)
				&& Arrays.equals(wavelet, that.wavelet);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(wavelet);
		result = 31 * result + dn;
		result = 31 * result + sn;
		result = 31 * result + cas;
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof DwtV4;
	}
}