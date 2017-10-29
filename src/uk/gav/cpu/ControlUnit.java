package uk.gav.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public final class ControlUnit implements BusTerminator<Bus<?, ?>>, InterruptHandler {
	private PC pc = new PC();
	private ALU alu = new ALU();
	private RegisterHolder rh = new RegisterHolder();
	private DataBus dataBus;
	private AddressBus addressBus;
	private MemoryController memoryController;
	private List<Command> supportedCommands = new ArrayList<>();
	private boolean terminateEvent = false;

	private Map<InterruptType, Set<InterruptListener>> interruptListeners;

	public ControlUnit() {
		// Register the commands that the CU supports
		supportedCommands.add(new LoadAccumulatorCommand(this));
		supportedCommands.add(new IncrementAccumulatorCommand(this));
		supportedCommands.add(new ExitCommand(this));
		supportedCommands.add(new StoreAccumulatorCommand(this));
		supportedCommands.add(new AddAccumulatorCommand(this));
	}

	@Override
	public void setBus(Bus<?, ?> bus) {
		if (bus instanceof DataBus) {
			dataBus = (DataBus) bus;
		} else {
			addressBus = (AddressBus) bus;
		}
	}
	
	public void interrupt(InterruptType type) {
		this.interrupt(type, null);
	}

	@Override
	public void interrupt(InterruptType type, String description) {
		Set<InterruptListener> listeners = interruptListeners.get(type);

		if (listeners != null) {
			for (InterruptListener l : listeners) {
				l.interrupted(type, description);
			}
		}
	}

	@Override
	public void addInterruptListener(InterruptListener listener, InterruptType type) {
		if (interruptListeners == null) {
			interruptListeners = new HashMap<>();
		}

		Set<InterruptListener> lSet = interruptListeners.get(type);
		if (lSet == null) {
			lSet = new HashSet<>();
		}
		lSet.add(listener);
		interruptListeners.put(type, lSet);
	}

	@Override
	public void addInterruptsListener(InterruptListener listener, InterruptType... types) {
		for (InterruptType t : types) {
			this.addInterruptListener(listener, t);
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

	public PC getPC() {
		return this.pc;
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
		this.interrupt(InterruptType.PRE_ADD_BUS, rh.getMar().getValue() + "");
		addressBus.push(rh.getMar());
	}

	public void pushData() {
		this.interrupt(InterruptType.PRE_DATA_BUS, rh.getMdr().getValue());
		dataBus.push(rh.getMdr());
	}

	public void terminateExecution() {
		terminateEvent = true;
	}

	public void runProgram() throws Exception {
		if (pc.getValue() == null) {
			throw new Exception("No program initialised");
		} else if (memoryController == null) {
			throw new Exception("No memory controller has been registered with the control unit");
		}

		while (!terminateEvent) {
			this.interrupt(InterruptType.PRE_FETCH);
			this.fetchStep();
			this.interrupt(InterruptType.POST_FETCH);
			this.interrupt(InterruptType.PRE_DECODE);
			CommandContext cc = this.decodeStep();
			this.interrupt(InterruptType.POST_DECODE);
			this.interrupt(InterruptType.PRE_EXECUTE);
			System.out.println(cc.getCommand());
			this.executeStep(cc);
			this.interrupt(InterruptType.POST_EXECUTE);
			this.interrupt(InterruptType.POST_CYCLE);
		}
		this.interrupt(InterruptType.POST_EXIT);

	}

	private void fetchStep() throws Exception {
		rh.getMar().setValue(pc.getValue());
		this.interrupt(InterruptType.POST_MAR);
		pc.setValue(pc.getValue() + 1);
		this.interrupt(InterruptType.POST_PC);
		pushAddress();
		memoryController.dataReady();
		String commandString = dataBus.pop();
		this.interrupt(InterruptType.POST_DATA_BUS, commandString);

		rh.getMdr().setValue(commandString);	

	}

	private CommandContext decodeStep() throws IllegalArgumentException {

		String commandString = rh.getMdr().getValue();
		for (Command c : supportedCommands) {
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
