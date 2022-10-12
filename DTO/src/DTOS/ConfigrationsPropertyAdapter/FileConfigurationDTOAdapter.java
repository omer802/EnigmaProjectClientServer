package DTOS.ConfigrationsPropertyAdapter;

import DTOS.Configuration.FileConfigurationDTO;
import javafx.beans.property.SimpleIntegerProperty;

public class FileConfigurationDTOAdapter {
    private SimpleIntegerProperty rotorsAmount;
    private SimpleIntegerProperty rotorsInUseAmount;
    private SimpleIntegerProperty reflectorsAmount;
    private SimpleIntegerProperty encryptedMessagesAmount;

    public FileConfigurationDTOAdapter(SimpleIntegerProperty rotorsAmount, SimpleIntegerProperty rotorsInUseAmount,
                                       SimpleIntegerProperty reflectorsAmount, SimpleIntegerProperty encryptedMessagesAmount) {
        this.rotorsAmount = rotorsAmount;
        this.rotorsInUseAmount = rotorsInUseAmount;
        this.reflectorsAmount = reflectorsAmount;
        this.encryptedMessagesAmount = encryptedMessagesAmount;
    }

    public void setDataFromFileDTO(FileConfigurationDTO MachineSpecification) {
        this.rotorsAmount.setValue(MachineSpecification.getCountOfRotors());
        this.rotorsInUseAmount.setValue(MachineSpecification.getCountOfRotorsInUse());
        this.reflectorsAmount.setValue(MachineSpecification.getCountOfReflectors());
        this.encryptedMessagesAmount.setValue(MachineSpecification.getNumberOfMessageEncrypted());
    }
    public void updateEncryptedMessagesAmount(FileConfigurationDTO MachineSpecification){
        this.encryptedMessagesAmount.setValue(MachineSpecification.getNumberOfMessageEncrypted());
    }
}
