package h03;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PartialMatchLengthUpdateValuesAsMatrix<T> extends PartialMatchLengthUpdateValues<T> {

	public PartialMatchLengthUpdateValuesAsMatrix(FunctionToInt<T> alphabet, T[] searchString) {
		super(alphabet);
		searchStringLength = searchString.length;

		Map<Integer, T> searchStringValueToIndex = new HashMap<>();
		for (int i = 0; i < searchString.length; i++)
			searchStringValueToIndex.put(alphabet.apply(searchString[i]), searchString[i]);

		matrix = new int[searchString.length + 1][alphabet.sizeOfAlphabet()];
		for (int i = 0; i < searchString.length; i++) {
			matrix[i][alphabet.apply(searchString[i])] = i + 1;

			for (int j : searchStringValueToIndex.keySet())
				if (j != alphabet.apply(searchString[i])) {
					T[] searchStringUntilHerePlusCurrentChar = Arrays.copyOfRange(searchString, 0, i + 1);
					searchStringUntilHerePlusCurrentChar[i] = searchStringValueToIndex.get(j);
					matrix[i][j] = computePartialMatchLengthUpdateValues(searchStringUntilHerePlusCurrentChar);
				}

			for (int j = 0; j < alphabet.sizeOfAlphabet(); j++)
				if (j != alphabet.apply(searchString[i]) && !searchStringValueToIndex.containsKey(j))
					matrix[i][j] = 0;
		}

		int relevantForLastRow = computePartialMatchLengthUpdateValues(searchString);
		for (int i = 0; i < alphabet.sizeOfAlphabet(); i++)
			matrix[searchString.length][i] = matrix[relevantForLastRow][i];
	}

	private int[][] matrix;

	public int[][] getMatrix() {
		// TODO Auto-generated method stub
		return matrix;
	}

	@Override
	public int getPartialMatchLengthUpdate(int a, T b) {
		return matrix[a][alphabet.apply(b)];
	}
}
