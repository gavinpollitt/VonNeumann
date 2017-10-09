package uk.gav.cpu;

import uk.gav.register.ACC;

public class ALU {
	private ACC acc = new ACC();
	
	public void load(Long l) {
		acc.setValue(l);
	}
	
	public void inc() {
		acc.setValue(acc.getValue()+1);
	}
	
	public void dec() {
		acc.setValue(acc.getValue()-1);
	}
	
	public Long extract() {
		return acc.getValue();
	}
	
	public String toString() {
		return "Accumulator:" + acc.getValue();
	}
}
