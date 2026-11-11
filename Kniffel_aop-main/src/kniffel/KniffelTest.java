package kniffel;

import java.util.List;

import categories.ScoreCategory;
import model.Dice;
import model.Player;
import presenter.GamePresenter;
import view.KniffelView;

/**
 * Testet das ganze Spiel über GamePresenter: ein komplettes Spiel mit
 * 2 Spielern, alle 13 Kategorien, dazu Bonus, erster Kniffel und das
 * Sperren nach einer Kategorie-Auswahl.
 * 
 * Grundstruktur und mehrere Testfälle mit KI-Unterstützung erstellt.
 */
public class KniffelTest {

    public static void main(String[] args) {
        playsFullTwoPlayerGameConsistently();
        upperBlockBonusIsGrantedAtExactly63();
        upperBlockBonusIsNotGrantedBelow63();
        firstKniffelGivesFifty();
        chanceEqualsSumOfAllDice();
        rollAndSelectAreBlockedAfterCategorySelected();
        selectingAlreadyScoredCategoryHasNoEffect();
        nextPlayerCyclesAndResetsRollsAndDice();
        toggleHoldChangesHeldState();
        System.out.println("Spielablauf-Tests erfolgreich.");
    }

    /**
     * Spielt ein komplettes Spiel mit 2 Spielern durch, alle 13 Kategorien.
     * Geprüft wird nicht der genaue Punktwert jeder Kategorie (die Formeln
     * kenne ich nicht), sondern dass Vorschau und gespeicherter Wert gleich
     * sind und die Summe am Ende stimmt.
     */
    private static void playsFullTwoPlayerGameConsistently() {
        TestView view = new TestView();
        GamePresenter presenter = new GamePresenter(view);
        presenter.startNewGame(List.of("Lea", "Emma"));
        presenter.toggleDebugMode(true);

        List<ScoreCategory> categories = presenter.getCategories();
        List<Player> players = view.lastCapturedPlayers;

        int[][] wuerfelSets = {
            {1,1,3,4,5}, {2,2,2,5,6}, {3,3,4,5,6}, {4,4,4,4,2}, {5,5,5,5,2},
            {6,6,1,2,3}, {3,3,3,2,5}, {4,4,4,4,1}, {2,2,5,5,5}, {1,2,3,4,6},
            {2,3,4,5,6}, {6,6,6,6,6}, {1,2,3,4,5}
        };
        // erwartete Werte anhand der echten calculateScore()-Formeln nachgerechnet
        int[] erwarteteScores = {2, 6, 6, 16, 20, 12, 16, 17, 25, 30, 40, 50, 15};

        for (int c = 0; c < categories.size(); c++) {
            ScoreCategory category = categories.get(c);
            for (int spielerNr = 0; spielerNr < players.size(); spielerNr++) {
                Player aktuellerSpieler = players.get(presenter.getCurrentPlayerIndex());

                presenter.setDebugValues(wuerfelSets[c]);
                presenter.selectCategory(category);

                Integer gespeichert = aktuellerSpieler.getScores().get(category);
                if (gespeichert == null || gespeichert != erwarteteScores[c]) {
                    throw new AssertionError(category.getName() + " bei " + aktuellerSpieler.getName()
                            + ": erwartet " + erwarteteScores[c] + ", war " + gespeichert);
                }

                presenter.nextPlayer();
            }
        }

        for (Player player : players) {
            if (player.getTotalScore() != 255) {
                throw new AssertionError(player.getName() + ": Endsumme muss 255 sein, war "
                        + player.getTotalScore());
            }
            if (player.getBonus() != 0) {
                throw new AssertionError(player.getName() + ": bei 62 Punkten im oberen Block darf kein Bonus stehen.");
            }
        }
    }

