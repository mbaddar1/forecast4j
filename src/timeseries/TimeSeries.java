package timeseries;

import java.time.ZonedDateTime;
import java.util.Date;

public abstract class TimeSeries {
	
	
	/*********
	 * Constants
	 */
	public static final int R_COMPATIBLE = 1;
	public static final double NA_FREQ = -1;
	/**************/
	private String name;
	private double frequency; //frequency can't be set externally , set based on seasonlity
	private Seasonality seasonality;
	private double[] data;
	private ZonedDateTime[] index;
	
	/**
	 * @param name 
	 * @param startDate
	 * @param frequency
	 * @param data
	 */
	protected TimeSeries(String name ,Seasonality seasonlity 
			, double[] data,ZonedDateTime[] index)
	{
		this.name = name;
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
		if(data.length != index.length)
			throw new IllegalArgumentException("data length is not equal to index length, data length = "
							+data.length+" index length = "+index.length);
		
		this.data = new double[data.length];
		this.index = new ZonedDateTime[index.length];
		for(int i=0;i<data.length;i++) {
			this.data[i] = data[i];
			this.index[i] = index[i];
		}
			
	}
	public Seasonality getSeasonality() {
		return seasonality;
	}
	protected String getName() {
		return name;
	}
	
	protected double[] getData() {
		return data;
	}
	public ZonedDateTime[] getIndex() {
		return index;
	}
	protected double getFrequency(){
		return frequency;
	}
	
}
