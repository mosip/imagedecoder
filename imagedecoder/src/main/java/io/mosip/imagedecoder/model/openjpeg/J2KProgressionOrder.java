package io.mosip.imagedecoder.model.openjpeg;

import java.util.Arrays;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class J2KProgressionOrder {
	private ProgressionOrder progressionOrder;
	private char[] progressionName = new char[4];

	public J2KProgressionOrder(ProgressionOrder progressionOrder, char[] progressionName) {
		super();
		this.progressionOrder = progressionOrder;
		this.progressionName = progressionName;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof J2KProgressionOrder))
			return false;
		J2KProgressionOrder that = (J2KProgressionOrder) obj;
		return canEqual(that) && Objects.equals(progressionOrder, that.progressionOrder)
				&& Arrays.equals(progressionName, that.progressionName);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(progressionOrder);
		result = 31 * result + Arrays.hashCode(progressionName);
		return result;
	}

	public boolean canEqual(Object obj) {
		return obj instanceof J2KProgressionOrder;
	}
}