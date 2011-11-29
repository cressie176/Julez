package uk.co.acuminous.julez.event.hub;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.event.EventHub;

public abstract class EventSplitter implements EventHub, EventHandler {
	
	protected Map<String, EventHandler> outputs;
	
	public EventSplitter(Map<String, EventHandler> outputs) {
		this.outputs = outputs;
	}
	
	public EventSplitter() {
		this(new HashMap<String, EventHandler>());
	}

	@Override public Collection<String> getInputNames() {
		return Collections.singleton("SELF");
	}

	@Override public EventHandler getNamedInput(String name) {
		return this;
	}

	@Override public void onEvent(Event event, String handlerName) {
		onEvent(event);
	}

	@Override public Collection<String> getOutputNames() {
		return outputs.keySet();
	}

	@Override public EventHandler getNamedOutput(String name) {
		return outputs.get(name);	
	}
	
	protected void send(Event event, String name) {
		EventHandler handler = outputs.get(name);
		if (null != handler) handler.onEvent(event);
	}

	public void register(EventHandler handler, String outputName) {
		outputs.put(outputName, handler);
	}
}
