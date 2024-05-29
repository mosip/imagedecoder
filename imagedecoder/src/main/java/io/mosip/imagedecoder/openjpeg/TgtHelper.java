package io.mosip.imagedecoder.openjpeg;

import io.mosip.imagedecoder.model.openjpeg.Bio;
import io.mosip.imagedecoder.model.openjpeg.TgtNode;
import io.mosip.imagedecoder.model.openjpeg.TgtTree;

public class TgtHelper {
	// Static variable reference of singleInstance of type Singleton
	private static TgtHelper singleInstance = null;

	private TgtHelper() {
		super();
	}

	// synchronized method to control simultaneous access
	public static synchronized TgtHelper getInstance() {
		if (singleInstance == null)
			singleInstance = new TgtHelper();

		return singleInstance;
	}

	/*
	 * ========================================================== Tag-tree coder
	 * interface ==========================================================
	 */
	@SuppressWarnings({ "java:S1659", "java:S3776" })
	public TgtTree tgtCreate(int numleafsh, int numleafsv) {
		int[] nplh = new int[32];
		int[] nplv = new int[32];
		int parentNodeIndex = 0;
		int parentNode0Index = 0;
		TgtTree tree = null;
		int i, j, k;
		int numlvls;
		int n;

		tree = new TgtTree();
		tree.setNoOfLeafSH(numleafsh);
		tree.setNoOfLeafSV(numleafsv);

		numlvls = 0;
		nplh[0] = numleafsh;
		nplv[0] = numleafsv;
		tree.setNoOfNodes(0);
		do {
			n = nplh[numlvls] * nplv[numlvls];
			nplh[numlvls + 1] = (nplh[numlvls] + 1) / 2;
			nplv[numlvls + 1] = (nplv[numlvls] + 1) / 2;
			tree.setNoOfNodes(tree.getNoOfNodes() + n);
			++numlvls;
		} while (n > 1);

		/* ADD */
		if (tree.getNoOfNodes() == 0) {
			tree = null;
			return tree;
		}

		tree.setNodes(new TgtNode[tree.getNoOfNodes()]);
		for (i = 0; i < tree.getNoOfNodes(); ++i) {
			tree.getNodes()[i] = new TgtNode();
		}
		int nodeIndex = 0;
		parentNodeIndex = tree.getNoOfLeafSH() * tree.getNoOfLeafSV();
		parentNode0Index = parentNodeIndex;

		for (i = 0; i < numlvls - 1; ++i) {
			for (j = 0; j < nplv[i]; ++j) {
				k = nplh[i];
				while (--k >= 0) {
					tree.getNodes()[nodeIndex].setParent(parentNodeIndex);
					++nodeIndex;
					if (--k >= 0) {
						tree.getNodes()[nodeIndex].setParent(parentNodeIndex);
						++nodeIndex;
					}
					++parentNodeIndex;
				}
				if ((j & 1) != 0 || j == nplv[i] - 1) {
					parentNode0Index = parentNodeIndex;
				} else {
					parentNodeIndex = parentNode0Index;
					parentNode0Index += nplh[i];
				}
			}
		}
		tree.getNodes()[nodeIndex].setParent(0);

		tgtReset(tree);

		return tree;
	}

	public void tgtDestroy(TgtTree tree) {
		tree.setNodes(null);
	}

	public void tgtReset(TgtTree tree) {
		int i;

		if (tree == null)
			return;

		for (i = 0; i < tree.getNoOfNodes(); i++) {
			tree.getNodes()[i].setValue(999);
			tree.getNodes()[i].setLow(0);
			tree.getNodes()[i].setKnown(0);
		}
	}

	public void tgtSetValue(TgtTree tree, int leafNo, int value) {
		TgtNode node;
		node = tree.getNodes()[leafNo];
		while (node != null && node.getValue() > value) {
			node.setValue(value);
			node = tree.getNodes()[node.getParent()];
		}
	}

	@SuppressWarnings({ "java:S3776" })
	public void tgtEncode(Bio bio, TgtTree tree, int leafNo, int threshold) {
		TgtNode[] stack = new TgtNode[31];
		TgtNode[] stkptr;
		TgtNode node;

		int low;
		int stackIndex = 0;
		int stackDataIndex = 0;
		stkptr = stack;
		node = tree.getNodes()[leafNo];
		while (tree.getNodes()[node.getParent()] != null) {
			stkptr[stackDataIndex++] = node;
			node = tree.getNodes()[node.getParent()];
		}

		low = 0;
		for (;;) {
			if (low > node.getLow()) {
				node.setLow(low);
			} else {
				low = node.getLow();
			}

			while (low < threshold) {
				if (low >= node.getValue()) {
					if (node.getKnown() == 0) {
						BioHelper.getInstance().bioWrite(bio, 1, 1);
						node.setKnown(1);
					}
					break;
				}
				BioHelper.getInstance().bioWrite(bio, 0, 1);
				++low;
			}

			node.setLow(low);
			if (stackDataIndex == stackIndex)
				break;
			node = stkptr[--stackDataIndex];
		}
	}

	public int tgtDecode(Bio bio, TgtTree tree, int leafNo, int threshold) {
		TgtNode[] stack = new TgtNode[31];
		TgtNode node;
		int low;

		int stackIndex = 0;
		int stackDataIndex = stackIndex;
		node = tree.getNodes()[leafNo];
		while (node.getParent() != 0) {
			stack[stackDataIndex++] = node;
			node = tree.getNodes()[node.getParent()];
		}

		low = 0;
		for (;;) {
			if (low > node.getLow()) {
				node.setLow(low);
			} else {
				low = node.getLow();
			}
			while (low < threshold && low < node.getValue()) {
				if (BioHelper.getInstance().bioRead(bio, 1) != 0) {
					node.setValue(low);
				} else {
					++low;
				}
			}
			node.setLow(low);
			if (stackDataIndex == stackIndex) {
				break;
			}
			node = stack[--stackDataIndex];
		}

		return (node.getValue() < threshold) ? 1 : 0;
	}
}