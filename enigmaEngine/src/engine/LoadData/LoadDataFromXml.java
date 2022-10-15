package engine.LoadData;

import DTOS.Validators.xmlFileValidatorDTO;
import dictionary.Dictionary;
import registerManagers.battleField.Battlefield;
import engine.decryptionManager.DM;
import engine.enigma.*;
import engine.enigma.Machine.EnigmaMachine;
import engine.enigma.reflector.Reflector;
import engine.enigma.reflector.inputOutputPair;
import engine.enigma.rotors.RotatingRotor;
import engine.LoadData.jaxb.schema.generated.*;
import registerManagers.battlefieldManager.BattlefieldManager;
import keyboard.Keyboard;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoadDataFromXml implements LoadData {
    private final static String JAXB_XML_PACKAGE_NAME = "engine.LoadData.jaxb.schema.generated";
    public Enigma loadDataFromInput(InputStream FileInputStream, xmlFileValidatorDTO validator, BattlefieldManager battleFieldManager){
        CTEEnigma enigmaCTEE = loadEnigmaFromFile(FileInputStream,validator);
        if(enigmaCTEE!=null) {
            EnigmaMachine machine = loadMachine(enigmaCTEE, validator);
            if (machine != null) {
                DM dm = loadDecipher(machine,enigmaCTEE, validator);
                if (dm != null) {
                    Battlefield battlefield = loadBattleField(enigmaCTEE,battleFieldManager, validator);
                    if(battlefield!=null)
                    return new Enigma(machine,dm,battlefield);
                    //decipher.getAgents().getAgents().toString();
                   // Battlefield battleField = loadBattleField(enigmaCTEE);
                }
               /* if(battleField != null){
                    return new Enigma(machine, dm);
                }*/
            }

        }
        return null;
    }

    private Battlefield loadBattleField(CTEEnigma enigmaCTEE, BattlefieldManager battleFieldManager, xmlFileValidatorDTO validator) {
        CTEBattlefield battleFieldCTE = enigmaCTEE.getCTEBattlefield();
        String battlefieldName = battleFieldCTE.getBattleName();
        String battlefieldLevel = battleFieldCTE.getLevel();
        int amountOfAllies = battleFieldCTE.getAllies();
        Battlefield battleField = new Battlefield(battlefieldName,battlefieldLevel,amountOfAllies);
        if(battleFieldManager.isBattlefieldExists(battleField)){
            validator.addException(new RuntimeException("The battlefield is already exist in the system!"));
        }
        return battleField;

    }

    private DM loadDecipher(EnigmaMachine machine, CTEEnigma enigma, xmlFileValidatorDTO validator){
        Dictionary dictionary = loadDictionary(enigma, validator);
        /*Agents agents = loadAgents(machine, enigma,validator);
        if(agents == null)
           return null;*/
        return new DM(dictionary,machine.clone());
    }
    /*private Agents loadAgents(EnigmaMachine machine,CTEEnigma enigma, xmlFileValidatorDTO validator){
        int amountOfAgents = enigma.getCTEDecipher().getAgents();
        validator.validateNumOfAgents(amountOfAgents);
        if(validator.getListOfExceptions().size() == 0)
        return new Agents(machine,amountOfAgents);
        else
            return null;
    }*/
    private Dictionary loadDictionary(CTEEnigma enigma, xmlFileValidatorDTO validator){
        String excludeChars = enigma.getCTEDecipher().getCTEDictionary().getExcludeChars();
        String words = enigma.getCTEDecipher().getCTEDictionary().getWords();
        return new Dictionary(words, excludeChars);
    }

    private CTEEnigma loadEnigmaFromFile(InputStream FileInputStream, xmlFileValidatorDTO validator){
        CTEEnigma enigma;
        try {
            enigma = deserializeFrom(FileInputStream);
        } catch (JAXBException e) {
            validator.addException(new RuntimeException("Error: invalid file were entered"));
            return null;
        }

        return enigma;
    }
    private EnigmaMachine loadMachine(CTEEnigma enigma, xmlFileValidatorDTO validator) {
        validator.setMachine(enigma.getCTEMachine());
        validator.isValidMachineInputFromFile();
        if(validator.getListOfExceptions().size()== 0)
            return deserializeMachineInput(enigma.getCTEMachine());
        else
            return null;

    }

    private EnigmaMachine deserializeMachineInput(CTEMachine MachineInput){
        Keyboard keyboard = getKeyBoard(MachineInput);
        List<RotatingRotor> rotors = getRotorsFromInput(MachineInput);
        int amoutOfRotorsToUse = MachineInput.getRotorsCount();
        List<CTEReflector> reflectorsToTransfer = MachineInput.getCTEReflectors().getCTEReflector();
        List<Reflector> reflectors = getReflectorsFromInput(reflectorsToTransfer);
        return new EnigmaMachine(keyboard, rotors,amoutOfRotorsToUse, reflectors);
    }
    private Keyboard getKeyBoard(CTEMachine MachineInput){
        String ABC = MachineInput.getABC();
        ABC = xmlFileValidatorDTO.cleanStringFromXMLFile(ABC);
        return new Keyboard(ABC);
    }



    private List<Reflector> getReflectorsFromInput(List<CTEReflector> reflectorsInput){
        List<Reflector> reflectorsToReturn = new ArrayList<>();
        for(CTEReflector inputReflector: reflectorsInput){
            Reflector reflector = translateReflectorInput(inputReflector);
            reflectorsToReturn.add(reflector);
        }
        return reflectorsToReturn;
    }
    private Reflector translateReflectorInput(CTEReflector inputReflector){
        String id = inputReflector.getId();
        List<inputOutputPair> pairs = getPairsInputOutput(inputReflector.getCTEReflect());
        return new Reflector(id,pairs);
    }
    private List<inputOutputPair> getPairsInputOutput(List<CTEReflect> inputReflector){
        List<inputOutputPair> pairOfData = new ArrayList<>();
        for (int i = 0; i <inputReflector.size() ; i++) {
            int input = inputReflector.get(i).getInput() -1;
            int output = inputReflector.get(i).getOutput() -1;
            pairOfData.add(new inputOutputPair(input,output));
        }
        return pairOfData;

    }
    private List<RotatingRotor> getRotorsFromInput(CTEMachine MachineInput){
        List<CTERotor> rotorsInput =  MachineInput.getCTERotors().getCTERotor();
        List<RotatingRotor> rotorsToReturn = new ArrayList<RotatingRotor>();
        for (CTERotor inputRotor: rotorsInput) {
            RotatingRotor rotor = translateRotorInput(inputRotor);
            rotorsToReturn.add(rotor);
        }
        return rotorsToReturn;
    }
    private RotatingRotor translateRotorInput(CTERotor inputRotor){
        int notch = inputRotor.getNotch() -1;
        int tempId = inputRotor.getId();
        String id = String.valueOf(tempId);
        List<CTEPositioning> inputPosition = inputRotor.getCTEPositioning();
        List<pairOfData> pairOfData = getPairOfData(inputPosition);
        return new RotatingRotor(notch, id, pairOfData);
    }
    private List<pairOfData> getPairOfData(List<CTEPositioning> inputPosition)
    {
        List<pairOfData> pairOfData = new ArrayList<>();
        for (int i = 0; i <inputPosition.size() ; i++) {
            Character right = inputPosition.get(i).getRight().charAt(0);
            Character left = inputPosition.get(i).getLeft().charAt(0);
            pairOfData.add(new pairOfData(right,left));
        }
        return pairOfData;
    }
    private static CTEEnigma deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (CTEEnigma) u.unmarshal(in);
    }
    private EnigmaMachine deserializeMachine(CTEEnigma enigmaMachineInput){




        return null;
    }

}
