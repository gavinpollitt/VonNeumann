package uk.gav.command.bus;

import uk.gav.register.MAR;

public class AddressBus extends Bus<MAR,Long> {

	@Override
	public void push(MAR register) {
		super.push(register);
		System.out.println("Address bus is conveying: " + register.getValue());
	}
}
