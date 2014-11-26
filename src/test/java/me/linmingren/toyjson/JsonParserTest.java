package me.linmingren.toyjson;


import org.junit.Test;

public class JsonParserTest {
	@Test
	public void parseObject() throws JsonException  {

		String text = "{ \"name\" : {\"food\": \"lemon\"} , \"addr\" : \"shen zhen\",\"kids\": [\"dudu\",\"nuonuo\"]} ";
		//String text = "{ \"name\":  \"lemon\"} }";
		JsonParser p = new JsonParser(text);
		ObjectNode o = p.parseObject();
		
	  
	    System.out.println(o.toString());
	}
}
