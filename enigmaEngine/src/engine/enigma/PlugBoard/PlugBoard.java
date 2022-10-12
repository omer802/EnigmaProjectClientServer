package engine.enigma.PlugBoard;

import java.io.Serializable;
import java.util.*;

public class PlugBoard implements Serializable {
    private Map<Character, Character> mapInputOutput;
    public int pairsInPlug;

    private String connections;
    public PlugBoard(String connections) {
        this.connections = connections;
        pairsInPlug = connections.length()/2;
        initialPlugBoard(connections);
    }
    public PlugBoard()
    {
        pairsInPlug = 0;
        mapInputOutput = new HashMap<>();
    }
    public void setConnections(String connections) {
        this.connections = connections;
    }
    private void initialPlugBoard(String connections) {
        mapInputOutput = new HashMap<>();
        //taking pair of plugs
        for (int i = 0; i < connections.length(); i+=2) {
            Character c1 = (Character)connections.charAt(i);
            Character c2 = (Character)connections.charAt(i+1);
            mapInputOutput.put(c1, c2);
            mapInputOutput.put(c2, c1);
        }
    }

    @Override
    public String toString() {
        boolean isFirst = true;
        String stringOfConnections = new String();
        Set<Character> alreadyAdded = new HashSet<>();
        for (int i = 0; i < mapInputOutput.size(); i++) {
            Character pluggedChar = connections.charAt(i);
            if (!alreadyAdded.contains(pluggedChar)) {
                if(!isFirst){
                    stringOfConnections+=",";
                }
                stringOfConnections += pluggedChar;
                stringOfConnections += "|";
                stringOfConnections += mapInputOutput.get(connections.charAt(i));
                alreadyAdded.add(pluggedChar);
                alreadyAdded.add(mapInputOutput.get(pluggedChar));
                isFirst = false;
            }
        }
        return stringOfConnections;
    }
    public boolean isPluged(Character checkIfPluged)
    {
        return mapInputOutput.containsKey(checkIfPluged);

    }
    public char getPlugedValue(char getValue){
        return mapInputOutput.get(getValue);
    }
}
