package DTOS.StatisticsDTO;

import engine.enigma.statistics.ConfigurationAndEncryption;
import engine.enigma.statistics.Statistics;

import java.util.List;

public class MachineStatisticsDTO {
    private Statistics statistics;
    public MachineStatisticsDTO(Statistics statistics){
        this.statistics = statistics.clone();
    }
    public List<ConfigurationAndEncryption> getConfigurations(){
        return statistics.getConfigurationList();
    }
    public List<ConfigurationAndEncryption> getConfigurationsInUse(){
        return statistics.getUsedConfiguration();
    }
    public boolean isConfig(){
        return statistics.isConfig();
    }
    public boolean haveEncoded(){
        if(statistics.haveEncoded())
            return true;
        return false;
    }
}
