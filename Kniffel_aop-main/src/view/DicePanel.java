package view;

import java.util.ArrayList;

import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import java.awt.Graphics;
import model.Dice;
import presenter.GamePresenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Zeigt die aktuellen 5 Würfel als Quadrate mit Punkten an. 
 */
public class DicePanel extends JPanel {

	private List<Dice> dice;
	private List<JCheckBox> holdBoxes;
	private GamePresenter presenter;
	private JButton rollButton;
	private JButton nextPlayerButton;

	private static final int SIZE = 60;
	private static final int GAP = 20;
	private static final int START_X = 20;
	private static final int START_Y = 20;
	private static final int ANZAHL_WUERFEL = 5;

	/**
	 * @param presenter der GamePresenter, dem Klicks gemeldet werden
	 */
	public DicePanel(GamePresenter presenter) {
		this.presenter = presenter;
		setLayout(null); // Positionierung erfolgt manuell, wie beim Zeichnen der Würfel

		holdBoxes = new ArrayList<>();
		int x = START_X;
		for (int i = 0; i < ANZAHL_WUERFEL; i++) {
			JCheckBox box = new JCheckBox("Halten");
			box.setBounds(x, START_Y + SIZE + 5, SIZE, 20);
			final int index = i; 
			box.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					presenter.toggleHold(index);
				}
			});
			holdBoxes.add(box);
			add(box);
			x += SIZE + GAP;
		}

		int buttonWidth = ANZAHL_WUERFEL * SIZE + (ANZAHL_WUERFEL - 1) * GAP;

		rollButton = new JButton("Würfeln");
		rollButton.setBounds(START_X, START_Y + SIZE + 5 + 20 + 10, buttonWidth, 30); //mit KI erstellt
		rollButton.addActionListener(e -> presenter.rollDice());
		
		add(rollButton);

		nextPlayerButton = new JButton("Nächster Spieler");
		nextPlayerButton.setBounds(START_X, START_Y + SIZE + 5 + 20 + 10 + 30 + 10, buttonWidth, 30); //mit KI erstellt
		nextPlayerButton.addActionListener(e -> presenter.nextPlayer());
		add(nextPlayerButton);
	}

	/**
	 * @param dice die aktuellen Würfel, die angezeigt werden sollen
	 */
	public void updateDice(List<Dice> dice) {
		this.dice = dice;
		for (int i = 0; i < holdBoxes.size() && i < dice.size(); i++) {
			holdBoxes.get(i).setSelected(dice.get(i).isHeld());
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (dice == null) {
			return;
		}

		int x = START_X;
		int y = START_Y;

		for (Dice d : dice) {
			g.drawRect(x, y, SIZE, SIZE);
			drawPips(g, d.getValue(), x, y, SIZE);
			x += SIZE + GAP;
		}
	}

	// Mit KI erstellt.
	private void drawPips(Graphics g, int value, int x, int y, int size) {
		int dotSize = 10;
		int left = x + 10;
		int center = x + size / 2 - dotSize / 2;
		int right = x + size - 20;
		int top = y + 10;
		int middle = y + size / 2 - dotSize / 2;
		int bottom = y + size - 20;

		switch (value) {
			case 1:
				g.fillOval(center, middle, dotSize, dotSize);
				break;
			case 2:
				g.fillOval(left, top, dotSize, dotSize);
				g.fillOval(right, bottom, dotSize, dotSize);
				break;
			case 3:
				g.fillOval(left, top, dotSize, dotSize);
				g.fillOval(center, middle, dotSize, dotSize);
				g.fillOval(right, bottom, dotSize, dotSize);
				break;
			case 4:
				g.fillOval(left, top, dotSize, dotSize);
				g.fillOval(right, top, dotSize, dotSize);
				g.fillOval(left, bottom, dotSize, dotSize);
				g.fillOval(right, bottom, dotSize, dotSize);
				break;
			case 5:
				g.fillOval(left, top, dotSize, dotSize);
				g.fillOval(right, top, dotSize, dotSize);
				g.fillOval(center, middle, dotSize, dotSize);
				g.fillOval(left, bottom, dotSize, dotSize);
				g.fillOval(right, bottom, dotSize, dotSize);
				break;
			case 6:
				g.fillOval(left, top, dotSize, dotSize);
				g.fillOval(right, top, dotSize, dotSize);
				g.fillOval(left, middle, dotSize, dotSize);
				g.fillOval(right, middle, dotSize, dotSize);
				g.fillOval(left, bottom, dotSize, dotSize);
				g.fillOval(right, bottom, dotSize, dotSize);
				break;
		}
	}

	/**
	 * Ohne das würde ein FlowLayout dem Panel die Größe 0 geben.
	 * Mit KI erstellt.
	 */
	@Override
	public Dimension getPreferredSize() {
		int width = START_X * 2 + ANZAHL_WUERFEL * SIZE + (ANZAHL_WUERFEL - 1) * GAP;
		int height = START_Y + SIZE + 5 + 20 + 10 + 30 + 10 + 30 + START_Y;
		return new Dimension(width, height);
	}
}