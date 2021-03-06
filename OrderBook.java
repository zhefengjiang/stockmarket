package pkg.order;
import java.util.ArrayList;
import java.util.HashMap;
import pkg.exception.StockMarketExpection;
import pkg.market.Market;
import pkg.market.api.*;
public class OrderBook {
	private Market m;
	HashMap<String, ArrayList<Order>> buyOrders;
	HashMap<String, ArrayList<Order>> sellOrders;
	PriceSetter priceSetter;
	public OrderBook(Market m) {
		this.m = m;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
		priceSetter = new PriceSetter();
	}

	public void addToOrderBook(Order order) {
		if (BuyOrder.class.isInstance(order)){
			if (this.buyOrders.containsKey(order.getStockSymbol())){
				ArrayList<Order> buyOrderList =buyOrders.get(order.getStockSymbol());
				buyOrderList.add(order);
			}
			else{
				ArrayList<Order> buyOrderList = new ArrayList<Order>();
				buyOrderList.add(order);
				this.buyOrders.put(order.getStockSymbol(), buyOrderList);
			}
		}
		else{
			if (this.sellOrders.containsKey(order.getStockSymbol())){
				ArrayList<Order> sellOrderList =sellOrders.get(order.getStockSymbol());
				sellOrderList.add(order);
			}
			else{
				ArrayList<Order> sellOrderList = new ArrayList<Order>();
				sellOrderList.add(order);
				this.sellOrders.put(order.getStockSymbol(), sellOrderList);
			}
		}
		
	}

	public void trade() {
		for (String stockSymbol : this.sellOrders.keySet()){
			if (this.buyOrders.containsKey(stockSymbol)){
				sellOrderList  = this.sellOrders.remove(stockSymbol);
				buyOrderList = this.buyOrders.remove(stockSymbol);
				Collections.sort(sellOrderList, new ComparatorLowToHigh());
				Collections.sort(buyOrderList, new ComparatorHighToLow());
				this.sellOrders.put(stockSymbol,sellOrderList);
				this.buyOrders.put(stockSymbol,buyOrderList);
				double matchPrice  = matchingPrice(this.sellOrders.get(stockSymbol), this.buyOrders.get(stockSymbol));
				if (matchPrice>0){
					try{
						m.updateStockPrice(stockSymbol, matchPrice);
					}
					catch (StockMarketExpection e){};
					int stockTradeSize =0;
					
				    for (Order sellOrder : this.sellOrders.get(stockSymbol)){
				    	if (sellOrder.getPrice()<=matchPrice){
				    		try{
				    			sellOrder.getTrader().tradePerformed(sellOrder, matchPrice);
				    			stockTradeSize += sellOrder.getSize();
				    		}
				    		catch (StockMarketExpection e){};
				    		this.sellOrders.remove(sellOrder);
				    	}
				    }
				    ArrayList<Order> tempOrders  = sortOrderPrice(this.buyOrders.get(stockSymbol));
				    this.buyOrders.remove(stockSymbol);
				    this.buyOrders.put(stockSymbol,tempOrders);
				    for (Order buyOrder : this.buyOrders.get(stockSymbol)){
				    	if (buyOrder.getPrice()>=matchPrice && stockTradeSize>0){
				    		try{
				    			if (stockTradeSize>buyOrder.getSize())
				    			{
				    				buyOrder.getTrader().tradePerformed(buyOrder, matchPrice);
				    				stockTradeSize -= buyOrder.getSize();
				    			}
				    			else{
				    				buyOrder.setSize(stockTradeSize);
				    				buyOrder.getTrader().tradePerformed(buyOrder, matchPrice);
				    				stockTradeSize = 0;
				    			}
				    							
				    		}
				    		catch (StockMarketExpection e){};
				    		this.buyOrders.remove(buyOrder);
				    	}
				    }
				}
			}
		}
	}
	private double matchingPrice(ArrayList<Order> sellOrderList, ArrayList<Order> buyOrderList){
		ArrayList<double[]> sellPriceList = new  ArrayList<double[]>();
		ArrayList<double[]> buyPriceList = new ArrayList<double[]>();
		for (int i = 0; i<sellOrderList.size(); i++){
			double[] priceSet = new double[3];
			priceSet[0] = sellOrderList.get(i).getPrice();
			priceSet[1] = sellOrderList.get(i).getSize();
			boolean flag = true;
			for (int j =0; j<sellPriceList.size();j++){
				if (priceSet[0]<sellPriceList.get(j)[0]){
					sellPriceList.add(j,priceSet);
					flag = false;
					break;
				}
			}
			if (flag){
				sellPriceList.add(priceSet);
			}
		}
		for (int i = 0; i < buyOrderList.size(); i++){
			double priceSet[] = new double[3];
			priceSet[0] = buyOrderList.get(i).getPrice();
			priceSet[1] = buyOrderList.get(i).getSize();
			boolean flag = true;
			for (int j =0; j<buyPriceList.size(); j++){
				if (priceSet[0]<buyPriceList.get(j)[0]){
					buyPriceList.add(j,priceSet);
					flag = false;
					break;
				}
			}
			if (flag){
				buyPriceList.add(priceSet);
			}
		}
		
		for(int i = 0; i < sellPriceList.size(); i++){
			if(i>0){
				sellPriceList.get(i)[2] =sellPriceList.get(i-1)[2] + sellPriceList.get(i)[1];
			}
			else{
				sellPriceList.get(0)[2] = sellPriceList.get(0)[1];
			}
			
		}
		
		for(int i = buyPriceList.size()-1; i>=0; i-- ){
			if (i<buyPriceList.size()-1){
				buyPriceList.get(i)[2] = buyPriceList.get(i+1)[2] +buyPriceList.get(i)[1];
			}
			else{
				buyPriceList.get(i)[2] = buyPriceList.get(i)[1];
			}
			
		}
		//get the price
		double matchPrice =0;
		for (int i = sellPriceList.size()-1; i>=0; i--){
			double bidPrice = sellPriceList.get(i)[0];
			double sellAmount = sellPriceList.get(i)[2];
			double buyAmount = 0;
			for (int j = 0;j <buyPriceList.size();j++ ){
				if (buyPriceList.get(j)[0]>=bidPrice){
					buyAmount = buyPriceList.get(j)[2];
					break;
				}
			}
			if (sellAmount<=buyAmount){
				matchPrice = bidPrice;
				break;
			}
		}
		return matchPrice; 
	}
private class ComparatorLowToHigh implements Comparator<Order> {
	    @Override
	    public int compare(Order o1, Order o2) {
	    	double compareIndicator= 100*(o1.getPrice() - o2.getPrice());
	    	int Indicator =(int) compareIndicator;
	    	return Indicator;
	    }
	}
	private class ComparatorHighToLow implements Comparator<Order> {
	    @Override
	    public int compare(Order o1, Order o2) {
	    	double compareIndicator= 100*(o1.getPrice() - o2.getPrice());
	    	int Indicator =(int) compareIndicator;
	    	return -Indicator;
	    }
	}
	
}
