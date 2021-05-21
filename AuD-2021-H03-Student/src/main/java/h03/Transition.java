package h03;

import java.util.List;

class Transition<T> {

	public Transition(int transitionIndex, List<T> listOfSearchStringElements) {
		this.transitionIndex = transitionIndex;
		this.listOfSearchStringElements = listOfSearchStringElements;
	}

	public final int transitionIndex;

	public List<T> listOfSearchStringElements;
}
