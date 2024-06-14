package io.mosip.imagedecoder.openjpeg;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.text.MessageFormat;
import java.util.Arrays;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.CodeStreamInfo;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CodingParameters;
import io.mosip.imagedecoder.model.openjpeg.J2KT2Mode;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponent;
import io.mosip.imagedecoder.model.openjpeg.PacketInfo;
import io.mosip.imagedecoder.model.openjpeg.StepSize;
import io.mosip.imagedecoder.model.openjpeg.Tcd;
import io.mosip.imagedecoder.model.openjpeg.TcdBand;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockDecoder;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockEncoder;
import io.mosip.imagedecoder.model.openjpeg.TcdImage;
import io.mosip.imagedecoder.model.openjpeg.TcdLayer;
import io.mosip.imagedecoder.model.openjpeg.TcdPass;
import io.mosip.imagedecoder.model.openjpeg.TcdPrecinct;
import io.mosip.imagedecoder.model.openjpeg.TcdResolution;
import io.mosip.imagedecoder.model.openjpeg.TcdTile;
import io.mosip.imagedecoder.model.openjpeg.TcdTileComponent;
import io.mosip.imagedecoder.model.openjpeg.Tcp;
import io.mosip.imagedecoder.model.openjpeg.Tier1;
import io.mosip.imagedecoder.model.openjpeg.Tier2;
import io.mosip.imagedecoder.model.openjpeg.TileComponentCodingParameters;
import io.mosip.imagedecoder.model.openjpeg.TileInfo;
import io.mosip.imagedecoder.util.openjpeg.MathUtil;

public class TcdHelper {
	private Logger logger = ImageDecoderLogger.getLogger(TcdHelper.class);
	// Static variable reference of singleInstance of type Singleton
	private static TcdHelper singleInstance = null;

	private TcdHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized TcdHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new TcdHelper();

