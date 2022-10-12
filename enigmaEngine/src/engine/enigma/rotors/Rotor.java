package engine.enigma.rotors;

import engine.enigma.keyboard.Keyboard;
import engine.enigma.pairOfData;

import java.io.Serializable;
import java.util.List;

public class Rotor implements Serializable {

     protected final String id;



     public char startingPositionCharacter;
     protected int position;
     // TODO: 8/7/2022 replace position index to method
     public int startingPositionIndex;
     protected List<pairOfData> PairOfDataArray;
     public Rotor( String id, List<pairOfData> setPairArray) {
          this.id = id;
          setPairOfDataArray(setPairArray);
     }

     public void SetStartingPosition(char charToPosition) {
          this.startingPositionCharacter = charToPosition;
          setPosition(charToPosition);

     }
     public void setPosition(char charToPosition){
          if(!Keyboard.isCharacterInRange(charToPosition))
               throw new IllegalArgumentException("You are trying to enter a character that does not exist on the keyboard");

          for (int i = 0; i < PairOfDataArray.size() ; i++) {
               if (PairOfDataArray.get(i).getRight() == charToPosition) {
                    this.position = i;
                    this.startingPositionIndex = i;
               }
          }
     }
     public int getStartingPositionIndex() {
          return startingPositionIndex;
     }

     //לאחד לפונקציה אחת את ימים ושמאל ולמנוע שכפול קוד
     public int convertRightToLeft(int index) {
          int indexWithPosition = (index + position) % PairOfDataArray.size();
          Character inputChar = PairOfDataArray.get(indexWithPosition).getRight();
          for (int leftIndex =0; leftIndex<PairOfDataArray.size();leftIndex++) {
               if (PairOfDataArray.get(leftIndex).getLeft() == inputChar)
                    return (leftIndex - position + PairOfDataArray.size()) % PairOfDataArray.size();
          }
          return -1;
     }

     public int convertLeftToRight(int index) {
          int indexWithPosition = (index + position) % PairOfDataArray.size();
          Character inputChar = PairOfDataArray.get(indexWithPosition).getLeft();
          for (int rightIndex = 0; rightIndex < PairOfDataArray.size(); rightIndex++) {
               if (PairOfDataArray.get(rightIndex).getRight() == inputChar)
                    return (rightIndex - position + PairOfDataArray.size()) % PairOfDataArray.size();
          }
          return -1;

     }
     public void setPairOfDataArray(List<pairOfData> pairOfDataArray) {
          this.PairOfDataArray = pairOfDataArray;
     }

     public int getPosition() {
          return position;
     }
     public String getId() {
          return id;
     }
     public char getStartingPositionCharacter() {
          return startingPositionCharacter;
     }

     @Override
     public String toString() {
          return id;
     }
}
