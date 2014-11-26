package me.linmingren.toyjson;

import java.util.HashMap;
import java.util.Set;


public class ObjectNode extends JsonNode {
	private HashMap<String, JsonNode> elements = new HashMap<String, JsonNode> ();
	
	public JsonNode get(String name) {
		return elements.get(name);
	}
	
	public void set(String name, JsonNode value) {
		elements.put(name, value);
	}
	
	public Set<String> getAllKeys() {
		return elements.keySet();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (String key: elements.keySet()) {
			if (!sb.toString().endsWith("{")) {
				sb.append(",");
			}
	    	sb.append("\"" + key + "\":\"");
	    	sb.append(elements.get(key).toString() + "\"");
	    }
		
		
		sb.append("}");
		return sb.toString();
	}
}
