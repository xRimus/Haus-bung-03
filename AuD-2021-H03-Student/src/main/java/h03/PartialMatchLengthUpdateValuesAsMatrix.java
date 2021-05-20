package h03;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PartialMatchLengthUpdateValuesAsMatrix<T> extends PartialMatchLengthUpdateValues<T> {

	public PartialMatchLengthUpdateValuesAsMatrix(FunctionToInt<T> lookUpTable, T[] searchString) {
		super(lookUpTable);
		/*if (lookUpTable instanceof SelectionOfCharsIndex) {
			int[] alphabet = new int[lookUpTable.sizeOfAlphabet()];
			alphabet = ((SelectionOfCharsIndex) lookUpTable).getTheChars();
		} else {
			int[] alphabet = new int[lookUpTable.sizeOfAlphabet()];
			for (int i = 0; i < alphabet.length; i++)
				alphabet[i] = i;
		}*/

		/*
		int[] elementID = new int[searchString.length];
		for(int i = 0; i < elementID.length; i++)
			elementID[i] = lookUpTable.apply(searchString[i]);
		
		matrix = new int[searchString.length + 1][lookUpTable.sizeOfAlphabet()];
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix[i].length; j++) {
				if (elementID[i] == i)
					;
					
			}
		}
		*/
		Map<Integer, T> searchStringValueToIndex = new HashMap<>();
		for (int i=0; i< searchString.length; i++) {
			searchStringValueToIndex.put(lookUpTable.apply(searchString[i]),searchString[i]);
		}
		matrix = new int[searchString.length+1][lookUpTable.sizeOfAlphabet()];
		for (int i=0; i<searchString.length; i++) {
			//Initialisiere Werte für Search String zuerst
			matrix[i][lookUpTable.apply(searchString[i])] = i+1;
			
			for (int j:searchStringValueToIndex.keySet()) {
				if (j!=lookUpTable.apply(searchString[i])) {
					T[] searchStringUntilHerePlusCurrentChar = Arrays.copyOfRange(searchString, 0, i+1);
					searchStringUntilHerePlusCurrentChar[i] = searchStringValueToIndex.get(j);
					matrix[i][j] = computePartialMatchLengthUpdateValues(searchStringUntilHerePlusCurrentChar);
				}
			}
			
			for (int j=0; j<lookUpTable.sizeOfAlphabet(); j++) {
				if (j!=lookUpTable.apply(searchString[i]) && !searchStringValueToIndex.containsKey(j)) {
					matrix[i][j] = 0;
				}
			}
		}
		
		
		int relevantForLastRow = computePartialMatchLengthUpdateValues(searchString);
		for (int j=0; j<lookUpTable.sizeOfAlphabet(); j++) {
			matrix[searchString.length][j] = matrix[relevantForLastRow][j];
		}
		
	}

	private int[][] matrix;
	
	public int[][] getMatrix() {
		return matrix;
	}

	@Override
	public int getPartialMatchLengthUpdate(int a, T b) {
		for (int c : matrix[a])
			return 0;
		return 0;
	}
}
