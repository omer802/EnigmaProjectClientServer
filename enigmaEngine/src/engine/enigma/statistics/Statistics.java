package engine.enigma.statistics;

import DTOS.Configuration.UserConfigurationDTO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Statistics implements Serializable, Cloneable {
    private List<ConfigurationAndEncryption> configurationList;

    public Statistics() {
        configurationList = new ArrayList<>();
    }
    public UserConfigurationDTO getCurrentStartingConfiguration(){
        return configurationList.get(configurationList.size()-1).getConfiguration();
    }
    public void addConfiguration(UserConfigurationDTO configuration) {
        ConfigurationAndEncryption newConfig = new ConfigurationAndEncryption(configuration);
        configurationList.add(newConfig);
    }

    public void addEncryptionToStatistics(String input, String output, long processingTime) {
        if (configurationList.size() < 1) {
            throw new ExceptionInInitializerError("Encryption information cannot be added without configuration initialization");
        }
        EncryptionData encryptionData = new EncryptionData(input, output, processingTime);
        configurationList.get(configurationList.size() - 1).addEncryptionData(encryptionData);
    }
    /*public Statistics getStatistics(){
        return this.clone();
    }*/

    //perform a deep clone using Serializable
    @Override
    public Statistics clone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Statistics) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
    public List<ConfigurationAndEncryption> getConfigurationList() {
        return configurationList;
    }
    public List<ConfigurationAndEncryption> getUsedConfiguration(){
        List<ConfigurationAndEncryption> configurationsInUse = new ArrayList<>();
        for (ConfigurationAndEncryption configuration: configurationList) {
            if(configuration.isUsed())
                configurationsInUse.add(configuration);
        }
        return configurationsInUse;
    }
    public boolean isConfig(){
        if(configurationList.size()>0)
        return true;
        return false;
    }
    public boolean haveEncoded(){
        if(getUsedConfiguration().size() > 0)
            return true;
        return false;
    }
}



