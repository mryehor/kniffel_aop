package view;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import categories.ScoreCategory;
import model.Player;
import presenter.GamePresenter;

/**
 * Zeigt den Spielzettel an: 
 * alle Kategorien als Zeilen, eine Spalte pro Spieler mit dessen aktuellen Punkten. 
 * Klickt der Nutzer in eine Kategorie-Zeile,meldet das Panel an GamePresenter weiter.
 */
public class ScoreTablePanel extends JPanel {
	
	private List<Player> players;
	private GamePresenter presenter;

	//mit KI erstellt
	private static final int ROW_HEIGHT = 24;
	private static final int COL_WIDTH = 100;
	private static final int START_X = 20;
	private static final int START_Y = 20;
	private static final int OBERER_BLOCK_GROESSE = 6; 

	/** Position jeder gezeichneten Kategorie-Zeile, für die Klick-Erkennung. */
	private List<Rectangle> rowBounds = new ArrayList<>();

	/** Zu rowBounds passende Kategorie an derselben Listenposition. */
	private List<ScoreCategory> rowCategories = new ArrayList<>();

	/**
	 * @param presenter der GamePresenter, dem die gewählte Kategorie gemeldet wird
	 */
	public ScoreTablePanel(GamePresenter presenter) {
		this.presenter = presenter;

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for (int i = 0; i < rowBounds.size(); i++) {
					if (rowBounds.get(i).contains(e.getPoint())) {
						presenter.selectCategory(rowCategories.get(i));
						return;
					}
				}
			}
		});
	}

	/**
	 * @param players die aktuellen Spieler, deren Punktestände angezeigt werden sollen
	 */
	public void updateScoreTable(List<Player> players) {
		this.players = players;
		repaint();
	}
		
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		rowBounds.clear();
		rowCategories.clear();

		if (players == null || players.isEmpty()) {
			return;
		}

		int rowWidth = COL_WIDTH * (players.size() + 1); // Namen-Spalte + eine Spalte pro Spieler
		int y = START_Y;

		int headerX = START_X + COL_WIDTH;
		for (Player player : players) {
			g.drawString(player.getName(), headerX + 10, y + 16); //mit KI erstellt
			headerX += COL_WIDTH;
		}
		y += ROW_HEIGHT;

		int categoryIndex = 0;
		for (ScoreCategory category : presenter.getCategories()) {
			rowBounds.add(new Rectangle(START_X, y, rowWidth, ROW_HEIGHT));
			rowCategories.add(category);

			g.drawRect(START_X, y, rowWidth, ROW_HEIGHT);
			g.drawString(category.getName(), START_X + 5, y + 16); //mit KI erstellt
			int x = START_X + COL_WIDTH;
			int playerIndex = 0;
			boolean hatGewuerfelt = presenter.getRollsLeft() < 3;

			for (Player player : players) {
				Integer punkte = player.getScores().get(category);
				String text;

				if (punkte != null) {
					text = String.valueOf(punkte);
				} else if (playerIndex == presenter.getCurrentPlayerIndex() && hatGewuerfelt
						&& !presenter.isCategorySelected()) {
				
					int vorschau = presenter.calculatePreviewScore(category);
					text = "(" + vorschau + ")";
				} else {
					text = "-";
				}
				
				//mit KI erstellt
				g.drawRect(x, y, COL_WIDTH, ROW_HEIGHT);
				g.drawString(text, x + 10, y + 16);
				x += COL_WIDTH;
				playerIndex++;
			}

			y += ROW_HEIGHT;
			categoryIndex++;

			// Bonus-Zeile nach den ersten 6 Kategorien 
			if (categoryIndex == OBERER_BLOCK_GROESSE) {
				g.drawRect(START_X, y, rowWidth, ROW_HEIGHT);
				g.drawString("Bonus (ab 63)", START_X + 5, y + 16);

				int bonusX = START_X + COL_WIDTH;
				for (Player player : players) {
					g.drawString(String.valueOf(player.getBonus()), bonusX + 10, y + 16); //mit KI erstellt
					bonusX += COL_WIDTH;
				}
				y += ROW_HEIGHT;
			}
		}

		g.drawRect(START_X, y, rowWidth, ROW_HEIGHT);
		g.drawString("Endsumme", START_X + 5, y + 16); //mit KI erstellt

		int summeX = START_X + COL_WIDTH;
		for (Player player : players) {
			g.drawString(String.valueOf(player.getTotalScore()), summeX + 10, y + 16); //mit KI erstellt
			summeX += COL_WIDTH;
		}
		y += ROW_HEIGHT;

		// zeigt laufend den aktuell führenden Spieler, kein Gleichstand 
		Player fuehrender = players.get(0);
		for (Player player : players) {
			if (player.getTotalScore() > fuehrender.getTotalScore()) {
				fuehrender = player;
			}
		}

		g.drawRect(START_X, y, rowWidth, ROW_HEIGHT);
		g.drawString("Gewinner: " + fuehrender.getName(), START_X + 5, y + 16); //mit KI erstellt
		y += ROW_HEIGHT;
	}
}