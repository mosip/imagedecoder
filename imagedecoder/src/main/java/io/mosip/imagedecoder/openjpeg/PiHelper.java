package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.constant.openjpeg.OpenJpegConstant;
import io.mosip.imagedecoder.model.openjpeg.CodingParameters;
import io.mosip.imagedecoder.model.openjpeg.J2KT2Mode;
import io.mosip.imagedecoder.model.openjpeg.OpenJpegImage;
import io.mosip.imagedecoder.model.openjpeg.PiComponent;
import io.mosip.imagedecoder.model.openjpeg.PiIterator;
import io.mosip.imagedecoder.model.openjpeg.PiResolution;
import io.mosip.imagedecoder.model.openjpeg.Poc;
import io.mosip.imagedecoder.model.openjpeg.Tcp;
import io.mosip.imagedecoder.model.openjpeg.TileComponentCodingParameters;
import io.mosip.imagedecoder.util.openjpeg.MathUtil;

public class PiHelper {
	// Static variable reference of singleInstance of type Singleton
	private static PiHelper singleInstance = null;

	private PiHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized PiHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new PiHelper();

		return singleInstance;
	}

	@SuppressWarnings({ "java:S1854", "java:S3776"})
	private int piNextLRCP(PiIterator pi) {
		PiComponent comp = null;
		PiResolution res = null;
		int index = 0;
		boolean labelSkip = false;
		if (pi.getFirst() == 0) {
			comp = pi.getComps()[pi.getCompNo()];
			res = comp.getResolutions()[pi.getResNo()];
			labelSkip = true;
		} else {
			pi.setFirst(0);
		}

		for (pi.setLayNo(pi.getPoc().getLayNo0()); pi.getLayNo() < pi.getPoc().getLayNo1(); pi
				.setLayNo(pi.getLayNo() + 1)) {
			for (pi.setResNo(pi.getPoc().getResNo0()); pi.getResNo() < pi.getPoc().getResNo1(); pi
					.setResNo(pi.getResNo() + 1)) {
				for (pi.setCompNo(pi.getPoc().getCompNo0()); pi.getCompNo() < pi.getPoc().getCompNo1(); pi
						.setCompNo(pi.getCompNo() + 1)) {
					if (!labelSkip) {
						comp = pi.getComps()[pi.getCompNo()];
						if (pi.getResNo() >= comp.getNoOfResolutions()) {
							continue;
						}
						res = comp.getResolutions()[pi.getResNo()];
						if (pi.getTilePartOn() == 0) {
							pi.getPoc().setPrecNo1(res.getPWidth() * res.getPHeight());
						}
					}
					for (pi.setPrecNo(pi.getPoc().getPrecNo0()); pi.getPrecNo() < pi.getPoc().getPrecNo1(); pi
							.setPrecNo(pi.getPrecNo() + 1)) {
						if (!labelSkip) {
							index = pi.getLayNo() * pi.getStepL() + pi.getResNo() * pi.getStepR()
									+ pi.getCompNo() * pi.getStepC() + pi.getPrecNo() * pi.getStepP();
							if (pi.getInclude()[index] == 0) {
								pi.getInclude()[index] = 1;
								return 1;
							}
						} else {
							labelSkip = false;
						}
					}
				}
			}
		}

		return 0;
	}

	@SuppressWarnings({ "java:S1854", "java:S3776"})
	private int piNextRLCP(PiIterator pi) {
		PiComponent comp = null;
		PiResolution res = null;
		int index = 0;
		boolean labelSkip = false;
		if (pi.getFirst() == 0) {
			comp = pi.getComps()[pi.getCompNo()];
			res = comp.getResolutions()[pi.getResNo()];
			labelSkip = true;
		} else {
			pi.setFirst(0);
		}

		for (pi.setResNo(pi.getPoc().getResNo0()); pi.getResNo() < pi.getPoc().getResNo1(); pi
				.setResNo(pi.getResNo() + 1)) {
			for (pi.setLayNo(pi.getPoc().getLayNo0()); pi.getLayNo() < pi.getPoc().getLayNo1(); pi
					.setLayNo(pi.getLayNo() + 1)) {
				for (pi.setCompNo(pi.getPoc().getCompNo0()); pi.getCompNo() < pi.getPoc().getCompNo1(); pi
						.setCompNo(pi.getCompNo() + 1)) {
					if (!labelSkip) {
						comp = pi.getComps()[pi.getCompNo()];
						if (pi.getResNo() >= comp.getNoOfResolutions()) {
							continue;
						}
						res = comp.getResolutions()[pi.getResNo()];
						if (pi.getTilePartOn() == 0) {
							pi.getPoc().setPrecNo1(res.getPWidth() * res.getPHeight());
						}
					}
					for (pi.setPrecNo(pi.getPoc().getPrecNo0()); pi.getPrecNo() < pi.getPoc().getPrecNo1(); pi
							.setPrecNo(pi.getPrecNo() + 1)) {
						if (!labelSkip) {
							index = pi.getLayNo() * pi.getStepL() + pi.getResNo() * pi.getStepR()
									+ pi.getCompNo() * pi.getStepC() + pi.getPrecNo() * pi.getStepP();
							if (pi.getInclude()[index] == 0) {
								pi.getInclude()[index] = 1;
								return 1;
							}
						} else {
							labelSkip = false;
						}
					}
				}
			}
		}

		return 0;
	}

	@SuppressWarnings({ "java:S117", "java:S135", "java:S1659", "java:S3776", "java:S6541" })
	private int piNextRPCL(PiIterator pi) {
		PiComponent comp = null;
		PiResolution res = null;
		int index = 0;
		boolean labelSkip = false;

		if (pi.getFirst() == 0) {
			labelSkip = true;
		} else {
			int compNo, resNo;
			pi.setFirst(0);
			pi.setDX(0);
			pi.setDY(0);
			for (compNo = 0; compNo < pi.getNoOfComps(); compNo++) {
				comp = pi.getComps()[compNo];
				for (resNo = 0; resNo < comp.getNoOfResolutions(); resNo++) {
					int dx;
					int dy;
					res = comp.getResolutions()[resNo];
					dx = comp.getDX() * (1 << (res.getPDX() + comp.getNoOfResolutions() - 1 - resNo));
					dy = comp.getDY() * (1 << (res.getPDY() + comp.getNoOfResolutions() - 1 - resNo));
					pi.setDX(pi.getDX() == 0 ? dx : MathUtil.getInstance().intMin(pi.getDX(), dx));
					pi.setDY(pi.getDY() == 0 ? dy : MathUtil.getInstance().intMin(pi.getDY(), dy));
				}
			}
		}
		if (!labelSkip && pi.getTilePartOn() == 0) {
			pi.getPoc().setTY0(pi.getTY0());
			pi.getPoc().setTX0(pi.getTX0());
			pi.getPoc().setTY1(pi.getTY1());
			pi.getPoc().setTX1(pi.getTX1());
		}
		for (pi.setResNo(pi.getPoc().getResNo0()); pi.getResNo() < pi.getPoc().getResNo1(); pi
				.setResNo(pi.getResNo() + 1)) {
			for (pi.setY(pi.getPoc().getTY0()); pi.getY() < pi.getPoc().getTY1(); pi
					.setY(pi.getY() + pi.getDY() - (pi.getY() % pi.getDY()))) {
				for (pi.setX(pi.getPoc().getTX0()); pi.getX() < pi.getPoc().getTX1(); pi
						.setX(pi.getX() + pi.getDX() - (pi.getX() % pi.getDX()))) {
					for (pi.setCompNo(pi.getPoc().getCompNo0()); pi.getCompNo() < pi.getPoc().getCompNo1(); pi
							.setCompNo(pi.getCompNo() + 1)) {
						if (!labelSkip) {
							int levelNo;
							int trx0, try0;
							int trx1, try1;
							int rpx, rpy;
							int prci, prcj;
							comp = pi.getComps()[pi.getCompNo()];
							if (pi.getResNo() >= comp.getNoOfResolutions()) {
								continue;
							}
							res = comp.getResolutions()[pi.getResNo()];
							levelNo = comp.getNoOfResolutions() - 1 - pi.getResNo();
							trx0 = MathUtil.getInstance().intCeilDiv(pi.getTX0(), comp.getDX() << levelNo);
							try0 = MathUtil.getInstance().intCeilDiv(pi.getTY0(), comp.getDY() << levelNo);
							trx1 = MathUtil.getInstance().intCeilDiv(pi.getTX1(), comp.getDX() << levelNo);
							try1 = MathUtil.getInstance().intCeilDiv(pi.getTY1(), comp.getDY() << levelNo);
							rpx = res.getPDX() + levelNo;
							rpy = res.getPDY() + levelNo;
							if (!((pi.getY() % (comp.getDY() << rpy) == 0)
									|| ((pi.getY() == pi.getTY0()) && ((try0 << levelNo) % (1 << rpx)) != 0))) {
								continue;
							}
							if (!((pi.getX() % (comp.getDX() << rpx) == 0)
									|| ((pi.getX() == pi.getTX0()) && ((trx0 << levelNo) % (1 << rpx)) != 0))) {
								continue;
							}

							if (res.getPWidth() == 0)
								continue;

							if ((trx0 == trx1) || (try0 == try1))
								continue;

							prci = MathUtil.getInstance().intFloorDivPow2(
									MathUtil.getInstance().intCeilDiv(pi.getX(), comp.getDX() << levelNo), res.getPDX())
									- MathUtil.getInstance().intFloorDivPow2(trx0, res.getPDX());
							prcj = MathUtil.getInstance().intFloorDivPow2(
									MathUtil.getInstance().intCeilDiv(pi.getY(), comp.getDY() << levelNo), res.getPDY())
									- MathUtil.getInstance().intFloorDivPow2(try0, res.getPDY());
							pi.setPrecNo(prci + prcj * res.getPWidth());
						}

						for (pi.setLayNo(pi.getPoc().getLayNo0()); pi.getLayNo() < pi.getPoc().getLayNo1(); pi
								.setLayNo(pi.getLayNo() + 1)) {
							index = pi.getLayNo() * pi.getStepL() + pi.getResNo() * pi.getStepR()
									+ pi.getCompNo() * pi.getStepC() + pi.getPrecNo() * pi.getStepP();
							if (pi.getInclude()[index] == 0) {
								pi.getInclude()[index] = 1;
								return 1;
							}
							labelSkip = false;
						}
					}
				}
			}
		}

		return 0;
	}

	@SuppressWarnings({ "java:S117", "java:S135", "java:S1659", "java:S3776", "java:S6541" })
	private int piNextPCRL(PiIterator pi) {
		PiComponent comp = null;
		PiResolution res = null;
		int index = 0;
		boolean labelSkip = false;

		if (pi.getFirst() == 0) {
			comp = pi.getComps()[pi.getCompNo()];
			labelSkip = true;
		} else {
			int compNo, resNo;
			pi.setFirst(0);
			pi.setDX(0);
			pi.setDY(0);
			for (compNo = 0; compNo < pi.getNoOfComps(); compNo++) {
				comp = pi.getComps()[compNo];
				for (resNo = 0; resNo < comp.getNoOfResolutions(); resNo++) {
					int dx;
					int dy;
					res = comp.getResolutions()[resNo];
					dx = comp.getDX() * (1 << (res.getPDX() + comp.getNoOfResolutions() - 1 - resNo));
					dy = comp.getDY() * (1 << (res.getPDY() + comp.getNoOfResolutions() - 1 - resNo));
					pi.setDX(pi.getDX() == 0 ? dx : MathUtil.getInstance().intMin(pi.getDX(), dx));
					pi.setDY(pi.getDY() == 0 ? dy : MathUtil.getInstance().intMin(pi.getDY(), dy));
				}
			}
		}
		if (!labelSkip && pi.getTilePartOn() == 0) {
			pi.getPoc().setTY0(pi.getTY0());
			pi.getPoc().setTX0(pi.getTX0());
			pi.getPoc().setTY1(pi.getTY1());
			pi.getPoc().setTX1(pi.getTX1());
		}
		for (pi.setY(pi.getPoc().getTY0()); pi.getY() < pi.getPoc().getTY1(); pi
				.setY(pi.getY() + pi.getDY() - (pi.getY() % pi.getDY()))) {
			for (pi.setX(pi.getPoc().getTX0()); pi.getX() < pi.getPoc().getTX1(); pi
					.setX(pi.getX() + pi.getDX() - (pi.getX() % pi.getDX()))) {
				for (pi.setCompNo(pi.getPoc().getCompNo0()); pi.getCompNo() < pi.getPoc().getCompNo1(); pi
						.setCompNo(pi.getCompNo() + 1)) {
					if (!labelSkip) {
						comp = pi.getComps()[pi.getCompNo()];
					}
					for (pi.setResNo(pi.getPoc().getResNo0()); pi.getResNo() < MathUtil.getInstance().intMin(
							pi.getPoc().getResNo1(), comp.getNoOfResolutions()); pi.setResNo(pi.getResNo() + 1)) {
						if (!labelSkip) {
							int levelNo;
							int trx0, try0;
							int trx1, try1;
							int rpx, rpy;
							int prci, prcj;
							res = comp.getResolutions()[pi.getResNo()];
							levelNo = comp.getNoOfResolutions() - 1 - pi.getResNo();
							trx0 = MathUtil.getInstance().intCeilDiv(pi.getTX0(), comp.getDX() << levelNo);
							try0 = MathUtil.getInstance().intCeilDiv(pi.getTY0(), comp.getDY() << levelNo);
							trx1 = MathUtil.getInstance().intCeilDiv(pi.getTX1(), comp.getDX() << levelNo);
							try1 = MathUtil.getInstance().intCeilDiv(pi.getTY1(), comp.getDY() << levelNo);
							rpx = res.getPDX() + levelNo;
							rpy = res.getPDY() + levelNo;
							if (!((pi.getY() % (comp.getDY() << rpy) == 0)
									|| ((pi.getY() == pi.getTY0()) && ((try0 << levelNo) % (1 << rpx)) != 0))) {
								continue;
							}
							if (!((pi.getX() % (comp.getDX() << rpx) == 0)
									|| ((pi.getX() == pi.getTX0()) && ((trx0 << levelNo) % (1 << rpx)) != 0))) {
								continue;
							}

							if (res.getPWidth() == 0)
								continue;

							if ((trx0 == trx1) || (try0 == try1))
								continue;

							prci = MathUtil.getInstance().intFloorDivPow2(
									MathUtil.getInstance().intCeilDiv(pi.getX(), comp.getDX() << levelNo), res.getPDX())
									- MathUtil.getInstance().intFloorDivPow2(trx0, res.getPDX());
							prcj = MathUtil.getInstance().intFloorDivPow2(
									MathUtil.getInstance().intCeilDiv(pi.getY(), comp.getDY() << levelNo), res.getPDY())
									- MathUtil.getInstance().intFloorDivPow2(try0, res.getPDY());
							pi.setPrecNo(prci + prcj * res.getPWidth());
						}
						for (pi.setLayNo(pi.getPoc().getLayNo0()); pi.getLayNo() < pi.getPoc().getLayNo1(); pi
								.setLayNo(pi.getLayNo() + 1)) {
							if (!labelSkip) {
								index = pi.getLayNo() * pi.getStepL() + pi.getResNo() * pi.getStepR()
										+ pi.getCompNo() * pi.getStepC() + pi.getPrecNo() * pi.getStepP();
								if (pi.getInclude()[index] == 0) {
									pi.getInclude()[index] = 1;
									return 1;
								}
							} else {
								labelSkip = false;
							}
						}
					}
				}
			}
		}

		return 0;
	}

	@SuppressWarnings({ "java:S117", "java:S135", "java:S1659", "java:S3776", "java:S6541" })
	private int piNextCPRL(PiIterator pi) {
		PiComponent comp = null;
		PiResolution res = null;
		int index = 0;
		boolean labelSkip = false;

		if (pi.getFirst() == 0) {
			comp = pi.getComps()[pi.getCompNo()];
			labelSkip = true;
		} else {
			pi.setFirst(0);
		}

		for (pi.setCompNo(pi.getPoc().getCompNo0()); pi.getCompNo() < pi.getPoc().getCompNo1(); pi
				.setCompNo(pi.getCompNo() + 1)) {
			int resNo;
			if (!labelSkip) {
				comp = pi.getComps()[pi.getCompNo()];
				pi.setDX(0);
				pi.setDY(0);

				for (resNo = 0; resNo < comp.getNoOfResolutions(); resNo++) {
					int dx;
					int dy;
					res = comp.getResolutions()[resNo];
					dx = comp.getDX() * (1 << (res.getPDX() + comp.getNoOfResolutions() - 1 - resNo));
					dy = comp.getDY() * (1 << (res.getPDY() + comp.getNoOfResolutions() - 1 - resNo));
					pi.setDX(pi.getDX() == 0 ? dx : MathUtil.getInstance().intMin(pi.getDX(), dx));
					pi.setDY(pi.getDY() == 0 ? dy : MathUtil.getInstance().intMin(pi.getDY(), dy));
				}
				if (pi.getTilePartOn() == 0) {
					pi.getPoc().setTY0(pi.getTY0());
					pi.getPoc().setTX0(pi.getTX0());
					pi.getPoc().setTY1(pi.getTY1());
					pi.getPoc().setTX1(pi.getTX1());
				}
			}
			for (pi.setY(pi.getPoc().getTY0()); pi.getY() < pi.getPoc().getTY1(); pi
					.setY(pi.getY() + pi.getDY() - (pi.getY() % pi.getDY()))) {
				for (pi.setX(pi.getPoc().getTX0()); pi.getX() < pi.getPoc().getTX1(); pi
						.setX(pi.getX() + pi.getDX() - (pi.getX() % pi.getDX()))) {
					for (pi.setResNo(pi.getPoc().getResNo0()); pi.getResNo() < MathUtil.getInstance().intMin(
							pi.getPoc().getResNo1(), comp.getNoOfResolutions()); pi.setResNo(pi.getResNo() + 1)) {
						if (!labelSkip) {
							int levelNo;
							int trx0, try0;
							int trx1, try1;
							int rpx, rpy;
							int prci, prcj;
							res = comp.getResolutions()[pi.getResNo()];
							levelNo = comp.getNoOfResolutions() - 1 - pi.getResNo();
							trx0 = MathUtil.getInstance().intCeilDiv(pi.getTX0(), comp.getDX() << levelNo);
							try0 = MathUtil.getInstance().intCeilDiv(pi.getTY0(), comp.getDY() << levelNo);
							trx1 = MathUtil.getInstance().intCeilDiv(pi.getTX1(), comp.getDX() << levelNo);
							try1 = MathUtil.getInstance().intCeilDiv(pi.getTY1(), comp.getDY() << levelNo);
							rpx = res.getPDX() + levelNo;
							rpy = res.getPDY() + levelNo;
							if (!((pi.getY() % (comp.getDY() << rpy) == 0)
									|| ((pi.getY() == pi.getTY0()) && ((try0 << levelNo) % (1 << rpx)) != 0))) {
								continue;
							}
							if (!((pi.getX() % (comp.getDX() << rpx) == 0)
									|| ((pi.getX() == pi.getTX0()) && ((trx0 << levelNo) % (1 << rpx)) != 0))) {
								continue;
							}

							if (res.getPWidth() == 0)
								continue;

							if ((trx0 == trx1) || (try0 == try1))
								continue;

							prci = MathUtil.getInstance().intFloorDivPow2(
									MathUtil.getInstance().intCeilDiv(pi.getX(), comp.getDX() << levelNo), res.getPDX())
									- MathUtil.getInstance().intFloorDivPow2(trx0, res.getPDX());
							prcj = MathUtil.getInstance().intFloorDivPow2(
									MathUtil.getInstance().intCeilDiv(pi.getY(), comp.getDY() << levelNo), res.getPDY())
									- MathUtil.getInstance().intFloorDivPow2(try0, res.getPDY());
							pi.setPrecNo(prci + prcj * res.getPWidth());
						}
						for (pi.setLayNo(pi.getPoc().getLayNo0()); pi.getLayNo() < pi.getPoc().getLayNo1(); pi
								.setLayNo(pi.getLayNo() + 1)) {
							if (!labelSkip) {
								index = pi.getLayNo() * pi.getStepL() + pi.getResNo() * pi.getStepR()
										+ pi.getCompNo() * pi.getStepC() + pi.getPrecNo() * pi.getStepP();
								if (pi.getInclude()[index] == 0) {
									pi.getInclude()[index] = 1;
									return 1;
								}
							} else {
								labelSkip = false;
							}
						}
					}
				}
			}
		}

		return 0;
	}

	/*
	 * =============================Packet iterator interface
	 * ===========================================
	 */
	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776" })
	public PiIterator[] piCreateDecode(OpenJpegImage image, CodingParameters codingParameters, int tileno) {
		int p, q;
		int compNo, resNo, piNo = 0;
		PiIterator[] pi = null;
		Tcp tcp = null;
		TileComponentCodingParameters tccp = null;

		tcp = codingParameters.getTcps()[tileno];

		pi = new PiIterator[(tcp.getNoOfPocs() + 1)];

		for (piNo = 0; piNo < tcp.getNoOfPocs() + 1; piNo++) { /* change */
			pi[piNo] = new PiIterator();
			pi[piNo].setPoc(new Poc());
			int maxres = 0;
			int maxprec = 0;
			p = tileno % codingParameters.getTileWidth();
			q = tileno / codingParameters.getTileWidth();

			pi[piNo].setTX0(MathUtil.getInstance()
					.intMax(codingParameters.getTileX0() + p * codingParameters.getTileDX(), image.getX0()));
			pi[piNo].setTY0(MathUtil.getInstance()
					.intMax(codingParameters.getTileY0() + q * codingParameters.getTileDY(), image.getY0()));
			pi[piNo].setTX1(MathUtil.getInstance()
					.intMin(codingParameters.getTileX0() + (p + 1) * codingParameters.getTileDX(), image.getX1()));
			pi[piNo].setTY1(MathUtil.getInstance()
					.intMin(codingParameters.getTileX0() + (q + 1) * codingParameters.getTileDY(), image.getY1()));
			pi[piNo].setNoOfComps(image.getNoOfComps());

			pi[piNo].setComps(new PiComponent[image.getNoOfComps()]);
			for (compNo = 0; compNo < pi[piNo].getNoOfComps(); compNo++) {
				pi[piNo].getComps()[compNo] = new PiComponent();
				int tcx0, tcy0, tcx1, tcy1;
				PiComponent comp = pi[piNo].getComps()[compNo];
				tccp = tcp.getTccps()[compNo];
				comp.setDX(image.getComps()[compNo].getDX());
				comp.setDY(image.getComps()[compNo].getDY());
				comp.setNoOfResolutions(tccp.getNoOfResolutions());

				comp.setResolutions(new PiResolution[comp.getNoOfResolutions()]);
				tcx0 = MathUtil.getInstance().intCeilDiv(pi[0].getTX0(), comp.getDX());
				tcy0 = MathUtil.getInstance().intCeilDiv(pi[0].getTY0(), comp.getDY());
				tcx1 = MathUtil.getInstance().intCeilDiv(pi[0].getTX1(), comp.getDX());
				tcy1 = MathUtil.getInstance().intCeilDiv(pi[0].getTY1(), comp.getDY());
				if (comp.getNoOfResolutions() > maxres) {
					maxres = comp.getNoOfResolutions();
				}

				for (resNo = 0; resNo < comp.getNoOfResolutions(); resNo++) {
					comp.getResolutions()[resNo] = new PiResolution();
					int levelNo;
					int rx0, ry0, rx1, ry1;
					int px0, py0, px1, py1;
					PiResolution res = comp.getResolutions()[resNo];
					if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CCP_CSTY_PRT) != 0) {
						res.setPDX(tccp.getPrecinctWidth()[resNo]);
						res.setPDY(tccp.getPrecinctHeight()[resNo]);
					} else {
						res.setPDX(15);
						res.setPDY(15);
					}
					levelNo = comp.getNoOfResolutions() - 1 - resNo;
					rx0 = MathUtil.getInstance().intCeilDivPow2(tcx0, levelNo);
					ry0 = MathUtil.getInstance().intCeilDivPow2(tcy0, levelNo);
					rx1 = MathUtil.getInstance().intCeilDivPow2(tcx1, levelNo);
					ry1 = MathUtil.getInstance().intCeilDivPow2(tcy1, levelNo);
					px0 = MathUtil.getInstance().intFloorDivPow2(rx0, res.getPDX()) << res.getPDX();
					py0 = MathUtil.getInstance().intFloorDivPow2(ry0, res.getPDY()) << res.getPDY();
					px1 = MathUtil.getInstance().intCeilDivPow2(rx1, res.getPDX()) << res.getPDX();
					py1 = MathUtil.getInstance().intCeilDivPow2(ry1, res.getPDY()) << res.getPDY();
					res.setPWidth((rx0 == rx1) ? 0 : ((px1 - px0) >> res.getPDX()));
					res.setPHeight((ry0 == ry1) ? 0 : ((py1 - py0) >> res.getPDY()));

					if (res.getPWidth() * res.getPHeight() > maxprec) {
						maxprec = res.getPWidth() * res.getPHeight();
					}

				}
			}

			tccp = tcp.getTccps()[0];
			pi[piNo].setStepP(1);
			pi[piNo].setStepC(maxprec * pi[piNo].getStepP());
			pi[piNo].setStepR(image.getNoOfComps() * pi[piNo].getStepC());
			pi[piNo].setStepL(maxres * pi[piNo].getStepR());

			if (piNo == 0) {
				pi[piNo].setInclude(new int[image.getNoOfComps() * maxres * tcp.getNoOfLayers() * maxprec]);
			} else {
				pi[piNo].setInclude(pi[piNo - 1].getInclude());
			}

			if (tcp.getIsPoc() == 0) {
				pi[piNo].setFirst(1);
				pi[piNo].getPoc().setResNo0(0);
				pi[piNo].getPoc().setCompNo0(0);
				pi[piNo].getPoc().setLayNo1(tcp.getNoOfLayers());
				pi[piNo].getPoc().setResNo1(maxres);
				pi[piNo].getPoc().setCompNo1(image.getNoOfComps());
				pi[piNo].getPoc().setProgressionOrder(tcp.getProgressionOrder());
			} else {
				pi[piNo].setFirst(1);
				pi[piNo].getPoc().setResNo0(tcp.getPocs()[piNo].getResNo0());
				pi[piNo].getPoc().setCompNo0(tcp.getPocs()[piNo].getCompNo0());
				pi[piNo].getPoc().setLayNo1(tcp.getPocs()[piNo].getLayNo1());
				pi[piNo].getPoc().setResNo1(tcp.getPocs()[piNo].getResNo1());
				pi[piNo].getPoc().setCompNo1(tcp.getPocs()[piNo].getCompNo1());
				pi[piNo].getPoc().setProgressionOrder(tcp.getPocs()[piNo].getProgressionOrder());
			}
			pi[piNo].getPoc().setLayNo0(0);
			pi[piNo].getPoc().setPrecNo0(0);
			pi[piNo].getPoc().setPrecNo1(maxprec);

		}

		return pi;
	}

	@SuppressWarnings({ "java:S1659", "java:S1854", "java:S3776", "java:S6541"})
	public PiIterator[] piInitEncode(OpenJpegImage image, CodingParameters codingParameters, int tileno,
			J2KT2Mode t2Mode) {
		int p, q, piNo = 0;
		int compNo, resNo;
		int maxres = 0;
		int maxprec = 0;
		PiIterator[] pi = null;
		Tcp tcp = null;
		TileComponentCodingParameters tccp = null;

		tcp = codingParameters.getTcps()[tileno];

		pi = new PiIterator[tcp.getNoOfPocs() + 1];

		for (piNo = 0; piNo < tcp.getNoOfPocs() + 1; piNo++) {
			pi[piNo] = new PiIterator();
			pi[piNo].setTilePartOn(codingParameters.getTilePartOn());

			p = tileno % codingParameters.getTileWidth();
			q = tileno / codingParameters.getTileWidth();

			pi[piNo].setTX0(MathUtil.getInstance()
					.intMax(codingParameters.getTileX0() + p * codingParameters.getTileDX(), image.getX0()));
			pi[piNo].setTY0(MathUtil.getInstance()
					.intMax(codingParameters.getTileY0() + q * codingParameters.getTileDY(), image.getY0()));
			pi[piNo].setTX1(MathUtil.getInstance()
					.intMin(codingParameters.getTileX0() + (p + 1) * codingParameters.getTileDX(), image.getX1()));
			pi[piNo].setTY1(MathUtil.getInstance()
					.intMin(codingParameters.getTileY0() + (q + 1) * codingParameters.getTileDY(), image.getY1()));
			pi[piNo].setNoOfComps(image.getNoOfComps());

			pi[piNo].setComps(new PiComponent[image.getNoOfComps()]);
			for (compNo = 0; compNo < pi[piNo].getNoOfComps(); compNo++) {
				pi[piNo].getComps()[compNo] = new PiComponent();
				int tcx0, tcy0, tcx1, tcy1;
				PiComponent comp = pi[piNo].getComps()[compNo];
				tccp = tcp.getTccps()[compNo];
				comp.setDX(image.getComps()[compNo].getDX());
				comp.setDY(image.getComps()[compNo].getDY());
				comp.setNoOfResolutions(tccp.getNoOfResolutions());

				comp.setResolutions(new PiResolution[comp.getNoOfResolutions()]);

				tcx0 = MathUtil.getInstance().intCeilDiv(pi[piNo].getTX0(), comp.getDX());
				tcy0 = MathUtil.getInstance().intCeilDiv(pi[piNo].getTY0(), comp.getDY());
				tcx1 = MathUtil.getInstance().intCeilDiv(pi[piNo].getTX1(), comp.getDX());
				tcy1 = MathUtil.getInstance().intCeilDiv(pi[piNo].getTY1(), comp.getDY());
				if (comp.getNoOfResolutions() > maxres) {
					maxres = comp.getNoOfResolutions();
				}

				for (resNo = 0; resNo < comp.getNoOfResolutions(); resNo++) {
					comp.getResolutions()[resNo] = new PiResolution();
					int levelNo;
					int rx0, ry0, rx1, ry1;
					int px0, py0, px1, py1;
					PiResolution res = comp.getResolutions()[resNo];
					if ((tccp.getCodingStyle() & OpenJpegConstant.J2K_CCP_CSTY_PRT) != 0) {
						res.setPDX(tccp.getPrecinctWidth()[resNo]);
						res.setPDY(tccp.getPrecinctHeight()[resNo]);
					} else {
						res.setPDX(15);
						res.setPDY(15);
					}
					levelNo = comp.getNoOfResolutions() - 1 - resNo;
					rx0 = MathUtil.getInstance().intCeilDivPow2(tcx0, levelNo);
					ry0 = MathUtil.getInstance().intCeilDivPow2(tcy0, levelNo);
					rx1 = MathUtil.getInstance().intCeilDivPow2(tcx1, levelNo);
					ry1 = MathUtil.getInstance().intCeilDivPow2(tcy1, levelNo);
					px0 = MathUtil.getInstance().intFloorDivPow2(rx0, res.getPDX()) << res.getPDX();
					py0 = MathUtil.getInstance().intFloorDivPow2(ry0, res.getPDY()) << res.getPDY();
					px1 = MathUtil.getInstance().intCeilDivPow2(rx1, res.getPDX()) << res.getPDX();
					py1 = MathUtil.getInstance().intCeilDivPow2(ry1, res.getPDY()) << res.getPDY();
					res.setPWidth((rx0 == rx1) ? 0 : ((px1 - px0) >> res.getPDX()));
					res.setPHeight((ry0 == ry1) ? 0 : ((py1 - py0) >> res.getPDY()));

					if (res.getPWidth() * res.getPHeight() > maxprec) {
						maxprec = res.getPWidth() * res.getPHeight();
					}
				}
			}

			tccp = tcp.getTccps()[0];
			pi[piNo].setStepP(1);
			pi[piNo].setStepC(maxprec * pi[piNo].getStepP());
			pi[piNo].setStepR(image.getNoOfComps() * pi[piNo].getStepC());
			pi[piNo].setStepL(maxres * pi[piNo].getStepR());

			for (compNo = 0; compNo < pi[piNo].getNoOfComps(); compNo++) {
				PiComponent comp = pi[piNo].getComps()[compNo];
				for (resNo = 0; resNo < comp.getNoOfResolutions(); resNo++) {
					int dx;
					int dy;
					PiResolution res = comp.getResolutions()[resNo];
					dx = comp.getDX() * (1 << (res.getPDX() + comp.getNoOfResolutions() - 1 - resNo));
					dy = comp.getDY() * (1 << (res.getPDY() + comp.getNoOfResolutions() - 1 - resNo));
					pi[piNo].setDX(pi[piNo].getDX() == 0 ? dx : MathUtil.getInstance().intMin(pi[piNo].getDX(), dx));
					pi[piNo].setDY(pi[piNo].getDY() == 0 ? dy : MathUtil.getInstance().intMin(pi[piNo].getDY(), dy));
				}
			}

			if (piNo == 0) {
				pi[piNo].setInclude(new int[tcp.getNoOfLayers() * pi[piNo].getStepL()]);
			} else {
				pi[piNo].setInclude(pi[piNo - 1].getInclude());
			}

			/* Generation of boundaries for each prog flag */
			if (tcp.getIsPoc() != 0 && (codingParameters.getCinemaMode().value() != 0
					|| ((codingParameters.getCinemaMode().value() == 0) && (t2Mode == J2KT2Mode.FINAL_PASS)))) {
				tcp.getPocs()[piNo].setCompS(tcp.getPocs()[piNo].getCompNo0());
				tcp.getPocs()[piNo].setCompE(tcp.getPocs()[piNo].getCompNo1());
				tcp.getPocs()[piNo].setResS(tcp.getPocs()[piNo].getResNo0());
				tcp.getPocs()[piNo].setResE(tcp.getPocs()[piNo].getResNo1());
				tcp.getPocs()[piNo].setLayE(tcp.getPocs()[piNo].getLayNo0());
				tcp.getPocs()[piNo].setProgressionOrder(tcp.getPocs()[piNo].getProgressionOrder1());
				if (piNo > 0)
					tcp.getPocs()[piNo].setLayS((tcp.getPocs()[piNo].getLayE() > tcp.getPocs()[piNo - 1].getLayE())
							? tcp.getPocs()[piNo - 1].getLayE()
							: 0);
			} else {
				tcp.getPocs()[piNo].setCompS(0);
				tcp.getPocs()[piNo].setCompE(image.getNoOfComps());
				tcp.getPocs()[piNo].setResS(0);
				tcp.getPocs()[piNo].setResE(maxres);
				tcp.getPocs()[piNo].setLayE(0);
				tcp.getPocs()[piNo].setProgressionOrder(tcp.getProgressionOrder());
			}

			tcp.getPocs()[piNo].setPrcS(0);
			tcp.getPocs()[piNo].setPrcE(maxprec);
			tcp.getPocs()[piNo].setTXS(pi[piNo].getTX0());
			tcp.getPocs()[piNo].setTXE(pi[piNo].getTX1());
			tcp.getPocs()[piNo].setTYS(pi[piNo].getTY0());
			tcp.getPocs()[piNo].setTYE(pi[piNo].getTY1());
			tcp.getPocs()[piNo].setDX(pi[piNo].getDX());
			tcp.getPocs()[piNo].setDY(pi[piNo].getDY());
		}
		return pi;
	}

	@SuppressWarnings({ "java:S3776"})
	public void piDestroy(PiIterator[] pi, CodingParameters codingParameters, int tileno) {
		int compNo;
		int piNo;
		Tcp tcp = codingParameters.getTcps()[tileno];
		if (pi != null) {
			for (piNo = 0; piNo < tcp.getNoOfPocs() + 1; piNo++) {
				if (pi[piNo].getComps() != null) {
					for (compNo = 0; compNo < pi[piNo].getNoOfComps(); compNo++) {
						PiComponent comp = pi[piNo].getComps()[compNo];
						if (comp.getResolutions() != null) {
							comp.setResolutions(null);
						}
					}
					pi[piNo].setComps(null);
				}
				if (pi[piNo].getInclude() != null) {
					pi[piNo].setInclude(null);
				}
			}
		}
	}

	public int piNext(PiIterator pi) {
		switch (pi.getPoc().getProgressionOrder()) {
		case LRCP:
			return piNextLRCP(pi);
		case RLCP:
			return piNextRLCP(pi);
		case RPCL:
			return piNextRPCL(pi);
		case PCRL:
			return piNextPCRL(pi);
		case CPRL:
			return piNextCPRL(pi);
		case PROG_UNKNOWN:
			return 0;
		}

		return 0;
	}

	@SuppressWarnings({ "java:S107", "java:S1659", "java:S3776", "java:S6208", "java:S6541" })
	public int piCreateEncode(PiIterator[] pi, CodingParameters codingParameters, int tileno, int piNo, int tilePartNo,
			int tppos, J2KT2Mode t2Mode, int currentTotalNoOfTilePart) {
		char[] prog = new char[4];
		int i;
		int incrementTop = 1, resetX = 0;
		Tcp tcps = codingParameters.getTcps()[tileno];
		Poc tcp = tcps.getPocs()[piNo];

		pi[piNo].setFirst(1);
		pi[piNo].getPoc().setProgressionOrder(tcp.getProgressionOrder());

		switch (tcp.getProgressionOrder()) {
		case CPRL:
			prog = "CPRL".toCharArray();
			break;
		case LRCP:
			prog = "LRCP".toCharArray();
			break;
		case PCRL:
			prog = "PCRL".toCharArray();
			break;
		case RLCP:
			prog = "RLCP".toCharArray();
			break;
		case RPCL:
			prog = "RPCL".toCharArray();
			break;
		case PROG_UNKNOWN:
			return 1;
		}

		if (!(codingParameters.getTilePartOn() != 0
				&& ((codingParameters.getCinemaMode().value() == 0 && (t2Mode == J2KT2Mode.FINAL_PASS))
						|| codingParameters.getCinemaMode().value() != 0))) {
			pi[piNo].getPoc().setResNo0(tcp.getResS());
			pi[piNo].getPoc().setResNo1(tcp.getResE());
			pi[piNo].getPoc().setCompNo0(tcp.getCompS());
			pi[piNo].getPoc().setCompNo1(tcp.getCompE());
			pi[piNo].getPoc().setLayNo0(tcp.getLayS());
			pi[piNo].getPoc().setLayNo1(tcp.getLayE());
			pi[piNo].getPoc().setPrecNo0(tcp.getPrcS());
			pi[piNo].getPoc().setPrecNo1(tcp.getPrcE());
			pi[piNo].getPoc().setTX0(tcp.getTXS());
			pi[piNo].getPoc().setTY0(tcp.getTXS());
			pi[piNo].getPoc().setTX1(tcp.getTXE());
			pi[piNo].getPoc().setTY1(tcp.getTXE());
		} else {
			if (tilePartNo < currentTotalNoOfTilePart) {
				for (i = 3; i >= 0; i--) {
					switch (prog[i]) {
					case 'C':
						if (i > tppos) {
							pi[piNo].getPoc().setCompNo0(tcp.getCompS());
							pi[piNo].getPoc().setCompNo1(tcp.getCompE());
						} else {
							if (tilePartNo == 0) {
								tcp.setCompTmp(tcp.getCompS());
								pi[piNo].getPoc().setCompNo0(tcp.getCompTmp());
								pi[piNo].getPoc().setCompNo1(tcp.getCompTmp() + 1);
								tcp.setCompTmp(tcp.getCompTmp() + 1);
							} else {
								if (incrementTop == 1) {
									if (tcp.getCompTmp() == tcp.getCompE()) {
										tcp.setCompTmp(tcp.getCompS());
										pi[piNo].getPoc().setCompNo0(tcp.getCompTmp());
										pi[piNo].getPoc().setCompNo1(tcp.getCompTmp() + 1);
										tcp.setCompTmp(tcp.getCompTmp() + 1);
										incrementTop = 1;
									} else {
										pi[piNo].getPoc().setCompNo0(tcp.getCompTmp());
										pi[piNo].getPoc().setCompNo1(tcp.getCompTmp() + 1);
										tcp.setCompTmp(tcp.getCompTmp() + 1);
										incrementTop = 0;
									}
								} else {
									pi[piNo].getPoc().setCompNo0(tcp.getCompTmp() - 1);
									pi[piNo].getPoc().setCompNo1(tcp.getCompTmp());
								}
							}
						}
						break;

					case 'R':
						if (i > tppos) {
							pi[piNo].getPoc().setResNo0(tcp.getResS());
							pi[piNo].getPoc().setResNo1(tcp.getResE());
						} else {
							if (tilePartNo == 0) {
								tcp.setResTmp(tcp.getResS());
								pi[piNo].getPoc().setResNo0(tcp.getResTmp());
								pi[piNo].getPoc().setResNo1(tcp.getResTmp() + 1);
								tcp.setResTmp(tcp.getResTmp() + 1);
							} else {
								if (incrementTop == 1) {
									if (tcp.getResTmp() == tcp.getResE()) {
										tcp.setResTmp(tcp.getResS());
										pi[piNo].getPoc().setResNo0(tcp.getResTmp());
										pi[piNo].getPoc().setResNo1(tcp.getResTmp() + 1);
										tcp.setResTmp(tcp.getResTmp() + 1);
										incrementTop = 1;
									} else {
										pi[piNo].getPoc().setResNo0(tcp.getResTmp());
										pi[piNo].getPoc().setResNo1(tcp.getResTmp() + 1);
										tcp.setResTmp(tcp.getResTmp() + 1);
										incrementTop = 0;
									}
								} else {
									pi[piNo].getPoc().setResNo0(tcp.getResTmp() - 1);
									pi[piNo].getPoc().setResNo1(tcp.getResTmp());
								}
							}
						}
						break;

					case 'L':
						if (i > tppos) {
							pi[piNo].getPoc().setLayNo0(tcp.getLayS());
							pi[piNo].getPoc().setLayNo1(tcp.getLayE());
						} else {
							if (tilePartNo == 0) {
								tcp.setLayTmp(tcp.getLayS());
								pi[piNo].getPoc().setLayNo0(tcp.getLayTmp());
								pi[piNo].getPoc().setLayNo1(tcp.getLayTmp() + 1);
								tcp.setLayTmp(tcp.getLayTmp() + 1);
							} else {
								if (incrementTop == 1) {
									if (tcp.getLayTmp() == tcp.getLayE()) {
										tcp.setLayTmp(tcp.getLayS());
										pi[piNo].getPoc().setLayNo0(tcp.getLayTmp());
										pi[piNo].getPoc().setLayNo1(tcp.getLayTmp() + 1);
										tcp.setLayTmp(tcp.getLayTmp() + 1);
										incrementTop = 1;
									} else {
										pi[piNo].getPoc().setLayNo0(tcp.getLayTmp());
										pi[piNo].getPoc().setLayNo1(tcp.getLayTmp() + 1);
										tcp.setLayTmp(tcp.getLayTmp() + 1);
										incrementTop = 0;
									}
								} else {
									pi[piNo].getPoc().setLayNo0(tcp.getLayTmp() - 1);
									pi[piNo].getPoc().setLayNo1(tcp.getLayTmp());
								}
							}
						}
						break;

					case 'P':
						switch (tcp.getProgressionOrder()) {
						case LRCP:
						case RLCP:
							if (i > tppos) {
								pi[piNo].getPoc().setPrecNo0(tcp.getPrcS());
								pi[piNo].getPoc().setPrecNo1(tcp.getPrcE());
							} else {
								if (tilePartNo == 0) {
									tcp.setPrcTmp(tcp.getPrcS());
									pi[piNo].getPoc().setPrecNo0(tcp.getPrcTmp());
									pi[piNo].getPoc().setPrecNo1(tcp.getPrcTmp() + 1);
									tcp.setPrcTmp(tcp.getPrcTmp() + 1);
								} else {
									if (incrementTop == 1) {
										if (tcp.getPrcTmp() == tcp.getPrcE()) {
											tcp.setPrcTmp(tcp.getPrcS());
											pi[piNo].getPoc().setPrecNo0(tcp.getPrcTmp());
											pi[piNo].getPoc().setPrecNo1(tcp.getPrcTmp() + 1);
											tcp.setPrcTmp(tcp.getPrcTmp() + 1);
											incrementTop = 1;
										} else {
											pi[piNo].getPoc().setPrecNo0(tcp.getPrcTmp());
											pi[piNo].getPoc().setPrecNo1(tcp.getPrcTmp() + 1);
											tcp.setPrcTmp(tcp.getPrcTmp() + 1);
											incrementTop = 0;
										}
									} else {
										pi[piNo].getPoc().setPrecNo0(tcp.getPrcTmp() - 1);
										pi[piNo].getPoc().setPrecNo1(tcp.getPrcTmp());
									}
								}
							}
							break;
						default:
							if (i > tppos) {
								pi[piNo].getPoc().setTX0(tcp.getTXS());
								pi[piNo].getPoc().setTY0(tcp.getTYS());
								pi[piNo].getPoc().setTX1(tcp.getTXE());
								pi[piNo].getPoc().setTY1(tcp.getTYE());
							} else {
								if (tilePartNo == 0) {
									tcp.setTx0Tmp(tcp.getTXS());
									tcp.setTy0Tmp(tcp.getTYS());
									pi[piNo].getPoc().setTX0(tcp.getTx0Tmp());
									pi[piNo].getPoc()
											.setTX1(tcp.getTx0Tmp() + tcp.getDX() - (tcp.getTx0Tmp() % tcp.getDX()));
									pi[piNo].getPoc().setTY0(tcp.getTy0Tmp());
									pi[piNo].getPoc()
											.setTY1(tcp.getTy0Tmp() + tcp.getDY() - (tcp.getTy0Tmp() % tcp.getDY()));
									tcp.setTx0Tmp(pi[piNo].getPoc().getTX1());
									tcp.setTy0Tmp(pi[piNo].getPoc().getTY1());
								} else {
									if (incrementTop == 1) {
										if (tcp.getTx0Tmp() >= tcp.getTXE()) {
											if (tcp.getTy0Tmp() >= tcp.getTYE()) {
												tcp.setTy0Tmp(tcp.getTYS());
												pi[piNo].getPoc().setTY0(tcp.getTy0Tmp());
												pi[piNo].getPoc().setTY1(tcp.getTy0Tmp() + tcp.getDY()
														- (tcp.getTy0Tmp() % tcp.getDY()));
												tcp.setTy0Tmp(pi[piNo].getPoc().getTY1());
												incrementTop = 1;
												resetX = 1;
											} else {
												pi[piNo].getPoc().setTY0(tcp.getTy0Tmp());
												pi[piNo].getPoc().setTY1(tcp.getTy0Tmp() + tcp.getDY()
														- (tcp.getTy0Tmp() % tcp.getDY()));
												tcp.setTy0Tmp(pi[piNo].getPoc().getTY1());
												incrementTop = 0;
												resetX = 1;
											}
											if (resetX == 1) {
												tcp.setTx0Tmp(tcp.getTXS());
												pi[piNo].getPoc().setTX0(tcp.getTx0Tmp());
												pi[piNo].getPoc().setTX1(tcp.getTx0Tmp() + tcp.getDX()
														- (tcp.getTx0Tmp() % tcp.getDX()));
												tcp.setTx0Tmp(pi[piNo].getPoc().getTX1());
											}
										} else {
											pi[piNo].getPoc().setTX0(tcp.getTx0Tmp());
											pi[piNo].getPoc().setTX1(
													tcp.getTx0Tmp() + tcp.getDX() - (tcp.getTx0Tmp() % tcp.getDX()));
											tcp.setTx0Tmp(pi[piNo].getPoc().getTX1());
											pi[piNo].getPoc().setTY0(
													tcp.getTy0Tmp() - tcp.getDY() - (tcp.getTy0Tmp() % tcp.getDY()));
											pi[piNo].getPoc().setTY1(tcp.getTy0Tmp());
											incrementTop = 0;
										}
									} else {
										pi[piNo].getPoc().setTX0(
												tcp.getTx0Tmp() - tcp.getDX() - (tcp.getTx0Tmp() % tcp.getDX()));
										pi[piNo].getPoc().setTX1(tcp.getTx0Tmp());
										pi[piNo].getPoc().setTY0(
												tcp.getTy0Tmp() - tcp.getDY() - (tcp.getTy0Tmp() % tcp.getDY()));
										pi[piNo].getPoc().setTY1(tcp.getTy0Tmp());
									}
								}
							}
							break;
						}
						break;
					default:
						break;
					}
				}
			}
		}
		return 0;
	}
}