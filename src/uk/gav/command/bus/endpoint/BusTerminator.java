package uk.gav.command.bus.endpoint;

import uk.gav.command.bus.Bus;

public interface BusTerminator<T extends Bus<?,?>> {
	void setBus(T bus);
}
