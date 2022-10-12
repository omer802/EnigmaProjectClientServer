package engine.enigma;

import engine.battleField.Battlefield;
import engine.decryptionManager.DM;
import engine.enigma.Machine.EnigmaMachine;

public class Enigma {

    private Battlefield battlefield;
    private DM decipher;
    protected EnigmaMachine Machine;
    public Enigma(EnigmaMachine machine, DM decipher, Battlefield battlefield){
        this.Machine = machine;
        this.decipher = decipher;
        this.battlefield = battlefield;
    }
    public void setMachine(EnigmaMachine machine) {
        Machine = machine;
    }

    public EnigmaMachine getMachine() {
        return Machine;
    }
    public DM getDecipher() {
        return decipher;
    }

    public void setDecipher(DM decipher) {
        this.decipher = decipher;
    }

    public Battlefield getBattleField() {
        return battlefield;
    }
}
