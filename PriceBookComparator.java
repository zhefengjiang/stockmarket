package pkg.order;

import java.util.Comparator;

public class PriceBookComparator implements Comparator<PriceBook> {

	@Override
	public int compare(PriceBook arg0, PriceBook arg1) {
		if (arg0.price < arg1.price) {
			return -1;
		}
		if (arg0.price > arg1.price) {
			return 1;
		}
		return 0;
	}
	

}
