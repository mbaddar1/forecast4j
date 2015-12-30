package timeseries;

import java.util.Date;

public abstract class TimeSeries {
	
	
	/*********
	 * Constants
	 */
	public static final int R_COMPATIBLE = 1;
	public static final double NA_FREQ = -1;
	/**************/
	private String name;
	private Date startDate;
	private double frequency; //frequency can't be set externally , set based on seasonlity
	private Seasonality seasonality;
	private double[] data;
	/**
	 * @param name 
	 * @param startDate
	 * @param frequency
	 * @param data
	 */
	protected TimeSeries(String name , Date startDate ,Seasonality seasonlity , double[] data)
	{
		this.name = name;
		this.startDate = startDate;
		this.seasonality = seasonlity;
		
		switch(seasonlity) {
		case DAY_OF_WEEK :
			frequency = 7;
			break;
		case MONTH_OF_YEAR :
			frequency =12;
			break;
		case NO_SEASONLAITY :
			frequency = NA_FREQ;
			break;
		default :
			throw new IllegalArgumentException("Seasonlity = "+seasonlity+" is not supported");		
		}
		this.data = new double[data.length];
		for(int i=0;i<data.length;i++)
			this.data[i]=data[i];
	}
	public Seasonality getSeasonality() {
		return seasonality;
	}
	protected String getName() {
		return name;
	}
	protected Date getStartDate() {
		return startDate;
	}
	protected double[] getData() {
		return data;
	}
	protected double getFrequency(){
		return frequency;
	}
	
}
