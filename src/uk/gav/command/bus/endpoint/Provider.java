package uk.gav.command.bus.endpoint;

public interface Provider<T> {
	void send(T item);
}
