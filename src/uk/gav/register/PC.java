package uk.gav.register;

public class PC implements Register<Long> {
	private Long value = 0L;
	
	@Override
	public void setValue(Long l) {
		this.value = l;
	}

	@Override
	public Long getValue() {
		return this.value;
	}

}
