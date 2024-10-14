package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * pi iterator
 */
@Data
@ToString
public class PiIterator {
	/** Enabling Tile part generation */
	private int tilePartOn;
	/**
	 * precise if the packet has been already used (usefull for progression order
	 * change)
	 */
	private int[] include;
	/** layer step used to localize the packet in the include vector */
	private int stepL;
	/** resolution step used to localize the packet in the include vector */
	private int stepR;
	/** component step used to localize the packet in the include vector */
	private int stepC;
	/** precinct step used to localize the packet in the include vector */
	private int stepP;
	/** component that identify the packet */
	private int compNo;
	/** resolution that identify the packet */
	private int resNo;
	/** precinct that identify the packet */
	private int precNo;
	/** layer that identify the packet */
	private int layNo;
	/** 0 if the first packet */
	private int first;
	/** progression order change information */
	private Poc poc;
	/** number of components in the image */
	private int noOfComps;
	/** Components */
	private PiComponent[] comps;
	private int tX0;
	private int tY0;
	private int tX1;
	private int tY1;
	private int x;
	private int y;
	private int dX;
	private int dY;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PiIterator))
			return false;
		PiIterator that = (PiIterator) obj;
		return canEqual(that) && tilePartOn == that.tilePartOn && stepL == that.stepL && stepR == that.stepR
				&& stepC == that.stepC && stepP == that.stepP && compNo == that.compNo && resNo == that.resNo
				&& precNo == that.precNo && layNo == that.layNo && first == that.first && noOfComps == that.noOfComps
				&& tX0 == that.tX0 && tY0 == that.tY0 && tX1 == that.tX1 && tY1 == that.tY1 && x == that.x
				&& y == that.y && dX == that.dX && dY == that.dY && Arrays.equals(include, that.include)
				&& Objects.equals(poc, that.poc) && Arrays.equals(comps, that.comps);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(tilePartOn, stepL, stepR, stepC, stepP, compNo, resNo, precNo, layNo, first, poc,
				noOfComps, tX0, tY0, tX1, tY1, x, y, dX, dY);
		result = 31 * result + Arrays.hashCode(include);
		result = 31 * result + Arrays.hashCode(comps);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof PiIterator;
	}
}