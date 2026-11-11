package view;

import java.awt.BorderLayout;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.util.ArrayList;
import java.util.List;
import model.Player;
import model.Dice;
import presenter.GamePresenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Das Hauptfenster des Spiels. Implementiert KniffelView und leitet alle
 * Anzeige-Aufrufe des GamePresenters an die passenden Panels weiter.
 * Ändert den Spielzustand nicht.
 */
public class MainFrame extends JFrame implements KniffelView {

	private GamePresenter presenter;
	private DicePanel dicePanel;
	private ScoreTablePanel scoreTablePanel;
	private DebugPanel debugPanel;

	private JPanel startPanel;
	private JTextField[] nameFields;
	private JLabel currentPlayerLabel;

	private static final int MAX_SPIELER = 5;

	/**
	 * Erzeugt den GamePresenter mit sich selbst (this) als View, noch bevor
	 * die Panels existieren - der Presenter speichert die Referenz im
	 * Konstruktor nur, ruft zu diesem Zeitpunkt aber keine Methode darauf auf.
	 */
	public MainFrame() {
		super("Kniffel");

		presenter = new GamePresenter(this);
		dicePanel = new DicePanel(presenter);
		scoreTablePanel = new ScoreTablePanel(presenter);
		debugPanel = new DebugPanel(presenter);

		buildStartPanel();

		setLayout(new BorderLayout());
		add(startPanel, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(700, 500);
		setVisible(true);
	}

	/**
	 * Baut den Startbildschirm: für jeden Spieler eine Überschrift ("Spieler X")
	 * mit Eingabefeld darunter, plus Start-Button.
	 */
	private void buildStartPanel() {
		startPanel = new JPanel(new GridLayout(MAX_SPIELER * 2 + 1, 1));
		nameFields = new JTextField[MAX_SPIELER];

		for (int i = 0; i < MAX_SPIELER; i++) {
			startPanel.add(new JLabel("Spieler " + (i + 1)));
			nameFields[i] = new JTextField();
			startPanel.add(nameFields[i]);
		}

		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startSpiel();
			}
		});
		startPanel.add(startButton);
	}

	/**
	 * Ersetzt den Startbildschirm durch die Spiel-Panels (Würfel, Spielzettel,
	 * Debug-Bereich) und startet das Spiel mit den eingegebenen Spielernamen.
	 * Zeigt eine Fehlermeldung, wenn kein Name eingegeben wurde.
	 */
	private void startSpiel() {
		List<String> namen = new ArrayList<>();
		for (JTextField feld : nameFields) {
			String text = feld.getText().trim();
			if (!text.isEmpty()) {
				namen.add(text);
			}
		}
		
		if(namen.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Bitte mindestens einen Spielernamen eingeben.", "Fehler", JOptionPane.ERROR_MESSAGE);
			return;
		}
		remove(startPanel);

		// mit Ki erstellt 
		JPanel diceArea = new JPanel(new BorderLayout());
		currentPlayerLabel = new JLabel(" ", SwingConstants.RIGHT);
		diceArea.add(currentPlayerLabel, BorderLayout.NORTH);
		diceArea.add(dicePanel, BorderLayout.CENTER);

		JPanel bottomRightWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomRightWrapper.add(diceArea);

		JPanel southContainer = new JPanel();
		southContainer.setLayout(new BoxLayout(southContainer, BoxLayout.Y_AXIS));
		southContainer.add(bottomRightWrapper);
		southContainer.add(debugPanel);

		add(scoreTablePanel, BorderLayout.CENTER);
		add(southContainer, BorderLayout.SOUTH);

		revalidate();
		repaint();

		presenter.startNewGame(namen);
	}

	@Override
	public void updateDice(List<Dice> dice, int rollsLeft) {
		dicePanel.updateDice(dice);
		setTitle("Kniffel – Würfe übrig: " + rollsLeft);
	}

	@Override
	public void updateScoreTable(List<Player> players) {
		scoreTablePanel.updateScoreTable(players);
	}

	@Override
	public void updateCurrentPlayer(String name) {
		currentPlayerLabel.setText(name + " ist dran");
	}

	@Override
	public void showWinner(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

}