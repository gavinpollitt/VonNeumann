package uk.gav.loaders;

public class MemoryProgramLoader extends ProgramLoader {

	@Override
	public void loadProgram() {
		instructions.add("LDA #15");
		instructions.add("INCA");
		instructions.add("STA 10");
		instructions.add("LDA 9");
		instructions.add("ADDA 10");
		instructions.add("STA 11");
		instructions.add("STA 10");
		instructions.add("EXIT");
		
		dataImage.add(null);
		dataImage.add(30L);
	}

}
