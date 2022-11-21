package DTOS.Validators;

import engine.LoadData.jaxb.schema.generated.CTEMachine;
import engine.LoadData.jaxb.schema.generated.CTEPositioning;
import engine.LoadData.jaxb.schema.generated.CTEReflector;
import engine.LoadData.jaxb.schema.generated.CTERotor;
import engine.enigma.reflector.Reflectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class xmlFileValidatorDTO {
    private static List<Exception> listOfExceptions;
    CTEMachine enigmaMachineFromFile;
    private String alphabet;


    public xmlFileValidatorDTO() {
        listOfExceptions = new ArrayList<>();
    }

    public void setMachine(CTEMachine MachineInput){
        this.enigmaMachineFromFile = MachineInput;
        String ABC = enigmaMachineFromFile.getABC().toUpperCase();
        this.alphabet = cleanStringFromXMLFile(ABC);

        this.listOfExceptions = new ArrayList<>();
    }
    public static void  addException(Exception e){
        listOfExceptions.add(e);
    }

    public List<Exception> getListOfExceptions() {
        return listOfExceptions;
    }
    public void validateAlphabet() {
        if (alphabet.length() % 2 != 0)
            addException(new RuntimeException("Error loading file: The size of the alphabet must be even"));
    }
    public static String cleanStringFromXMLFile(String str) {
        str = str.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        str = str.replaceAll("\\p{C}", "");
        str = str.trim();
        str = str.toUpperCase();
        return str;
    }

    public void validateRotors(){
        List<CTERotor> rotorsInput = enigmaMachineFromFile.getCTERotors().getCTERotor();
        int amountOfRotorsToUse = enigmaMachineFromFile.getRotorsCount();
        validateAmountOfRotors(rotorsInput, amountOfRotorsToUse);
        validateIdAndUniqueElement(rotorsInput);
        validateNonDuplicatedMappingsInRotors(rotorsInput);
        validateRotorsNotch(rotorsInput);
    }
    public void validateRotorsNotch(List<CTERotor> rotorsInput){
        for (CTERotor rotor: rotorsInput) {
            if(rotor.getNotch()>alphabet.length()|| rotor.getNotch()<1) {
                addException(new RuntimeException("Error: notch is is out of range at rotor: " + rotor.getId()));
                return;
            }
        }
    }
    public void validateAmountOfRotors(List<CTERotor> rotorsInput, int amountOfRotorsToUse){
        if(amountOfRotorsToUse>99)
            addException(new RuntimeException("Error: initialize with more than 99 rotors. Please upload" +
                    " a file with less than 99 rotors in use"));
        if(amountOfRotorsToUse <2)
            addException(new RuntimeException("Error: initialize with less than 2 rotors. Please upload" +
                    " a file with more than 2 rotors in use"));
        if(rotorsInput.size() < amountOfRotorsToUse)
            addException(new RuntimeException("Error: It is not possible to use an amount of rotors that is greater " +
                    "than the amount of rotors received in the file"));

    }

    public void validateNonDuplicatedMappingsInRotors(List<CTERotor> rotorsInput){
        for (CTERotor rotor:rotorsInput) {
            validateRotorsMapping(rotor);
        }
    }
    public void validateRotorsMapping(CTERotor rotor){
        List<CTEPositioning> positions = rotor.getCTEPositioning();
        if(positions.size()!=alphabet.length())
            addException(new RuntimeException("Error: the number of pairs in rotor "+ rotor.getId()+" should be the same as the size of the alphabet"));

        List<String> positionsRight = positions.stream().map(p->p.getRight()).collect(Collectors.toList());
        isRotorColumnInRange(positionsRight,rotor);
        List<String> positionsLeft = positions.stream().map(p->p.getLeft()).collect(Collectors.toList());
        isRotorColumnInRange(positionsLeft,rotor);

        int differentPositionsRight = (int)positionsRight.stream().distinct().count();
        int differentPositionsLeft = (int) positionsLeft.stream().distinct().count();
        if(differentPositionsLeft!= positionsLeft.size()|| differentPositionsRight!= positionsRight.size())
            addException(new RuntimeException("Error: rotor "+ rotor.getId()+" contains a double mapping"));
    }

    public void isRotorColumnInRange(List<String> positions,CTERotor rotor)
    {
        for (String str:positions) {
            if (!alphabet.contains(str)) {
                addException(new RuntimeException("Error: rotor " + rotor.getId() + " contains a letter that is not in range"));
                return;
            }
        }
        for (int i = 0; i <alphabet.length() ; i++){
            if(!positions.contains(Character.toString(alphabet.charAt(i)))) {
                addException(new RuntimeException("Error: There is a character that is not mapped in rotor " + rotor.getId()));
                return;
            }

        }

    }
    public void validateIdAndUniqueElement(List<CTERotor> rotorsInput){
        int rotorsDifferentIdCount = (int)rotorsInput.stream().map(r-> r.getId()).distinct().count();
        if(rotorsDifferentIdCount!= rotorsInput.size())
            addException(new RuntimeException("Error: not all rotor ids are different"));
        else{
            List<Integer> allRotorsId = rotorsInput.stream().map(r->r.getId()).collect(Collectors.toList());
            for (int i = 1; i <= rotorsInput.size() ; i++) {
                if(!allRotorsId.contains(i))
                    addException(new RuntimeException("Error: The ids of the rotors is not numbered starting from 1"));
            }
        }
    }
    public void validateReflector() {
        List<CTEReflector> reflectors = enigmaMachineFromFile.getCTEReflectors().getCTEReflector();
        if (reflectors.size() > 5) {
            addException(new RuntimeException("Error: you cannot inital machine with more than 5 reflectors"));
        } else {
            validateReflectorsIdInRange(reflectors);
            validateReflectorsMapping(reflectors);
        }
    }
    public void validateReflectorsMapping(List<CTEReflector> reflectors){
        for (CTEReflector reflector: reflectors) {
            validateMapping(reflector);
            validateAllCharacterIsMapedReflector(reflector);

        }
    }
    public void validateAllCharacterIsMapedReflector(CTEReflector reflector){
        List<Integer> inputs = reflector.getCTEReflect().stream().map(r->r.getInput()).collect(Collectors.toList());
        List<Integer> outputs = reflector.getCTEReflect().stream().map(r->r.getOutput()).collect(Collectors.toList());
        for (int i = 1; i <=alphabet.length() ; i++) {
            if((!inputs.contains(i))&&(!outputs.contains(i))){
                addException(new RuntimeException("Error: in reflector "+ reflector.getId()+" there is an unmapped character "));
            }

        }
    }
    public void validateMapping(CTEReflector reflector){
        List<Integer> inputs = reflector.getCTEReflect().stream().map(r->r.getInput()).collect(Collectors.toList());
        reflectorReflectionsInRange(inputs,reflector);
        List<Integer> outputs = reflector.getCTEReflect().stream().map(r->r.getOutput()).collect(Collectors.toList());
        reflectorReflectionsInRange(outputs,reflector);
        if(!Collections.disjoint(inputs,outputs))
            addException(new RuntimeException("Error: One of the reflectors have character mapped to itself"));
    }
    public void reflectorReflectionsInRange(List<Integer> checkColumn,CTEReflector reflector){
        for (Integer index: checkColumn) {
            if(index>alphabet.length()|| index<1)
                addException(new RuntimeException("Error: reflector "+ reflector.getId()+" have out of range position"));
        }
    }
    public void validateReflectorsIdInRange(List<CTEReflector> reflectors){

        List<String> reflectorsId = reflectors.stream().map(r->r.getId()).collect(Collectors.toList());
        for (String reflectorId: reflectorsId) {
            if(!Reflectors.isReflectorInRange(reflectorId))
                addException(new RuntimeException("Error: Reflector in file with ID: "+reflectorId + " is out of range."+
                        "Please make sure that the file has rotors with an id between I and IV"));
        }
        isRunningNumberingOfRomanNumerals(reflectorsId);
        validateReflectorsUniqueId(reflectorsId);
    }
    public void validateReflectorsUniqueId(List<String> reflectorsId){
        int ReflectorDifferentIdCount = (int)reflectorsId.stream().distinct().count();
        if(ReflectorDifferentIdCount!= reflectorsId.size())
            addException(new RuntimeException("Error: not all reflector ids are different"));
    }
    public void isRunningNumberingOfRomanNumerals(List<String> reflectorsId){

        for (int i = 1; i <= reflectorsId.size() ; i++) {
            if(!reflectorsId.contains(Reflectors.IntegerToReflectorString(i))){
                addException(new RuntimeException("Error: The reflectors are not in running numbering"));
            }
        }
    }
    public void validateNumOfAgents(int amountOfAgents){
        if(amountOfAgents>50||amountOfAgents<2) {
            addException(new RuntimeException("Error: The amount of agents need to be " +
                    "in range of 2-55"));
        }
    }

    public void isValidMachineInputFromFile(){
        validateAlphabet();
        validateRotors();
        validateReflector();
    }



}
