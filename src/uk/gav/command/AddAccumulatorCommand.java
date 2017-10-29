package uk.gav.command;

import uk.gav.cpu.ControlUnit;
import uk.gav.cpu.InterruptType;

public class AddAccumulatorCommand extends Command {

	public static final String ADDA = "ADDA";
	
	public AddAccumulatorCommand(ControlUnit cc) {
		super(ADDA, cc, AddressType.ABSOLUTE, AddressType.VALUE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
		if (cc.getAddressType().equals(AddressType.VALUE)) {
			cc.getController().getAlu().load(cc.getController().getAlu().extract() + cc.getAddressValue());
			cc.getController().interrupt(InterruptType.POST_COMMAND, "Increase the accumulator by absolute value of " + cc.getAddressValue() + "\n");
		}
		else {
			cc.getController().getRh().getMar().setValue(cc.getAddressValue());
			cc.getController().pushAddress();
			cc.getController().getMemoryController().dataReady();
			String data = cc.getController().getDataBus().pop();
			cc.getController().interrupt(InterruptType.PRE_MAR);
			cc.getController().interrupt(InterruptType.POST_MDR);
		
			Long dataL = Long.parseLong(data);
			
			cc.getController().getAlu().load(cc.getController().getAlu().extract() + dataL);
			cc.getController().interrupt(InterruptType.POST_COMMAND, "Increase the accumulator by value " + dataL + " from memory location " + cc.getAddressValue() + "\n"); 
		}
	}
	
}
