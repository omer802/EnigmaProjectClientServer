package engine.enigma.rotors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RotatingRotors  implements Serializable {

    private List<RotatingRotor> rotors;
    private int rotorsAmount;
    private int rotorsAmountInUse;
    private List<RotatingRotor> chosenRotorsArray;

    public RotatingRotors(List<RotatingRotor> rotors){
        setRotors(rotors);
        setRotorsAmount(rotors.size());
    }
    public void setRotors(List<RotatingRotor> rotors) {this.rotors = rotors;
    }

    public List<RotatingRotor> getRotors() {
        return rotors;
    }

    public int getRotorsAmount() {
        return rotorsAmount;
    }

    public void setRotorsAmount(int rotorsAmount) {
        this.rotorsAmount = rotorsAmount;
    }


    public int getRotorsAmountInUse() {
        return rotorsAmountInUse;
    }

    public void setRotorsAmountInUse(int rotorsAmount) {this.rotorsAmountInUse = rotorsAmount;
    }

    public List<RotatingRotor> getChosenRotors() {

        return chosenRotorsArray;
    }

    public void setChosenRotorToUse(List<String> chosenRotors) {
        if(chosenRotors.size()>rotors.size())
            throw new IllegalArgumentException("It is not possible to initialize an amount of rotors in use that is greater than the existing amount of rotors");
        List<RotatingRotor> chosenRotorsToUpdate = new ArrayList<>();
        for (int i = 0; i <chosenRotors.size() ; i++) {
            for (int j = 0; j <rotors.size() ; j++) {
                if(chosenRotors.get(i).equals(rotors.get(j).getId()))
                    chosenRotorsToUpdate.add(rotors.get(j));
            }
        }
        chosenRotorsArray = chosenRotorsToUpdate;
        setRotorsChain();
    }


    public void advanceRotorsInChain()
    {
        getChosenRotors().get(0).advanceNextRotor();
    }
    public  void setRotorsChain(){
        for (int i = 0; i < chosenRotorsArray.size()-1; i++) {
            chosenRotorsArray.get(i).setNextRotor(chosenRotorsArray.get(i+1));
            chosenRotorsArray.get(i).setIslastRotor(false);
        }
        chosenRotorsArray.get(chosenRotorsArray.size()-1).setNextRotor(null);
        chosenRotorsArray.get(chosenRotorsArray.size()-1).setIslastRotor(true);
    }

    @Override
    public String toString() {
        String toStringArray = new String();
        for (int i = getRotors().size() -1; i >=0 ; i--) {
            toStringArray+= getRotors().get(i).toString();
        }
        return toStringArray;
    }
    public List<String> getChosenRotorsString(){
        List<String> chosenRotors = new ArrayList<>();
        for (int i = getChosenRotors().size() -1; i >=0 ; i--) {
            chosenRotors.add((getChosenRotors().get(i).toString()));
        }
        return chosenRotors;
    }

    public List<String> getAllRotorsAsStringList(){
        List<String> chosenRotors = new ArrayList<>();
        for (int i = rotors.size()-1; i >=0 ; i--) {
            chosenRotors.add(getRotors().get(i).toString());
        }
        return chosenRotors;
    } 
    public void returnRotorsToStartingPositions(){
        this.getChosenRotors().stream().forEach(RotatingRotor::returnToStartingPosition);
    }
    public void setPositions(String positions)
    {
        for (int i = 0; i <positions.length() ; i++) {
            // The positions of the rotors were determined from right to left, although the data is typed from left to right
            char charPositionEndToBegin = positions.charAt(positions.length() - i - 1);
            getChosenRotors().get(i).SetStartingPosition(charPositionEndToBegin);
        }
    }
    public List<Character>  getAllLettersAtPeekPane(){
        List<Character> allLettersAtPeekPane = this.getChosenRotors().stream()
                .map(r-> r.getCurrentPositionCharacter()).collect(Collectors.toList());
        Collections.reverse(allLettersAtPeekPane);
        return allLettersAtPeekPane;
    }
}
