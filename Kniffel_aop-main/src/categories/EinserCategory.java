package categories;

import java.util.List;
import model.Dice;

public class EinserCategory implements ScoreCategory{
	
    /**
     * Berechnet die Punkte für die Kategorie "Einser".
     *
     * @param dice Liste mit allen Würfeln
     * @param firstKniffel wird für diese Kategorie nicht benötigt
     * @return Summe aller Würfel mit dem Wert 1
     */
	
	@Override
	public int calculateScore(List<Dice> dice, boolean firstKniffel) {
		int score = 0;
		for(Dice die: dice) {
			if(die.getValue() == 1) {
				score += 1;
			}
		}
		return score;
	}
	
	@Override
	public String getName() {
		return "Einser";
	}
}
