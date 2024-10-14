package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;

import lombok.Data;
import lombok.ToString;

/**
 * Coding parameters Discrete Wavelet Transform
 */
@Data
@ToString
public class Dwt {
	private int memIndex;
	private int[] mem;
	private int dn;
	private int sn;
	private int cas;
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Dwt)) return false;
        Dwt that = (Dwt) obj;
        return memIndex == that.memIndex &&
               dn == that.dn &&
               sn == that.sn &&
               cas == that.cas &&
               canEqual(that) &&
               Arrays.equals(mem, that.mem);
    }

    @Override
    public int hashCode() {
        int result = memIndex;
        result = 31 * result + Arrays.hashCode(mem);
        result = 31 * result + dn;
        result = 31 * result + sn;
        result = 31 * result + cas;
        return result;
    }

    public boolean canEqual(Object obj) {
        return obj instanceof Dwt;
    }
}