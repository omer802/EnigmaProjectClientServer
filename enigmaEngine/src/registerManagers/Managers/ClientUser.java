package registerManagers.Managers;

import registerManagers.clients.Client;
import registerManagers.clients.User;
import registerManagers.mediators.Mediator;

import java.util.Objects;

public class ClientUser implements User {
    private String username;

    private RegisterManager.ClientType clientType;
    private Mediator mediator;

    public ClientUser(RegisterManager.ClientType client, String username) {
        this.username = username;
        this.clientType = client;
    }



    public RegisterManager.ClientType getClientType() {
        return clientType;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientUser that = (ClientUser) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }


    @Override
    public String getUserName() {
        return username;
    }
}
