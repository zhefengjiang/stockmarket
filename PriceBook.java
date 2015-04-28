package pkg.order;

public class PriceBook implements Comparable<PriceBook>{
	double price;
	
	int BuyIndivdualOrders;
	int BuyCumulativePerPriceOrders;
	int BuyCumulativeLeastFavorablePrice;
	
	int SellCumulativeLeastFavorablePrice;
	int SellCumulativePerPrice;
	int SellIndividualOrders;
	
	public PriceBook(double price, int BuyIndivdualOrders, int BuyCumulativePerPriceOrders,
			int BuyCumulativeLeastFavorablePrice, int SellCumulativeLeasFavorablePrice,
			int SellCumulativePerPrice, int SellIndividualOrders) {
			
		this.BuyIndivdualOrders = BuyIndivdualOrders;
		this.BuyCumulativePerPriceOrders = BuyCumulativePerPriceOrders;
		this.BuyCumulativeLeastFavorablePrice = BuyCumulativeLeastFavorablePrice;
		
		this.SellCumulativeLeastFavorablePrice = SellCumulativeLeasFavorablePrice;
		this.SellCumulativePerPrice = SellCumulativePerPrice;
		this.SellIndividualOrders = SellIndividualOrders;
		
		this.price = price;
		
	}
	
	public PriceBook(Order o) {
		if (o == null) {
			return;
		}
		if (o instanceof BuyOrder) {
			this.BuyIndivdualOrders = o.getSize();
			this.BuyCumulativePerPriceOrders = 0;
			this.BuyCumulativeLeastFavorablePrice = 0;
			
			this.SellCumulativeLeastFavorablePrice = 0;
			this.SellCumulativePerPrice = 0;
			this.SellIndividualOrders = 0;
			
			this.price = o.getPrice();
		}
		else {
			
			this.BuyIndivdualOrders = 0;
			this.BuyCumulativePerPriceOrders = 0;
			this.BuyCumulativeLeastFavorablePrice = 0;
			
			this.SellCumulativeLeastFavorablePrice = 0;
			this.SellCumulativePerPrice = 0;
			this.SellIndividualOrders = o.getSize();
			
			this.price = o.getPrice();
		}
	}
	
	public int compareTo(PriceBook book) {
		if (this.price < book.price) {
			return -1;
		}
		else if (this.price > book.price) {
			return 1;
		}
		return 0;
	}
	public String toString() {
		String output = this.BuyIndivdualOrders + "\t" + this.BuyCumulativeLeastFavorablePrice + "\t" 
				+ this.price + "\t"
				+ this.SellCumulativeLeastFavorablePrice + "\t"
				+ this.BuyIndivdualOrders;
		return output;
	}
}
