package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Progression order changes
 */
@Data
@ToString
public class Poc {
	/** Resolution num start, Component num start, given by POC */
	private int resNo0;
	private int compNo0;
	/** Layer num end,Resolution num end, Component num end, given by POC */
	private int layNo1;
	private int resNo1;
	private int compNo1;
	/** Layer num start,Precinct num start, Precinct num end */
	private int layNo0;
	private int precNo0;
	private int precNo1;
	/** Progression order enum */
	private ProgressionOrder progressionOrder;
	private ProgressionOrder progressionOrder1;
	/** Progression order string */
	private char[] progressionName = new char[5];
	/** Tile number */
	private int tile;
	/** Start and end values for Tile width and height */
	private int tX0;
	private int tX1;
	private int tY0;
	private int tY1;
	/** Start value, initialised in piInitEncode */
	private int layS;
	private int resS;
	private int compS;
	private int prcS;
	/** End value, initialised in piInitEncode */
	private int layE;
	private int resE;
	private int compE;
	private int prcE;
	/**
	 * Start and end values of Tile width and height, initialised in piInitEncode
	 */
	private int tXS;
	private int tXE;
	private int tYS;
	private int tYE;
	private int dX;
	private int dY;
	/** Temporary values for Tile parts, initialised in piCreateEncode */
	private int layTmp;
	private int resTmp;
	private int compTmp;
	private int prcTmp;
	private int tx0Tmp;
	private int ty0Tmp;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Poc))
			return false;
		Poc that = (Poc) obj;
		return canEqual(that) && resNo0 == that.resNo0 && compNo0 == that.compNo0 && layNo1 == that.layNo1
				&& resNo1 == that.resNo1 && compNo1 == that.compNo1 && layNo0 == that.layNo0 && precNo0 == that.precNo0
				&& precNo1 == that.precNo1 && tile == that.tile && tX0 == that.tX0 && tX1 == that.tX1 && tY0 == that.tY0
				&& tY1 == that.tY1 && layS == that.layS && resS == that.resS && compS == that.compS && prcS == that.prcS
				&& layE == that.layE && resE == that.resE && compE == that.compE && prcE == that.prcE && tXS == that.tXS
				&& tXE == that.tXE && tYS == that.tYS && tYE == that.tYE && dX == that.dX && dY == that.dY
				&& layTmp == that.layTmp && resTmp == that.resTmp && compTmp == that.compTmp && prcTmp == that.prcTmp
				&& tx0Tmp == that.tx0Tmp && ty0Tmp == that.ty0Tmp
				&& Objects.equals(progressionOrder, that.progressionOrder)
				&& Objects.equals(progressionOrder1, that.progressionOrder1)
				&& Arrays.equals(progressionName, that.progressionName);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(resNo0, compNo0, layNo1, resNo1, compNo1, layNo0, precNo0, precNo1, progressionOrder,
				progressionOrder1, tile, tX0, tX1, tY0, tY1, layS, resS, compS, prcS, layE, resE, compE, prcE, tXS, tXE,
				tYS, tYE, dX, dY, layTmp, resTmp, compTmp, prcTmp, tx0Tmp, ty0Tmp);
		result = 31 * result + Arrays.hashCode(progressionName);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof Poc;
	}
}