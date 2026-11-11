package categories;

import java.util.List;
import model.Dice;

public class ViererpaschCategory implements ScoreCategory{
	
	
	/**
	 * Berechnet die Punkte für die Kategorie "Dreierpasch".
	 *
	 * @param dice Liste mit allen Würfeln
	 * @param firstKniffel wird für diese Kategorie nicht benötigt
	 * @return Summe aller Würfel, wenn mindestens 4 gleiche Würfel vorhanden sind, sonst 0
	 */
	
	@Override
	public int calculateScore(List<Dice> dice, boolean firstKniffel) {
		
		int[] count = new int[7];
		int sum = 0;
		
		for(Dice die: dice) {
			int value = die.getValue();
			count[value]++;
			sum += value;
		}
		
		for (int i = 1; i <= 6; i++) {
			if(count[i] >= 4) {
				return sum;
			}
		}
		return 0;
	}
	
	@Override
	public String getName() {
		return "Viererpasch";
	}
}
