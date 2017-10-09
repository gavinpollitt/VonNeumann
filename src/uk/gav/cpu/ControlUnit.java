package uk.gav.cpu;

import java.util.ArrayList;
import java.util.List;

import uk.gav.command.AddAccumulatorCommand;
import uk.gav.command.Command;
import uk.gav.command.Command.CommandContext;
import uk.gav.command.ExitCommand;
import uk.gav.command.IncrementAccumulatorCommand;
import uk.gav.command.LoadAccumulatorCommand;
import uk.gav.command.StoreAccumulatorCommand;
import uk.gav.command.bus.AddressBus;
import uk.gav.command.bus.Bus;
import uk.gav.command.bus.DataBus;
import uk.gav.command.bus.endpoint.BusTerminator;
import uk.gav.memory.MemoryController;
import uk.gav.register.PC;

public final class ControlUnit implements BusTerminator<Bus<?,?>> {
	private PC pc 					= new PC();
	private ALU alu 				= new ALU();
	private RegisterHolder rh 		= new RegisterHolder();
	private DataBus 	dataBus;
	private AddressBus 	addressBus;
	private MemoryController memoryController;
	private List<Command> supportedCommands = new ArrayList<>();
	private boolean terminateEvent = false;

	
	public ControlUnit() {
		//Register the commands that the CU supports
		supportedCommands.add(new LoadAccumulatorCommand(this));
		supportedCommands.add(new IncrementAccumulatorCommand(this));
		supportedCommands.add(new ExitCommand(this));
		supportedCommands.add(new StoreAccumulatorCommand(this));
		supportedCommands.add(new AddAccumulatorCommand(this));
	}
	
	@Override
	public void setBus(Bus<?, ?> bus) {
		if (bus instanceof DataBus) {
			dataBus = (DataBus)bus;
		}
		else {
			addressBus = (AddressBus)bus;
		}		
	}

	public void registerController(MemoryController cont) {
		this.memoryController = cont;
	}
	
	public void initialiseProgram(Long memorySlot) {
		pc.setValue(memorySlot);
	}
	
	public ALU getAlu() {
		return alu;
	}

	public RegisterHolder getRh() {
		return rh;
	}
	
	
	public DataBus getDataBus() {
		return dataBus;
	}

	public AddressBus getAddressBus() {
		return addressBus;
	}

	public MemoryController getMemoryController() {
		return memoryController;
	}

	public void pushAddress() {
		addressBus.push(rh.getMar());
	}
	
	public void pushData() {
		dataBus.push(rh.getMdr());
	}
	
	public void terminateExecution() {
		terminateEvent = true;
	}
	
	public void runProgram() throws Exception {
		if (pc.getValue() == null) {
			throw new Exception("No program initialised");
		}
		else if (memoryController == null) {
			throw new Exception("No memory controller has been registered with the control unit");
		}
		
		while (!terminateEvent) {
			this.fetchStep();
			CommandContext cc = this.decodeStep();
			System.out.println(cc.getCommand());	
			this.executeStep(cc);
		}
	}
	
	private void fetchStep() throws Exception {
		rh.getMar().setValue(pc.getValue());
		pushAddress();
		memoryController.dataReady();
		pc.setValue(pc.getValue()+1);
	}
	
	private CommandContext decodeStep() throws IllegalArgumentException {
		String commandString = dataBus.pop();
		
		for (Command c:supportedCommands) {
			CommandContext cc = c.matchCommand(commandString);
			if (cc != null) {
				return cc;
			}
		}
		
		throw new IllegalArgumentException("Unsupported command read:" + commandString);
		
	}
	
	private void executeStep(CommandContext cc) throws Exception {
		cc.run();
		System.out.println("At end of cycle:\n" + this.toString());
	}

	public String toString() {
		String output = "CPU Contents:\n";
		output += this.getAlu().toString() + "\n";
		output += this.getRh().toString();
		return output;
	}

}
