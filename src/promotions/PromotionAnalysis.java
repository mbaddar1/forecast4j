package promotions;

import java.util.HashMap;
import java.util.Map;

public class PromotionAnalysis {
	private Map<String, Double> promotionEffect;
	public PromotionAnalysis() {
		promotionEffect = new HashMap<String, Double>();
	}
	public void addPromotionEffect(String promoName,Double effect) throws Exception {
		if(promotionEffect.containsKey(promoName)) {
			throw new Exception("Adding duplicate promotion");
		}
		promotionEffect.put(promoName, effect);
	}
	public double getPromotionEffect(String promoName) throws Exception {
		if(promotionEffect.containsKey(promoName)) {
			return promotionEffect.get(promoName);
		}
		else {
			throw new IllegalArgumentException("Promotion :"+promoName+" is not in this PromotionAnalysis instance");
		}
	}
}
