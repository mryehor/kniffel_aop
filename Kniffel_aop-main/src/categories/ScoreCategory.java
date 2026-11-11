package categories;

import java.util.List;
import model.Dice;

/**
 * Interface für alle Punkte-Kategorien im Kniffel-Spiel.
 *
 * Jede Kategorie berechnet ihre Punkte selbst
 * und gibt ihren Namen zurück.
 */

public interface ScoreCategory {

	/**
	 * Berechnet die Punkte der jeweiligen Kategorie.
	 *
	 * @param dice Liste mit allen Würfeln
	 * @param firstKniffel zeigt, ob es der erste Kniffel ist
	 * @return berechnete Punktzahl
	 */
	int calculateScore(List<Dice> dice, boolean firstKniffel);
	 
	/**
	 * @return Name der Kategorie
	 */
	String getName();
}