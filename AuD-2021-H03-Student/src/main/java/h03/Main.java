package h03;

import java.util.List;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello World!");

		UnicodeNumberOfCharIndex aCharIndex = new UnicodeNumberOfCharIndex();
		System.out.println(aCharIndex.apply('1'));

		List<Character> chars = List.of('a', 'g', 'i', 'n');
		SelectionOfCharsIndex lookUpTable = new SelectionOfCharsIndex(chars);
		Character[] searchString = new Character[] { 'g', 'a', 'g' };
		PartialMatchLengthUpdateValuesAsMatrix<Character> test = new PartialMatchLengthUpdateValuesAsMatrix<Character>(
				lookUpTable, searchString);

		int[][] matrix = test.getMatrix();

		for (int i = 0; i < matrix.length; i++) {
			StringBuilder sb = new StringBuilder("[");
			for (int j = 0; j < matrix[i].length; j++) {
				sb.append(matrix[i][j]);
				if (j < matrix[i].length - 1) {
					sb.append(", ");
				}
			}
			sb.append("]");
			System.out.println(sb.toString());
		}

		PartialMatchLengthUpdateValuesAsAutomaton<Character> test2 = new PartialMatchLengthUpdateValuesAsAutomaton<>(
				lookUpTable, searchString);

		List<Transition<Character>>[] automaton = test2.theStates;

		for (int i = 0; i < automaton.length; i++) {
			for (int j = 0; j < automaton[i].size(); j++) {
				System.out.print(automaton[i].get(j).transitionIndex);
				System.out.print(automaton[i].get(j).listOfSearchStringElements.get(0) + " ");
			}
			System.out.println();
		}

		StringMatcher<Character> sm = new StringMatcher<>(test);
		Character[] source = new Character[] { 'g', 'a', 'g', 'g', 'i', 'n', 'g', 'a', 'g', 'a', 'g' };
		List<Integer> result = sm.findAllMathes(source);
		for (int i : result)
			System.out.println(i);
	}
}
