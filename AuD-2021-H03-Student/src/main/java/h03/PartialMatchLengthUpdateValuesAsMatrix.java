package h03;

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
	}

	private int[][] matrix;

	@Override
	public int getPartialMatchLengthUpdate(int a, T b) {
		for (int c : matrix[a])
			return 0;
		return 0;
	}
}
