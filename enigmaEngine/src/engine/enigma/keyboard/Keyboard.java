package engine.enigma.keyboard;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Keyboard implements Serializable {
    private static Map<Character,Integer> ABCToIndex;
    private static Map<Integer,Character> IndexToABC;

    public static String getAlphabet() {
        return alphabet;
    }

    public static String alphabet;

    public Keyboard(String alphabet)
    {
        this.alphabet = alphabet;
        BuildDictionaries();
    }
    public static int convertAbcToIndex(Character charInput){
        return ABCToIndex.get(charInput);
    }
    public static char convertIndexToABC(Integer intInput){
        return IndexToABC.get(intInput);
    }
    protected static void BuildDictionaries(){
        IndexToABC = new HashMap<>();
        ABCToIndex = new HashMap<>();
        for (int i = 0; i < alphabet.length(); i++) {
            IndexToABC.put(i,alphabet.charAt(i));
            ABCToIndex.put(alphabet.charAt(i),i);
        }
    }
    public static boolean isCharacterInRange(char ch){ return alphabet.contains(Character.toString(ch));}

    public static boolean isStringInRange(String str){
        for (int i = 0; i <str.length() ; i++) {
            if(!isCharacterInRange(str.charAt(i)))
                return false;
        }
        return true;
    }
    public static char getCharFromAlphabet(int index){
        return alphabet.charAt(index);
    }
    public static int keyboardSize() {
        return alphabet.length();
    }
    
}
