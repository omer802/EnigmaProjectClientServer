package engine.enigma.statistics;

import DTOS.Configuration.UserConfigurationDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationAndEncryption implements Serializable {

    private List<EncryptionData> EncryptionDataList;

    private UserConfigurationDTO configuration;
    ConfigurationAndEncryption(UserConfigurationDTO configuration){
        this.configuration = configuration;
        this.EncryptionDataList = new ArrayList<>();
    }
    public void addEncryptionData(EncryptionData encryptionData){
        this.EncryptionDataList.add(encryptionData);
    }
    public List<EncryptionData> getEncryptionDataList() {
        return EncryptionDataList;
    }
    public UserConfigurationDTO getConfiguration() {
        return configuration;
    }
    public boolean isUsed(){
        if(EncryptionDataList.size()>0)
            return true;
        return false;
    }
}
