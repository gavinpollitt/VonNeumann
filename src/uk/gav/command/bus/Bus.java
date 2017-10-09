package uk.gav.command.bus;

import java.util.Stack;

import uk.gav.register.Register;

public abstract class Bus<T extends Register<U>, U> {
	protected Stack<U> bus = new Stack<>();

	public void push(T register) {
		bus.push(register.getValue());
	}

	public void push(U item) {
		bus.push(item);
	}

	public U pop() {
		return bus.pop();
	}

}
