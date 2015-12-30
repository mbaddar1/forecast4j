package timeseries;

import java.util.Date;

public abstract class TimeSeries {
	
	
	/*********
	 * Constants
	 */
	public static final int R_COMPATIBLE = 1;
	/**************/
	private String name;
	private Date startDate;
	private double frequency;
	private double[] data;
	/**
	 * @param name 
	 * @param startDate
	 * @param frequency
	 * @param data
	 */
	protected TimeSeries(String name , Date startDate ,double frequency , double[] data)
	{
		this.name = name;
		this.startDate = startDate;
		this.frequency = frequency;
		this.data = new double[data.length];
		for(int i=0;i<data.length;i++)
			this.data[i]=data[i];
	}
	protected String getName() {
		return name;
	}
	protected Date getStartDate() {
		return startDate;
	}
	
	protected double getFrequency() {
		return frequency;
	}
	protected double[] getData() {
		return data;
	}
	
	
}
