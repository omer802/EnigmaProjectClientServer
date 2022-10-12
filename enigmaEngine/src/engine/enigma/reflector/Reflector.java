package engine.enigma.reflector;

import java.io.Serializable;
import java.util.*;

public class Reflector implements Serializable {
    Map<Integer,Integer> mapInputOutput;

    private String id;
    public Reflector(String id, List <inputOutputPair> setPairs){
        if(setPairs.size()<1)
            throw new ExceptionInInitializerError("A reflector cannot contain zero pairs");
        else {
            setDictionary(setPairs);
            this.id = id;
        }

    }
    private void setDictionary(List <inputOutputPair> setPairs) {
        mapInputOutput = new HashMap<>();
        for (inputOutputPair pair : setPairs) {
            int input = pair.getInput();
            int output = pair.getOutput();
            mapInputOutput.put(input, output);
            mapInputOutput.put(output, input);
        }
    }
    public Integer reflect(int index){
        return mapInputOutput.get(index);
    }

    @Override
    public String toString() {
        return "reflector{" +
                "mapInputOutput=" + mapInputOutput +
                '}';
    }
    public String getId() {
        return id;
    }
}
