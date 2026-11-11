package model;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import categories.ScoreCategory;

/**
 * Repräsentiert einen Spieler mit Namen und seiner Spalte
 * Ordnet erzielte Punkte zur Kategorie zu.
 */
public class Player {

	private String name;
	private Map<ScoreCategory, Integer> scores;

	private static final Set<String> OBERER_BLOCK =
			Set.of("Einser", "Zweier", "Dreier", "Vierer", "Fuenfer", "Sechser");
	private static final int BONUS_GRENZE = 63;
	private static final int BONUS_PUNKTE = 35;

	/**
	 * @param name Name des Spielers
	 */
	public Player(String name) {
		this.name = name;
		this.scores = new LinkedHashMap<>();
	}

	public String getName() {
		return name;
	}

	/**
	 * @return schreibgeschützte Sicht auf die Punktestände
	 */
	public Map<ScoreCategory, Integer> getScores() {
		return Collections.unmodifiableMap(scores);
	}

	/**
	 * @param category, zu bewertende Kategorie
	 * @param score, erzielte Punktzahl
	 * @throws IllegalArgumentException, wenn score negativ ist
	 * @throws IllegalStateException, wenn die Kategorie bereits bewertet wurde
	 */
	public void setScore(ScoreCategory category, int score) {
		if (score < 0) {
			throw new IllegalArgumentException("Die Punktzahl darf nicht negativ sein.");
		}
		if (hasScored(category)) {
			throw new IllegalStateException("Die Kategorie wurde bereits bewertet: " + category.getName());
		}
		scores.put(category, score);
	}

	public boolean hasScored(ScoreCategory category) {
		return scores.containsKey(category);
	}

	/**
	 * @return Summe der Punkte aus dem oberen Block, wichtig für Bonusberechnung
	 */
	private int getObererBlockSumme() {
		int summe = 0;
		for (Map.Entry<ScoreCategory, Integer> entry : scores.entrySet()) {
			if (OBERER_BLOCK.contains(entry.getKey().getName())) {
				summe += entry.getValue();
			}
		}
		return summe;
	}

	/**
	 * @return 35, wenn der obere Block bereits 63 oder mehr Punkte erreicht hat, sonst 0
	 */
	public int getBonus() {
		return getObererBlockSumme() >= BONUS_GRENZE ? BONUS_PUNKTE : 0;
	}

	/**
	 * @return Summe aller bepunkteten Kategorien mit Einberechnung des Bonus
	 */
	public int getTotalScore() {
		int total = 0;
		for (int value : scores.values()) {
			total += value;
		}
		return total + getBonus();
	}

	/**
	 * @return true, wenn dieser Spieler bereits eine Kniffel-Wertung erhalten hat;
	 * entscheidet, ob ein weiterer Kniffel 50 oder 100 Punkte gibt
	 * Mit KI erstellt
	 */
	public boolean hasScoredFirstKniffel() {
		for (Map.Entry<ScoreCategory, Integer> entry : scores.entrySet()) {
			if (entry.getKey().getName().equals("Kniffel") && entry.getValue() > 0) {
				return true;
			}
		}
		return false;
	}
}