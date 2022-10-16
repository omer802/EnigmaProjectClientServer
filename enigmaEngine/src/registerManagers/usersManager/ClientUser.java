package registerManagers.usersManager;

import registerManagers.RegisterManager;

import java.util.Objects;

public class ClientUser {
    private String username;

    private RegisterManager.ClientType clientType;

    public ClientUser(RegisterManager.ClientType client, String username) {
        this.username = username;
        this.clientType = client;
    }

    public String getUsername() {
        return username;
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
}
