package uk.gav.cpu;

public interface InterruptHandler {
	void interrupt(InterruptType type, String description);
	void interrupt(InterruptType type);
	public void addInterruptListener(InterruptListener listener,InterruptType type);
	public void addInterruptsListener(InterruptListener listener, InterruptType ...types);

}
