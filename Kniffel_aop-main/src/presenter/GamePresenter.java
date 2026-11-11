package presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import categories.ChanceCategory;
import categories.DreierCategory;
import categories.DreierpaschCategory;
import categories.EinserCategory;
import categories.FuenferCategory;
import categories.FullHouseCategory;
import categories.GrosseStrasseCategory;
import categories.KleineStrasseCategory;
import categories.KniffelCategory;
import categories.ScoreCategory;
import categories.SechserCategory;
import categories.ViererCategory;
import categories.ViererpaschCategory;
import categories.ZweierCategory;
import model.Dice;
import model.Player;
import view.KniffelView;

/**
 * Steuert den kompletten Spielablauf.
 * Verbindet View (Anzeige) und Model (Spieldaten), enthält die gesamte Spiellogik.
 */
public class GamePresenter {
	
	private KniffelView view;
	private List<Player> players;
	private List<Dice> dice;
	private List<ScoreCategory> categories;
	private int currentPlayerIndex;
	private int rollsLeft;
	private boolean debugMode;
	private boolean categorySelected;
	
	public GamePresenter(KniffelView view) {
		this.view = view;
	}
	
	/**
	 * Startet ein neues Spiel: legt für jeden übergebenen Namen einen Spieler
	 * an, erzeugt die Würfel und alle 13 Kategorien und setzt den Spielzustand
	 * zurück.
	 *
	 * @param playerNames Namen der teilnehmenden Spieler
	 */
	public void startNewGame(List<String> playerNames) {
		players = new ArrayList<>();
		for (String name : playerNames) {
			players.add(new Player(name));
		}

		view.updateCurrentPlayer(players.get(currentPlayerIndex).getName());

		dice = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			dice.add(new Dice());
		}

		categories = new ArrayList<>();
		categories.add(new EinserCategory());
		categories.add(new ZweierCategory());
		categories.add(new DreierCategory());
		categories.add(new ViererCategory());
		categories.add(new FuenferCategory());
		categories.add(new SechserCategory());
		categories.add(new DreierpaschCategory());
		categories.add(new ViererpaschCategory());
		categories.add(new FullHouseCategory());
		categories.add(new KleineStrasseCategory());
		categories.add(new GrosseStrasseCategory());
		categories.add(new KniffelCategory());
		categories.add(new ChanceCategory());
		currentPlayerIndex = 0;
		rollsLeft = 3;
		categorySelected = false;
		
		view.updateDice(dice, rollsLeft);
		view.updateScoreTable(players);
	}
	
	/**
	 * Ermöglicht es, die Würfelwerte selbst anzugeben.
	 * Wichtig um Spiel testen zu können.
	 */
	public void toggleDebugMode(boolean isActive) {
		debugMode = isActive;
	}

	/**
	 * Möglichkeit, einen Würfel auf hold zu setzen, damit er nicht noch ein Mal
	 * gewürfelt wird.
	 */
	public void toggleHold(int index) {
		dice.get(index).toggleHeld();
	}

	/**
	 * @return schreibgeschützte Sicht auf die aktuellen Würfel
	 */
	public List<Dice> getDice() {
		return Collections.unmodifiableList(dice);
	}

	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}

	public int getRollsLeft() {
		return rollsLeft;
	}
	
	/**
	 * @return schreibgeschützte Sicht auf die verfügbaren Kategorien
	 */
	public List<ScoreCategory> getCategories() {
		return Collections.unmodifiableList(categories);
	}

	/**
	 * Würfelt alle nicht gehaltenen Würfel neu und verringert die Anzahl
	 * verbleibender Würfe um eins. Ist bereits eine Kategorie gewählt oder
	 * kein Wurf mehr übrig, passiert nichts.
	 */
	public void rollDice() {
		
		if(categorySelected) {
			return;
		}
		
		if (rollsLeft <= 0) {
			return;
		}

		for (Dice die : dice) {
			die.roll();
		}
		rollsLeft--;

		view.updateDice(dice, rollsLeft); 
		view.updateScoreTable(players);
	}
	
	/**
	 * @return true, wenn in der aktuellen Runde bereits eine Kategorie gewählt wurde.
	 * Wird von der View gebraucht, um die Punkte-Vorschau auszublenden, sobald ein
	 * weiterer Klick ohnehin keine Wirkung mehr hätte (siehe selectCategory()).
	 */
	public boolean isCategorySelected() {
	    return categorySelected;
	}

	/**
	 * Berechnet, wie viele Punkte der aktuelle Spieler für die übergebene
	 * Kategorie beim jetzigen Wurf bekommen würde, ohne sie einzutragen.
	 *
	 * @param category die zu berechnende Kategorie
	 * @return die berechnete Punktzahl
	 */
	public int calculatePreviewScore(ScoreCategory category) {
		Player current = players.get(currentPlayerIndex);

		if (current.hasScoredFirstKniffel() && isKniffel()) {
			return 100;
		}

		return category.calculateScore(
				dice,
				!current.hasScoredFirstKniffel());
	}
		
	/**
	 * Trägt die Punkte der gewählten Kategorie beim aktuellen Spieler ein
	 * und sperrt Würfeln/Auswählen für den Rest der Runde.
	 *
	 * @param category die vom Spieler gewählte Kategorie
	 */ 
	public void selectCategory(ScoreCategory category) {
		Player current = players.get(currentPlayerIndex);
		
		if(categorySelected) {
			return;
		}
		if (current.hasScored(category)) {
			return;
		}

		int score = calculatePreviewScore(category);

		current.setScore(category, score);
		categorySelected = true;

		view.updateScoreTable(players);
	}
	
	/**
	 * Prüft, ob alle 5 Würfel denselben Augenwert zeigen, verglichen mit dem ersten Würfelwert.
	 *
	 * @return true, wenn ein Kniffel vorliegt
	 */
	private boolean isKniffel() {
	    int firstValue = dice.get(0).getValue();

	    for (Dice die : dice) {
	        if (die.getValue() != firstValue) {
	            return false;
	        }
	    }

	    return true;
	}
	
	/**
	 * Setzt die Möglichkeit drei Mal zu würfeln jedes Mal zurück, wenn ein
	 * neuer Spieler dran ist.
	 */
	public void nextPlayer() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();	//KI
		rollsLeft = 3;
		categorySelected = false;
		
		dice = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			dice.add(new Dice());
		}

		view.updateDice(dice, rollsLeft);
		view.updateScoreTable(players);
		view.updateCurrentPlayer(players.get(currentPlayerIndex).getName());
	}

	/**
	 * Setzt im Debug-Modus feste Werte für alle Würfel.
	 *
	 * @param debugValues fünf Würfelwerte 
	 * @throws IllegalArgumentException wenn nicht genau fünf Werte übergeben
	 *         werden oder ein Wert außerhalb von 1 bis 6 liegt
	 */
	public void setDebugValues(int[] debugValues) {
		if (!debugMode) {
			return;
		}
		if (debugValues == null || debugValues.length != dice.size()) {
			throw new IllegalArgumentException("Es werden genau fünf Würfelwerte benötigt.");
		}
		for (int value : debugValues) {
			if (value < 1 || value > 6) {
				throw new IllegalArgumentException("Jeder Würfelwert muss zwischen 1 und 6 liegen.");
			}
		}
		
		//Übergibt die Debug Werte auf die Würfel
		for (int index = 0; index < debugValues.length; index++) {
			dice.get(index).setDebugValue(debugValues[index]);
		}

		view.updateDice(dice, rollsLeft);
		view.updateScoreTable(players);
	}

}