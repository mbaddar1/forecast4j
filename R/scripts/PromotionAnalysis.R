#We use the model in the paper
#Price- and Cross-Price Elasticity Estimation using SAS
#http://support.sas.com/resources/papers/proceedings13/425-2013.pdf
analyze.promotions<-function(formula,transactions,dateTimeIndexColName,method = "lm",...) {
  mf = model.frame(formula = formula,data = transactions)
  X = model.matrix(object = attr(mf, "terms"), data=mf)
  Y = model.response(mf)
  methods = c("lm","arima.xreg","co")
  model = NA
  if(method == "lm")
  {
    model = lm(formula = formula,data = transactions)
    print(summary(model))
  }
  ret = list()
  class(ret) <- "PromoAnalysis"
  ret$model = model
  ret
}

consume.promo.analysis <-function(promo.analysis) {
  sig.thr = 0.1
  if(!is(promo.analysis,"PromoAnalysis"))
    stop("Parameter promo.analysis.model must be of class PromoAnalysis")
  model = promo.analysis$model
  
  if(is(model,"lm")) {
    summ = summary(model)
    coeff = summ$coefficients
    coeff.row.names = rownames(coeff)
    promo.effect = data.frame(promotion = coeff.row.names
                              ,effect = vector(mode = "numeric"
                              ,length = nrow(coeff))
                              ,stringsAsFactors = F)
    
    for(i in 1: nrow(coeff)) {
      if(coeff[i,"Pr(>|t|)"] <= sig.thr) {
        promo.effect[i,"effect"] = coeff[i,"Estimate"]
      }
      else {
        promo.effect[i,"effect"] = 0
      }
    }
    return (promo.effect)
  }
  
}