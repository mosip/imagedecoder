package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * J2k codestream reader/writer
 */
@Data
@ToString
public class J2K {
	/** codec context */
	private CodecContextInfo codecContextInfo;

	/**
	 * locate in which part of the codestream the decoder is (main header, tile
	 * header, end)
	 */
	private int state;
	/** number of the tile curently concern by coding/decoding */
	private int curTileNo;
	/** Tile part number */
	private int tilePartNo;
	/** Tilepart number currently coding */
	private int curTilePartNo;
	/** Total number of tileparts of the current tile */
	private int[] curTotalNoOfTilePart;
	/**
	 * locate the start position of the TLM marker after encoding the tilepart, a
	 * jump (in j2kWriteSod) is done to the TLM marker to store the value of its
	 * length.
	 */
	private int tlmStart;
	/**
	 * Total num of tile parts in whole image = num tiles* num tileparts in each
	 * tile
	 */
	/** used in TLMmarker */
	private int totalNoOfTilePart;
	/**
	 * locate the position of the end of the tile in the codestream, used to detect
	 * a truncated codestream (in j2kReadSod)
	 */
	private int eot;
	/**
	 * locate the start position of the SOT marker of the current coded tile: after
	 * encoding the tile, a jump (in j2kWriteSod) is done to the SOT marker to store
	 * the value of its length.
	 */
	private int sotStart;
	private int sodStart;
	/**
	 * as the J2K-file is written in several parts during encoding, it enables to
	 * make the right correction in position return by cioTell
	 */
	private int posCorrection;
	/** array used to store the data of each tile */
	private byte[][] tileData;
	/** array used to store the length of each tile */
	private int[] tileLength;
	/**
	 * decompression only : store decoding parameters common to all tiles
	 * (information like COD, COC in main header)
	 */
	private Tcp defaultTcp;
	/** pointer to the encoded / decoded image */
	private OpenJpegImage image;
	/** pointer to the coding parameters */
	private CodingParameters codingParameters;
	/** helper used to write the index file */
	private CodeStreamInfo codeStreamInfo;
	/** pointer to the byte i/o stream */
	private Cio cio;

	/** used in endcode */
	private int endCode;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof J2K))
			return false;
		J2K that = (J2K) obj;
		return state == that.state && curTileNo == that.curTileNo && tilePartNo == that.tilePartNo
				&& curTilePartNo == that.curTilePartNo && tlmStart == that.tlmStart
				&& totalNoOfTilePart == that.totalNoOfTilePart && eot == that.eot && sotStart == that.sotStart
				&& sodStart == that.sodStart && posCorrection == that.posCorrection && endCode == that.endCode
				&& Arrays.equals(curTotalNoOfTilePart, that.curTotalNoOfTilePart)
				&& Arrays.equals(tileData, that.tileData) && Arrays.equals(tileLength, that.tileLength)
				&& canEqual(that) && Objects.equals(codecContextInfo, that.codecContextInfo)
				&& Objects.equals(defaultTcp, that.defaultTcp) && Objects.equals(image, that.image)
				&& Objects.equals(codingParameters, that.codingParameters)
				&& Objects.equals(codeStreamInfo, that.codeStreamInfo) && Objects.equals(cio, that.cio);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(codecContextInfo, state, curTileNo, tilePartNo, curTilePartNo, tlmStart,
				totalNoOfTilePart, eot, sotStart, sodStart, posCorrection, defaultTcp, image, codingParameters,
				codeStreamInfo, cio, endCode);
		result = 31 * result + Arrays.hashCode(curTotalNoOfTilePart);
		result = 31 * result + Arrays.hashCode(tileData);
		result = 31 * result + Arrays.hashCode(tileLength);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof J2K;
	}
}