		return singleInstance;
	}

	/**
	 * Create a new TCD handle
	 */
	public Tcd tcdCreate(CodecContextInfo codecContextInfo) {
		/* create the tcd structure */
		Tcd tcd = new Tcd();
		tcd.setCodecContextInfo(codecContextInfo);
		tcd.setTcdImage(new TcdImage());
		return tcd;
	}

	/**
	 * Destroy a previously created TCD handle
	 */
	public void tcdDestroy(Tcd tcd) {
		if (tcd != null) {
			tcd.setTcdImage(null);
		}
	}

	/* ----------------------------------------------------------------------- */
	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S2184", "java:S3776", "java:S3358", "java:S6541", "unused" })
	public void tcdMallocEncode(Tcd tcd, OpenJpegImage image, CodingParameters codingParameters, int currentTileNo) {
		int tileNo, compNo, resNo, bandNo, precNo, codeBlockNo;

		tcd.setImage(image);
		tcd.setCodingParameters(codingParameters);
		tcd.getTcdImage().setTileWidth(codingParameters.getTileWidth());
		tcd.getTcdImage().setTileHeight(codingParameters.getTileHeight());
		tcd.getTcdImage().setTiles(new TcdTile[1]);

		for (tileNo = 0; tileNo < 1; tileNo++) {
			Tcp tcp = codingParameters.getTcps()[currentTileNo];
			int j;

			/* cfr p59 ISO/IEC FDIS15444-1 : 2000 (18 august 2000) */
			int p = currentTileNo % codingParameters.getTileWidth(); /* si numerotation matricielle .. */
			int q = currentTileNo / codingParameters
					.getTileWidth(); /* .. coordonnees de la tile (q,p) q pour ligne et p pour colonne */

			TcdTile tile = tcd.getTcdImage().getTiles()[0];

			/* 4 borders of the tile rescale on the image if necessary */
			tile.setX0(MathUtil.getInstance().intMax(codingParameters.getTileX0() + p * codingParameters.getTileDX(),
					image.getX0()));
			tile.setY0(MathUtil.getInstance().intMax(codingParameters.getTileY0() + q * codingParameters.getTileDY(),
					image.getY0()));
			tile.setX1(MathUtil.getInstance()
					.intMin(codingParameters.getTileX0() + (p + 1) * codingParameters.getTileDX(), image.getX1()));
			tile.setY1(MathUtil.getInstance()
					.intMin(codingParameters.getTileY0() + (q + 1) * codingParameters.getTileDY(), image.getY1()));
			tile.setNoOfComps(image.getNoOfComps());

			/* Modification of the RATE >> */
			for (j = 0; j < tcp.getNoOfLayers(); j++) {
				tcp.getRates()[j] = tcp.getRates()[j] != 0 ? codingParameters.getTilePartOn() != 0
						? ((tile.getNoOfComps() * (tile.getX1() - tile.getX0()) * (tile.getY1() - tile.getY0())
								* image.getComps()[0].getPrec())
								/ (tcp.getRates()[j] * 8 * image.getComps()[0].getDX() * image.getComps()[0].getDY()))
								- (((tcd.getCurTotalNoOfTileParts() - 1) * 14) / tcp.getNoOfLayers())
						: (tile.getNoOfComps() * (tile.getX1() - tile.getX0()) * (tile.getY1() - tile.getY0())
								* image.getComps()[0].getPrec())
								/ (tcp.getRates()[j] * 8 * image.getComps()[0].getDX() * image.getComps()[0].getDY())
						: 0;

				if (tcp.getRates()[j] != 0) {
					if (j != 0 && tcp.getRates()[j] < tcp.getRates()[j - 1] + 10) {
						tcp.getRates()[j] = tcp.getRates()[j - 1] + 20;
					} else {
						if (j == 0 && tcp.getRates()[j] < 30)
							tcp.getRates()[j] = 30;
					}

					if (j == (tcp.getNoOfLayers() - 1)) {
						tcp.getRates()[j] = tcp.getRates()[j] - 2;
					}
				}
			}
			/* << Modification of the RATE */

			tile.setComps(new TcdTileComponent[image.getNoOfComps()]);
			for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
				tile.getComps()[compNo] = new TcdTileComponent();
				TileComponentCodingParameters tccp = tcp.getTccps()[compNo];

				TcdTileComponent tilec = tile.getComps()[compNo];

				/* border of each tile component (global) */
				tilec.setX0(MathUtil.getInstance().intCeilDiv(tile.getX0(), image.getComps()[compNo].getDX()));
				tilec.setY0(MathUtil.getInstance().intCeilDiv(tile.getY0(), image.getComps()[compNo].getDY()));
				tilec.setX1(MathUtil.getInstance().intCeilDiv(tile.getX1(), image.getComps()[compNo].getDX()));
				tilec.setY1(MathUtil.getInstance().intCeilDiv(tile.getY1(), image.getComps()[compNo].getDY()));

				tilec.setIData(new int[(tilec.getX1() - tilec.getX0()) * (tilec.getY1() - tilec.getY0())]);
				tilec.setFData(new double[(tilec.getX1() - tilec.getX0()) * (tilec.getY1() - tilec.getY0())]);
				tilec.setNoOfResolutions(tccp.getNoOfResolutions());

				tilec.setResolutions(new TcdResolution[tilec.getNoOfResolutions()]);
				for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
					int pdx, pdy;
					int levelno = tilec.getNoOfResolutions() - 1 - resNo;
					int tlprcxStart, tlprcyStart, brprcxEnd, brprcyEnd;
					int tlcbgxStart, tlcbgyStart, brcbgxEnd, brcbgyEnd;
					int cbgwidthexpn, cbgheightexpn;
					int cblkwidthexpn, cblkheightexpn;

					TcdResolution res = tilec.getResolutions()[resNo];

					/* border for each resolution level (global) */
					res.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0(), levelno));
					res.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0(), levelno));
					res.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1(), levelno));
					res.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1(), levelno));

					res.setNoOfBands(resNo == 0 ? 1 : 3);
					/* p. 35, table A-23, ISO/IEC FDIS154444-1 : 2000 (18 august 2000) */
					if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CCP_CSTY_PRT) != 0) {
						pdx = tccp.getPrecinctWidth()[resNo];
						pdy = tccp.getPrecinctHeight()[resNo];
					} else {
						pdx = 15;
						pdy = 15;
					}
					/* p. 64, B.6, ISO/IEC FDIS15444-1 : 2000 (18 august 2000) */
					tlprcxStart = MathUtil.getInstance().intFloorDivPow2(res.getX0(), pdx) << pdx;
					tlprcyStart = MathUtil.getInstance().intFloorDivPow2(res.getY0(), pdy) << pdy;

					brprcxEnd = MathUtil.getInstance().intCeilDivPow2(res.getX1(), pdx) << pdx;
					brprcyEnd = MathUtil.getInstance().intCeilDivPow2(res.getY1(), pdy) << pdy;

					res.setPWidth((brprcxEnd - tlprcxStart) >> pdx);
					res.setPHeight((brprcyEnd - tlprcyStart) >> pdy);

					if (resNo == 0) {
						tlcbgxStart = tlprcxStart;
						tlcbgyStart = tlprcyStart;
						brcbgxEnd = brprcxEnd;
						brcbgyEnd = brprcyEnd;
						cbgwidthexpn = pdx;
						cbgheightexpn = pdy;
					} else {
						tlcbgxStart = MathUtil.getInstance().intCeilDivPow2(tlprcxStart, 1);
						tlcbgyStart = MathUtil.getInstance().intCeilDivPow2(tlprcyStart, 1);
						brcbgxEnd = MathUtil.getInstance().intCeilDivPow2(brprcxEnd, 1);
						brcbgyEnd = MathUtil.getInstance().intCeilDivPow2(brprcyEnd, 1);
						cbgwidthexpn = pdx - 1;
						cbgheightexpn = pdy - 1;
					}

					cblkwidthexpn = MathUtil.getInstance().intMin(tccp.getCodeBlockWidth(), cbgwidthexpn);
					cblkheightexpn = MathUtil.getInstance().intMin(tccp.getCodeBlockHeight(), cbgheightexpn);

					for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
						int x0b, y0b, i;
						int gain, noOfBps;
						StepSize ss = null;

						TcdBand band = res.getBands()[bandNo];

						band.setBandNo(resNo == 0 ? 0 : bandNo + 1);
						x0b = (band.getBandNo() == 1) || (band.getBandNo() == 3) ? 1 : 0;
						y0b = (band.getBandNo() == 2) || (band.getBandNo() == 3) ? 1 : 0;

						if (band.getBandNo() == 0) {
							/* band border (global) */
							band.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0(), levelno));
							band.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0(), levelno));
							band.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1(), levelno));
							band.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1(), levelno));
						} else {
							/* band border (global) */
							band.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0() - (1 << levelno) * x0b,
									levelno + 1));
							band.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0() - (1 << levelno) * y0b,
									levelno + 1));
							band.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1() - (1 << levelno) * x0b,
									levelno + 1));
							band.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1() - (1 << levelno) * y0b,
									levelno + 1));
						}

						ss = tccp.getStepsizes()[resNo == 0 ? 0 : 3 * (resNo - 1) + bandNo + 1];
						gain = tccp.getQmfbid() == 0 ? DwtHelper.getInstance().dwtGetGainReal(band.getBandNo())
								: DwtHelper.getInstance().dwtGetGain(band.getBandNo());
						noOfBps = image.getComps()[compNo].getPrec() + gain;

						band.setStepSize(
								(float) ((1.0 + ss.getMant() / 2048.0) * Math.pow(2.0, noOfBps - ss.getExpn())));
						band.setNoOfBps(ss.getExpn() + tccp.getNoOfGaurdBits() - 1); /* WHY -1 ? */

						band.setPrecincts(new TcdPrecinct[3 * res.getPWidth() * res.getPHeight()]);
						for (i = 0; i < 3 * res.getPWidth() * res.getPHeight(); i++) {
							band.getPrecincts()[i] = new TcdPrecinct();
							band.getPrecincts()[i].setImsbTree(null);
							band.getPrecincts()[i].setInclTree(null);
						}

						for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); precNo++) {
							int tlcblkxStart, tlcblkyStart, brcblkxEnd, brcblkyEnd;

							int cbgxStart = tlcbgxStart + (precNo % res.getPWidth()) * (1 << cbgwidthexpn);
							int cbgyStart = tlcbgyStart + (precNo / res.getPWidth()) * (1 << cbgheightexpn);
							int cbgxEnd = cbgxStart + (1 << cbgwidthexpn);
							int cbgyEnd = cbgyStart + (1 << cbgheightexpn);

							TcdPrecinct prc = band.getPrecincts()[precNo];

							/* precinct size (global) */
							prc.setX0(MathUtil.getInstance().intMax(cbgxStart, band.getX0()));
							prc.setY0(MathUtil.getInstance().intMax(cbgyStart, band.getY0()));
							prc.setX1(MathUtil.getInstance().intMin(cbgxEnd, band.getX1()));
							prc.setY1(MathUtil.getInstance().intMin(cbgyEnd, band.getY1()));

							tlcblkxStart = MathUtil.getInstance().intFloorDivPow2(prc.getX0(),
									cblkwidthexpn) << cblkwidthexpn;
							tlcblkyStart = MathUtil.getInstance().intFloorDivPow2(prc.getY0(),
									cblkheightexpn) << cblkheightexpn;
							brcblkxEnd = MathUtil.getInstance().intCeilDivPow2(prc.getX1(),
									cblkwidthexpn) << cblkwidthexpn;
							brcblkyEnd = MathUtil.getInstance().intCeilDivPow2(prc.getY1(),
									cblkheightexpn) << cblkheightexpn;
							prc.setCWidth((brcblkxEnd - tlcblkxStart) >> cblkwidthexpn);
							prc.setCHeight((brcblkyEnd - tlcblkyStart) >> cblkheightexpn);

							prc.setTcdCodeBlockEncoder(new TcdCodeBlockEncoder[prc.getCWidth() * prc.getCHeight()]);
							prc.setInclTree(TgtHelper.getInstance().tgtCreate(prc.getCWidth(), prc.getCHeight()));
							prc.setImsbTree(TgtHelper.getInstance().tgtCreate(prc.getCWidth(), prc.getCHeight()));

							for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
								prc.getTcdCodeBlockEncoder()[codeBlockNo] = new TcdCodeBlockEncoder();

								int cblkxStart = tlcblkxStart + (codeBlockNo % prc.getCWidth()) * (1 << cblkwidthexpn);
								int cblkyStart = tlcblkyStart + (codeBlockNo / prc.getCWidth()) * (1 << cblkheightexpn);
								int cblkxEnd = cblkxStart + (1 << cblkwidthexpn);
								int cblkyEnd = cblkyStart + (1 << cblkheightexpn);

								TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];

								/* code-block size (global) */
								cblk.setX0(MathUtil.getInstance().intMax(cblkxStart, prc.getX0()));
								cblk.setY0(MathUtil.getInstance().intMax(cblkyStart, prc.getY0()));
								cblk.setX1(MathUtil.getInstance().intMin(cblkxEnd, prc.getX1()));
								cblk.setY1(MathUtil.getInstance().intMin(cblkyEnd, prc.getY1()));

								cblk.setData(new byte[8192 + 2]);
								/*
								 * FIX ME: mqcInitEncode and mqc_byteout underrun the buffer if we don't do
								 * this. Why?
								 */
								cblk.setDataIndex(cblk.getDataIndex() + 2);
								cblk.setLayers(new TcdLayer[100]);
								cblk.setPasses(new TcdPass[100]);
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "java:S125", "java:S1659", "java:S3776" })
	public void tcdFreeEncode(Tcd tcd) {
		int tileNo, compNo, resNo, bandNo, precNo, codeBlockNo;

		for (tileNo = 0; tileNo < 1; tileNo++) {
			TcdTile tile = tcd.getTcdImage().getTiles()[0];

			for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
				TcdTileComponent tilec = tile.getComps()[compNo];

				for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
					TcdResolution res = tilec.getResolutions()[resNo];

					for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
						TcdBand band = res.getBands()[bandNo];

						for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); precNo++) {
							TcdPrecinct prc = band.getPrecincts()[precNo];

							if (prc.getInclTree() != null) {
								TgtHelper.getInstance().tgtDestroy(prc.getInclTree());
								prc.setInclTree(null);
							}
							if (prc.getInclTree() != null) {
								TgtHelper.getInstance().tgtDestroy(prc.getInclTree());
								prc.setImsbTree(null);
							}
							for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
								prc.getTcdCodeBlockEncoder()[codeBlockNo].setData(null);
								prc.getTcdCodeBlockEncoder()[codeBlockNo].setLayers(null);
								prc.getTcdCodeBlockEncoder()[codeBlockNo].setPasses(null);
							}
							prc.setTcdCodeBlockEncoder(null);
						} /* for (precNo */
						band.setPrecincts(null);
					} /* for (bandNo */
				} /* for (resNo */
				tilec.setResolutions(null);
			} /* for (compNo */
			tile.setComps(null);
		} /* for (tileNo */
		tcd.getTcdImage().setTiles(null);
	}

	@SuppressWarnings({ "java:S1659", "java:1854", "java:S3358", "java:S3776", "java:S6541", "unused" })
	public void tcdInitEncode(Tcd tcd, OpenJpegImage image, CodingParameters codingParameters, int currentTileNo) {
		int tileNo, compNo, resNo, bandNo, precNo, codeBlockNo;

		for (tileNo = 0; tileNo < 1; tileNo++) {
			Tcp tcp = codingParameters.getTcps()[currentTileNo];
			int j;
			/* cfr p59 ISO/IEC FDIS15444-1 : 2000 (18 august 2000) */
			int p = currentTileNo % codingParameters.getTileWidth();
			int q = currentTileNo / codingParameters.getTileWidth();

			TcdTile tile = tcd.getTcdImage().getTiles()[0];

			/* 4 borders of the tile rescale on the image if necessary */
			tile.setX0(MathUtil.getInstance().intMax(codingParameters.getTileX0() + p * codingParameters.getTileDX(),
					image.getX0()));
			tile.setY0(MathUtil.getInstance().intMax(codingParameters.getTileY0() + q * codingParameters.getTileDY(),
					image.getY0()));
			tile.setX1(MathUtil.getInstance()
					.intMin(codingParameters.getTileX0() + (p + 1) * codingParameters.getTileDX(), image.getX1()));
			tile.setY1(MathUtil.getInstance()
					.intMin(codingParameters.getTileY0() + (q + 1) * codingParameters.getTileDY(), image.getY1()));

			tile.setNoOfComps(image.getNoOfComps());

			/* Modification of the RATE >> */
			for (j = 0; j < tcp.getNoOfLayers(); j++) {
				tcp.getRates()[j] = tcp.getRates()[j] != 0 ? codingParameters.getTilePartOn() != 0
						? (tile.getNoOfComps() * (tile.getX1() - tile.getX0()) * (tile.getY1() - tile.getY0())
								* image.getComps()[0].getPrec())
								/ (tcp.getRates()[j] * 8f * image.getComps()[0].getDX() * image.getComps()[0].getDY())
								- (((tcd.getCurTotalNoOfTileParts() - 1) * 14f) / tcp.getNoOfLayers())
						: (tile.getNoOfComps() * (tile.getX1() - tile.getX0()) * (tile.getY1() - tile.getY0())
								* image.getComps()[0].getPrec())
								/ (tcp.getRates()[j] * 8 * image.getComps()[0].getDX() * image.getComps()[0].getDY())
						: 0;

				if (tcp.getRates()[j] != 0) {
					if (j != 0 && tcp.getRates()[j] < tcp.getRates()[j - 1] + 10) {
						tcp.getRates()[j] = tcp.getRates()[j - 1] + 20;
					} else {
						if (j == 0 && tcp.getRates()[j] < 30)
							tcp.getRates()[j] = 30;
					}
				}
			}
			/* << Modification of the RATE */
			for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
				TileComponentCodingParameters tccp = tcp.getTccps()[compNo];

				TcdTileComponent tilec = tile.getComps()[compNo];

				/* border of each tile component (global) */
				tilec.setX0(MathUtil.getInstance().intCeilDiv(tile.getX0(), image.getComps()[compNo].getDX()));
				tilec.setY0(MathUtil.getInstance().intCeilDiv(tile.getY0(), image.getComps()[compNo].getDY()));
				tilec.setX1(MathUtil.getInstance().intCeilDiv(tile.getX1(), image.getComps()[compNo].getDX()));
				tilec.setY1(MathUtil.getInstance().intCeilDiv(tile.getY1(), image.getComps()[compNo].getDY()));

				tilec.setIData(new int[(tilec.getX1() - tilec.getX0()) * (tilec.getY1() - tilec.getY0())]);
				tilec.setFData(new double[(tilec.getX1() - tilec.getX0()) * (tilec.getY1() - tilec.getY0())]);
				tilec.setNoOfResolutions(tccp.getNoOfResolutions());
				for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
					int pdx, pdy;

					int levelno = tilec.getNoOfResolutions() - 1 - resNo;
					int tlprcxStart, tlprcyStart, brprcxEnd, brprcyEnd;
					int tlcbgxStart, tlcbgyStart, brcbgxEnd, brcbgyEnd;
					int cbgwidthexpn, cbgheightexpn;
					int cblkwidthexpn, cblkheightexpn;

					TcdResolution res = tilec.getResolutions()[resNo];

					/* border for each resolution level (global) */
					res.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0(), levelno));
					res.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0(), levelno));
					res.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1(), levelno));
					res.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1(), levelno));
					res.setNoOfBands(resNo == 0 ? 1 : 3);

					/* p. 35, table A-23, ISO/IEC FDIS154444-1 : 2000 (18 august 2000) */
					if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CCP_CSTY_PRT) != 0) {
						pdx = tccp.getPrecinctWidth()[resNo];
						pdy = tccp.getPrecinctHeight()[resNo];
					} else {
						pdx = 15;
						pdy = 15;
					}
					/* p. 64, B.6, ISO/IEC FDIS15444-1 : 2000 (18 august 2000) */
					tlprcxStart = MathUtil.getInstance().intFloorDivPow2(res.getX0(), pdx) << pdx;
					tlprcyStart = MathUtil.getInstance().intFloorDivPow2(res.getY0(), pdy) << pdy;
					brprcxEnd = MathUtil.getInstance().intCeilDivPow2(res.getX1(), pdx) << pdx;
					brprcyEnd = MathUtil.getInstance().intCeilDivPow2(res.getY1(), pdy) << pdy;

					res.setPWidth((brprcxEnd - tlprcxStart) >> pdx);
					res.setPHeight((brprcyEnd - tlprcyStart) >> pdy);

					if (resNo == 0) {
						tlcbgxStart = tlprcxStart;
						tlcbgyStart = tlprcyStart;
						brcbgxEnd = brprcxEnd;
						brcbgyEnd = brprcyEnd;
						cbgwidthexpn = pdx;
						cbgheightexpn = pdy;
					} else {
						tlcbgxStart = MathUtil.getInstance().intCeilDivPow2(tlprcxStart, 1);
						tlcbgyStart = MathUtil.getInstance().intCeilDivPow2(tlprcyStart, 1);
						brcbgxEnd = MathUtil.getInstance().intCeilDivPow2(brprcxEnd, 1);
						brcbgyEnd = MathUtil.getInstance().intCeilDivPow2(brprcyEnd, 1);
						cbgwidthexpn = pdx - 1;
						cbgheightexpn = pdy - 1;
					}

					cblkwidthexpn = MathUtil.getInstance().intMin(tccp.getCodeBlockWidth(), cbgwidthexpn);
					cblkheightexpn = MathUtil.getInstance().intMin(tccp.getCodeBlockHeight(), cbgheightexpn);

					for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
						int x0b, y0b;
						int gain, noOfBps;
						StepSize ss = null;

						TcdBand band = res.getBands()[bandNo];

						band.setBandNo(resNo == 0 ? 0 : bandNo + 1);
						x0b = (band.getBandNo() == 1) || (band.getBandNo() == 3) ? 1 : 0;
						y0b = (band.getBandNo() == 2) || (band.getBandNo() == 3) ? 1 : 0;

						if (band.getBandNo() == 0) {
							/* band border */
							band.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0(), levelno));
							band.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0(), levelno));
							band.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1(), levelno));
							band.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1(), levelno));
						} else {
							band.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0() - (1 << levelno) * x0b,
									levelno + 1));
							band.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0() - (1 << levelno) * y0b,
									levelno + 1));
							band.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1() - (1 << levelno) * x0b,
									levelno + 1));
							band.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1() - (1 << levelno) * y0b,
									levelno + 1));
						}

						ss = tccp.getStepsizes()[resNo == 0 ? 0 : 3 * (resNo - 1) + bandNo + 1];
						gain = tccp.getQmfbid() == 0 ? DwtHelper.getInstance().dwtGetGainReal(band.getBandNo())
								: DwtHelper.getInstance().dwtGetGain(band.getBandNo());
						noOfBps = image.getComps()[compNo].getPrec() + gain;
						band.setStepSize(
								(float) ((1.0 + ss.getMant() / 2048.0) * Math.pow(2.0, (double)noOfBps - ss.getExpn())));
						band.setNoOfBps(ss.getExpn() + tccp.getNoOfGaurdBits() - 1); /* WHY -1 ? */

						for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); precNo++) {
							int tlcblkxStart, tlcblkyStart, brcblkxEnd, brcblkyEnd;

							int cbgxStart = tlcbgxStart + (precNo % res.getPWidth()) * (1 << cbgwidthexpn);
							int cbgyStart = tlcbgyStart + (precNo / res.getPWidth()) * (1 << cbgheightexpn);
							int cbgxEnd = cbgxStart + (1 << cbgwidthexpn);
							int cbgyEnd = cbgyStart + (1 << cbgheightexpn);

							TcdPrecinct prc = band.getPrecincts()[precNo];

							/* precinct size (global) */
							prc.setX0(MathUtil.getInstance().intMax(cbgxStart, band.getX0()));
							prc.setY0(MathUtil.getInstance().intMax(cbgyStart, band.getY0()));
							prc.setX1(MathUtil.getInstance().intMin(cbgxEnd, band.getX1()));
							prc.setY1(MathUtil.getInstance().intMin(cbgyEnd, band.getY1()));

							tlcblkxStart = MathUtil.getInstance().intFloorDivPow2(prc.getX0(),
									cblkwidthexpn) << cblkwidthexpn;
							tlcblkyStart = MathUtil.getInstance().intFloorDivPow2(prc.getY0(),
									cblkheightexpn) << cblkheightexpn;
							brcblkxEnd = MathUtil.getInstance().intCeilDivPow2(prc.getX1(),
									cblkwidthexpn) << cblkwidthexpn;
							brcblkyEnd = MathUtil.getInstance().intCeilDivPow2(prc.getY1(),
									cblkheightexpn) << cblkheightexpn;
							prc.setCWidth((brcblkxEnd - tlcblkxStart) >> cblkwidthexpn);
							prc.setCHeight((brcblkyEnd - tlcblkyStart) >> cblkheightexpn);

							prc.setTcdCodeBlockEncoder(null);
							prc.setTcdCodeBlockEncoder(new TcdCodeBlockEncoder[prc.getCWidth() * prc.getCHeight()]);

							if (prc.getInclTree() != null) {
								TgtHelper.getInstance().tgtDestroy(prc.getInclTree());
							}
							if (prc.getImsbTree() != null) {
								TgtHelper.getInstance().tgtDestroy(prc.getImsbTree());
							}

							prc.setInclTree(TgtHelper.getInstance().tgtCreate(prc.getCWidth(), prc.getCHeight()));
							prc.setImsbTree(TgtHelper.getInstance().tgtCreate(prc.getCWidth(), prc.getCHeight()));

							for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
								int cblkxStart = tlcblkxStart + (codeBlockNo % prc.getCWidth()) * (1 << cblkwidthexpn);
								int cblkyStart = tlcblkyStart + (codeBlockNo / prc.getCWidth()) * (1 << cblkheightexpn);
								int cblkxEnd = cblkxStart + (1 << cblkwidthexpn);
								int cblkyEnd = cblkyStart + (1 << cblkheightexpn);

								TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];

								/* code-block size (global) */
								cblk.setX0(MathUtil.getInstance().intMax(cblkxStart, prc.getX0()));
								cblk.setY0(MathUtil.getInstance().intMax(cblkyStart, prc.getY0()));
								cblk.setX1(MathUtil.getInstance().intMin(cblkxEnd, prc.getX1()));
								cblk.setY1(MathUtil.getInstance().intMin(cblkyEnd, prc.getY1()));
								cblk.setData(new byte[8192 + 2]);
								/*
								 * FIX ME: mqcInitEncode and mqc_byteout underrun the buffer if we don't do this.
								 * Why?
								 */
								cblk.setDataIndex(cblk.getDataIndex() + 2);
								cblk.setLayers(new TcdLayer[100]);
								cblk.setPasses(new TcdPass[100]);
							}
						} /* precNo */
					} /* bandNo */
				} /* resNo */
			} /* compNo */
		} /* tileNo */
	}

	@SuppressWarnings({ "java:S1659", "java:S3626", "java:S3776"})
	public void tcdMallocDecode(Tcd tcd, OpenJpegImage image, CodingParameters codingParameters) {
		int i, j, tileNo, p, q;
		long x0 = 0, y0 = 0, x1 = 0, y1 = 0, w, h;

		tcd.setImage(image);
		tcd.getTcdImage().setTileWidth(codingParameters.getTileWidth());
		tcd.getTcdImage().setTileHeight(codingParameters.getTileHeight());
		tcd.getTcdImage().setTiles(new TcdTile[codingParameters.getTileWidth() * codingParameters.getTileHeight()]);

		/*
		 * Allocate place to store the decoded data = final image Place limited by the
		 * tile really present in the codestream
		 */

		for (j = 0; j < codingParameters.getTileNoSize(); j++) {
			tileNo = codingParameters.getTileNo()[j];
			tcd.getTcdImage().getTiles()[codingParameters.getTileNo()[tileNo]] = new TcdTile();
			TcdTile tile = tcd.getTcdImage().getTiles()[codingParameters.getTileNo()[tileNo]];
			tile.setNoOfComps(image.getNoOfComps());
			tile.setComps(new TcdTileComponent[image.getNoOfComps()]);
		}

		for (i = 0; i < image.getNoOfComps(); i++) {
			for (j = 0; j < codingParameters.getTileNoSize(); j++) {
				TcdTile tile;
				TcdTileComponent tilec;

				/* cfr p59 ISO/IEC FDIS15444-1 : 2000 (18 august 2000) */

				tileNo = codingParameters.getTileNo()[j];

				tile = tcd.getTcdImage().getTiles()[codingParameters.getTileNo()[tileNo]];
				tile.getComps()[i] = new TcdTileComponent();
				tilec = tile.getComps()[i];

				p = tileNo % codingParameters.getTileWidth(); /* si numerotation matricielle .. */
				q = tileNo / codingParameters
						.getTileWidth(); /* .. coordonnees de la tile (q,p) q pour ligne et p pour colonne */

				/* 4 borders of the tile rescale on the image if necessary */
				tile.setX0(MathUtil.getInstance()
						.intMax(codingParameters.getTileX0() + p * codingParameters.getTileDX(), image.getX0()));
				tile.setY0(MathUtil.getInstance()
						.intMax(codingParameters.getTileY0() + q * codingParameters.getTileDY(), image.getY0()));
				tile.setX1(MathUtil.getInstance()
						.intMin(codingParameters.getTileX0() + (p + 1) * codingParameters.getTileDX(), image.getX1()));
				tile.setY1(MathUtil.getInstance()
						.intMin(codingParameters.getTileY0() + (q + 1) * codingParameters.getTileDY(), image.getY1()));

				tilec.setX0(MathUtil.getInstance().intCeilDiv(tile.getX0(), image.getComps()[i].getDX()));
				tilec.setY0(MathUtil.getInstance().intCeilDiv(tile.getY0(), image.getComps()[i].getDY()));
				tilec.setX1(MathUtil.getInstance().intCeilDiv(tile.getX1(), image.getComps()[i].getDX()));
				tilec.setY1(MathUtil.getInstance().intCeilDiv(tile.getY1(), image.getComps()[i].getDY()));

				x0 = j == 0 ? tilec.getX0() : MathUtil.getInstance().intMin((int) x0, tilec.getX0());
				y0 = j == 0 ? tilec.getY0() : MathUtil.getInstance().intMin((int) y0, tilec.getX0());
				x1 = j == 0 ? tilec.getX1() : MathUtil.getInstance().intMax((int) x1, tilec.getX1());
				y1 = j == 0 ? tilec.getY1() : MathUtil.getInstance().intMax((int) y1, tilec.getY1());
			}

			w = MathUtil.getInstance().intCeilDivPow2((int) (x1 - x0), image.getComps()[i].getFactor());
			h = MathUtil.getInstance().intCeilDivPow2((int) (y1 - y0), image.getComps()[i].getFactor());

			image.getComps()[i].setWidth((int) w);
			image.getComps()[i].setHeight((int) h);
			image.getComps()[i].setX0((int) x0);
			image.getComps()[i].setY0((int) y0);
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "java:S6541", "unused"})
	public void tcdMallocDecodeTile(Tcd tcd, OpenJpegImage image, CodingParameters codingParameters, int tileNo,
			CodeStreamInfo codeStreamInfo) {
		int compNo, resNo, bandNo, precNo, codeBlockNo;
		Tcp tcp;
		TcdTile tile;

		tcd.setCodingParameters(codingParameters);

		tcp = codingParameters.getTcps()[codingParameters.getTileNo()[tileNo]];
		tile = tcd.getTcdImage().getTiles()[codingParameters.getTileNo()[tileNo]];

		tileNo = codingParameters.getTileNo()[tileNo];

		for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
			TileComponentCodingParameters tccp = tcp.getTccps()[compNo];
			TcdTileComponent tilec = tile.getComps()[compNo];

			/* border of each tile component (global) */
			tilec.setX0(MathUtil.getInstance().intCeilDiv(tile.getX0(), image.getComps()[compNo].getDX()));
			tilec.setY0(MathUtil.getInstance().intCeilDiv(tile.getY0(), image.getComps()[compNo].getDY()));
			tilec.setX1(MathUtil.getInstance().intCeilDiv(tile.getX1(), image.getComps()[compNo].getDX()));
			tilec.setY1(MathUtil.getInstance().intCeilDiv(tile.getY1(), image.getComps()[compNo].getDY()));

			tilec.setNoOfResolutions(tccp.getNoOfResolutions());
			tilec.setResolutions(new TcdResolution[tilec.getNoOfResolutions()]);

			for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
				int pdx, pdy;
				int levelno = tilec.getNoOfResolutions() - 1 - resNo;
				int tlprcxStart, tlprcyStart, brprcxEnd, brprcyEnd;
				int tlcbgxStart, tlcbgyStart, brcbgxEnd, brcbgyEnd;
				int cbgwidthexpn, cbgheightexpn;
				int cblkwidthexpn, cblkheightexpn;

				tilec.getResolutions()[resNo] = new TcdResolution();
				TcdResolution res = tilec.getResolutions()[resNo];

				/* border for each resolution level (global) */
				res.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0(), levelno));
				res.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0(), levelno));
				res.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1(), levelno));
				res.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1(), levelno));
				res.setNoOfBands(resNo == 0 ? 1 : 3);

				/* p. 35, table A-23, ISO/IEC FDIS154444-1 : 2000 (18 august 2000) */
				if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CCP_CSTY_PRT) != 0) {
					pdx = tccp.getPrecinctWidth()[resNo];
					pdy = tccp.getPrecinctHeight()[resNo];
				} else {
					pdx = 15;
					pdy = 15;
				}

				/* p. 64, B.6, ISO/IEC FDIS15444-1 : 2000 (18 august 2000) */
				tlprcxStart = MathUtil.getInstance().intFloorDivPow2(res.getX0(), pdx) << pdx;
				tlprcyStart = MathUtil.getInstance().intFloorDivPow2(res.getY0(), pdy) << pdy;
				brprcxEnd = MathUtil.getInstance().intCeilDivPow2(res.getX1(), pdx) << pdx;
				brprcyEnd = MathUtil.getInstance().intCeilDivPow2(res.getY1(), pdy) << pdy;

				res.setPWidth((res.getX0() == res.getX1()) ? 0 : ((brprcxEnd - tlprcxStart) >> pdx));
				res.setPHeight((res.getY0() == res.getY1()) ? 0 : ((brprcyEnd - tlprcyStart) >> pdy));

				if (resNo == 0) {
					tlcbgxStart = tlprcxStart;
					tlcbgyStart = tlprcyStart;
					brcbgxEnd = brprcxEnd;
					brcbgyEnd = brprcyEnd;
					cbgwidthexpn = pdx;
					cbgheightexpn = pdy;
				} else {
					tlcbgxStart = MathUtil.getInstance().intCeilDivPow2(tlprcxStart, 1);
					tlcbgyStart = MathUtil.getInstance().intCeilDivPow2(tlprcyStart, 1);
					brcbgxEnd = MathUtil.getInstance().intCeilDivPow2(brprcxEnd, 1);
					brcbgyEnd = MathUtil.getInstance().intCeilDivPow2(brprcyEnd, 1);
					cbgwidthexpn = pdx - 1;
					cbgheightexpn = pdy - 1;
				}

				cblkwidthexpn = MathUtil.getInstance().intMin(tccp.getCodeBlockWidth(), cbgwidthexpn);
				cblkheightexpn = MathUtil.getInstance().intMin(tccp.getCodeBlockHeight(), cbgheightexpn);

				for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
					res.getBands()[bandNo] = new TcdBand();
					int x0b, y0b;
					int gain, noOfBps;
					StepSize ss = null;

					TcdBand band = res.getBands()[bandNo];
					band.setBandNo(resNo == 0 ? 0 : bandNo + 1);
					x0b = (band.getBandNo() == 1) || (band.getBandNo() == 3) ? 1 : 0;
					y0b = (band.getBandNo() == 2) || (band.getBandNo() == 3) ? 1 : 0;

					if (band.getBandNo() == 0) {
						/* band border (global) */
						band.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0(), levelno));
						band.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0(), levelno));
						band.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1(), levelno));
						band.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1(), levelno));
					} else {
						/* band border (global) */
						band.setX0(MathUtil.getInstance().intCeilDivPow2(tilec.getX0() - (1 << levelno) * x0b,
								levelno + 1));
						band.setY0(MathUtil.getInstance().intCeilDivPow2(tilec.getY0() - (1 << levelno) * y0b,
								levelno + 1));
						band.setX1(MathUtil.getInstance().intCeilDivPow2(tilec.getX1() - (1 << levelno) * x0b,
								levelno + 1));
						band.setY1(MathUtil.getInstance().intCeilDivPow2(tilec.getY1() - (1 << levelno) * y0b,
								levelno + 1));
					}

					ss = tccp.getStepsizes()[resNo == 0 ? 0 : 3 * (resNo - 1) + bandNo + 1];

					gain = tccp.getQmfbid() == 0 ? DwtHelper.getInstance().dwtGetGainReal(band.getBandNo())
							: DwtHelper.getInstance().dwtGetGain(band.getBandNo());
					noOfBps = image.getComps()[compNo].getPrec() + gain;
					band.setStepSize(
							(float) (((1.0 + ss.getMant() / 2048.0) * Math.pow(2.0, (double)noOfBps - ss.getExpn())) * 0.5));
					band.setNoOfBps(ss.getExpn() + tccp.getNoOfGaurdBits() - 1); /* WHY -1 ? */

					band.setPrecincts(new TcdPrecinct[res.getPWidth() * res.getPHeight()]);

					for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); precNo++) {
						band.getPrecincts()[precNo] = new TcdPrecinct();
						int tlcblkxStart, tlcblkyStart, brcblkxEnd, brcblkyEnd;
						int cbgxStart = tlcbgxStart + (precNo % res.getPWidth()) * (1 << cbgwidthexpn);
						int cbgyStart = tlcbgyStart + (precNo / res.getPWidth()) * (1 << cbgheightexpn);
						int cbgxEnd = cbgxStart + (1 << cbgwidthexpn);
						int cbgyEnd = cbgyStart + (1 << cbgheightexpn);

						TcdPrecinct prc = band.getPrecincts()[precNo];
						/* precinct size (global) */
						prc.setX0(MathUtil.getInstance().intMax(cbgxStart, band.getX0()));
						prc.setY0(MathUtil.getInstance().intMax(cbgyStart, band.getY0()));
						prc.setX1(MathUtil.getInstance().intMin(cbgxEnd, band.getX1()));
						prc.setY1(MathUtil.getInstance().intMin(cbgyEnd, band.getY1()));

						tlcblkxStart = MathUtil.getInstance().intFloorDivPow2(prc.getX0(),
								cblkwidthexpn) << cblkwidthexpn;
						tlcblkyStart = MathUtil.getInstance().intFloorDivPow2(prc.getY0(),
								cblkheightexpn) << cblkheightexpn;
						brcblkxEnd = MathUtil.getInstance().intCeilDivPow2(prc.getX1(), cblkwidthexpn) << cblkwidthexpn;
						brcblkyEnd = MathUtil.getInstance().intCeilDivPow2(prc.getY1(),
								cblkheightexpn) << cblkheightexpn;
						prc.setCWidth((brcblkxEnd - tlcblkxStart) >> cblkwidthexpn);
						prc.setCHeight((brcblkyEnd - tlcblkyStart) >> cblkheightexpn);

						prc.setTcdCodeBlockDecoder(new TcdCodeBlockDecoder[prc.getCWidth() * prc.getCHeight()]);

						prc.setInclTree(TgtHelper.getInstance().tgtCreate(prc.getCWidth(), prc.getCHeight()));
						prc.setImsbTree(TgtHelper.getInstance().tgtCreate(prc.getCWidth(), prc.getCHeight()));

						for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
							prc.getTcdCodeBlockDecoder()[codeBlockNo] = new TcdCodeBlockDecoder();
							int cblkxStart = tlcblkxStart + (codeBlockNo % prc.getCWidth()) * (1 << cblkwidthexpn);
							int cblkyStart = tlcblkyStart + (codeBlockNo / prc.getCWidth()) * (1 << cblkheightexpn);
							int cblkxEnd = cblkxStart + (1 << cblkwidthexpn);
							int cblkyEnd = cblkyStart + (1 << cblkheightexpn);

							TcdCodeBlockDecoder cblk = prc.getTcdCodeBlockDecoder()[codeBlockNo];
							cblk.setData(null);
							cblk.setSegs(null);
							/* code-block size (global) */
							cblk.setX0(MathUtil.getInstance().intMax(cblkxStart, prc.getX0()));
							cblk.setY0(MathUtil.getInstance().intMax(cblkyStart, prc.getY0()));
							cblk.setX1(MathUtil.getInstance().intMin(cblkxEnd, prc.getX1()));
							cblk.setY1(MathUtil.getInstance().intMin(cblkyEnd, prc.getY1()));
							cblk.setNoOfSegs(0);
						}
					} /* precNo */
				} /* bandNo */
			} /* resNo */
		} /* compNo */
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "java:S6541"})
	private void tcdMakeLayerFixed(Tcd tcd, int layno, int final1) {
		int compNo, resNo, bandNo, precNo, codeBlockNo;
		int value; 
		int[][][] matrice = new int[10][10][3];
		int i, j, k;

		CodingParameters codingParameters = tcd.getCodingParameters();
		TcdTile tcdTile = tcd.getTcdTile();
		Tcp tcdTcp = tcd.getTcp();

		for (compNo = 0; compNo < tcdTile.getNoOfComps(); compNo++) {
			TcdTileComponent tilec = tcdTile.getComps()[compNo];
			for (i = 0; i < tcdTcp.getNoOfLayers(); i++) {
				for (j = 0; j < tilec.getNoOfResolutions(); j++) {
					for (k = 0; k < 3; k++) {
						matrice[i][j][k] = (int) (codingParameters.getMatrice()[i * tilec.getNoOfResolutions() * 3
								+ j * 3 + k] * (float) (tcd.getImage().getComps()[compNo].getPrec() / 16.0));
					}
				}
			}

			for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
				TcdResolution res = tilec.getResolutions()[resNo];
				for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
					TcdBand band = res.getBands()[bandNo];
					for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); precNo++) {
						TcdPrecinct prc = band.getPrecincts()[precNo];
						for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
							TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];
							TcdLayer layer = cblk.getLayers()[layno];
							int n;
							int imsb = tcd.getImage().getComps()[compNo].getPrec()
									- cblk.getNoOfBps(); /* number of bit-plan equal to zero */
							/* Correction of the matrix of coefficient to include the IMSB information */
							if (layno == 0) {
								value = matrice[layno][resNo][bandNo];
								if (imsb >= value) {
									value = 0;
								} else {
									value -= imsb;
								}
							} else {
								value = matrice[layno][resNo][bandNo] - matrice[layno - 1][resNo][bandNo];
								if (imsb >= matrice[layno - 1][resNo][bandNo]) {
									value -= (imsb - matrice[layno - 1][resNo][bandNo]);
									if (value < 0) {
										value = 0;
									}
								}
							}

							if (layno == 0) {
								cblk.setNoOfPassesInLayers(0);
							}

							n = cblk.getNoOfPassesInLayers();
							if (cblk.getNoOfPassesInLayers() == 0) {
								if (value != 0) {
									n = 3 * value - 2 + cblk.getNoOfPassesInLayers();
								} else {
									n = cblk.getNoOfPassesInLayers();
								}
							} else {
								n = 3 * value + cblk.getNoOfPassesInLayers();
							}

							layer.setNoOfPasses(n - cblk.getNoOfPassesInLayers());

							if (layer.getNoOfPasses() == 0)
								continue;

							if (cblk.getNoOfPassesInLayers() == 0) {
								layer.setLength(cblk.getPasses()[n - 1].getRate());
								layer.setData(cblk.getData());
							} else {
								layer.setLength(cblk.getPasses()[n - 1].getRate()
										- cblk.getPasses()[cblk.getNoOfPassesInLayers() - 1].getRate());
								layer.setData(Arrays.copyOfRange(cblk.getData(),
										cblk.getPasses()[cblk.getNoOfPassesInLayers() - 1].getRate(),
										cblk.getData().length));
							}
							if (final1 != 0)
								cblk.setNoOfPassesInLayers(n);
						}
					}
				}
			}
		}
	}

	private void tcdRateAllocateFixed(Tcd tcd) {
		int layno;
		for (layno = 0; layno < tcd.getTcp().getNoOfLayers(); layno++) {
			tcdMakeLayerFixed(tcd, layno, 1);
		}
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541" })
	private void tcdMakeLayer(Tcd tcd, int layno, double thresh, int final1) {
		int compNo, resNo, bandNo, precNo, codeBlockNo, passno;

		TcdTile tcdTile = tcd.getTcdTile();

		tcdTile.getDistortionLayer()[layno] = 0; /* fixed_quality */

		for (compNo = 0; compNo < tcdTile.getNoOfComps(); compNo++) {
			TcdTileComponent tilec = tcdTile.getComps()[compNo];
			for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
				TcdResolution res = tilec.getResolutions()[resNo];
				for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
					TcdBand band = res.getBands()[bandNo];
					for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); precNo++) {
						TcdPrecinct prc = band.getPrecincts()[precNo];
						for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
							TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];
							TcdLayer layer = cblk.getLayers()[layno];

							int n;
							if (layno == 0) {
								cblk.setNoOfPassesInLayers(0);
							}
							n = cblk.getNoOfPassesInLayers();
							for (passno = cblk.getNoOfPassesInLayers(); passno < cblk.getTotalPasses(); passno++) {
								int dr;
								double dd;
								TcdPass pass = cblk.getPasses()[passno];
								if (n == 0) {
									dr = pass.getRate();
									dd = pass.getDistortionDec();
								} else {
									dr = pass.getRate() - cblk.getPasses()[n - 1].getRate();
									dd = pass.getDistortionDec() - cblk.getPasses()[n - 1].getDistortionDec();
								}
								if (dr == 0) {
									if (dd != 0)
										n = passno + 1;
									continue;
								}
								if ((dd / dr) >= thresh)
									n = passno + 1;
							}
							layer.setNoOfPasses(n - cblk.getNoOfPassesInLayers());

							if (layer.getNoOfPasses() == 0) {
								layer.setDistortion(0);
								continue;
							}
							if (cblk.getNoOfPassesInLayers() == 0) {
								layer.setLength(cblk.getPasses()[n - 1].getRate());
								layer.setData(cblk.getData());
								layer.setDistortion(cblk.getPasses()[n - 1].getDistortionDec());
							} else {
								layer.setLength(cblk.getPasses()[n - 1].getRate()
										- cblk.getPasses()[cblk.getNoOfPassesInLayers() - 1].getRate());
								layer.setData(Arrays.copyOfRange(cblk.getData(),
										cblk.getPasses()[cblk.getNoOfPassesInLayers() - 1].getRate(),
										cblk.getData().length));
								layer.setDistortion(cblk.getPasses()[n - 1].getDistortionDec()
										- cblk.getPasses()[cblk.getNoOfPassesInLayers() - 1].getDistortionDec());
							}

							tcdTile.getDistortionLayer()[layno] = tcdTile.getDistortionLayer()[layno]
									+ layer.getDistortion(); /* fixed_quality */

							if (final1 != 0)
								cblk.setNoOfPassesInLayers(n);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings({ "java:S135", "java:S1659", "java:S3626", "java:S3776", "java:S6541" })
	private int tcdRateAllocate(Tcd tcd, byte[] dest, int len, CodeStreamInfo codeStreamInfo) {
		int compNo, resNo, bandNo, precNo, codeBlockNo, passno, layno;
		double min, max;
		double[] cumDistortion = new double[100]; /* fixed_quality */
		final double K = 1; /* 1.1; fixed_quality */
		double maxSE = 0;

		CodingParameters codingParameters = tcd.getCodingParameters();
		TcdTile tcdTile = tcd.getTcdTile();
		Tcp tcdTcp = tcd.getTcp();

		min = OpenJpegConstant.DBL_MAX;
		max = 0;

		tcdTile.setNoOfPixels(0); /* fixed_quality */

		for (compNo = 0; compNo < tcdTile.getNoOfComps(); compNo++) {
			TcdTileComponent tilec = tcdTile.getComps()[compNo];
			tilec.setNoOfPixels(0);

			for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
				TcdResolution res = tilec.getResolutions()[resNo];

				for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
					TcdBand band = res.getBands()[bandNo];

					for (precNo = 0; precNo < res.getPWidth() * res.getPHeight(); precNo++) {
						TcdPrecinct prc = band.getPrecincts()[precNo];

						for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
							TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];

							for (passno = 0; passno < cblk.getTotalPasses(); passno++) {
								TcdPass pass = cblk.getPasses()[passno];
								int dr;
								double dd, rdslope;
								if (passno == 0) {
									dr = pass.getRate();
									dd = pass.getDistortionDec();
								} else {
									dr = pass.getRate() - cblk.getPasses()[passno - 1].getRate();
									dd = pass.getDistortionDec() - cblk.getPasses()[passno - 1].getDistortionDec();
								}
								if (dr == 0) {
									continue;
								}
								rdslope = dd / dr;
								if (rdslope < min) {
									min = rdslope;
								}
								if (rdslope > max) {
									max = rdslope;
								}
							} /* passno */

							/* fixed_quality */
							tcdTile.setNoOfPixels(tcdTile.getNoOfPixels()
									+ ((cblk.getX1() - cblk.getX0()) * (cblk.getY1() - cblk.getY0())));
							tilec.setNoOfPixels(tilec.getNoOfPixels()
									+ ((cblk.getX1() - cblk.getX0()) * (cblk.getY1() - cblk.getY0())));
						} /* cbklno */
					} /* precNo */
				} /* bandNo */
			} /* resNo */

			maxSE += (((1 << tcd.getImage().getComps()[compNo].getPrec()) - 1.0)
					* ((1 << tcd.getImage().getComps()[compNo].getPrec()) - 1.0)) * (tilec.getNoOfPixels());
		} /* compNo */

		/* index file */
		if (codeStreamInfo != null) {
			TileInfo tileInfo = codeStreamInfo.getTileInfo()[tcd.getTcdTileNo()];
			tileInfo.setNoOfPixel(tcdTile.getNoOfPixels());
			tileInfo.setDistortionTile(tcdTile.getDistortionTile());
			tileInfo.setThresh(new double[tcdTcp.getNoOfLayers()]);
		}

		for (layno = 0; layno < tcdTcp.getNoOfLayers(); layno++) {
			double lo = min;
			double hi = max;
			int success = 0;
			int maxlen = tcdTcp.getRates()[layno] != 0
					? MathUtil.getInstance().intMin(((int) Math.ceil(tcdTcp.getRates()[layno])), len)
					: len;
			double goodThresh = 0;
			double stableThresh = 0;
			int i;
			double distotarget; /* fixed_quality */

			/* fixed_quality */
			distotarget = tcdTile.getDistortionTile()
					- ((K * maxSE) / Math.pow(10, tcdTcp.getDistortionRatio()[layno] / 10));

			/*
			 * Don't try to find an optimal threshold but rather take everything not
			 * included yet, if -r xx,yy,zz,0 (disto_alloc == 1 and rates == 0) -q
			 * xx,yy,zz,0 (fixed_quality == 1 and distoratio == 0) ==> possible to have some
			 * lossy layers and the last layer for sure lossless
			 */
			if (((codingParameters.getDistortionAllocation() == 1) && (tcdTcp.getRates()[layno] > 0))
					|| ((codingParameters.getFixedQuality() == 1) && (tcdTcp.getDistortionRatio()[layno] > 0))) {
				Tier2 t2 = Tier2Helper.getInstance().tier2Create(tcd.getCodecContextInfo(), tcd.getImage(),
						codingParameters);
				double thresh = 0;

				for (i = 0; i < 128; i++) {
					int l = 0;
					double distoachieved = 0; /* fixed_quality */
					thresh = (lo + hi) / 2;

					tcdMakeLayer(tcd, layno, thresh, 0);

					if (codingParameters.getFixedQuality() != 0) { /* fixed_quality */
						if (codingParameters.getCinemaMode().value() != 0) {
							l = Tier2Helper.getInstance().tier2EncodePackets(t2, tcd.getTcdTileNo(), tcdTile, layno + 1,
									dest, maxlen, codeStreamInfo, tcd.getCurTilePartNo(), tcd.getTilePartPosition(),
									tcd.getCurPiNo(), J2KT2Mode.THRESH_CALC, tcd.getCurTotalNoOfTileParts());
							if (l == -999) {
								lo = thresh;
								continue;
							} else {
								distoachieved = layno == 0 ? tcdTile.getDistortionLayer()[0]
										: cumDistortion[layno - 1] + tcdTile.getDistortionLayer()[layno];
								if (distoachieved < distotarget) {
									hi = thresh;
									stableThresh = thresh;
									continue;
								} else {
									lo = thresh;
								}
							}
						} else {
							distoachieved = (layno == 0) ? tcdTile.getDistortionLayer()[0]
									: (cumDistortion[layno - 1] + tcdTile.getDistortionLayer()[layno]);
							if (distoachieved < distotarget) {
								hi = thresh;
								stableThresh = thresh;
								continue;
							}
							lo = thresh;
						}
					} else {
						l = Tier2Helper.getInstance().tier2EncodePackets(t2, tcd.getTcdTileNo(), tcdTile, layno + 1,
								dest, maxlen, codeStreamInfo, tcd.getCurTilePartNo(), tcd.getTilePartPosition(),
								tcd.getCurPiNo(), J2KT2Mode.THRESH_CALC, tcd.getCurTotalNoOfTileParts());
						/* TO DO: what to do with l ??? seek / tell ??? */
						if (l == -999) {
							lo = thresh;
							continue;
						}
						hi = thresh;
						stableThresh = thresh;
					}
				}
				success = 1;
				goodThresh = stableThresh == 0 ? thresh : stableThresh;
				Tier2Helper.getInstance().tier2Destroy(t2);
			} else {
				success = 1;
				goodThresh = min;
			}

			if (success == 0) {
				return 0;
			}

			if (codeStreamInfo != null) { /* Threshold for Marcela Index */
				codeStreamInfo.getTileInfo()[tcd.getTcdTileNo()].getThresh()[layno] = goodThresh;
			}
			tcdMakeLayer(tcd, layno, goodThresh, 1);

			/* fixed_quality */
			cumDistortion[layno] = (layno == 0) ? tcdTile.getDistortionLayer()[0]
					: (cumDistortion[layno - 1] + tcdTile.getDistortionLayer()[layno]);
		}

		return 1;
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541" })
	public int tcdEncodeTile(Tcd tcd, int tileNo, byte[] dest, int len, CodeStreamInfo codeStreamInfo) {
		int compNo;
		int l, i, numpacks = 0;
		TcdTile tile = null;
		Tcp tcdTcp = null;
		CodingParameters codingParameters = null;

		Tcp tcp = tcd.getCodingParameters().getTcps()[0];
		TileComponentCodingParameters tccp = tcp.getTccps()[0];
		OpenJpegImage image = tcd.getImage();

		Tier1 t1 = null; /* T1 component */
		Tier2 t2 = null; /* T2 component */

		tcd.setTcdTileNo(tileNo);
		int tileIndex = 0;
		tcd.setTcdTile(tcd.getTcdImage().getTiles()[tileIndex]);
		tcd.setTcp(tcd.getCodingParameters().getTcps()[tileNo]);

		tile = tcd.getTcdTile();
		tcdTcp = tcd.getTcp();
		codingParameters = tcd.getCodingParameters();

		if (tcd.getCurTilePartNo() == 0) {
			tcd.setEncodingTime(System.currentTimeMillis()); /* time needed to encode a tile */
			/* INDEX >> "Precinct_nb_X et Precinct_nb_Y" */
			if (codeStreamInfo != null) {
				TcdTileComponent tilecIdx = tile.getComps()[0]; /* based on component 0 */
				for (i = 0; i < tilecIdx.getNoOfResolutions(); i++) {
					TcdResolution tcdResolution = tilecIdx.getResolutions()[i];

					codeStreamInfo.getTileInfo()[tileNo].getPWidth()[i] = tcdResolution.getPWidth();
					codeStreamInfo.getTileInfo()[tileNo].getPHeight()[i] = tcdResolution.getPHeight();

					numpacks += tcdResolution.getPWidth() * tcdResolution.getPHeight();

					codeStreamInfo.getTileInfo()[tileNo].getPDX()[i] = tccp.getPrecinctWidth()[i];
					codeStreamInfo.getTileInfo()[tileNo].getPDY()[i] = tccp.getPrecinctHeight()[i];
				}
				codeStreamInfo.getTileInfo()[tileNo].setPacket(
						new PacketInfo[codeStreamInfo.getNoOfComps() * codeStreamInfo.getNoOfLayers() * numpacks]);
			}
			/* << INDEX */

			/*---------------TILE-------------------*/

			for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
				int x, y;

				int adjust = image.getComps()[compNo].getSgnd() != 0 ? 0
						: 1 << (image.getComps()[compNo].getPrec() - 1);
				int offsetX = MathUtil.getInstance().intCeilDiv(image.getX0(), image.getComps()[compNo].getDX());
				int offsetY = MathUtil.getInstance().intCeilDiv(image.getY0(), image.getComps()[compNo].getDY());

				TcdTileComponent tilec = tile.getComps()[compNo];
				int tw = tilec.getX1() - tilec.getX0();
				int w = MathUtil.getInstance().intCeilDiv(image.getX1() - image.getX0(),
						image.getComps()[compNo].getDX());

				/* extract tile data */

				if (tcdTcp.getTccps()[compNo].getQmfbid() == 1) {
					for (y = tilec.getY0(); y < tilec.getY1(); y++) {
						/* Start of the src tile scanline */
						int dataIndex = (tilec.getX0() - offsetX) + (y - offsetY) * w;

						/* Start of the dst tile scanline */
						int tileDataIndex = (y - tilec.getY0()) * tw;
						for (x = tilec.getX0(); x < tilec.getX1(); x++) {
							tilec.getIData()[tileDataIndex++] = image.getComps()[compNo].getData()[dataIndex++]
									- adjust;
						}
					}
				} else if (tcdTcp.getTccps()[compNo].getQmfbid() == 0) {
					for (y = tilec.getY0(); y < tilec.getY1(); y++) {
						/* Start of the src tile scanline */
						int dataIndex = (tilec.getX0() - offsetX) + (y - offsetY) * w;

						/* Start of the dst tile scanline */
						int tileDataIndex = (y - tilec.getY0()) * tw;
						for (x = tilec.getX0(); x < tilec.getX1(); x++) {
							tilec.getFData()[tileDataIndex++] = (image.getComps()[compNo].getData()[dataIndex++]
									- adjust) << 11;
						}
					}
				}
			}

			/*----------------MCT-------------------*/
			if (tcdTcp.getMct() != 0) {
				int samples = (tile.getComps()[0].getX1() - tile.getComps()[0].getX0())
						* (tile.getComps()[0].getY1() - tile.getComps()[0].getY0());
				if (tcdTcp.getTccps()[0].getQmfbid() == 0) {
					MctHelper.getInstance().mctEncodeReal(tile.getComps()[0].getFData(), tile.getComps()[1].getFData(),
							tile.getComps()[2].getFData(), samples);
				} else {
					MctHelper.getInstance().mctEncode(tile.getComps()[0].getIData(), tile.getComps()[1].getIData(),
							tile.getComps()[2].getIData(), samples);
				}
			}

			/*----------------DWT---------------------*/

			for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
				TcdTileComponent tilec = tile.getComps()[compNo];
				if (tcdTcp.getTccps()[compNo].getQmfbid() == 1) {
					DwtHelper.getInstance().dwtEncode(tilec);
				} else if (tcdTcp.getTccps()[compNo].getQmfbid() == 0) {
					DwtHelper.getInstance().dwtEncodeReal(tilec);
				}
			}

			/*------------------TIER1-----------------*/
			t1 = Tier1Helper.getInstance().tier1Create(tcd.getCodecContextInfo());
			Tier1Helper.getInstance().tier1EncodeCodeBlocks(t1, tile, tcdTcp);
			Tier1Helper.getInstance().tier1Destroy(t1);

			/*-----------RATE-ALLOCATE------------------*/

			/* INDEX */
			if (codeStreamInfo != null) {
				codeStreamInfo.setIndexWrite(0);
			}
			if (codingParameters.getDistortionAllocation() != 0
					|| codingParameters.getFixedQuality() != 0) { /* fixed_quality */
				/* Normal Rate/distortion allocation */
				tcdRateAllocate(tcd, dest, len, codeStreamInfo);
			} else {
				/* Fixed layer allocation */
				tcdRateAllocateFixed(tcd);
			}
		}
		/*--------------TIER2------------------*/

		/* INDEX */
		if (codeStreamInfo != null) {
			codeStreamInfo.setIndexWrite(1);
		}

		t2 = Tier2Helper.getInstance().tier2Create(tcd.getCodecContextInfo(), image, codingParameters);
		l = Tier2Helper.getInstance().tier2EncodePackets(t2, tileNo, tile, tcdTcp.getNoOfLayers(), dest, len,
				codeStreamInfo, tcd.getTilePartNo(), tcd.getTilePartPosition(), tcd.getCurPiNo(), J2KT2Mode.FINAL_PASS,
				tcd.getCurTotalNoOfTileParts());
		Tier2Helper.getInstance().tier2Destroy(t2);

		/*---------------CLEAN-------------------*/

		if (tcd.getCurTilePartNo() == tcd.getCurTotalNoOfTileParts() - 1) {
			tcd.setEncodingTime(System.currentTimeMillis() - tcd.getEncodingTime());

			/* cleaning memory */
			for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
				TcdTileComponent tilec = tile.getComps()[compNo];
				tilec.setIData(null);
				tilec.setFData(null);
			}
		}

		return l;
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541" })
	public int tcdDecodeTile(Tcd tcd, byte[] src, int len, int tileNo, CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		int l;
		int compNo;
		int eof = 0;
		double tileTime, tier1Time, tier2Time, dwtTime;
		TcdTile tile = null;

		Tier1 t1 = null; /* Tier1 component */
		Tier2 t2 = null; /* Tier2 component */

		tcd.setTcdTileNo(tileNo);
		tcd.setTcdTile(tcd.getTcdImage().getTiles()[tileNo]);
		tcd.setTcp(tcd.getCodingParameters().getTcps()[tileNo]);
		tile = tcd.getTcdTile();

		tileTime = System.currentTimeMillis(); /* time needed to decode a tile */

		/* INDEX >> */
		if (codeStreamInfo != null) {
			int resNo, numprec = 0;
			for (compNo = 0; compNo < codeStreamInfo.getNoOfComps(); compNo++) {
				Tcp tcp = tcd.getCodingParameters().getTcps()[0];
				TileComponentCodingParameters tccp = tcp.getTccps()[compNo];
				TcdTileComponent tilecIdx = tile.getComps()[compNo];
				for (resNo = 0; resNo < tilecIdx.getNoOfResolutions(); resNo++) {
					TcdResolution tcdResolution = tilecIdx.getResolutions()[resNo];
					codeStreamInfo.getTileInfo()[tileNo].getPWidth()[resNo] = tcdResolution.getPWidth();
					codeStreamInfo.getTileInfo()[tileNo].getPHeight()[resNo] = tcdResolution.getPHeight();
					numprec += tcdResolution.getPWidth() * tcdResolution.getPHeight();
					if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_PRT) != 0) {
						codeStreamInfo.getTileInfo()[tileNo].getPDX()[resNo] = tccp.getPrecinctWidth()[resNo];
						codeStreamInfo.getTileInfo()[tileNo].getPDY()[resNo] = tccp.getPrecinctHeight()[resNo];
					} else {
						codeStreamInfo.getTileInfo()[tileNo].getPDX()[resNo] = 15;
						codeStreamInfo.getTileInfo()[tileNo].getPDY()[resNo] = 15;
					}
				}
			}
			codeStreamInfo.getTileInfo()[tileNo].setPacket(new PacketInfo[codeStreamInfo.getNoOfLayers() * numprec]);
			codeStreamInfo.setPacketNo(0);
		}
		/* << INDEX */

		/*--------------TIER2------------------*/

		tier2Time = System.currentTimeMillis(); /* time needed to decode a tile */
		t2 = Tier2Helper.getInstance().tier2Create(tcd.getCodecContextInfo(), tcd.getImage(),
				tcd.getCodingParameters());
		l = Tier2Helper.getInstance().tier2DecodePackets(t2, src, len, tileNo, tile, codeStreamInfo, useJPWL);
		Tier2Helper.getInstance().tier2Destroy(t2);
		tier2Time = System.currentTimeMillis() - tier2Time;
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("tier2Time {0}", tier2Time));

		if (l == -999) {
			eof = 1;
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "tcd_decode: incomplete bistream");
		}

		/*------------------TIER1-----------------*/

		tier1Time = System.currentTimeMillis(); /* time needed to decode a tile */
		t1 = Tier1Helper.getInstance().tier1Create(tcd.getCodecContextInfo());
		for (compNo = 0; compNo < tile.getNoOfComps(); ++compNo) {
			TcdTileComponent tilec = tile.getComps()[compNo];
			/* The +3 is headroom required by the vectorized DWT */
			tilec.setIData(new int[(((tilec.getX1() - tilec.getX0()) * (tilec.getY1() - tilec.getY0())) + 3)]);
			tilec.setFData(new double[(((tilec.getX1() - tilec.getX0()) * (tilec.getY1() - tilec.getY0())) + 3)]);
			Tier1Helper.getInstance().tier1DecodeCodeBlocks(t1, tilec, tcd.getTcp().getTccps()[compNo]);
		}
		Tier1Helper.getInstance().tier1Destroy(t1);
		tier1Time = System.currentTimeMillis() - tier1Time;
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("tier1Time{0}", tier1Time));

		/*----------------DWT---------------------*/

		dwtTime = System.currentTimeMillis(); /* time needed to decode a tile */
		for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
			TcdTileComponent tilec = tile.getComps()[compNo];
			int numres2decode;

			if (tcd.getCodingParameters().getReduce() != 0) {
				tcd.getImage().getComps()[compNo].setResNoDecoded(
						tile.getComps()[compNo].getNoOfResolutions() - tcd.getCodingParameters().getReduce() - 1);
				if (tcd.getImage().getComps()[compNo].getResNoDecoded() < 0) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,LOGGER_EMPTY, MessageFormat.format(
							"Error decoding tile. The number of resolutions to remove {0} is higher than the number of resolutions in the original codestream {1} Modify the cp_reduce parameter.",
							tcd.getCodingParameters().getReduce(), tile.getComps()[compNo].getNoOfResolutions()));
					return 0;
				}
			}

			numres2decode = tcd.getImage().getComps()[compNo].getResNoDecoded() + 1;
			if (numres2decode > 0) {
				if (tcd.getTcp().getTccps()[compNo].getQmfbid() == 1) {
					DwtHelper.getInstance().dwtDecode(tilec, numres2decode);
				} else {
					DwtHelper.getInstance().dwtDecodeReal(tilec, numres2decode);
				}
			}
		}
		dwtTime = System.currentTimeMillis() - dwtTime;
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("dwtTime{0}", dwtTime));

		/*----------------MCT-------------------*/

		if (tcd.getTcp().getMct() != 0) {
			int n = (tile.getComps()[0].getX1() - tile.getComps()[0].getX0())
					* (tile.getComps()[0].getY1() - tile.getComps()[0].getY0());
			if (tcd.getTcp().getTccps()[0].getQmfbid() == 1) // Lossless
			{
				MctHelper.getInstance().mctDecode(tile.getComps()[0].getIData(), tile.getComps()[1].getIData(),
						tile.getComps()[2].getIData(), n);
			} else // Lossy
			{
				MctHelper.getInstance().mctDecodeReal(tile.getComps()[0].getFData(), tile.getComps()[1].getFData(),
						tile.getComps()[2].getFData(), n);
			}
		}

		/*---------------TILE-------------------*/
		for (compNo = 0; compNo < tile.getNoOfComps(); ++compNo) {
			TcdTileComponent tilec = tile.getComps()[compNo];
			OpenJpegImageComponent imagec = tcd.getImage().getComps()[compNo];
			TcdResolution res = tilec.getResolutions()[imagec.getResNoDecoded()];
			int adjust = imagec.getSgnd() != 0 ? 0 : 1 << (imagec.getPrec() - 1);
			int min = imagec.getSgnd() != 0 ? -(1 << (imagec.getPrec() - 1)) : 0;
			int max = imagec.getSgnd() != 0 ? (1 << (imagec.getPrec() - 1)) - 1 : (1 << imagec.getPrec()) - 1;

			int tw = tilec.getX1() - tilec.getX0();
			int w = imagec.getWidth();

			int offsetX = MathUtil.getInstance().intCeilDivPow2(imagec.getX0(), imagec.getFactor());
			int offsetY = MathUtil.getInstance().intCeilDivPow2(imagec.getY0(), imagec.getFactor());

			int i, j;
			if (imagec.getData() == null) {
				imagec.setData(new int[imagec.getWidth() * imagec.getHeight()]);
				tcd.getImage().setQmfbid(tcd.getTcp().getTccps()[compNo].getQmfbid());
			}
			if (tcd.getTcp().getTccps()[compNo].getQmfbid() == 1) {
				for (j = res.getY0(); j < res.getY1(); ++j) {
					for (i = res.getX0(); i < res.getX1(); ++i) {
						int v = tilec.getIData()[i - res.getX0() + (j - res.getY0()) * tw];
						v += adjust;
						imagec.getData()[(i - offsetX) + (j - offsetY) * w] = MathUtil.getInstance().intClamp(v, min,
								max);
					}
				}
			} else {
				for (j = res.getY0(); j < res.getY1(); ++j) {
					for (i = res.getX0(); i < res.getX1(); ++i) {
						double tmp = tilec.getFData()[i - res.getX0() + (j - res.getY0()) * tw];
						int v = (int) leftRightIntDouble(tmp);
						v += adjust;
						imagec.getData()[(i - offsetX) + (j - offsetY) * w] = MathUtil.getInstance().intClamp(v, min,
								max);
					}
				}
			}
			tilec.setIData(null);
			tilec.setFData(null);
		}

		tileTime = System.currentTimeMillis() - tileTime; /* time needed to decode a tile */
		logger.debug(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("tileTime{0}", tileTime));

		if (eof != 0) {
			return 0;
		}

		return 1;
	}

	private long leftRightIntDouble(double x) {
		return (int) (roundInteger(x));
	}

	private double roundInteger(double x) {
		return x >= 0 ? Math.floor(x + 0.5) : Math.ceil(x - 0.5);
	}

	public void tcdFreeDecode(Tcd tcd) {
		TcdImage tcdImage = tcd.getTcdImage();
		tcdImage.setTiles(null);
	}

	@SuppressWarnings({ "java:S1659", "java:S3776" })
	public void tcdFreeDecodeTile(Tcd tcd, int tileNo) {
		int compNo, resNo, bandNo, precNo;

		TcdImage tcdImage = tcd.getTcdImage();

		TcdTile tile = tcdImage.getTiles()[tileNo];
		for (compNo = 0; compNo < tile.getNoOfComps(); compNo++) {
			TcdTileComponent tilec = tile.getComps()[compNo];
			for (resNo = 0; resNo < tilec.getNoOfResolutions(); resNo++) {
				TcdResolution res = tilec.getResolutions()[resNo];
				for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
					TcdBand band = res.getBands()[bandNo];
					for (precNo = 0; precNo < res.getPHeight() * res.getPWidth(); precNo++) {
						TcdPrecinct prec = band.getPrecincts()[precNo];
						if (prec.getImsbTree() != null)
							TgtHelper.getInstance().tgtDestroy(prec.getImsbTree());
						if (prec.getInclTree() != null)
							TgtHelper.getInstance().tgtDestroy(prec.getInclTree());
					}
					band.setPrecincts(null);
				}
			}
			tilec.setResolutions(null);
		}
		tile.setComps(null);
	}
}
