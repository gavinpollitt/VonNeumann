package uk.gav.command;

import uk.gav.cpu.ControlUnit;

public class ExitCommand extends Command {

	public static final String EXIT = "EXIT";
	
	public ExitCommand(ControlUnit cc) {
		super(EXIT, cc, AddressType.NONE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
		cc.getController().terminateExecution();
	}
	
}
