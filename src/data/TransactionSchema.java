package data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TransactionSchema implements Schema {
	private Map<String, Class> schema;
	public TransactionSchema() {
		schema = new HashMap<String, Class>();
	}
	@Override
	public boolean addField(String fieldName, Class fieldType) {
		// TODO Auto-generated method stub
		if(schema.containsKey(fieldName)) {
			throw new IllegalArgumentException("Field : "+fieldName+" already exists.");
		}
		schema.put(fieldName, fieldType);
		return true;
	}
	
	@Override
	public Class getFieldType(String fieldName) {
		if(!schema.containsKey(fieldName)) {
			throw new IllegalArgumentException("Field : "+fieldName+" doesn't exist.");
		}
		return schema.get(fieldName);
	}
	@Override
	public boolean hasField(String fieldName) {
		return schema.containsKey(fieldName);
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		for (Entry<String, Class> e : this.schema.entrySet()) {
			if(!((TransactionSchema)obj).hasField(e.getKey())) {
				return false;
			}
			if(e.getValue() != ((TransactionSchema)obj).getFieldType(e.getKey()))
				return false;
		}
		return true;
	}
	@Override
	public Set<String> getColNames() {
		return schema.keySet();
	}
	
	
	
}
