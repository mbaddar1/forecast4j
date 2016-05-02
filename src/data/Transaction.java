package data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Transaction {
	
	private TransactionSchema schema ;
	private Map<String, Object> fields;
	public Transaction(TransactionSchema schema) {
		this.schema = schema;
		fields = new HashMap<String, Object>();
	}
	public boolean addField(String fieldName , Object fieldValue) {
		if(!schema.hasField(fieldName))
			throw new IllegalArgumentException("Field : "+fieldName+" doesn't exist in schema.");
		if(schema.getFieldType(fieldName) != fieldValue.getClass())
			throw new IllegalArgumentException("Invalid value type for field  : "+fieldName+" must be "
					+schema.getFieldType(fieldName)+" but the actual type is "+fieldValue.getClass());
		fields.put(fieldName, fieldValue);
		return true;
	}
	public Object getField(String fieldName) {
		if(!this.schema.hasField(fieldName)) {
			throw new IllegalArgumentException("Field : "+fieldName+" doesn't exist in the schema");
		}
		return fields.get(fieldName);
	}
	public Iterable<String> getFieldsNames() {
		return fields.keySet();
	}
}
