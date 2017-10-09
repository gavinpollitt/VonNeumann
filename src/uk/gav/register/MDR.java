package uk.gav.register;

public class MDR implements Register<String> {
	private String value;
	
	@Override
	public void setValue(String v) {
		this.value = v;
	}

	@Override
	public String getValue() {
		return this.value;
	}

}
