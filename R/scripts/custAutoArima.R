auto.arima.cust<-function(train.df,seasonality = "my",dataTimeCol,targetCol,regressorsCols=NULL) {
  #browser()
  library(forecast)
  library(lubridate)
  startDateTime = train.df[1,dataTimeCol]
  print(startDateTime)
  endDateTime = train.df[nrow(train.df),dataTimeCol]
  ncol.train.df = ncol(train.df)
  if(ncol.train.df <2)
    stop("Number of train data frame columns is < 2")
  step = NA
  freq = NA
  start.vec.1 = NA
  start.vec.2 = NA
  if(seasonality == "my"){
      step = "month"
      freq = 12
      start.vec.1 = year(startDateTime)
      start.vec.2 = month(startDateTime)
    }
  if(seasonality == "qy")
  {
    step = "quarter"
    freq = 4
    start.vec.1 = year(startDateTime)
    start.vec.2 = quarter(startDateTime)
  }
  fullDateTimeRange = seq.POSIXt(from = startDateTime,to = endDateTime
                                 ,by = step)  
  idx = match(fullDateTimeRange,train.df[,dataTimeCol])
  train.df.full = train.df[idx,]
  train.df.full[,dataTimeCol] = fullDateTimeRange
  train.df.imputed = impute(train.df.full)
  
  train.ts = ts(data = train.df.imputed[,targetCol],start = c(start.vec.1,start.vec.2)
                ,frequency = freq)
  #plot(train.ts)
  model = NA
  if(is.null(regressorsCols)) {
    model = auto.arima(x = train.ts)
  }
  else{
    model = auto.arima(x = train.ts,xreg =  train.df.imputed[,regressorsCols])
  }
  return(model)
}

impute<-function(df){
  #browser()
  for(j in 2: ncol(df)) {
    na.idx = is.na(df[,j])
    k = any(na.idx)
    mean.val = mean(df[,j],na.rm = T)
    mean.val = ifelse(is.na(mean.val),0,mean.val)
    df[na.idx,j] = mean.val
  }
  return (df)
}