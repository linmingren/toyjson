package me.linmingren.toyjson;


public class PrimitiveNode extends JsonNode {
    private Object data;
    
    public PrimitiveNode(String value) {
    	this.data = value;
    }
    
    public String toString() {
    	return data != null ? data.toString() : "null";
    }
}
