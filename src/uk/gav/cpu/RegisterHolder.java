package uk.gav.cpu;

import uk.gav.register.MAR;
import uk.gav.register.MDR;

public class RegisterHolder {
	private MAR mar = new MAR();
	private MDR mdr = new MDR();
	
	public MAR getMar() {
		return mar;
	}
	public MDR getMdr() {
		return mdr;
	}
	
	public String toString() {
		return "MAR: " + mar.getValue() + "; MDR: " + mdr.getValue();
	}

}
