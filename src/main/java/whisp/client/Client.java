package whisp.client;

import whisp.interfaces.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Client extends UnicastRemoteObject implements ClientInterface {

    String username;
    HashMap<String, ClientInterface> friends;

    protected Client(String username) throws RemoteException {
        super();
        this.username = username;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public void receiveMessage(String message, String senderName) throws RemoteException {
        System.out.println(senderName + ": " + message);
    }

    @Override
    public void receiveActiveClients(HashMap<String,ClientInterface> clients) throws RemoteException {
        friends = clients;

        printActiveClients();
    }

    @Override
    public void receiveNewClient(ClientInterface client) throws RemoteException {
        System.out.println(client.getUsername() + " connected");

        friends.put(client.getUsername(), client);

        printActiveClients();
    }

    @Override
    public void disconnectClient(ClientInterface client) throws RemoteException {
        String clientUsername = "";
        for (Map.Entry<String, ClientInterface> entry : friends.entrySet()) {
            if (entry.getValue().equals(client)) {
                clientUsername = entry.getKey();
                break;
            }
        }

        System.out.println( clientUsername + " disconnected");

        friends.remove(clientUsername);

        printActiveClients();
    }

    @Override
    public void ping() throws RemoteException {}

    private void printActiveClients() throws RemoteException {
        System.out.println("\n-----Active friends (" + friends.size() + ")-----");
        System.out.print("[ ");
        for (ClientInterface c : friends.values()) System.out.print(c.getUsername()+ " ");
        System.out.print("]\n\n");
    }
}
