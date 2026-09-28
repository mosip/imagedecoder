package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import io.mosip.imagedecoder.model.wsq.WsqQuantization;
import io.mosip.imagedecoder.model.wsq.WsqQuantizationTree;
import io.mosip.imagedecoder.model.wsq.WsqTableDqt;
import io.mosip.imagedecoder.model.wsq.WsqTableDtt;
import io.mosip.imagedecoder.model.wsq.WsqWavletTree;
import io.mosip.imagedecoder.util.wsq.WsqUtil;
import io.mosip.imagedecoder.wsq.WsqTreeHelper;

/**
 * WSQ wavelet decomposition / quantization / reconstruction round trips using
 * the encoder-side routines of {@link WsqUtil}.
 */
class WsqCodecMathTest {

	private static final float[] LO_9 = { 0.037828455506995f, -0.023849465019380f, -0.110624404418423f,
			0.377402855612654f, 0.852698679009403f, 0.377402855612654f, -0.110624404418423f, -0.023849465019380f,
			0.037828455506995f };
	private static final float[] HI_7 = { 0.064538882628938f, -0.040689417609558f, -0.418092273222212f,
			0.788485616405664f, -0.418092273222212f, -0.040689417609558f, 0.064538882628938f };

	private final WsqUtil util = WsqUtil.getInstance();

	@ParameterizedTest(name = "{0}x{1} noise={2} bitrate={3}")
	@CsvSource({ "128,128,60,0.75", "97,75,60,2.25", "128,96,2,0.75", "120,130,127,0.1", "80,80,127,6.0" })
	void wsqRoundTripReconstructsImage(int width, int height, int noise, float bitRate) {
		byte[] pixels = texture(width, height, noise, 17L * width + height);
		int n = width * height;

		float[] fImage = new float[n];
		float[] shift = new float[1];
		float[] scale = new float[1];
		assertEquals(0, util.convertImage2Floats(fImage, shift, scale, pixels, n));

		WsqWavletTree[] wTree = waveletTree();
		WsqQuantizationTree[] qTree = quantizationTree();
		WsqTreeHelper.getInstance().buildWsqTrees(wTree, WsqConstant.W_TREELEN, qTree, WsqConstant.Q_TREELEN, width,
				height);

		assertEquals(0, util.wsqDecompose(fImage, width, height, wTree, WsqConstant.W_TREELEN, HI_7.clone(),
				HI_7.length, LO_9.clone(), LO_9.length));

		WsqQuantization quant = new WsqQuantization();
		quant.setCompressionBitRate(bitRate);
		util.variance(quant, qTree, WsqConstant.Q_TREELEN, fImage.clone(), width, height);

		short[] sip = new short[n];
		int[] cmpSize = new int[1];
		assertEquals(0, util.quantize(sip, cmpSize, quant, qTree, WsqConstant.Q_TREELEN, fImage, width, height));

		int[] q1 = new int[1];
		int[] q2 = new int[1];
		int[] q3 = new int[1];
		util.quantizedBlockSizes(q1, q2, q3, quant, wTree, WsqConstant.W_TREELEN, qTree, WsqConstant.Q_TREELEN);
		assertTrue(q1[0] + q2[0] + q3[0] <= n);

		WsqTableDqt dqt = new WsqTableDqt();
		dqt.setDqtDef(1);
		dqt.setBinCenter(0.44f);
		for (int i = 0; i < WsqConstant.MAX_SUBBANDS; i++) {
			dqt.getQBin()[i] = quant.getQbss()[i];
			dqt.getZBin()[i] = quant.getQzbs()[i];
		}
		long[] sipLong = new long[n];
		for (int i = 0; i < n; i++) {
			sipLong[i] = sip[i];
		}
		float[] restored = new float[n];
		assertEquals(0, util.unquantize(restored, dqt, qTree, WsqConstant.Q_TREELEN, sipLong, width, height));

		WsqTableDtt dtt = dtt(LO_9, HI_7);
		assertEquals(0, util.wsqReconstruct(restored, width, height, wTree, WsqConstant.W_TREELEN, dtt));

		byte[] out = new byte[n];
		util.convertImage2Bytes(out, restored, width, height, shift[0], scale[0]);
		assertTrue(meanAbsError(pixels, out) < 64.0, "reconstruction should resemble the source");
	}

