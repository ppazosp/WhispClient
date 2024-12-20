package whisp.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface ClientInterface extends Remote {

    String getUsername() throws RemoteException;
    void receiveMessage(String message, String senderName, boolean isText) throws RemoteException;
    void receiveActiveClients(HashMap<String, ClientInterface> clients) throws RemoteException;
    void receiveNewClient(ClientInterface clients) throws RemoteException;
    void disconnectClient(ClientInterface client) throws RemoteException;
    void ping() throws RemoteException;
    void receiveFriendRequest(String requestSender) throws RemoteException;
    void receiveRequests(List<String> sentRequests, List<String> receivedRequests) throws RemoteException;
    void receiveRequestCancelled(String receiverName) throws RemoteException;
    void receiveKeys(HashMap<String, String> keys) throws RemoteException;
    void receiveNewKey(String username, String key) throws RemoteException;
}