package me.linmingren.toyjson;

import java.io.IOException;
import java.io.StringReader;

public class JsonParser {
    StringReader strReader;
	StringBuffer readedString; //�Ѿ���������ַ������������쳣ʱ��ӡ����
    int ch = -1; //��ǰ�ַ�
	
	
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
			throw new JsonException("�ַ�������, �Ѿ���ȡ�����ַ�  " + readedString.toString());
		}
	}
	
	//����[��һλ
	private void beginArray() throws JsonException {
		characterAfter('[');
	}
		
	//����]��һλ
	private void endArray() throws JsonException {
		characterAfter(']');
	}
		
	//����{��һλ
	private void beginObject() throws JsonException {
		characterAfter('{');
	}
	
	//����}��һλ
	private void endObject() throws JsonException {
		characterAfter('}');
	}
	//����"��һλ
	private void beginString() throws JsonException {
		characterAfter('"');
	}
	
	//key��value֮��ļ����
	private void separator() throws JsonException {
		characterAfter(':');
	}
		
	//��һ�������ķǿ��ַ�������',' ����'}', ���������Ϊ�쳣
	private boolean hasNextProperty() throws JsonException {
		while (ch > 0) {
			if (ch == ',') {
				return true;
			}
			
			if (ch == '}') {
				return false;
			}
			
			if (!isBlankCharacter(ch)) {
				// ��'"' ǰ�����������ַ�
				throw new JsonException("����û����ֹ��, �Ѿ���ȡ���ַ� >>>>" + readedString.toString() + "<<<<");
			}
			ch = nextCharacter();
		}
		
		throw new JsonException("����û����ֹ��, �Ѿ���ȡ���ַ� >>>>" + readedString.toString() + "<<<<");
	}
	
	//��һ�������ķǿ��ַ�������',' ����']'
	private boolean hasNextElement() throws JsonException {
		while (ch > 0) {
			if (ch == ',') {
				return true;
			}

			if (ch == ']') {
				return false;
			}

			if (!isBlankCharacter(ch)) {
				// ��'"' ǰ�����������ַ�
				throw new JsonException("û���ҵ��������ֹ��, �Ѿ���ȡ���ַ� >>>>" + readedString.toString() + "<<<<");
			}
			ch = nextCharacter();
		}

		throw new JsonException("û���ҵ��������ֹ��, �Ѿ���ȡ���ַ� >>>>" + readedString.toString() + "<<<<");
	}
	
	//����'"' ���� [������{
	private int beginValue() throws JsonException {
		while (ch > 0) {
			if (ch == '"' || ch == '{' || ch == '[') {
				return ch;
			}
			
			if (!isBlankCharacter(ch)) {
				// ��'"','{', '[' ǰ�����������ַ�
				throw new JsonException("ֵ������  \", { ����["  + " ��ʼ, �Ѿ���ȡ���ַ� >>>>" + readedString.toString() + "<<<<");
			}

			ch = nextCharacter();
		} 
		
		throw new JsonException("ֵ������  \", { ����["  + " ��ʼ, �Ѿ���ȡ���ַ� >>>>" + readedString.toString() + "<<<<");
	}
	
	
	//�ѵ�ǰ�ַ��ƶ��� ĳ���ض��ַ��ĺ�һλ
	//�ڴ��ַ�ǰ�����������ǿհ��ַ�
	private void characterAfter(int stopChar) throws JsonException {
		while (ch > 0) {
			if (ch == stopChar) {
				ch = nextCharacter();
				return;
			}

			if (!isBlankCharacter(ch)) {
				// ��'"' ǰ�����������ַ�
				throw new JsonException("������ " + (char)stopChar + " ��ʼ, �Ѿ���ȡ���ַ� >>>>" + readedString.toString() + "<<<<");
			}

			ch = nextCharacter();
		} 
		
		throw new JsonException("δ�ҵ� " + (char)stopChar);
	}
	
	private String parseString() throws JsonException {
		// ����'"'
		beginString();

		//�ӵ�ǰλ�õ�"���������ַ���������
		StringBuffer sb = new StringBuffer();
		while (ch > 0 && ch != '"' ) {
			    sb.append((char) ch);
			    ch = nextCharacter();
		}

		if (ch == -1) {
			throw new JsonException("�ַ���������\"����");
		}
		
		//��λ���ƶ�"��һλ
		ch = nextCharacter();
		return sb.toString();
	}
	
	private JsonNode parseValue() throws JsonException {
		
		int beginValue = beginValue();
		JsonNode node = new JsonNode();
		
		if (beginValue == '"') {
			//ֵ��һ���ַ���
			String s = parseString();
			node = new PrimitiveNode(s);
		} else if (beginValue == '[') {
			//ֵ��һ���ַ���
			node = parseArray();
		}else if (beginValue == '{') {
			//ֵ��һ������
			node = parseObject();
		} 

		return node;
	}
	
	public JsonNode parseArray() throws JsonException {
		ArrayNode o = new ArrayNode();

		// ����[
		beginArray();

		// []�ڲ��Ľ�����ÿ��Ԫ�صľ���һ��ֵ
		while (ch > 0) {
			JsonNode node = parseValue();
			o.addNode(node);
			
			if (!hasNextElement() ) {
				break;
			}
			ch = nextCharacter();
		}

		// ����]
		endArray();
		return o;
	}
	
	public ObjectNode parseObject() throws JsonException {
		ObjectNode o = new ObjectNode();
		
		//����{
		beginObject();
		
		//{}�ڲ��Ľ���
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
		
		//����}
		endObject();
		return o;
	}
}
