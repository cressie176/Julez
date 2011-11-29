package uk.co.acuminous.julez.event;

import java.util.Collection;

public interface EventHub {
	Collection<String> getInputNames();
	EventHandler getNamedInput(String name);
	Collection<String> getOutputNames();
	EventHandler getNamedOutput(String name);
	
	void onEvent(Event event, String inputName);
	void register(EventHandler handler, String outputName);
}
