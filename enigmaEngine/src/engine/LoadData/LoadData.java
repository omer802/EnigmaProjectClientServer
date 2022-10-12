package engine.LoadData;

import DTOS.Validators.xmlFileValidatorDTO;
import engine.enigma.Enigma;
import engine.registerManagers.BattlefieldManager;

import java.io.InputStream;

public interface LoadData {
    // TODO: 8/2/2022 change to object and not a string path. in the implimntation we move this object to string
    public Enigma loadDataFromInput(InputStream fileInputStream, xmlFileValidatorDTO validator, BattlefieldManager battleFieldManager);

}
