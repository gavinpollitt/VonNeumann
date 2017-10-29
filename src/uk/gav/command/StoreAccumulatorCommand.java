package uk.gav.command;

import uk.gav.cpu.ControlUnit;
import uk.gav.cpu.InterruptType;

public class StoreAccumulatorCommand extends Command {

	public static final String STA = "STA";

	public StoreAccumulatorCommand(ControlUnit cc) {
		super(STA, cc, AddressType.ABSOLUTE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {

		cc.getController().getRh().getMar().setValue(cc.getAddressValue());
		cc.getController().pushAddress();
		cc.getController().interrupt(InterruptType.PRE_MAR);

		cc.getController().getRh().getMdr().setValue("" + cc.getController().getAlu().extract());
		cc.getController().pushData();
		cc.getController().interrupt(InterruptType.PRE_MDR);

		cc.getController().getMemoryController().dataReady();
		cc.getController().interrupt(InterruptType.POST_COMMAND, "Stored the accumulator value " + cc.getController().getAlu().extract() + " at memory location " + cc.getAddressValue() + "\n"); 
	}

}
