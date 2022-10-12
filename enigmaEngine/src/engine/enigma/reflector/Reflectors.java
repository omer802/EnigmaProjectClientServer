package engine.enigma.reflector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Reflectors implements Serializable {
    public enum ReflectorEnum {
        I, II, III, IV,V
    }
    private int chosenReflector;
    private final static int didntchooseReflector = -1;
    private List<Reflector> reflectors;
    private int reflectorsAmount;

    public Reflectors(List<Reflector> reflectors ){
        chosenReflector = didntchooseReflector;
        this.reflectors = reflectors;
        this.reflectorsAmount = reflectors.size();
    }
    public Reflector getReflectorInUse(){
        return reflectors.get(chosenReflector);
    }
    public int ToReflect(int index){
        return getReflectorInUse().reflect(index);
    }
    public List<Reflector> getReflectors() {
        return reflectors;
    }
    public List<String> getPossibleReflectors(){
        List<String> possiblesReflectors = new ArrayList<>();
        for (Reflector possibleReflector: reflectors) {
            possiblesReflectors.add(possibleReflector.getId());
        }
        return possiblesReflectors;
    }

    public void setReflectors(List<Reflector> reflectors) {
        this.reflectors = reflectors;
    }

    public int getReflectorsAmount() {
        return reflectorsAmount;
    }

    public void SetChosenReflector(String reflector){
        ReflectorEnum reflectorEnum = ReflectorEnum.valueOf(reflector);
        SetChosenReflector(reflectorEnum);
    }


    public void SetChosenReflector(ReflectorEnum chosenReflector) {
        switch (chosenReflector) {
            case I:
                this.chosenReflector = 0;
                break;
            case II:
                this.chosenReflector = 1;
                break;
            case III:
                this.chosenReflector = 2;
                break;
            case IV:
                this.chosenReflector = 3;
                break;
            case V:
                this.chosenReflector = 4;
                break;
        }
    }
    public ReflectorEnum getChosenReflectorByRomeNumerals() {
        ReflectorEnum ReflectorToReturn = null;
        switch (this.chosenReflector) {
            case 0:
                ReflectorToReturn =  ReflectorEnum.I;
                break;
            case 1:
                ReflectorToReturn = ReflectorEnum.II;
                break;
            case 2:
                ReflectorToReturn = ReflectorEnum.III;
                break;
            case 3:
                ReflectorToReturn = ReflectorEnum.IV;
                break;
            case 4:
                ReflectorToReturn = ReflectorEnum.V;
                break;
        }
        return ReflectorToReturn;
    }
   /* public static int ReflectorEnumToInteger(ReflectorEnum ReflectorEnum){
        int integerFromEnum = 0;
        switch (ReflectorEnum) {
            case I:
                integerFromEnum =  1;
                break;
            case II:
                integerFromEnum = 2;
                break;
            case III:
                integerFromEnum = 3;
                break;
            case IV:
                integerFromEnum = 4;
                break;
            case V:
                integerFromEnum = 5;
                break;
        }
        return integerFromEnum;
    }*/
    public static String IntegerToReflectorString(int reflectorId){
        String intToString = new String();
        switch (reflectorId) {
            case 1:
                intToString = "I";
                break;
            case 2:
                intToString = "II";
                break;
            case 3:
                intToString = "III";
                break;
            case 4:
                intToString = "IV";
                break;
            case 5:
                intToString = "V";
                break;
        }
        return intToString;
    }
    public static boolean isReflectorInRange(String reflectorString){
        ReflectorEnum[] reflectors = ReflectorEnum.values();
        for(ReflectorEnum reflector: reflectors ){
            if(reflector.toString().equals(reflectorString))
                return true;
        }
        return false;
    }

}
