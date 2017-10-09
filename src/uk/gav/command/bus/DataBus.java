package uk.gav.command.bus;

import uk.gav.register.MDR;

public class DataBus extends Bus<MDR,String> {

	@Override
	public void push(String value) {
		super.push(value);
		System.out.println("Data bus is conveying: " + value);
	}

}
