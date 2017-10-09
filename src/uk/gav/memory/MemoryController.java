package uk.gav.memory;

import java.util.Iterator;

import uk.gav.command.bus.AddressBus;
import uk.gav.command.bus.Bus;
import uk.gav.command.bus.DataBus;
import uk.gav.command.bus.endpoint.BusTerminator;
import uk.gav.command.bus.endpoint.Endpoint;
import uk.gav.loaders.ProgramLoader;

public class MemoryController implements BusTerminator<Bus<?,?>>, Endpoint{
	private DataBus 	dataBus;
	private AddressBus 	addressBus;
	private Memory		memory;
	private Long		freeSlot = 0L;
	
	public MemoryController(Memory mem) {
		this.memory = mem;
	}
	
	@Override
	public void setBus(Bus<?,?> bus) {
		if (bus instanceof DataBus) {
			dataBus = (DataBus)bus;
		}
		else {
			addressBus = (AddressBus)bus;
		}		
	}

	@Override
	public void dataReady() throws Exception {
		// Check address
		Long address = addressBus.pop();
		
		// Check for data
		String data = null; 
		try {
			data = dataBus.pop();
		}
		catch (Exception e) {
			
		}
		
		if (data != null) {
			//Must be a write
			memory.write(address, data);
		}
		else {
			//Must be a read
			String content= memory.read(address);
			dataBus.push(content);
		}
		
	}
	
	public Long loadProgram(ProgramLoader pl) throws Exception {
		Long startSlot = freeSlot;
		pl.loadProgram();
		Iterator<String> code = pl.iterateInstructions();
		while (code.hasNext()) {
			memory.write(freeSlot++, code.next());
		}
		
		//Write data image if required
		Iterator<Long> data = pl.iterateData();
		while (data.hasNext()) {
			memory.write(freeSlot++, ""+data.next());
		}
		
		return startSlot;
	}	
	
	public String toString() {
		String output = "Current memory footprint:\n";
		return output + memory.toString();
	}

	
}
