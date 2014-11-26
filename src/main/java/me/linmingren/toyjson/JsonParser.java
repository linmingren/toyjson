package me.linmingren.toyjson;

import java.io.IOException;
import java.io.StringReader;

public class JsonParser {
    StringReader strReader;
	StringBuffer readedString; //已经处理过的字符串，当出现异常时打印出来
    int ch = -1; //当前字符
	
	
	public JsonParser(String text) throws JsonException {
		strReader = new StringReader(text);
		readedString = new StringBuffer();
		ch = nextCharacter();
	}
	
	private boolean isBlankCharacter(int c)
    {
        return (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n')? true: false;
    }
	
	private int nextCharacter() throws JsonException {
		try {
			int c = strReader.read();
			readedString.append((char)c);
			return c;
		} catch (IOException e) {
			throw new JsonException("字符流错误, 已经读取到的字符  " + readedString.toString());
		}
	}
	
	//跳到[后一位
	private void beginArray() throws JsonException {
		characterAfter('[');
	}
		
	//跳到]后一位
	private void endArray() throws JsonException {
		characterAfter(']');
	}
		
	//跳到{后一位
	private void beginObject() throws JsonException {
		characterAfter('{');
	}
	
	//跳到}后一位
	private void endObject() throws JsonException {
		characterAfter('}');
	}
	//跳到"后一位
	private void beginString() throws JsonException {
		characterAfter('"');
	}
	
	//key和value之间的间隔符
	private void separator() throws JsonException {
		characterAfter(':');
	}
		
	//第一个碰到的非空字符必须是',' 或者'}', 其他情况均为异常
	private boolean hasNextProperty() throws JsonException {
		while (ch > 0) {
			if (ch == ',') {
				return true;
			}
			
			if (ch == '}') {
				return false;
			}
			
			if (!isBlankCharacter(ch)) {
				// 在'"' 前不能有其他字符
				throw new JsonException("对象没有终止符, 已经读取到字符 >>>>" + readedString.toString() + "<<<<");
			}
			ch = nextCharacter();
		}
		
		throw new JsonException("对象没有终止符, 已经读取到字符 >>>>" + readedString.toString() + "<<<<");
	}
	
	//第一个碰到的非空字符必须是',' 或者']'
	private boolean hasNextElement() throws JsonException {
		while (ch > 0) {
			if (ch == ',') {
				return true;
			}

			if (ch == ']') {
				return false;
			}

			if (!isBlankCharacter(ch)) {
				// 在'"' 前不能有其他字符
				throw new JsonException("没有找到数组的终止符, 已经读取到字符 >>>>" + readedString.toString() + "<<<<");
			}
			ch = nextCharacter();
		}

		throw new JsonException("没有找到数组的终止符, 已经读取到字符 >>>>" + readedString.toString() + "<<<<");
	}
	
	//查找'"' 或者 [，或者{
	private int beginValue() throws JsonException {
		while (ch > 0) {
			if (ch == '"' || ch == '{' || ch == '[') {
				return ch;
			}
			
			if (!isBlankCharacter(ch)) {
				// 在'"','{', '[' 前不能有其他字符
				throw new JsonException("值必须以  \", { 或者["  + " 开始, 已经读取到字符 >>>>" + readedString.toString() + "<<<<");
			}

			ch = nextCharacter();
		} 
		
		throw new JsonException("值必须以  \", { 或者["  + " 开始, 已经读取到字符 >>>>" + readedString.toString() + "<<<<");
	}
	
	
	//把当前字符移动后 某个特定字符的后一位
	//在此字符前不能有其他非空白字符
	private void characterAfter(int stopChar) throws JsonException {
		while (ch > 0) {
			if (ch == stopChar) {
				ch = nextCharacter();
				return;
			}

			if (!isBlankCharacter(ch)) {
				// 在'"' 前不能有其他字符
				throw new JsonException("必须以 " + (char)stopChar + " 开始, 已经读取到字符 >>>>" + readedString.toString() + "<<<<");
			}

			ch = nextCharacter();
		} 
		
		throw new JsonException("未找到 " + (char)stopChar);
	}
	
	private String parseString() throws JsonException {
		// 处理'"'
		beginString();

		//从当前位置到"结束都是字符串的内容
		StringBuffer sb = new StringBuffer();
		while (ch > 0 && ch != '"' ) {
			    sb.append((char) ch);
			    ch = nextCharacter();
		}

		if (ch == -1) {
			throw new JsonException("字符串必须以\"结束");
		}
		
		//把位置移动"后一位
		ch = nextCharacter();
		return sb.toString();
	}
	
	private JsonNode parseValue() throws JsonException {
		
		int beginValue = beginValue();
		JsonNode node = new JsonNode();
		
		if (beginValue == '"') {
			//值是一个字符串
			String s = parseString();
			node = new PrimitiveNode(s);
		} else if (beginValue == '[') {
			//值是一个字符串
			node = parseArray();
		}else if (beginValue == '{') {
			//值是一个对象
			node = parseObject();
		} 

		return node;
	}
	
	public JsonNode parseArray() throws JsonException {
		ArrayNode o = new ArrayNode();

		// 处理[
		beginArray();

		// []内部的解析，每个元素的就是一个值
		while (ch > 0) {
			JsonNode node = parseValue();
			o.addNode(node);
			
			if (!hasNextElement() ) {
				break;
			}
			ch = nextCharacter();
		}

		// 处理]
		endArray();
		return o;
	}
	
	public ObjectNode parseObject() throws JsonException {
		ObjectNode o = new ObjectNode();
		
		//处理{
		beginObject();
		
		//{}内部的解析
		while (ch > 0) {
			String name = parseString();
			separator();
			JsonNode value = parseValue();
			o.set(name, value);
			
			if (!hasNextProperty() ) {
				break;
			}
			ch = nextCharacter();
		}
		
		//处理}
		endObject();
		return o;
	}
}