    /**
     * Regel, die wir sicher kennen: 63 Punkte im oberen Block geben Bonus 35.
     */
    private static void upperBlockBonusIsGrantedAtExactly63() {
        TestView view = new TestView();
        GamePresenter presenter = new GamePresenter(view);
        presenter.startNewGame(List.of("Lea"));
        presenter.toggleDebugMode(true);

        // 3 + 6 + 9 + 12 + 15 + 18 = 63
        bepunkteOberenBlock(presenter, new int[] {3, 6, 9, 12, 15, 18});

        Player lea = view.lastCapturedPlayers.get(0);
        if (lea.getBonus() != 35) {
            throw new AssertionError("Bei genau 63 Punkten im oberen Block muss der Bonus 35 sein, war: "
                    + lea.getBonus());
        }
    }

    private static void upperBlockBonusIsNotGrantedBelow63() {
        TestView view = new TestView();
        GamePresenter presenter = new GamePresenter(view);
        presenter.startNewGame(List.of("Lea"));
        presenter.toggleDebugMode(true);

        // 3 + 6 + 9 + 12 + 15 + 12 = 57 -> unter der Grenze
        bepunkteOberenBlock(presenter, new int[] {3, 6, 9, 12, 15, 12});

        Player lea = view.lastCapturedPlayers.get(0);
        if (lea.getBonus() != 0) {
            throw new AssertionError("Bei 57 Punkten im oberen Block darf kein Bonus vergeben werden, war: "
                    + lea.getBonus());
        }
    }

    /**
     * Trägt Einser bis Sechser mit den gewünschten Summen ein
     * (3 bei Einser heißt: drei Einsen).
     */
    private static void bepunkteOberenBlock(GamePresenter presenter, int[] summenProKategorie) {
        String[] namen = {"Einser", "Zweier", "Dreier", "Vierer", "Fuenfer", "Sechser"};
        for (int i = 0; i < namen.length; i++) {
            int augenzahl = i + 1;
            int anzahlTreffer = summenProKategorie[i] / augenzahl;
            int[] wuerfel = new int[5];
            for (int w = 0; w < 5; w++) {
                wuerfel[w] = (w < anzahlTreffer) ? augenzahl : (augenzahl == 6 ? 1 : 6);
            }
            presenter.setDebugValues(wuerfel);
            presenter.selectCategory(findCategoryByName(presenter, namen[i]));
            presenter.nextPlayer();
        }
    }

    /**
     * Regel, die wir sicher kennen: der erste Kniffel gibt 50 Punkte.
     */
    private static void firstKniffelGivesFifty() {
        TestView view = new TestView();
        GamePresenter presenter = new GamePresenter(view);
        presenter.startNewGame(List.of("Lea"));
        presenter.toggleDebugMode(true);
        presenter.setDebugValues(new int[] {4, 4, 4, 4, 4});

        ScoreCategory kniffel = findCategoryByName(presenter, "Kniffel");
        presenter.selectCategory(kniffel);

        Integer score = view.lastCapturedPlayers.get(0).getScores().get(kniffel);
        if (score == null || score != 50) {
            throw new AssertionError("Der erste Kniffel muss 50 Punkte geben, war: " + score);
        }
    }

    /**
     * Regel, die wir sicher kennen: Chance ist einfach die Summe aller 5 Würfel.
     */
    private static void chanceEqualsSumOfAllDice() {
        TestView view = new TestView();
        GamePresenter presenter = new GamePresenter(view);
        presenter.startNewGame(List.of("Lea"));
        presenter.toggleDebugMode(true);
        presenter.setDebugValues(new int[] {2, 3, 3, 5, 6});

        ScoreCategory chance = findCategoryByName(presenter, "Chance");
        presenter.selectCategory(chance);

        Integer score = view.lastCapturedPlayers.get(0).getScores().get(chance);
        if (score == null || score != 19) {
            throw new AssertionError("Chance muss die Summe aller Würfel sein (2+3+3+5+6=19), war: " + score);
        }
    }

