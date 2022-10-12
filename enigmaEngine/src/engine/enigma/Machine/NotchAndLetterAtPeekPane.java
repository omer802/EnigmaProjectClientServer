package engine.enigma.Machine;

import java.io.Serializable;

public class NotchAndLetterAtPeekPane implements Serializable {
    private final Character LetterAtPeekPane;
    private final int rotorNotch;

    public NotchAndLetterAtPeekPane(Character rotorId, int rotorNotch){
        this.LetterAtPeekPane = rotorId;
        this.rotorNotch = rotorNotch;
    }

    public int getRotorNotch() {
        return rotorNotch;
    }
    public Character getLetterAtPeekPane() {
        return LetterAtPeekPane;
    }

    @Override
    public String toString() {
        return LetterAtPeekPane.toString() + '(' + rotorNotch + ')';

    }
}
