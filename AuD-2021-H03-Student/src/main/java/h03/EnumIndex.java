package h03;

public class EnumIndex<T extends Enum<T>> implements FunctionToInt<T> {

	public EnumIndex(Class<T> enumClass) {
		this.enumClass = enumClass;
	}

	Class<T> enumClass;

	@Override
	public int sizeOfAlphabet() {
		return enumClass.getEnumConstants().length;
	}

	@Override
	public int apply(T t) throws IllegalArgumentException {
		T[] enumElements = enumClass.getEnumConstants();
		for (int i = 0; i < enumClass.getEnumConstants().length; i++) {
			if (enumElements[i].equals(t))
				return i;
		}
		return enumClass.getEnumConstants().length;
	}
}
