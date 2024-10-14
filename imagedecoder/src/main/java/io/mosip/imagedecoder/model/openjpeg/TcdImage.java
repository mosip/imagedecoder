package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

/**
 * Tile Image
 */
@Data
@ToString
public class TcdImage {
	private int tileWidth; /* number of tiles in width */
	private int tileHeight; /* number of tiles in heigth */
	private TcdTile[] tiles; /* Tiles information */

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TcdImage tcdImage = (TcdImage) o;
		return tileWidth == tcdImage.tileWidth && tileHeight == tcdImage.tileHeight
				&& Arrays.equals(tiles, tcdImage.tiles);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(tileWidth, tileHeight);
		result = 31 * result + Arrays.hashCode(tiles);
		return result;
	}

	public boolean canEqual(Object other) {
		return other instanceof TcdImage;
	}
}