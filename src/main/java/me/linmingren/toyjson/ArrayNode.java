package me.linmingren.toyjson;

import java.util.ArrayList;


public class ArrayNode extends JsonNode {
	private ArrayList<JsonNode> elements = new ArrayList<JsonNode>();
	
	public ArrayList<JsonNode> getAllNodes() {
		return elements;
	}
	
	public void addNode(JsonNode node) {
		elements.add(node);
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (JsonNode e: elements) {
			if (!sb.toString().endsWith("[")) {
				sb.append(",");
			}
	    	sb.append("\"" + e.toString() + "\"");
	    }
		sb.append("]");
		return sb.toString();
	}
}
