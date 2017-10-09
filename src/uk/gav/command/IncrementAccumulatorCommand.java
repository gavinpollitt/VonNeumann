package uk.gav.command;

import uk.gav.cpu.ControlUnit;

public class IncrementAccumulatorCommand extends Command {

	public static final String INCA = "INCA";
	
	public IncrementAccumulatorCommand(ControlUnit cc) {
		super(INCA, cc, AddressType.NONE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
			cc.getController().getAlu().inc();
	}
	
}
