package h03;

import java.util.ArrayList;

public abstract class PartialMatchLengthUpdateValues<T> {

	public PartialMatchLengthUpdateValues(FunctionToInt<T> alphabet) {
		this.alphabet = alphabet;
	}

	protected FunctionToInt<T> alphabet;

	protected int searchStringLength;

	abstract public int getPartialMatchLengthUpdate(int a, T b);

	protected int computePartialMatchLengthUpdateValues(T[] searchString) {

		if (searchString == null || searchString.length == 0) {
			return 0;
		}

		ArrayList<Integer> possibleStartsOfSubstring = new ArrayList<>();
		for (int i = 1; i < searchString.length; i++)
			if (searchString[0].equals(searchString[i]))
				possibleStartsOfSubstring.add(i);

		if (possibleStartsOfSubstring.size() == 0)
			return 0;

		int substringLength = 0;
		for (int i = 0; i < possibleStartsOfSubstring.size(); i++) {
			int followingSubstringElements = searchString.length - 1 - possibleStartsOfSubstring.get(i);
			int counter = 0;
			for (int j = 0; j < followingSubstringElements; j++) {
				if (!searchString[j + 1].equals(searchString[possibleStartsOfSubstring.get(i) + j + 1]))
					break;
				counter++;
			}
			if (counter == followingSubstringElements) {
				substringLength = counter + 1;
				break;
			}
		}
		return substringLength;
	}
}
