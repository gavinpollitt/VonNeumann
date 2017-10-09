package uk.gav.command;

import uk.gav.cpu.ControlUnit;

public class StoreAccumulatorCommand extends Command {

	public static final String STA = "STA";

	public StoreAccumulatorCommand(ControlUnit cc) {
		super(STA, cc, AddressType.ABSOLUTE);
	}

	@Override
	public void run(CommandContext cc) throws Exception {

		cc.getController().getRh().getMar().setValue(cc.getAddressValue());
		cc.getController().pushAddress();

		cc.getController().getRh().getMdr().setValue("" + cc.getController().getAlu().extract());
		cc.getController().pushData();

		cc.getController().getMemoryController().dataReady();
	}

}
