package data;

import java.util.Set;

public interface Schema {
	public boolean hasField(String fieldName);
	public boolean addField(String fieldName , Class fieldType);
	public Class getFieldType(String filedName);
	public Set<String> getColNames();
}
