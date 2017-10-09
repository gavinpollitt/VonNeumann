package uk.gav.register;

public interface Register<T> {
	void setValue(T l);
	T getValue();
}
