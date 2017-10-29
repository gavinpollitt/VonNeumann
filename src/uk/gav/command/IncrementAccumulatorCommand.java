package uk.gav.command;

import uk.gav.cpu.ControlUnit;
import uk.gav.cpu.InterruptType;

public class IncrementAccumulatorCommand extends Command {

	public static final String INCA = "INCA";
	
	public IncrementAccumulatorCommand(ControlUnit cc) {
		super(INCA, cc, AddressType.NONE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
			cc.getController().getAlu().inc();
			cc.getController().interrupt(InterruptType.POST_COMMAND, "Incremented the accumulator by 1" + "\n"); 
	}
	
}