    private static void rollAndSelectAreBlockedAfterCategorySelected() {
        TestView view = new TestView();
        GamePresenter presenter = new GamePresenter(view);
        presenter.startNewGame(List.of("Lea", "Emma"));

        ScoreCategory einser = findCategoryByName(presenter, "Einser");
        presenter.selectCategory(einser);
        int rollsVorher = presenter.getRollsLeft();

        presenter.rollDice();
        if (presenter.getRollsLeft() != rollsVorher) {
            throw new AssertionError("Nach Kategorie-Auswahl darf in derselben Runde nicht mehr gewürfelt werden.");
        }

        ScoreCategory zweier = findCategoryByName(presenter, "Zweier");
        presenter.selectCategory(zweier);
        Player lea = view.lastCapturedPlayers.get(0);
        if (lea.hasScored(zweier)) {
            throw new AssertionError("Nach Kategorie-Auswahl darf in derselben Runde keine weitere Kategorie gewählt werden.");
        }
    }

    private static void selectingAlreadyScoredCategoryHasNoEffect() {
        TestView view = new TestView();
        GamePresenter presenter = new GamePresenter(view);
        presenter.startNewGame(List.of("Lea", "Emma"));

        ScoreCategory einser = findCategoryByName(presenter, "Einser");
        presenter.selectCategory(einser);
        Player lea = view.lastCapturedPlayers.get(0);
        Integer ersterWert = lea.getScores().get(einser);

        presenter.selectCategory(einser);
        Integer zweiterWert = lea.getScores().get(einser);

        if (!ersterWert.equals(zweiterWert)) {
            throw new AssertionError("Eine zweite Auswahl derselben Kategorie darf den Score nicht ändern.");
        }
    }

    private static void nextPlayerCyclesAndResetsRollsAndDice() {
        GamePresenter presenter = new GamePresenter(new TestView());
        presenter.startNewGame(List.of("Lea", "Emma"));

        presenter.toggleHold(0);
        presenter.rollDice();
        presenter.rollDice();

        presenter.nextPlayer();

        if (presenter.getCurrentPlayerIndex() != 1) {
            throw new AssertionError("Nach nextPlayer() muss Spieler 1 dran sein.");
        }
        if (presenter.getRollsLeft() != 3) {
            throw new AssertionError("Für den neuen Spieler müssen wieder 3 Würfe verfügbar sein.");
        }
        for (Dice die : presenter.getDice()) {
            if (die.isHeld()) {
                throw new AssertionError("Neue Würfel dürfen nicht mehr gehalten sein.");
            }
        }
    }

    private static void toggleHoldChangesHeldState() {
        GamePresenter presenter = new GamePresenter(new TestView());
        presenter.startNewGame(List.of("Lea", "Emma"));

        presenter.toggleHold(0);
        if (presenter.getDice().get(0).isHeld()) {
            throw new AssertionError("Vor dem ersten Wurf darf toggleHold() nichts bewirken.");
        }

        presenter.rollDice();
        presenter.toggleHold(0);
        if (!presenter.getDice().get(0).isHeld()) {
            throw new AssertionError("Nach dem ersten Wurf muss toggleHold() den Würfel halten.");
        }

        presenter.toggleHold(0);
        if (presenter.getDice().get(0).isHeld()) {
            throw new AssertionError("Erneutes toggleHold() muss den Würfel wieder freigeben.");
        }
    }

    private static ScoreCategory findCategoryByName(GamePresenter presenter, String name) {
        for (ScoreCategory category : presenter.getCategories()) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        throw new AssertionError("Kategorie nicht gefunden: " + name);
    }

    /**
     * Eigene, einfache KniffelView nur für den Test: merkt sich nur, was der
     * Presenter zuletzt geschickt hat, statt ein echtes Fenster zu öffnen.
     */
    private static class TestView implements KniffelView {
        List<Player> lastCapturedPlayers;

        @Override
        public void updateDice(List<Dice> dice, int rollsLeft) {
        }

        @Override
        public void updateScoreTable(List<Player> players) {
            lastCapturedPlayers = players;
        }

        @Override
        public void updateCurrentPlayer(String name) {
        }

        @Override
        public void showWinner(String message) {
        }

    }
}