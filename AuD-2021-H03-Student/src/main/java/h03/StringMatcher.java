package h03;

import java.util.ArrayList;
import java.util.List;

public class StringMatcher<T> {

	public StringMatcher(PartialMatchLengthUpdateValues<T> alphabetAndSearchString) {
		this.alphabetAndSearchString = alphabetAndSearchString;
	}

	private PartialMatchLengthUpdateValues<T> alphabetAndSearchString;

	public List<Integer> findAllMathes(T[] source) {
		ArrayList<Integer> allMatches = new ArrayList<>();
		int j = 0;
		for(int i = 0; i < source.length; i++) {
			j = alphabetAndSearchString.getPartialMatchLengthUpdate(j, source[i]);
			if (j == alphabetAndSearchString.searchStringLength)
				allMatches.add(i - alphabetAndSearchString.searchStringLength + 2);
		}
		return allMatches;
	}
}
