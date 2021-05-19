package h03;

public class UnicodeNumberOfCharIndex implements FunctionToInt<Character> {

	@Override
	public int sizeOfAlphabet() {
		return Character.MAX_VALUE + 1;
	}

	@Override
	public int apply(Character t) throws IllegalArgumentException {
		return t;
	}
}
