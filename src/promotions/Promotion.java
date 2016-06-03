package promotions;

public class Promotion {
	String name;
	public Promotion(String name, double value) {
		super();
		this.name = name;
		this.value = value;
	}
	double value;
	public String getName() {
		return name;
	}
	public double getValue() {
		return value;
	}
}
