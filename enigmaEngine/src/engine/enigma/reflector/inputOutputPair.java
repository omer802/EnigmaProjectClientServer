package engine.enigma.reflector;

public class inputOutputPair {
    private final int output;
    private final int input;
    public inputOutputPair(int input, int output) {
        this.input = input;
        this.output = output;
    }
    public int getInput() {
        return input;
    }

    public int getOutput() {
        return output;
    }

}
