package io.mosip.imagedecoder.openjpeg;

import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_EMPTY;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_IDTYPE;
import static io.mosip.imagedecoder.constant.DecoderConstant.LOGGER_SESSIONID;

import java.text.MessageFormat;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.imagedecoder.logger.ImageDecoderLogger;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.Bio;
import io.mosip.imagedecoder.model.openjpeg.CodeStreamInfo;
import io.mosip.imagedecoder.model.openjpeg.CodecContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CodingParameters;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.PacketInfo;
import io.mosip.imagedecoder.model.openjpeg.J2KT2Mode;
import io.mosip.imagedecoder.model.openjpeg.JP2CinemeaMode;
import io.mosip.imagedecoder.model.openjpeg.PiIterator;
import io.mosip.imagedecoder.model.openjpeg.TcdBand;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockDecoder;
import io.mosip.imagedecoder.model.openjpeg.TcdCodeBlockEncoder;
import io.mosip.imagedecoder.model.openjpeg.TcdLayer;
import io.mosip.imagedecoder.model.openjpeg.TcdPass;
import io.mosip.imagedecoder.model.openjpeg.TcdPrecinct;
import io.mosip.imagedecoder.model.openjpeg.TcdResolution;
import io.mosip.imagedecoder.model.openjpeg.TcdSegment;
import io.mosip.imagedecoder.model.openjpeg.TcdTile;
import io.mosip.imagedecoder.model.openjpeg.TcdTileComponent;
import io.mosip.imagedecoder.model.openjpeg.Tcp;
import io.mosip.imagedecoder.model.openjpeg.Tier2;
import io.mosip.imagedecoder.model.openjpeg.TileInfo;
import io.mosip.imagedecoder.util.openjpeg.MathUtil;

public class Tier2Helper {
	private Logger logger = ImageDecoderLogger.getLogger(Tier2Helper.class);
	// Static variable reference of singleInstance of type Singleton
	private static Tier2Helper singleInstance = null;

	private Tier2Helper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized Tier2Helper getInstance() {
		if (singleInstance == null)
			singleInstance = new Tier2Helper();

		return singleInstance;
	}

	private void tier2PutCommaCode(Bio bio, int n) {
		while (--n >= 0) {
			BioHelper.getInstance().bioWrite(bio, 1, 1);
		}
		BioHelper.getInstance().bioWrite(bio, 0, 1);
	}

	@SuppressWarnings({ "java:S108"})	
	private int tier2GetCommaCode(Bio bio) {
		int n;
		for (n = 0; BioHelper.getInstance().bioRead(bio, 1) != 0; n++) {
		}
		return n;
	}

	private void tier2PutNoOfPasses(Bio bio, int n) {
		if (n == 1) {
			BioHelper.getInstance().bioWrite(bio, 0, 1);
		} else if (n == 2) {
			BioHelper.getInstance().bioWrite(bio, 2, 2);
		} else if (n <= 5) {
			BioHelper.getInstance().bioWrite(bio, 0xc | (n - 3), 4);
		} else if (n <= 36) {
			BioHelper.getInstance().bioWrite(bio, 0x1e0 | (n - 6), 9);
		} else if (n <= 164) {
			BioHelper.getInstance().bioWrite(bio, 0xff80 | (n - 37), 16);
		}
	}

