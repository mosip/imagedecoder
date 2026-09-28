package io.mosip.imagedecoder.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import io.mosip.imagedecoder.util.openjpeg.MathUtil;

class MathUtilTest {

	@Test
	void singleton() {
		assertSame(MathUtil.getInstance(), MathUtil.getInstance());
	}

	@Test
	void minMaxClampAbs() {
		assertEquals(1, MathUtil.getInstance().intMin(1, 2));
		assertEquals(2, MathUtil.getInstance().intMax(1, 2));
		assertEquals(0, MathUtil.getInstance().intClamp(-1, 0, 5));
		assertEquals(5, MathUtil.getInstance().intClamp(9, 0, 5));
		assertEquals(3, MathUtil.getInstance().intClamp(3, 0, 5));
		assertEquals(4, MathUtil.getInstance().intAbs(-4));
		assertEquals(4, MathUtil.getInstance().intAbs(4));
	}

	@Test
	void ceilDiv() {
		assertEquals(3, MathUtil.getInstance().intCeilDiv(5, 2));
		assertEquals(2, MathUtil.getInstance().intCeilDivPow2(5, 2));
		assertEquals(1, MathUtil.getInstance().intFloorDivPow2(5, 2));
		assertEquals(3, MathUtil.getInstance().intFloorLog2(8));
		assertEquals(0, MathUtil.getInstance().intFloorLog2(1));
	}
}