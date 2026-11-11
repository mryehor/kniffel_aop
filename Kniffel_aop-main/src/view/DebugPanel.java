package view;

import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import presenter.GamePresenter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Steuerelemente für den Debug-Modus. Im aktivierten Zustand können die
 * Werte der fünf Würfel manuell festgelegt werden.
 */
public class DebugPanel extends JPanel {
	
	// Verbindung zum Presenter, über den die Debug-Werte an das Spiel übergeben werden
	// presenter: nimmt die eingegebenen Debug-Werte entgegen
	// debugCheckbox, valueFields, applyButton: die Bedienelemente dieses Panels
	private GamePresenter presenter;
	private JCheckBox debugCheckbox;
	private JTextField[] valueFields;
	private JButton applyButton;

	private static final int ANZAHL_WUERFEL = 5;
	
	/**
	 * Erstellt das Panel: Checkbox zum Aktivieren, fünf zunächst versteckte
	 * Eingabefelder und ein Button zum Übernehmen der Werte.
	 *
	 * @param presenter Presenter, an den die Debug-Werte übergeben werden
	 */
	public DebugPanel(GamePresenter presenter) {
		this.presenter = presenter;

		debugCheckbox = new JCheckBox("Debug-Modus aktivieren");
		valueFields = new JTextField[ANZAHL_WUERFEL];
		applyButton = new JButton("Werte übernehmen");
		applyButton.setVisible(false);

		add(debugCheckbox);
		for (int i = 0; i < ANZAHL_WUERFEL; i++) {
			valueFields[i] = new JTextField(2);
			valueFields[i].setVisible(false);
			add(valueFields[i]);
		}
		add(applyButton);
	
		// Aktiviert/deaktiviert den Debug-Modus
		debugCheckbox.addActionListener(e -> {
			boolean isActive = debugCheckbox.isSelected();
			toggleDebugMode(isActive);
			presenter.toggleDebugMode(isActive);
		});
		
		applyButton.addActionListener (new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyDebugValues();
			}
			
		});
	}
		
	/**
	 * Liest die eingegebenen Würfelwerte ein und übergibt sie an den Presenter.
	 * Bei einer ungültigen Eingabe wird eine Fehlermeldung angezeigt.
	 */
	private void applyDebugValues() {
		try {
			presenter.setDebugValues(readDebugValues());
		} catch (NumberFormatException exception) {
			showInputError("Bitte geben Sie für jeden Würfel eine Zahl von 1 bis 6 ein.");
		} catch (IllegalArgumentException exception) {
			showInputError(exception.getMessage());
		}
	}
	
	/**
	 * Liest die fünf Werte aus den Eingabefeldern.
	 *
	 * @return Array mit den eingegebenen Würfelwerten
	 * @throws NumberFormatException wenn ein Feld keine gültige Zahl enthält
	 */
	private int[] readDebugValues() {
		int[] debugValues = new int[ANZAHL_WUERFEL];
		for (int i = 0; i < ANZAHL_WUERFEL; i++) {
			debugValues[i] = Integer.parseInt(valueFields[i].getText().trim());
		}
		return debugValues;
	}
	
	/**
	 * Zeigt eine Fehlermeldung bei ungültiger Debug-Eingabe an.
	 *
	 * @param message Text der Fehlermeldung
	 */
	private void showInputError(String message) {
		JOptionPane.showMessageDialog(this, message, "Ungültige Eingabe", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Blendet die Eingabefelder und den Übernehmen-Button ein oder aus,
	 * je nachdem ob der Debug-Modus aktiv ist.
	 *
	 * @param isActive true, wenn der Debug-Modus aktiv sein soll
	 */
	public void toggleDebugMode(boolean isActive) {
		for (JTextField field : valueFields) {
			field.setVisible(isActive);
		}
		applyButton.setVisible(isActive);
	}
}