package uk.gav.memory;

import java.util.HashMap;
import java.util.Map;

public class Memory {
	private Map<Long,String> RAM = new HashMap<>();
	private Long limit;
	
	public Memory(Long limit) {
		this.limit = limit;		
	}
	
	public String read(Long loc) throws Exception {
		if (loc > limit) {
			throw new Exception("Out of Memory Error");
		}
		
		return RAM.get(loc);
	}
	
	public void write(Long loc, String val) throws Exception {
		if (loc > limit) {
			throw new Exception("Out of Memory Error");
		}
		
		RAM.put(loc, val);

	}
	
	public String toString() {
		String output = " --------------------------\n";
		for (long l:RAM.keySet()) {
			output+=String.format("| %5d  | %-10s      |\n",l,RAM.get(l));
		}
		output += " --------------------------\n";
		
		return output;
	}

}