	@ParameterizedTest(name = "lo={0} hi={1} len2={2} inv={3}")
	@CsvSource({ "8,8,16,0", "8,8,17,1", "4,4,12,0", "4,4,13,1", "2,2,10,0", "2,2,11,1", "9,7,16,1", "9,7,17,0",
			"6,2,14,0", "2,6,15,1" })
	void getLetsAndJoinLetsHandleFilterAndLengthParity(int loSize, int hiSize, int len2, int inv) {
		int len1 = len2;
		float[] lo = filter(loSize, 0.5f);
		float[] hi = filter(hiSize, -0.25f);
		float[] src = new float[len1 * len2];
		Random random = new Random(len2 * 31L + loSize);
		for (int i = 0; i < src.length; i++) {
			src[i] = random.nextFloat() * 200f - 100f;
		}

		float[] split = new float[src.length];
		util.getLets(split, 0, src.clone(), 0, len1, len2, len2, 1, hi, hiSize, lo, loSize, inv);
		float[] joined = new float[src.length];
		util.joinLets(joined, 0, split, 0, len1, len2, len2, 1, hi, hiSize, lo, loSize, inv);

		float[] column = new float[src.length];
		util.getLets(column, 0, src.clone(), 0, len2, len1, 1, len2, hi, hiSize, lo, loSize, inv);
		float[] columnJoined = new float[src.length];
		util.joinLets(columnJoined, 0, column, 0, len2, len1, 1, len2, hi, hiSize, lo, loSize, inv);

		assertTrue(isFinite(joined) && isFinite(columnJoined));
		assertNotEquals(0f, sumAbs(split));
		assertEquals(filter(hiSize, -0.25f)[0], hi[0], 0f, "high-pass filter must be restored after use");
	}

	@Test
	void reconstructRejectsUndefinedFilters() {
		WsqWavletTree[] wTree = waveletTree();
		float[] image = new float[64 * 64];
		WsqTableDtt dtt = dtt(LO_9, HI_7);
		dtt.setLowDef(0);
		assertNotEquals(0, util.wsqReconstruct(image, 64, 64, wTree, WsqConstant.W_TREELEN, dtt));
		dtt.setLowDef(1);
		dtt.setHighDef(0);
		assertNotEquals(0, util.wsqReconstruct(image, 64, 64, wTree, WsqConstant.W_TREELEN, dtt));
	}

	@Test
	void unquantizeRejectsUndefinedTable() {
		WsqTableDqt dqt = new WsqTableDqt();
		assertNotEquals(0, util.unquantize(new float[16], dqt, quantizationTree(), WsqConstant.Q_TREELEN,
				new long[16], 4, 4));
	}

	@Test
	void convertImage2BytesClampsOutOfRange() {
		byte[] out = new byte[3];
		util.convertImage2Bytes(out, new float[] { -10f, 0f, 10f }, 3, 1, 128f, 64f);
		assertEquals(0, out[0]);
		assertEquals((byte) 128, out[1]);
		assertEquals((byte) 255, out[2]);
	}

	private static WsqWavletTree[] waveletTree() {
		WsqWavletTree[] tree = new WsqWavletTree[WsqConstant.W_TREELEN];
		for (int i = 0; i < tree.length; i++) {
			tree[i] = new WsqWavletTree();
		}
		return tree;
	}

	private static WsqQuantizationTree[] quantizationTree() {
		WsqQuantizationTree[] tree = new WsqQuantizationTree[WsqConstant.Q_TREELEN];
		for (int i = 0; i < tree.length; i++) {
			tree[i] = new WsqQuantizationTree();
		}
		return tree;
	}

	private static WsqTableDtt dtt(float[] lo, float[] hi) {
		WsqTableDtt dtt = new WsqTableDtt();
		dtt.setLowFilter(lo.clone());
		dtt.setHighFilter(hi.clone());
		dtt.setLowSize(lo.length);
		dtt.setHighSize(hi.length);
		dtt.setLowDef(1);
		dtt.setHighDef(1);
		return dtt;
	}

	private static float[] filter(int size, float base) {
		float[] f = new float[size];
		for (int i = 0; i < size; i++) {
			f[i] = base / (1 + Math.abs(i - size / 2));
		}
		return f;
	}

	private static byte[] texture(int width, int height, int noise, long seed) {
		Random random = new Random(seed);
		byte[] data = new byte[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int ridge = (int) (64 * Math.sin((x + y) / 3.0));
				int v = 128 + ridge + random.nextInt(2 * noise + 1) - noise;
				data[y * width + x] = (byte) Math.max(0, Math.min(255, v));
			}
		}
		return data;
	}

	private static double meanAbsError(byte[] a, byte[] b) {
		long sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += Math.abs((a[i] & 0xff) - (b[i] & 0xff));
		}
		return sum / (double) a.length;
	}

	private static boolean isFinite(float[] values) {
		for (float v : values) {
			if (!Float.isFinite(v)) {
				return false;
			}
		}
		return true;
	}

	private static float sumAbs(float[] values) {
		float s = 0;
		for (float v : values) {
			s += Math.abs(v);
		}
		return s;
	}
}
