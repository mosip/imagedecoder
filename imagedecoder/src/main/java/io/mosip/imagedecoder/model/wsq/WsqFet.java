package io.mosip.imagedecoder.model.wsq;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WsqFet {
	private int alloc = WsqConstant.MAXFETS;
	private int num = 0;
	private String[] names = new String[WsqConstant.MAXFETS];
	private String[] values = new String[WsqConstant.MAXFETS];
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WsqFet)) return false;
        WsqFet that = (WsqFet) obj;
        if (this.num != that.num || this.alloc != that.alloc) {
            return false;
        }
        // Check if names and values arrays are equal
        return arrayEquals(this.names, that.names) && arrayEquals(this.values, that.values);
    }

    private boolean arrayEquals(String[] a1, String[] a2) {
        if (a1 == a2) return true;
        if (a1 == null || a2 == null) return false;
        if (a1.length != a2.length) return false;
        for (int i = 0; i < a1.length; i++) {
            if (!a1[i].equals(a2[i])) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(alloc);
        result = 31 * result + Integer.hashCode(num);
        result = 31 * result + arrayHashCode(names);
        result = 31 * result + arrayHashCode(values);
        return result;
    }

    private int arrayHashCode(String[] array) {
        if (array == null) return 0;
        int hash = 1;
        for (String str : array) {
            hash = 31 * hash + (str == null ? 0 : str.hashCode());
        }
        return hash;
    }

    public boolean canEqual(Object other) {
        return other instanceof WsqFet;
    }
}