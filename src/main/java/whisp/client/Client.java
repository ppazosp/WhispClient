package whisp.client;

import whisp.ClientApplication;
import whisp.utils.Logger;
import whisp.interfaces.ClientInterface;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {

    public final String username;
    private HashMap<String, ClientInterface> friends;
    private ClientApplication mainApp;

    public HashMap<String, ClientInterface> getFriends() {
        return friends;
    }

    public Client(String username, ClientApplication mainApp) throws RemoteException {
        super();
        this.username = username;
        this.mainApp = mainApp;
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public void receiveMessage(String message, String senderName, boolean isText) throws RemoteException {
        Logger.info("Received message from " + senderName);

        mainApp.receiveMessage(message, senderName, isText);
    }

    @Override
    public void receiveActiveClients(HashMap<String,ClientInterface> clients) throws RemoteException {
        Logger.info("received " + clients.size() + " clients from server...");
        Logger.info("settings friends on backend...");
        friends = clients;
        mainApp.setFriends(clients);

    }

    @Override
    public void receiveNewClient(ClientInterface client) throws RemoteException {
        Logger.info("User " + client.getUsername() + " received from server");

        Logger.info("updating backend...");
        friends.put(client.getUsername(), client);

        mainApp.friendConnected(client.getUsername());
    }

    @Override
    public void disconnectClient(ClientInterface client) throws RemoteException {
        Logger.info("A friend disconnected, searching identity...");
        String clientUsername = "";
        for (Map.Entry<String, ClientInterface> entry : friends.entrySet()) {
            if (entry.getValue().equals(client)) {
                clientUsername = entry.getKey();
                break;
            }
        }
        Logger.info("Friend recognized as " + clientUsername);

        Logger.info("Removing friend reference from backend");
        friends.remove(clientUsername);

        mainApp.friendDisconnected(clientUsername);
    }

    @Override
    public void receiveFriendRequest(String requestSender) throws RemoteException {
        Logger.info("Friend request from " + requestSender + "received from server");
        mainApp.addResquest(requestSender);
    }

    @Override
    public void receiveRequests(List<String> requestSenders) throws RemoteException {
        Logger.info("Received " + requestSenders.size() + " from server db...");
        for(String requestSender : requestSenders){
            mainApp.addResquest(requestSender);
        }
    }

    @Override
    public void receiveRequestCancelled(String receiverName) throws RemoteException {
        Logger.info("Received a request cancellation from server from " + receiverName);
        mainApp.removeResquest(receiverName);
    }

    @Override
    public void ping() throws RemoteException {}

    public void sendMessage(String message, String receiver, boolean isText) {
        try {
            Logger.info("Sending " + message + " to " + receiver + "...");
            ClientInterface friendClient = friends.get(receiver);
            friendClient.receiveMessage(message, username, isText);
            Logger.info("Message sended");
        }catch (RemoteException e){
            Logger.error(receiver + " is not available right now. Try messaging him later");
        }
    }

    public boolean isFriend(String username){
        return friends.get(username)!= null;
    }
}
