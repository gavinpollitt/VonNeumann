package uk.gav.command;

import uk.gav.cpu.ControlUnit;
import uk.gav.cpu.InterruptType;

public class ExitCommand extends Command {

	public static final String EXIT = "EXIT";
	
	public ExitCommand(ControlUnit cc) {
		super(EXIT, cc, AddressType.NONE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
		cc.getController().terminateExecution();
		cc.getController().interrupt(InterruptType.POST_COMMAND, "EXIT Command registered and program terminated" + "\n"); 
	}
	
}
