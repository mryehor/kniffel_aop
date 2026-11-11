package view;

import java.util.List;

import model.Dice;
import model.Player;

/**
 * Definiert die Anzeigeoperationen, die vom Spiel-Presenter verwendet werden.
 * Implementierungen zeigen den Spielzustand an, verändert nicht die Spieldaten.
 */
public interface KniffelView {
	/**
	 * Zeigt die aktuellen Würfel und die verbleibenden Würfe des aktiven Zuges an.
	 *
	 * @param dice die anzuzeigenden Würfel
	 * @param rollsLeft Anzahl der noch verfügbaren Würfe
	 */
	void updateDice(List<Dice> dice, int rollsLeft);

	/**
	 * Zeigt den Spielzettel mit den Spalten aller Spieler an.
	 * 
	 * @param players Spieler, deren Spalten angezeigt werden sollen
	 */
	void updateScoreTable(List<Player> players);

	/**
	 * Zeigt nach der letzten Runde den Gewinner an.
	 *
	 * @param message anzuzeigende Gewinnermeldung
	 */
	void showWinner(String message);

	/**
	 * Zeigt an, welcher Spieler aktuell am Zug ist.
	 *
	 * @param name Name des aktuellen Spielers
	 */
	void updateCurrentPlayer(String name);
}
