package pkg.order;

import pkg.exception.StockMarketExpection;
import pkg.trader.Trader;

public class SellOrder extends Order {
	
	public final OrderType orderType = OrderType.SELL;
			
	public SellOrder(String stockSymbol, int size, double price, Trader trader) {
		super();
		this.stockSymbol = stockSymbol;
		this.size = size;
		this.price = price;
		this.trader = trader;
		
	}

	public SellOrder(String stockSymbol, int size, boolean isMarketOrder,
			Trader trader) throws StockMarketExpection {
		super();
		if (!isMarketOrder) {
			throw new StockMarketExpection("An order has been placed without a valid price");
		}
		this.stockSymbol = stockSymbol;
		this.size = size;
		this.price = 0;
		this.trader = trader;
		this.isMarketOrder = true;
	}

	public void printOrder() {
		System.out.println("Stock: " + stockSymbol + " $" + price + " x "
				+ size + " (Sell)");
	}

}
