package categories;

import java.util.List;
import model.Dice;

public class GrosseStrasseCategory implements ScoreCategory{
	
	/**
	 * Berechnet die Punkte für die Kategorie "Große Straße".
	 *
	 * @param dice Liste mit allen Würfeln
	 * @param firstKniffel wird für diese Kategorie nicht benötigt
	 * @return 40 Punkte bei einer großen Straße, sonst 0
	 */
	
	@Override
	public int calculateScore(List<Dice> dice, boolean firstKniffel) {
		
		boolean[] present = new boolean[7];
		
		for(Dice die: dice) {
			present[die.getValue()] = true;
		}
		
		if ((present[1] && present[2] && present[3] && present[4] && present[5]) ||
		    (present[2] && present[3] && present[4] && present[5] && present[6])){
			 
			return 40;
		}
		
		return 0;
	}
	
	@Override
	public String getName() {
		return "Große Straße";
	}
}
