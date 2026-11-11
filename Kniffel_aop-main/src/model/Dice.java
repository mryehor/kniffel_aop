package model;
import java.util.Random;

/**
 * Repräsentiert einen einzelnen Würfel mit aktuellem Wert und Halten-Status.
 */
public class Dice {

	private int value;
	private boolean held;

	public Dice() {
		value = 1;
		held = false;
	}

	public int getValue() {
		return value;
	}

	public boolean isHeld() {
		return held;
	}

	/** Würfelt neu, sofern der Würfel nicht gehalten wird.*/
	public void roll() {
		if(!held) {
			Random random = new Random();
			value = random.nextInt(6) + 1;
		}
	}

	public void toggleHeld() {
		held = !held;
	}

	/**
	 * Setzt den Würfelwert von außen mittels Debug-Modus fest. 
	 * Werte außerhalb 1 bis 6 stillschweigend ignoriert.
	 *
	 * @param value gewünschter Würfelwert (1-6)
	 */
	public void setDebugValue(int value) {
		if(value >= 1 && value <= 6) {
			this.value = value;
		}
	}
}