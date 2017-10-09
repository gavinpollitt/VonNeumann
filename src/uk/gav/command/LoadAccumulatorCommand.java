package uk.gav.command;

import uk.gav.cpu.ControlUnit;

public class LoadAccumulatorCommand extends Command {

	public static final String LDA = "LDA";
	
	public LoadAccumulatorCommand(ControlUnit cc) {
		super(LDA, cc, AddressType.ABSOLUTE, AddressType.VALUE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {
		if (cc.getAddressType().equals(AddressType.VALUE)) {
			cc.getController().getAlu().load(cc.getAddressValue());
		}
		else {
			cc.getController().getRh().getMar().setValue(cc.getAddressValue());
			cc.getController().pushAddress();
			cc.getController().getMemoryController().dataReady();
			String data = cc.getController().getDataBus().pop();
			
			Long dataL = Long.parseLong(data);
			
			cc.getController().getAlu().load(dataL);
		}
	}
	
}