	private int tier2GetNoOfPasses(Bio bio) {
		int n;
		if (BioHelper.getInstance().bioRead(bio, 1) == 0)
			return 1;
		if (BioHelper.getInstance().bioRead(bio, 1) == 0)
			return 2;
		if ((n = BioHelper.getInstance().bioRead(bio, 2)) != 3)
			return (3 + n);
		if ((n = BioHelper.getInstance().bioRead(bio, 5)) != 31)
			return (6 + n);
		return (37 + BioHelper.getInstance().bioRead(bio, 7));
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541" })
	private int tier2EncodePacket(TcdTile tile, Tcp tcp, PiIterator pi, byte[] dest, int length,
			CodeStreamInfo codeStreamInfo, int tileno) {
		int bandNo, codeBlockNo;
		int destIndex = 0;

		int compNo = pi.getCompNo(); /* component value */
		int resNo = pi.getResNo(); /* resolution level value */
		int precNo = pi.getPrecNo(); /* precinct value */
		int layNo = pi.getLayNo(); /* quality layer value */

		TcdTileComponent tilec = tile.getComps()[compNo];
		TcdResolution res = tilec.getResolutions()[resNo];

		Bio bio = null; /* BIO component */

		/* <SOP 0xff91> */
		if ((tcp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_SOP) != 0) {
			dest[0] = (byte) 255;
			dest[1] = (byte) 145;
			dest[2] = (byte) 0;
			dest[3] = (byte) 4;
			dest[4] = (byte) ((tile.getPacketNo() % 65536) / 256);
			dest[5] = (byte) ((tile.getPacketNo() % 65536) % 256);
			destIndex += 6;
		}
		/* </SOP> */

		if (layNo == 0) {
			for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
				TcdBand band = res.getBands()[bandNo];
				TcdPrecinct prc = band.getPrecincts()[precNo];
				TgtHelper.getInstance().tgtReset(prc.getInclTree());
				TgtHelper.getInstance().tgtReset(prc.getImsbTree());
				for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
					TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];
					cblk.setNoOfPasses(0);
					TgtHelper.getInstance().tgtSetValue(prc.getImsbTree(), codeBlockNo,
							band.getNoOfBps() - cblk.getNoOfBps());
				}
			}
		}

		bio = BioHelper.getInstance().bioCreate();
		BioHelper.getInstance().bioInitEncoder(bio, dest, length);
		BioHelper.getInstance().bioWrite(bio, 1, 1); /* Empty header bit */

		/* Writing Packet header */
		for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
			TcdBand band = res.getBands()[bandNo];
			TcdPrecinct prc = band.getPrecincts()[precNo];
			for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
				TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];
				TcdLayer layer = cblk.getLayers()[layNo];
				if (cblk.getNoOfPasses() == 0 && layer.getNoOfPasses() != 0) {
					TgtHelper.getInstance().tgtSetValue(prc.getInclTree(), codeBlockNo, layNo);
				}
			}
			for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
				TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];
				TcdLayer layer = cblk.getLayers()[layNo];
				int increment = 0;
				int nump = 0;
				int len = 0, passNo;
				/* cblk inclusion bits */
				if (cblk.getNoOfPasses() == 0) {
					TgtHelper.getInstance().tgtEncode(bio, prc.getInclTree(), codeBlockNo, layNo + 1);
				} else {
					BioHelper.getInstance().bioWrite(bio, layer.getNoOfPasses() != 0 ? 1 : 0, 1);
				}
				/* if cblk not included, go to the next cblk */
				if (layer.getNoOfPasses() == 0) {
					continue;
				}
				/* if first instance of cblk --> zero bit-planes information */
				if (cblk.getNoOfPasses() == 0) {
					cblk.setNoOfLengthBits(3);
					TgtHelper.getInstance().tgtEncode(bio, prc.getImsbTree(), codeBlockNo, 999);
				}
				/* number of coding passes included */
				tier2PutNoOfPasses(bio, layer.getNoOfPasses());

				/*
				 * computation of the increase of the length indicator and insertion in the
				 * header
				 */
				for (passNo = cblk.getNoOfPasses(); passNo < cblk.getNoOfPasses() + layer.getNoOfPasses(); passNo++) {
					TcdPass pass = cblk.getPasses()[passNo];
					nump++;
					len += pass.getLength();
					if (pass.getTerm() != 0 || passNo == (cblk.getNoOfPasses() + layer.getNoOfPasses()) - 1) {
						increment = MathUtil.getInstance().intMax(increment, MathUtil.getInstance().intFloorLog2(len)
								+ 1 - (cblk.getNoOfLengthBits() + MathUtil.getInstance().intFloorLog2(nump)));
						len = 0;
						nump = 0;
					}
				}
				tier2PutCommaCode(bio, increment);

				/* computation of the new Length indicator */
				cblk.setNoOfLengthBits(cblk.getNoOfLengthBits() + increment);

				/* insertion of the codeword segment length */
				for (passNo = cblk.getNoOfPasses(); passNo < cblk.getNoOfPasses() + layer.getNoOfPasses(); passNo++) {
					TcdPass pass = cblk.getPasses()[passNo];
					nump++;
					len += pass.getLength();
					if (pass.getTerm() != 0 || passNo == (cblk.getNoOfPasses() + layer.getNoOfPasses()) - 1) {
						BioHelper.getInstance().bioWrite(bio, len,
								cblk.getNoOfLengthBits() + MathUtil.getInstance().intFloorLog2(nump));
						len = 0;
						nump = 0;
					}
				}
			}
		}

		if (BioHelper.getInstance().bioFlush(bio) != 0) {
			BioHelper.getInstance().bioDestroy(bio);
			return -999; /* modified to eliminate long jmp !! */
		}

		destIndex += BioHelper.getInstance().bioNoOfBytes(bio);
		BioHelper.getInstance().bioDestroy(bio);

		/* <EPH 0xff92> */
		if ((tcp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_EPH) != 0) {
			dest[0] = (byte) OpenJpegConstant.LAST_DATA_BYTE;
			dest[1] = (byte) 146;
			destIndex += 2;
		}
		/* </EPH> */

		/* << INDEX */
		// End of packet header position. Currently only represents the distance to
		// start of packet
		// Will be updated later by incrementing with packet start value
		if (codeStreamInfo != null && codeStreamInfo.getIndexWrite() != 0) {
			PacketInfo packetInfo = codeStreamInfo.getTileInfo()[tileno].getPacket()[codeStreamInfo.getPacketNo()];
			packetInfo.setEndPHPosition((destIndex - 0));
		}
		/* INDEX >> */

		/* Writing the packet body */
		for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
			TcdBand band = res.getBands()[bandNo];
			TcdPrecinct prc = band.getPrecincts()[precNo];
			for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
				TcdCodeBlockEncoder cblk = prc.getTcdCodeBlockEncoder()[codeBlockNo];
				TcdLayer layer = cblk.getLayers()[layNo];
				if (layer.getNoOfPasses() == 0) {
					continue;
				}
				if ((destIndex + layer.getLength()) > (0 + length)) {
					return -999;
				}

				System.arraycopy(layer.getData(), 0, dest, destIndex, layer.getLength());
				cblk.setNoOfPasses(cblk.getNoOfPasses() + layer.getNoOfPasses());
				destIndex += layer.getLength();
				/* << INDEX */
				if (codeStreamInfo != null && codeStreamInfo.getIndexWrite() != 0) {
					PacketInfo packetInfo = codeStreamInfo.getTileInfo()[tileno].getPacket()[codeStreamInfo
							.getPacketNo()];
					packetInfo.setDistortion(packetInfo.getDistortion() + layer.getDistortion());
					if (codeStreamInfo.getDistortionMax() < packetInfo.getDistortion()) {
						codeStreamInfo.setDistortionMax(packetInfo.getDistortion());
					}
				}
				/* INDEX >> */
			}
		}

		return (destIndex - 0);
	}

	@SuppressWarnings({ "java:S3012", "java:S3776" })
	private void tier2InitSegment(TcdCodeBlockDecoder cblk, int index, int codeblockStyle, int first) {
		TcdSegment seg;
		TcdSegment[] segs = null;
		if (cblk.getSegs() != null) {
			segs = new TcdSegment[cblk.getSegs().length + (index + 1)];
			for (int segIndex = 0; segIndex < cblk.getSegs().length; segIndex++) {
				segs[segIndex] = cblk.getSegs()[segIndex];
			}
			for (int segIndex = cblk.getSegs().length; segIndex < (cblk.getSegs().length + (index + 1)); segIndex++) {
				segs[segIndex] = new TcdSegment();
			}
		} else {
			segs = new TcdSegment[(index + 1)];
			for (int segIndex = 0; segIndex < segs.length; segIndex++) {
				segs[segIndex] = new TcdSegment();
			}
		}
		cblk.setSegs(segs);
		seg = cblk.getSegs()[index];
		seg.setData(null);
		seg.setDataIndex(0);
		seg.setNoOfPasses(0);
		seg.setLength(0);
		if ((codeblockStyle & OpenJpegConstant.J2K_CCP_CBLKSTY_TERMALL) != 0) {
			seg.setMaxPasses(1);
		} else if ((codeblockStyle & OpenJpegConstant.J2K_CCP_CBLKSTY_LAZY) != 0) {
			if (first != 0) {
				seg.setMaxPasses(10);
			} else {
				// Add extra check
				if (cblk.getSegs()[index - 1] != null)
					seg.setMaxPasses(((cblk.getSegs()[index - 1].getMaxPasses() == 1)
							|| (cblk.getSegs()[index].getMaxPasses() == 10)) ? 2 : 1);
			}
		} else {
			seg.setMaxPasses(109);
		}
	}

	@SuppressWarnings({ "java:S107", "java:S108", "java:S1066", "java:S1659", "java:S2629", "java:S3776", "java:S6541" })
	private int tier2DecodePacket(Tier2 t2, byte[] src, int len, TcdTile tile, Tcp tcp, PiIterator pi,
			PacketInfo packetInfo, boolean useJPWL) {
		int bandNo, codeBlockNo;
		byte[] cData = src;
		int cIndex = 0;
		int srcStartIndex = 0;

		CodingParameters codingParameters = t2.getCodingParameters();

		int compNo = pi.getCompNo(); /* component value */
		int resNo = pi.getResNo(); /* resolution level value */
		int precNo = pi.getPrecNo(); /* precinct value */
		int layNo = pi.getLayNo(); /* quality layer value */

		TcdResolution res = tile.getComps()[compNo].getResolutions()[resNo];

		int hdIndex = 0;
		byte[] hd = null;
		int present;

		Bio bio = null; /* BIO component */

		if (layNo == 0) {
			for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
				TcdBand band = res.getBands()[bandNo];
				TcdPrecinct prc = band.getPrecincts()[precNo];

				if ((band.getX1() - band.getX0() == 0) || (band.getY1() - band.getY0() == 0))
					continue;

				TgtHelper.getInstance().tgtReset(prc.getInclTree());
				TgtHelper.getInstance().tgtReset(prc.getImsbTree());
				for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
					TcdCodeBlockDecoder cblk = prc.getTcdCodeBlockDecoder()[codeBlockNo];
					cblk.setNoOfSegs(0);
				}
			}
		}

		/* SOP markers */
		if ((tcp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_SOP) != 0) {
			if (cData[cIndex + 0] != (byte) 0xff || cData[cIndex + 1] != (byte) 0x91) {
				logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("Expected SOP marker {0} ",
						t2.getCodecContextInfo()));
			} else {
				cIndex += 6;
			}

			/** TO DO : check the Nsop value */
		}

		/*
		 * When the marker PPT/PPM is used the packet header are store in PPT/PPM marker
		 * This part deal with this caracteristic step 1: Read packet header in the
		 * saved structure step 2: Return to codestream for decoding
		 */

		bio = BioHelper.getInstance().bioCreate();
		len = Math.abs(len);
		if (codingParameters.getPpm() == 1) { /* PPM */
			hd = codingParameters.getPpmData();
			BioHelper.getInstance().bioInitDecoder(bio, hd, codingParameters.getPpmLength());
		} else if (tcp.getPpt() == 1) { /* PPT */
			hd = tcp.getPptData();
			BioHelper.getInstance().bioInitDecoder(bio, hd, tcp.getPptLength());
		} else { /* Normal Case */
			hd = cData;
			BioHelper.getInstance().bioInitDecoder(bio, hd, srcStartIndex + len - hdIndex);
		}

		present = BioHelper.getInstance().bioRead(bio, 1);

		if (present == 0) {
			BioHelper.getInstance().bioInAlign(bio);
			hdIndex += BioHelper.getInstance().bioNoOfBytes(bio);
			BioHelper.getInstance().bioDestroy(bio);

			/* EPH markers */

			if ((tcp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_EPH) != 0) {
				if (hd[hdIndex] != (byte) 0xff || hd[hdIndex + 1] != (byte) 0x92) {
					logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Expected EPH marker");
				} else {
					hdIndex += 2;
				}
			}

			/* << INDEX */
			// End of packet header position. Currently only represents the distance to
			// start of packet
			// Will be updated later by incrementing with packet start value
			if (packetInfo != null) {
				packetInfo.setEndPHPosition(cIndex - srcStartIndex);
			}
			/* INDEX >> */

			if (codingParameters.getPpm() == 1) { /* PPM case */
				codingParameters
						.setPpmLength(codingParameters.getPpmLength() + codingParameters.getPpmDataIndex() - hdIndex);
				codingParameters.setPpmData(hd);
				return (cIndex - srcStartIndex);
			}
			if (tcp.getPpt() == 1) { /* PPT case */
				tcp.setPptLength(tcp.getPptLength() + tcp.getPptDataIndex() - hdIndex);
				tcp.setPptData(hd);
				return (cIndex - srcStartIndex);
			}

			return (hdIndex - srcStartIndex);
		}

		for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
			TcdBand band = res.getBands()[bandNo];
			TcdPrecinct prc = band.getPrecincts()[precNo];

			if ((band.getX1() - band.getX0() == 0) || (band.getY1() - band.getY0() == 0))
				continue;

			for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
				int included, increment, n, segno;
				TcdCodeBlockDecoder cblk = prc.getTcdCodeBlockDecoder()[codeBlockNo];
				/* if cblk not yet included before --> inclusion tagtree */
				if (cblk.getNoOfSegs() == 0) {
					included = TgtHelper.getInstance().tgtDecode(bio, prc.getInclTree(), codeBlockNo, layNo + 1);
					/* else one bit */
				} else {
					included = BioHelper.getInstance().bioRead(bio, 1);
				}
				/* if cblk not included */
				if (included == 0) {
					cblk.setNoOfNewPasses(0);
					continue;
				}
				/* if cblk not yet included --> zero-bitplane tagtree */
				if (cblk.getNoOfSegs() == 0) {
					int i, numimsbs;
					for (i = 0; TgtHelper.getInstance().tgtDecode(bio, prc.getImsbTree(), codeBlockNo, i) == 0; i++) {
					}
					numimsbs = i - 1;
					cblk.setNoOfBps(band.getNoOfBps() - numimsbs);
					cblk.setNoOfLengthBits(3);
				}
				/* number of coding passes */
				cblk.setNoOfNewPasses(tier2GetNoOfPasses(bio));
				increment = tier2GetCommaCode(bio);
				/* length indicator increment */
				cblk.setNoOfLengthBits(cblk.getNoOfLengthBits() + increment);
				segno = 0;
				if (cblk.getNoOfSegs() == 0) {
					tier2InitSegment(cblk, segno, tcp.getTccps()[compNo].getCodeBlockStyle(), 1);
				} else {
					segno = cblk.getNoOfSegs() - 1;
					if (cblk.getSegs()[segno].getNoOfPasses() == cblk.getSegs()[segno].getMaxPasses()) {
						++segno;
						tier2InitSegment(cblk, segno, tcp.getTccps()[compNo].getCodeBlockStyle(), 0);
					}
				}
				n = cblk.getNoOfNewPasses();

				do {
					cblk.getSegs()[segno].setNoOfNewPasses(MathUtil.getInstance()
							.intMin(cblk.getSegs()[segno].getMaxPasses() - cblk.getSegs()[segno].getNoOfPasses(), n));
					cblk.getSegs()[segno].setNewLength(BioHelper.getInstance().bioRead(bio, cblk.getNoOfLengthBits()
							+ MathUtil.getInstance().intFloorLog2(cblk.getSegs()[segno].getNoOfNewPasses())));
					n -= cblk.getSegs()[segno].getNoOfNewPasses();
					if (n > 0) {
						++segno;
						tier2InitSegment(cblk, segno, tcp.getTccps()[compNo].getCodeBlockStyle(), 0);
					}
				} while (n > 0);
			}
		}

		if (BioHelper.getInstance().bioInAlign(bio) != 0) {
			BioHelper.getInstance().bioDestroy(bio);
			logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "bioInAlign error");
			return -999;
		}

		hdIndex += BioHelper.getInstance().bioNoOfBytes(bio);
		BioHelper.getInstance().bioDestroy(bio);

		/* EPH markers */
		if ((tcp.getCodingStyle() & OpenJpegConstant.J2K_CP_CSTY_EPH) != 0) {
			if (hd[hdIndex] != (byte) 0xff || hd[hdIndex + 1] != (byte) 0x92) {
				logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, "Expected EPH marker");
			} else {
				hdIndex += 2;
			}
		}

		/* << INDEX */
		// End of packet header position. Currently only represents the distance to
		// start of packet
		// Will be updated later by incrementing with packet start value
		if (packetInfo != null) {
			packetInfo.setEndPHPosition((hdIndex - srcStartIndex));
		}
		/* INDEX >> */

		if (codingParameters.getPpm() == 1) {
			codingParameters
					.setPpmLength(codingParameters.getPpmLength() + codingParameters.getPpmDataIndex() - hdIndex);
			codingParameters.setPpmData(hd);
		} else if (tcp.getPpt() == 1) {
			tcp.setPptLength(tcp.getPptLength() + tcp.getPptDataIndex() - hdIndex);
			tcp.setPptData(hd);
		} else {
			cIndex += hdIndex;
		}

		for (bandNo = 0; bandNo < res.getNoOfBands(); bandNo++) {
			TcdBand band = res.getBands()[bandNo];
			TcdPrecinct prc = band.getPrecincts()[precNo];

			if ((band.getX1() - band.getX0() == 0) || (band.getY1() - band.getY0() == 0))
				continue;

			for (codeBlockNo = 0; codeBlockNo < prc.getCWidth() * prc.getCHeight(); codeBlockNo++) {
				TcdCodeBlockDecoder cblk = prc.getTcdCodeBlockDecoder()[codeBlockNo];
				int segIndex = 0;
				TcdSegment[] seg = null;
				if (cblk.getNoOfNewPasses() == 0)
					continue;
				if (cblk.getNoOfSegs() == 0) {
					seg = cblk.getSegs();
					cblk.setNoOfSegs(cblk.getNoOfSegs() + 1);
					cblk.setLength(0);
				} else {
					seg = cblk.getSegs();
					if (seg[segIndex].getNoOfPasses() == seg[segIndex].getMaxPasses()) {
						segIndex++;
						cblk.setNoOfSegs(cblk.getNoOfSegs() + 1);
					}
				}

				do {
					if ((cData[cIndex + seg[segIndex].getNewLength()] & 0xff) > (src[srcStartIndex + len - 1] & 0xff)) {
						logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("Data end error data={0}, info={1} ",
								(cData[cIndex + seg[segIndex].getNewLength()] & 0xff),
								(src[srcStartIndex + len - 1] & 0xff)));
						return -999;
					}

					if (useJPWL) {
						/*
						 * we need here a j2k handle to verify if making a check to the validity of
						 * cblocks parameters is selected from user (-W)
						 */

						/* let's check that we are not exceeding */
						if ((cblk.getLength() + seg[segIndex].getNewLength()) > 8192) {
							logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format(
									"JPWL: segment too long {0} for codeblock {1} (p={2}, b={3}, r={4}, c={5})",
									seg[segIndex].getNewLength(), codeBlockNo, precNo, bandNo, resNo, compNo));
							seg[segIndex].setNewLength(8192 - cblk.getLength());
							logger.warn(LOGGER_SESSIONID, LOGGER_IDTYPE, LOGGER_EMPTY, MessageFormat.format("- truncating segment to {0}",
									seg[segIndex].getNewLength()));
							break;
						}
					}

					int newSize = seg[segIndex].getNewLength();
					int oldSize = cblk.getData() == null ? 0 : cblk.getData().length;

					byte[] data = new byte[oldSize + newSize];
					if (oldSize != 0)
						System.arraycopy(cblk.getData(), 0, data, 0, cblk.getData().length);
					cblk.setData(data);

					System.arraycopy(cData, cIndex, cblk.getData(), cblk.getLength(), seg[segIndex].getNewLength());
					if (seg[segIndex].getNoOfPasses() == 0) {
						seg[segIndex].setDataIndex(cblk.getLength());
					}
					// moved here
					seg[segIndex].setData(cblk.getData());
					cIndex += seg[segIndex].getNewLength();
					cblk.setLength(cblk.getLength() + seg[segIndex].getNewLength());
					seg[segIndex].setLength(seg[segIndex].getLength() + seg[segIndex].getNewLength());
					seg[segIndex].setNoOfPasses(seg[segIndex].getNoOfPasses() + seg[segIndex].getNoOfNewPasses());
					cblk.setNoOfNewPasses(cblk.getNoOfNewPasses() - seg[segIndex].getNoOfNewPasses());
					if (cblk.getNoOfNewPasses() > 0) {
						segIndex++;
						cblk.setNoOfSegs(cblk.getNoOfSegs() + 1);
					}
				} while (cblk.getNoOfNewPasses() > 0);
			}
		}

		return (cIndex - srcStartIndex);
	}

	/* ----------------------------------------------------------------------- */
	@SuppressWarnings({ "java:S107", "java:S135", "java:S1659", "java:S2178", "java:S2629", "java:S3776", "java:S6541" })
	public int tier2EncodePackets(Tier2 t2, int tileno, TcdTile tile, int maxlayers, byte[] dest, int len,
			CodeStreamInfo codeStreamInfo, int tpnum, int tppos, int piNo, J2KT2Mode tier2Mode,
			int curTotalNoOfTilePart) {
		int destIndex = 0;
		int e = 0;
		int compNo;
		PiIterator[] pi = null;
		int poc;
		OpenJpegImage image = t2.getImage();
		CodingParameters codingParameters = t2.getCodingParameters();
		Tcp tcp = codingParameters.getTcps()[tileno];
		int pocno = codingParameters.getCinemaMode() == JP2CinemeaMode.CINEMA4K_24 ? 2 : 1;
		int maxcomp = codingParameters.getMaxCompSize() > 0 ? image.getNoOfComps() : 1;

		pi = PiHelper.getInstance().piInitEncode(image, codingParameters, tileno, tier2Mode);
		if (pi == null) {
			return -999;
		}

		if (tier2Mode == J2KT2Mode.THRESH_CALC) { /* Calculating threshold */
			for (compNo = 0; compNo < maxcomp; compNo++) {
				for (poc = 0; poc < pocno; poc++) {
					int compLength = 0;
					int tpnum1 = compNo;
					if (PiHelper.getInstance().piCreateEncode(pi, codingParameters, tileno, poc, tpnum1, tppos,
							tier2Mode, curTotalNoOfTilePart) != 0) {
						logger.error(LOGGER_SESSIONID, LOGGER_IDTYPE,  LOGGER_EMPTY, MessageFormat.format("Error initializing Packet Iterator {0} ",
								t2.getCodecContextInfo()));
						return -999;
					}
					while (PiHelper.getInstance().piNext(pi[poc]) != 0) {
						if (pi[poc].getLayNo() < maxlayers) {
							e = tier2EncodePacket(tile, codingParameters.getTcps()[tileno], pi[poc], dest,
									0 + len - destIndex, codeStreamInfo, tileno);
							compLength = compLength + e;
							if (e == -999) {
								break;
							} else {
								destIndex += e;
							}
						}
					}
					if (e == -999)
						break;
					if (codingParameters.getMaxCompSize() != 0 && compLength > codingParameters.getMaxCompSize()) {
						e = -999;
						break;
					}
				}
				if (e == -999)
					break;
			}
		} else { /* tier2Mode == FINAL_PASS */
			PiHelper.getInstance().piCreateEncode(pi, codingParameters, tileno, piNo, tpnum, tppos, tier2Mode,
					curTotalNoOfTilePart);
			while (PiHelper.getInstance().piNext(pi[piNo]) != 0) {
				if (pi[piNo].getLayNo() < maxlayers) {
					e = tier2EncodePacket(tile, codingParameters.getTcps()[tileno], pi[piNo], dest, 0 + len - destIndex,
							codeStreamInfo, tileno);
					if (e == -999) {
						break;
					} else {
						destIndex += e;
					}
					/* INDEX >> */
					if (codeStreamInfo != null) {
						if (codeStreamInfo.getIndexWrite() != 0) {
							TileInfo tileInfo = codeStreamInfo.getTileInfo()[tileno];
							PacketInfo packetInfo = tileInfo.getPacket()[codeStreamInfo.getPacketNo()];
							if (codeStreamInfo.getPacketNo() == 0) {
								packetInfo.setStartPosition(tileInfo.getEndHeader() + 1);
							} else {
								packetInfo
										.setStartPosition(((codingParameters.getTilePartOn() != 0 | tcp.getIsPoc() != 0)
												&& packetInfo.getStartPosition() != 0) ? packetInfo.getStartPosition()
														: tileInfo.getPacket()[codeStreamInfo.getPacketNo() - 1]
																.getEndPosition() + 1);
							}
							packetInfo.setEndPosition(packetInfo.getStartPosition() + e - 1);
							/*
							 * End of packet header which now only represents the distance to start of
							 * packet is incremented by value of start of packet
							 */
							packetInfo.setEndPHPosition(
									packetInfo.getEndPHPosition() + packetInfo.getStartPosition() - 1);
						}

						codeStreamInfo.setPacketNo(codeStreamInfo.getPacketNo() + 1);
					}
					/* << INDEX */
					tile.setPacketNo(tile.getPacketNo() + 1);
				}
			}
		}

		PiHelper.getInstance().piDestroy(pi, codingParameters, tileno);

		if (e == -999) {
			return e;
		}

		return (destIndex - 0);
	}

	@SuppressWarnings({ "java:S1659", "java:S3776", "java:S6541", "unused" })
	public int tier2DecodePackets(Tier2 t2, byte[] src, int len, int tileno, TcdTile tile,
			CodeStreamInfo codeStreamInfo, boolean useJPWL) {
		int srcIndex = 0;
		int[] cIndex = new int[1];
		cIndex[0] = srcIndex;
		PiIterator[] pi;
		int piNo, e = 0;
		int n = 0, currentTilePart = 0;
		int tpStartPacketNo;

		OpenJpegImage image = t2.getImage();
		CodingParameters codingParameters = t2.getCodingParameters();

		/* create a packet iterator */
		pi = PiHelper.getInstance().piCreateDecode(image, codingParameters, tileno);
		if (pi == null) {
			return -999;
		}

		tpStartPacketNo = 0;

		for (piNo = 0; piNo <= codingParameters.getTcps()[tileno].getNoOfPocs(); piNo++) {
			while (PiHelper.getInstance().piNext(pi[piNo]) != 0) {
				if ((codingParameters.getLayer() == 0)
						|| (codingParameters.getLayer() >= ((pi[piNo].getLayNo()) + 1))) {
					PacketInfo packetInfo;
					if (codeStreamInfo != null)
						packetInfo = codeStreamInfo.getTileInfo()[tileno].getPacket()[codeStreamInfo.getPacketNo()];
					else
						packetInfo = null;

					byte[] cData = new byte[srcIndex + len - cIndex[0]];
					System.arraycopy(src, cIndex[0], cData, 0, cData.length);

					e = tier2DecodePacket(t2, cData, srcIndex + len - cIndex[0], tile,
							codingParameters.getTcps()[tileno], pi[piNo], packetInfo, useJPWL);
				} else {
					e = 0;
				}

				/* progression in resolution */
				image.getComps()[pi[piNo].getCompNo()].setResNoDecoded((e > 0)
						? MathUtil.getInstance().intMax(pi[piNo].getResNo(),
								image.getComps()[pi[piNo].getCompNo()].getResNoDecoded())
						: image.getComps()[pi[piNo].getCompNo()].getResNoDecoded());
				n++;

				/* INDEX >> */
				if (codeStreamInfo != null) {
					TileInfo tileInfo = codeStreamInfo.getTileInfo()[tileno];
					PacketInfo packetInfo = tileInfo.getPacket()[codeStreamInfo.getPacketNo()];
					if (codeStreamInfo.getPacketNo() == 0) {
						packetInfo.setStartPosition(tileInfo.getEndHeader() + 1);
					} else if (tileInfo.getPacket()[codeStreamInfo.getPacketNo() - 1]
							.getEndPosition() >= codeStreamInfo.getTileInfo()[tileno].getTp()[currentTilePart]
									.getTpEndPosition()) { // New
						// tile
						// part
						tileInfo.getTp()[currentTilePart]
								.setTpNoOfPackets(codeStreamInfo.getPacketNo() - tpStartPacketNo); // Number of
						// packets in
						// previous
						// tile-part
						tpStartPacketNo = codeStreamInfo.getPacketNo();
						currentTilePart++;
						packetInfo.setStartPosition(
								codeStreamInfo.getTileInfo()[tileno].getTp()[currentTilePart].getTpEndHeader() + 1);
					} else {
						packetInfo.setStartPosition(
								(codingParameters.getTilePartOn() != 0 && packetInfo.getStartPosition() != 0)
										? packetInfo.getStartPosition()
										: tileInfo.getPacket()[codeStreamInfo.getPacketNo() - 1].getEndPosition() + 1);
					}
					packetInfo.setEndPosition(packetInfo.getStartPosition() + e - 1);
					packetInfo.setEndPHPosition(packetInfo.getEndPHPosition() + packetInfo.getStartPosition() - 1); // End
																													// of
																													// packet
																													// header
					// which now only
					// represents the
					// distance
					// to start of
					// packet is
					// incremented by
					// value of start of
					// packet
					codeStreamInfo.setPacketNo(codeStreamInfo.getPacketNo() + 1);
				}
				/* << INDEX */

				if (e == -999) { /* ADD */
					break;
				} else {
					cIndex[0] += e;
				}
			}
		}
		/* INDEX >> */
		if (codeStreamInfo != null) {
			codeStreamInfo.getTileInfo()[tileno].getTp()[currentTilePart]
					.setTpNoOfPackets(codeStreamInfo.getPacketNo() - tpStartPacketNo);
			// Number of packets in last tile-part
		}
		/* << INDEX */

		/* don't forget to release pi */
		PiHelper.getInstance().piDestroy(pi, codingParameters, tileno);

		if (e == -999) {
			return e;
		}

		return (cIndex[0] - srcIndex);
	}

	public Tier2 tier2Create(CodecContextInfo codecContextInfo, OpenJpegImage image,
			CodingParameters codingParameters) {
		/* create the tcd structure */
		Tier2 t2 = new Tier2();
		t2.setCodecContextInfo(codecContextInfo);
		t2.setImage(image);
		t2.setCodingParameters(codingParameters);

		return t2;
	}

	@SuppressWarnings({ "java:S1186"})
	public void tier2Destroy(Tier2 t2) {
	}
}