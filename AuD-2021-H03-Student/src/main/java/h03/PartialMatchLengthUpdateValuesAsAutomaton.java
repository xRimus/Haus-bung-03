package h03;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartialMatchLengthUpdateValuesAsAutomaton<T> extends PartialMatchLengthUpdateValues<T> {

	@SuppressWarnings("unchecked")
	public PartialMatchLengthUpdateValuesAsAutomaton(FunctionToInt<T> alphabet, T[] searchString) {
		super(alphabet);
		searchStringLength = searchString.length;

		Map<Integer, T> searchStringValueToIndex = new HashMap<>();
		for (int i = 0; i < searchString.length; i++)
			searchStringValueToIndex.put(alphabet.apply(searchString[i]), searchString[i]);

		theStates = new List[searchString.length + 1];
		for (int i = 0; i < searchString.length; i++) {
			theStates[i] = new ArrayList<Transition<T>>();
			theStates[i].add(new Transition<>(i + 1, List.of(searchString[i])));

			for (int j : searchStringValueToIndex.keySet())
				if (j != alphabet.apply(searchString[i])) {
					T[] searchStringUntilHerePlusCurrentChar = Arrays.copyOfRange(searchString, 0, i + 1);
					searchStringUntilHerePlusCurrentChar[i] = searchStringValueToIndex.get(j);
					if (computePartialMatchLengthUpdateValues(searchStringUntilHerePlusCurrentChar) != 0)
						theStates[i].add(new Transition<>(
								computePartialMatchLengthUpdateValues(searchStringUntilHerePlusCurrentChar),
								List.of(searchStringValueToIndex.get(j))));
				}
		}

		int relevantForLastRow = computePartialMatchLengthUpdateValues(searchString);
		theStates[searchString.length] = new ArrayList<Transition<T>>();
		for (int i = 0; i < theStates[relevantForLastRow].size(); i++)
			theStates[searchString.length].add(theStates[relevantForLastRow].get(i));
	}

	List<Transition<T>>[] theStates;

	@Override
	public int getPartialMatchLengthUpdate(int a, T b) {
		for (int i = 0; i < theStates[a].size(); i++)
			if (theStates[a].get(i).listOfSearchStringElements.contains(b))
				return theStates[a].get(i).transitionIndex;
		return 0;
	}
}
