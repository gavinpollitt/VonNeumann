package uk.gav.command;

import java.util.HashSet;
import java.util.Set;

import uk.gav.cpu.ControlUnit;

public abstract class Command {
	private String commandString;
	private Set<AddressType> addressTypes = new HashSet<>();
	private ControlUnit commandController;

	private Command() {
	}

	public Command(String comString, ControlUnit commandController, AddressType... types) {
		this.commandString = comString;
		this.commandController = commandController;
		
		for (int i = 0; i < types.length; i++) {
			this.addressTypes.add(types[i]);
		}
	}

	public CommandContext matchCommand(String contents) {
		boolean matched = false;
		CommandContext cc = null;
		String[] parts = contents.split(" +");
		if (parts.length > 0 && parts.length <= 2 && parts[0].equals(this.commandString)) {
			try {
				cc = identifyAddressType(parts);
				cc.setCommand(this);
				cc.setController(this.commandController);
				matched = addressTypes.contains(cc.getAddressType());
			}
			catch (Exception e) {}
		}

		return matched?cc:null;
	}

	public static CommandContext identifyAddressType(String... contents) throws Exception {
		if (contents.length == 1) {
			return new CommandContext(AddressType.NONE);
		} else {
			AddressType at = null;
			String add = contents[1];

			if (add.charAt(0) == '#' && add.length() > 1) {
				at = AddressType.VALUE;
				add = add.substring(1);
			}
			else {
				at = AddressType.ABSOLUTE;
			}

			return new CommandContext(at,new Long(add));
		}
		
	}
	
	protected abstract void run(CommandContext cc) throws Exception;
	
	public String toString() {
		return commandString + "," + addressTypes;
	}

	@Override
	public int hashCode() {
		return this.commandString.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		Command c = (Command) obj;
		return this.commandString.equals(c.commandString);
	}
	
	public static class CommandContext {
		private AddressType addressType;
		private Long addressValue;
		private Command command;
		private ControlUnit controller;
		
		private CommandContext(AddressType addressType, Long addressValue) {
			this.addressType = addressType;
			this.addressValue = addressValue;
		}

		private CommandContext(AddressType addressType) {
			this(addressType,null);
		}

		private void setCommand(Command c) {
			this.command = c;
		}
		
		public Command getCommand() {
			return this.command;
		}
		
		public AddressType getAddressType() {
			return addressType;
		}

		public Long getAddressValue() {
			return addressValue;
		}

		public ControlUnit getController() {
			return controller;
		}

		public void setController(ControlUnit controller) {
			this.controller = controller;
		}
		
		public void run() throws Exception{
			this.command.run(this);
		}
	}
}
