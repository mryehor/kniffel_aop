package categories;

import java.util.List;
import model.Dice;

public class ChanceCategory implements ScoreCategory{
	
	/**
	 * Berechnet die Punkte für die Kategorie "Chance".
	 *
	 * @param dice Liste mit allen Würfeln
	 * @param firstKniffel wird für diese Kategorie nicht benötigt
	 * @return Summe von allen Würfeln
	 */
	
	@Override
	public int calculateScore(List<Dice> dice, boolean firstKniffel) {

		int score = 0;

		for (Dice die : dice) {
			score += die.getValue();
		}

		return score;
	}

	@Override
	public String getName() {
		return "Chance";
	}
}
