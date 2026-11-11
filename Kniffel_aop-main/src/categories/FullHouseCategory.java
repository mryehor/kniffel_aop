package categories;

import java.util.List;
import model.Dice;

public class FullHouseCategory implements ScoreCategory{
	
	/**
	 * Berechnet die Punkte für die Kategorie "Full House".
	 *
	 * @param dice Liste mit allen Würfeln
	 * @param firstKniffel wird für diese Kategorie nicht benötigt
	 * @return 25 Punkte bei einem Full House, sonst 0
	 */
	
	@Override
	public int calculateScore(List<Dice> dice, boolean firstKniffel) {
		
		int[] count = new int[7];
		
		for(Dice die: dice) {
			int value = die.getValue();
			count[value]++;
		}
		
		boolean hasThree = false;
		boolean hasTwo = false;
		
		for (int i = 1; i <= 6; i++) {
			if(count[i] == 3) {
				hasThree = true;
			}
			if(count[i] == 2) {
				hasTwo = true;
			}
		}
		
		if(hasThree & hasTwo ) {
			return 25;
		}
		return  0;
	}
	
	@Override
	public String getName() {
		return "Full House";
	}
}
