package forecasting.forecastEngine.forecastParameters;


public class CochraneOrcuttParameters extends ForecastMethodParameters
{
	/**
	 * Maximum number of iterations
	 */
	private int maxIter;
	public	CochraneOrcuttParameters(int maxIter) {
		this.maxIter = maxIter;
	}
	public int getMaxIter() {
		return maxIter;
	}
	
	
}
