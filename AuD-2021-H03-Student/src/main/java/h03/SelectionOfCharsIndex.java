package h03;

import java.util.List;

public class SelectionOfCharsIndex implements FunctionToInt<Character> {

	/**
	 * Initiates the attribute theChars with the elements of an input list
	 * 
	 * @param theAlphabet != null, theAlphabet.size() > 0, theAlphabet every element
	 *                    is unique
	 */
	public SelectionOfCharsIndex(List<Character> theAlphabet) {

		int listSize = theAlphabet.size();
		theChars = new char[listSize];
		for (int i = 0; i < listSize; i++)
			theChars[i] = theAlphabet.get(i);
	}

	private char[] theChars;

	/*
	 * public int[] getTheChars() { int[] indeces = new int[theChars.length]; for
	 * (int i = 0; i < indeces.length; i++) indeces[i] = apply(theChars[i]); return
	 * indeces; }
	 */

	@Override
	public int sizeOfAlphabet() {
		return theChars.length;
	}

	@Override
	public int apply(Character t) throws IllegalArgumentException {

		for (int i = 0; i < theChars.length; i++)
			if (theChars[i] == t)
				return i;
		throw new IllegalArgumentException();
	}
}
