package promotions;

import java.util.HashMap;
import java.util.Map;

public class PromotionAnalysis {
	private Map<Promotion, Double> promotionEffect;
	public PromotionAnalysis() {
		promotionEffect = new HashMap<Promotion, Double>();
	}
	public void addPromotionEffect(Promotion promo,Double effect) throws Exception {
		if(promotionEffect.containsKey(promo)) {
			throw new Exception("Adding duplicate promotion");
		}
		promotionEffect.put(promo, effect);
	}
	public double getPromotionEffect(Promotion promo) throws Exception {
		if(promotionEffect.containsKey(promo)) {
			return promotionEffect.get(promo);
		}
		else {
			throw new Exception("Promotion :"+promo+" is not in this PromotionAnalysis instance");
		}
	}
}
