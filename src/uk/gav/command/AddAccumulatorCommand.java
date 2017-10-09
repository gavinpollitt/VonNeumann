package uk.gav.command;

import uk.gav.cpu.ControlUnit;

public class AddAccumulatorCommand extends Command {

	public static final String ADDA = "ADDA";
	
	public AddAccumulatorCommand(ControlUnit cc) {
		super(ADDA, cc, AddressType.ABSOLUTE, AddressType.VALUE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
		if (cc.getAddressType().equals(AddressType.VALUE)) {
			cc.getController().getAlu().load(cc.getController().getAlu().extract() + cc.getAddressValue());
		}
		else {
			cc.getController().getRh().getMar().setValue(cc.getAddressValue());
			cc.getController().pushAddress();
			cc.getController().getMemoryController().dataReady();
			String data = cc.getController().getDataBus().pop();
			
			Long dataL = Long.parseLong(data);
			
			cc.getController().getAlu().load(cc.getController().getAlu().extract() + dataL);
		}
	}
	
}
