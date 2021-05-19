package h03;

public interface FunctionToInt<T> {

	int sizeOfAlphabet();

	/**
	 * 
	 * @param t
	 * @return number >= 0 && number < sizeOfAlphabet()
	 * @throws IllegalArgumentException
	 */
	int apply(T t) throws IllegalArgumentException;
}
