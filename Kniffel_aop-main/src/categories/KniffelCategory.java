package categories;

import java.util.List;
import model.Dice;

public class KniffelCategory implements ScoreCategory{
	
	/**
	 * Berechnet die Punkte für die Kategorie "Kniffel".
	 *
	 * @param dice Liste mit allen Würfeln
	 * @param firstKniffel zeigt, ob es der erste Kniffel ist
	 * @return 50 Punkte beim ersten Kniffel, 100 Punkte bei einem weiteren Kniffel, sonst 0
	 */
	
	@Override
	public int calculateScore(List<Dice> dice, boolean firstKniffel) {
		
		int firstValue = dice.get(0).getValue();
		
		for(Dice die: dice) {
			if(die.getValue() != firstValue) {
				return 0;
			}
		}
		
		if(firstKniffel) {
			return 50;
		}
		
		return 100;
	}
	
	@Override
	public String getName() {
		return "Kniffel";
	}
}
