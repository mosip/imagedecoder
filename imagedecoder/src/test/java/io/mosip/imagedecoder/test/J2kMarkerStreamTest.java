package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Timeout.ThreadMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.mosip.imagedecoder.model.DecoderRequestInfo;
import io.mosip.imagedecoder.model.DecoderResponseInfo;
import io.mosip.imagedecoder.model.Response;
import io.mosip.imagedecoder.model.openjpeg.Cio;
import io.mosip.imagedecoder.model.openjpeg.CompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.CompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.DecompressionContextInfo;
import io.mosip.imagedecoder.model.openjpeg.DecompressionParameters;
import io.mosip.imagedecoder.model.openjpeg.JP2CodecFormat;
import io.mosip.imagedecoder.model.openjpeg.Jp2ColorSpace;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImageComponentParameters;
import io.mosip.imagedecoder.openjpeg.CioHelper;
import io.mosip.imagedecoder.openjpeg.ImageHelper;
import io.mosip.imagedecoder.openjpeg.OpenJpegHelper;
import io.mosip.imagedecoder.util.Base64UrlUtil;
import io.mosip.imagedecoder.wsq.WsqDecoder;

/**
 * Optional J2K marker segments, JPT-stream decoding and robustness of the
 * header parsers against corrupted / truncated input.
 */
class J2kMarkerStreamTest {

	private static final int SOT = 0xFF90;
	private static final int SOD = 0xFF93;
	private static final int EOC = 0xFFD9;

	private OpenJpegHelper codec;
	private byte[] j2k;
	private int[] reference;

	@BeforeEach
	void setUp() {
		codec = new OpenJpegHelper();
		j2k = encode(JP2CodecFormat.CODEC_J2K, 32, 32);
		OpenJpegImage image = decode(JP2CodecFormat.CODEC_J2K, j2k, false);
		assertNotNull(image);
		reference = image.getComps()[0].getData().clone();
	}

	@ParameterizedTest(name = "main header segment {0}")
	@ValueSource(strings = { "FF55 0009 00 50 00 00000100", // TLM st=1 sp=1
			"FF55 0008 00 20 0000 0100", // TLM st=2 sp=0
			"FF55 0006 00 00 0100", // TLM st=0 sp=0
			"FF57 000A 00 00000003 81 05 07", // PLM
			"FF57 0009 00 00000005 81 05", // PLM with truncated packet list
			"FF53 0009 00 00 05 04 04 00 01", // COC identical to COD
			"FF64 0008 0001 41 42 43 44" // COM
	})
	void informationalMainHeaderMarkersDoNotChangePixels(String segment) {
		byte[] stream = insert(j2k, indexOf(j2k, SOT, 2), hex(segment));
		OpenJpegImage image = decode(JP2CodecFormat.CODEC_J2K, stream, false);
		assertNotNull(image);
		assertArrayEquals(reference, image.getComps()[0].getData());
	}

	@Test
	void pltInTileHeaderDoesNotChangePixels() {
		byte[] stream = insertInTileHeader(j2k, hex("FF58 0006 00 81 05 07"));
		OpenJpegImage image = decode(JP2CodecFormat.CODEC_J2K, stream, false);
		assertNotNull(image);
		assertArrayEquals(reference, image.getComps()[0].getData());
	}

	@Test
	void rgnMarkerIsParsed() {
		byte[] stream = insert(j2k, indexOf(j2k, SOT, 2), hex("FF5E 0005 00 00 02"));
		assertNotNull(decode(JP2CodecFormat.CODEC_J2K, stream, false));
	}

