package uk.gav.command.bus.endpoint;

public interface Receiver<T> {
	void receive(T item);
}
