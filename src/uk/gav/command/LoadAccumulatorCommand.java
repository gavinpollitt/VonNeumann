package uk.gav.command;

import uk.gav.cpu.ControlUnit;
import uk.gav.cpu.InterruptType;

public class LoadAccumulatorCommand extends Command {

	public static final String LDA = "LDA";
	
	public LoadAccumulatorCommand(ControlUnit cc) {
		super(LDA, cc, AddressType.ABSOLUTE, AddressType.VALUE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
		if (cc.getAddressType().equals(AddressType.VALUE)) {
			cc.getController().getAlu().load(cc.getAddressValue());
			cc.getController().interrupt(InterruptType.POST_COMMAND, "Loaded the accumulator with absolute value of " + cc.getAddressValue() + "\n");
		}
		else {
			cc.getController().getRh().getMar().setValue(cc.getAddressValue());
			cc.getController().pushAddress();
			cc.getController().getMemoryController().dataReady();
			String data = cc.getController().getDataBus().pop();
			cc.getController().getRh().getMdr().setValue(data);
			cc.getController().interrupt(InterruptType.PRE_MAR);
			cc.getController().interrupt(InterruptType.POST_MDR);
			
			Long dataL = Long.parseLong(data);
			
			cc.getController().getAlu().load(dataL);
			cc.getController().interrupt(InterruptType.POST_COMMAND, "Loaded the accumulator with value " + data + " from memory location " + cc.getAddressValue() + "\n"); 
		}
	}
	
}
