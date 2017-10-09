package uk.gav.loaders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ProgramLoader {
	protected List<String> instructions = new ArrayList<>();
	
	protected List<Long> dataImage = new ArrayList<>(); 
	
	public Iterator<String> iterateInstructions() {
		return instructions.iterator();
	}
	
	public Iterator<Long> iterateData() {
		return dataImage.iterator();
	}
	
	public abstract void loadProgram();

}
