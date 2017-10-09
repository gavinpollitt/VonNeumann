package uk.gav;

import uk.gav.command.bus.AddressBus;
import uk.gav.command.bus.DataBus;
import uk.gav.cpu.ControlUnit;
import uk.gav.loaders.MemoryProgramLoader;
import uk.gav.memory.Memory;
import uk.gav.memory.MemoryController;

public class Computer {
	private DataBus 			databus = new DataBus();
	private AddressBus 			addressbus = new AddressBus();
	private ControlUnit 		controlUnit = new ControlUnit();
	private Memory 				memory = new Memory(30L);
	private MemoryController 	memoryController;


	public static void main(String[] args) throws Exception {
		Computer c = new Computer();
		Long pMemorySlot = c.loadProgram();
		c.runProgram(pMemorySlot);
	}
	
	public Computer() {
		controlUnit.setBus(databus);
		controlUnit.setBus(addressbus);
		memoryController = new MemoryController(memory);
		memoryController.setBus(databus);
		memoryController.setBus(addressbus);
		controlUnit.registerController(memoryController);
	}
	
	public Long loadProgram() throws Exception {
		Long loadedTo = memoryController.loadProgram(new MemoryProgramLoader());
		System.out.println(memoryController.toString());	
		return loadedTo;
	}
	
	public void runProgram(Long start) throws Exception {
		controlUnit.initialiseProgram(start);
		controlUnit.runProgram();
		
	}
}
