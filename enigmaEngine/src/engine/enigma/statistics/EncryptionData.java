package engine.enigma.statistics;

import java.io.Serializable;

public class EncryptionData implements Serializable {
    private final String input;
    private final String output;
    private final long processingTime;

    public EncryptionData(String input, String output, long processingTime)
    {
        this.input = input;
        this.output = output;
        this.processingTime = processingTime;
    }
    public long getProcessingTime() {
        return processingTime;
    }
    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    @Override
    public String toString() {
        return "#." +
                "<" + input +">"+
                ", output=" + output +
                ", processingTime=" + processingTime +
                '}';
    }
}