	@ParameterizedTest(name = "packed packet headers {0}")
	@ValueSource(strings = { "main:FF60 000A 00 00000003 01 02 03", "main:FF60 0008 00 00000005 01",
			"main:FF60 0008 00 00000001 01 FF60 0006 01 00000001", "tile:FF61 0006 00 01 02 03",
			"tile:FF61 0005 00 01 02 FF61 0005 01 03 04" })
	@Timeout(value = 10, unit = TimeUnit.SECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
	void packedPacketHeadersAreParsed(String spec) {
		String[] parts = spec.split(":");
		byte[] segment = hex(parts[1]);
		byte[] stream = "main".equals(parts[0]) ? insert(j2k, indexOf(j2k, SOT, 2), segment)
				: insertInTileHeader(j2k, segment);
		tryDecode(JP2CodecFormat.CODEC_J2K, stream, false);
		assertTrue(stream.length > j2k.length);
	}

	@Test
	@Timeout(value = 10, unit = TimeUnit.SECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
	void unknownMarkerIsSubstitutedUnderJpwl() {
		byte[] stream = insert(j2k, indexOf(j2k, SOT, 2), hex("FF56 0004 0000"));
		tryDecode(JP2CodecFormat.CODEC_J2K, stream, true);
		byte[] far = insert(j2k, indexOf(j2k, SOT, 2), hex("FF01 0004 0000"));
		tryDecode(JP2CodecFormat.CODEC_J2K, far, true);
		assertNotNull(decode(JP2CodecFormat.CODEC_J2K, j2k, true));
	}

	@Test
	void jptStreamDecodesLikeCodestream() {
		OpenJpegImage image = decode(JP2CodecFormat.CODEC_JPT, jpt(j2k, 0x40, 6, 4), false);
		assertNotNull(image);
		assertArrayEquals(reference, image.getComps()[0].getData());
	}

	@Test
	void jptStreamWithCodestreamIndexDecodes() {
		assertNotNull(decode(JP2CodecFormat.CODEC_JPT, jpt(j2k, 0x60, 6, 4), false));
	}

	@Test
	void jptStreamRejectsUnexpectedClasses() {
		assertNull(decode(JP2CodecFormat.CODEC_JPT, jpt(j2k, 0x40, 4, 4), false));
		assertNull(decode(JP2CodecFormat.CODEC_JPT, jpt(j2k, 0x40, 6, 6), false));
		assertNull(tryDecode(JP2CodecFormat.CODEC_JPT, new byte[] { 0x00, 0x06, 0x00, 0x02, 0x12, 0x34 }, false));
	}

	@Test
	@Timeout(value = 60, unit = TimeUnit.SECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
	void corruptedCodestreamsNeverHang() {
		int from = sizEnd(j2k);
		int to = Math.min(j2k.length, from + 160);
		int survived = 0;
		for (int i = from; i < to; i++) {
			for (int variant = 0; variant < 3; variant++) {
				byte[] copy = j2k.clone();
				copy[i] = mutate(copy[i], variant);
				if (tryDecode(JP2CodecFormat.CODEC_J2K, copy, false) != null) {
					survived++;
				}
			}
		}
		for (int len = 2; len < j2k.length; len += 5) {
			tryDecode(JP2CodecFormat.CODEC_J2K, java.util.Arrays.copyOf(j2k, len), false);
			tryDecode(JP2CodecFormat.CODEC_JPT, java.util.Arrays.copyOf(j2k, len), false);
		}
		assertTrue(survived > 0, "some single-byte corruptions must still decode");
	}

	@Test
	@Timeout(value = 60, unit = TimeUnit.SECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
	void corruptedJp2BoxesNeverHang() {
		byte[] jp2 = encode(JP2CodecFormat.CODEC_JP2, 24, 24);
		assertNotNull(decode(JP2CodecFormat.CODEC_JP2, jp2, false));
		int ihdr = indexOfAscii(jp2, "ihdr");
		for (int i = 0; i < Math.min(jp2.length, ihdr + 40); i++) {
			if (i >= ihdr + 4 && i < ihdr + 14) {
				continue;
			}
			for (int variant = 0; variant < 3; variant++) {
				byte[] copy = jp2.clone();
				copy[i] = mutate(copy[i], variant);
				tryDecode(JP2CodecFormat.CODEC_JP2, copy, false);
			}
		}
		for (int len = 4; len < jp2.length; len += 9) {
			tryDecode(JP2CodecFormat.CODEC_JP2, java.util.Arrays.copyOf(jp2, len), false);
		}
		assertTrue(ihdr > 0);
	}

	@Test
	@Timeout(value = 120, unit = TimeUnit.SECONDS, threadMode = ThreadMode.SEPARATE_THREAD)
	void corruptedWsqNeverHangs() throws IOException {
		byte[] wsq;
		try (InputStream in = getClass().getClassLoader().getResourceAsStream("sample-wsq.b64")) {
			assertNotNull(in);
			wsq = Base64UrlUtil.getInstance()
					.decodeURLSafeBase64(new String(in.readAllBytes(), StandardCharsets.UTF_8));
		}
		int sof = indexOf(wsq, 0xFFA2, 2);
		int sofEnd = sof + 2 + (((wsq[sof + 2] & 0xff) << 8) | (wsq[sof + 3] & 0xff));
		int sob = indexOf(wsq, 0xFFA3, sofEnd);
		int end = Math.min(wsq.length, sob + 40);
		int failures = 0;
		for (int i = 0; i < end; i += 3) {
			if (i >= sof && i < sofEnd) {
				continue;
			}
			byte[] copy = wsq.clone();
			copy[i] = mutate(copy[i], i % 3);
			if (!wsqDecodes(copy)) {
				failures++;
			}
		}
		for (int len = 2; len < end; len += 23) {
			wsqDecodes(java.util.Arrays.copyOf(wsq, len));
		}
		assertTrue(failures > 0, "header corruption must be reported");
	}

	private static boolean wsqDecodes(byte[] data) {
		DecoderRequestInfo req = new DecoderRequestInfo();
		req.setImageData(data);
		req.setBufferedImage(false);
		try {
			Response<DecoderResponseInfo> info = new WsqDecoder().decode(req);
			return info.getStatusCode() == 0;
		} catch (RuntimeException ex) {
			return false;
		}
	}

	private static byte mutate(byte b, int variant) {
		switch (variant) {
		case 0:
			return 0;
		case 1:
			return (byte) 0xFF;
		default:
			return (byte) (b ^ 0x5A);
		}
	}

	/** Wraps the codestream into a main-header data-bin followed by one tile data-bin. */
	private static byte[] jpt(byte[] codestream, int binId, int mainClass, int tileClass) {
		int sot = indexOf(codestream, SOT, 2);
		int eoc = lastIndexOf(codestream, EOC);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeMessage(out, binId, mainClass, codestream, 0, sot);
		writeMessage(out, binId, tileClass, codestream, sot, eoc);
		return out.toByteArray();
	}

	private static void writeMessage(ByteArrayOutputStream out, int binId, int classId, byte[] src, int from,
			int to) {
		out.write(binId);
		out.writeBytes(vbas(classId));
		if ((binId & 0x60) == 0x60) {
			out.writeBytes(vbas(0));
		}
		out.writeBytes(vbas(0));
		out.writeBytes(vbas(to - from));
		out.write(src, from, to - from);
	}

	private static byte[] vbas(int value) {
		int groups = 1;
		while ((value >>> (7 * groups)) != 0) {
			groups++;
		}
		byte[] out = new byte[groups];
		for (int g = 0; g < groups; g++) {
			int bits = (value >>> (7 * (groups - 1 - g))) & 0x7f;
			out[g] = (byte) (g < groups - 1 ? bits | 0x80 : bits);
		}
		return out;
	}

	private static byte[] insertInTileHeader(byte[] stream, byte[] segment) {
		int sot = indexOf(stream, SOT, 2);
		int sod = indexOf(stream, SOD, sot);
		byte[] out = insert(stream, sod, segment);
		int psotAt = sot + 6;
		long psot = ((out[psotAt] & 0xffL) << 24) | ((out[psotAt + 1] & 0xff) << 16)
				| ((out[psotAt + 2] & 0xff) << 8) | (out[psotAt + 3] & 0xff);
		if (psot != 0) {
			psot += segment.length;
			out[psotAt] = (byte) (psot >> 24);
			out[psotAt + 1] = (byte) (psot >> 16);
			out[psotAt + 2] = (byte) (psot >> 8);
			out[psotAt + 3] = (byte) psot;
		}
		return out;
	}

	private static int sizEnd(byte[] stream) {
		int siz = indexOf(stream, 0xFF51, 0);
		return siz + 2 + (((stream[siz + 2] & 0xff) << 8) | (stream[siz + 3] & 0xff));
	}

	private static byte[] insert(byte[] src, int at, byte[] extra) {
		byte[] out = new byte[src.length + extra.length];
		System.arraycopy(src, 0, out, 0, at);
		System.arraycopy(extra, 0, out, at, extra.length);
		System.arraycopy(src, at, out, at + extra.length, src.length - at);
		return out;
	}

	private static int indexOf(byte[] data, int marker, int from) {
		for (int i = from; i + 1 < data.length; i++) {
			if ((data[i] & 0xff) == (marker >> 8) && (data[i + 1] & 0xff) == (marker & 0xff)) {
				return i;
			}
		}
		throw new AssertionError("marker not found: " + Integer.toHexString(marker));
	}

	private static int lastIndexOf(byte[] data, int marker) {
		for (int i = data.length - 2; i >= 0; i--) {
			if ((data[i] & 0xff) == (marker >> 8) && (data[i + 1] & 0xff) == (marker & 0xff)) {
				return i;
			}
		}
		throw new AssertionError("marker not found: " + Integer.toHexString(marker));
	}

	private static int indexOfAscii(byte[] data, String text) {
		byte[] needle = text.getBytes(StandardCharsets.US_ASCII);
		outer: for (int i = 0; i + needle.length <= data.length; i++) {
			for (int j = 0; j < needle.length; j++) {
				if (data[i + j] != needle[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}

	private static byte[] hex(String text) {
		String clean = text.replace(" ", "");
		byte[] out = new byte[clean.length() / 2];
		for (int i = 0; i < out.length; i++) {
			out[i] = (byte) Integer.parseInt(clean.substring(2 * i, 2 * i + 2), 16);
		}
		return out;
	}

	private OpenJpegImage tryDecode(JP2CodecFormat format, byte[] data, boolean jpwl) {
		try {
			return decode(format, data, jpwl);
		} catch (RuntimeException | StackOverflowError ex) {
			return null;
		}
	}

	private OpenJpegImage decode(JP2CodecFormat format, byte[] data, boolean jpwl) {
		DecompressionContextInfo dInfo = codec.createDecompression(format);
		DecompressionParameters params = new DecompressionParameters();
		codec.setDefaultDecoderParameters(params, jpwl);
		if (jpwl) {
			params.setJpwlCorrect(1);
			params.setJpwlExpComps(1);
			params.setJpwlMaxTiles(4);
		}
		codec.setupDecoder(dInfo, params, jpwl);
		Cio cio = CioHelper.getInstance().cioOpen(dInfo, data, data.length);
		try {
			return codec.decode(dInfo, cio, jpwl);
		} finally {
			CioHelper.getInstance().cioClose(cio);
			codec.destroyDecompression(dInfo);
		}
	}

	private byte[] encode(JP2CodecFormat format, int w, int h) {
		OpenJpegImageComponentParameters p = new OpenJpegImageComponentParameters();
		p.setDx(1);
		p.setDy(1);
		p.setWidth(w);
		p.setHeight(h);
		p.setPrec(8);
		p.setBpp(8);
		OpenJpegImage image = ImageHelper.getInstance().imageCreate(1,
				new OpenJpegImageComponentParameters[] { p }, Jp2ColorSpace.CLRSPC_GRAY);
		image.setX1(w);
		image.setY1(h);
		int[] data = image.getComps()[0].getData();
		for (int i = 0; i < data.length; i++) {
			data[i] = (i * 37 + (i / w) * 11) & 0xff;
		}
		CompressionParameters params = new CompressionParameters();
		codec.setDefaultEncodeParameters(params, false);
		params.setTcpNoOfLayers(1);
		params.setTcpRates(new float[] { 0f });
		CompressionContextInfo cInfo = codec.createCompression(format);
		codec.setupEncoder(cInfo, params, image, false);
		Cio cio = CioHelper.getInstance().cioOpen(cInfo, null, 0);
		assertEquals(0, codec.encode(cInfo, cio, image, null, false));
		byte[] out = java.util.Arrays.copyOf(cio.getBuffer(), CioHelper.getInstance().cioTell(cio));
		CioHelper.getInstance().cioClose(cio);
		codec.destroyCompression(cInfo);
		return out;
	}
}
