package promotions;

public class Promotion {
	String name;
	public Promotion(String name, String type, double value) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}
	String type;
	double value;
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public double getValue() {
		return value;
	}
}
