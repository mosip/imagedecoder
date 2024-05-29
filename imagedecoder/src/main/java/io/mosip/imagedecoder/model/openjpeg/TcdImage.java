package io.mosip.imagedecoder.model.openjpeg;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/**
Tile Image
*/
public class TcdImage {
	private int tileWidth;			/* number of tiles in width */
	private int tileHeight;			/* number of tiles in heigth */
	private TcdTile[] tiles;		/* Tiles information */
}