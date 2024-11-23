package whisp.client;

import whisp.Logger;
import whisp.interfaces.ClientInterface;
import whisp.interfaces.ServerInterface;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {

    final String username;
    private HashMap<String, ClientInterface> friends;
    private MenuViewController controller;
    private static ServerInterface server;

    //setter para server
    public static void setServer(ServerInterface server) {
        Client.server = server;
    }

    public HashMap<String, ClientInterface> getFriends() {
        return friends;
    }

    public void setController(MenuViewController controller) {
        this.controller = controller;
    }

    protected Client(String username) throws RemoteException {
        super();
        this.username = username;
        setServer(ClientApplication.getServer());
    }

    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    @Override
    public void receiveMessage(String message, String senderName, boolean isText) throws RemoteException {
        if(isText) {
            System.out.println("-----New text received-----");
            System.out.println("[" + senderName + "]: " + message);
            System.out.println("------------------------------");
        }else {
            System.out.println("-----New image received-----");
            System.out.println("[" + senderName + "]: image");
            System.out.println("------------------------------");
        }

        controller.receiveMessage(new Message(senderName, message, username, isText));
    }

    @Override
    public void receiveActiveClients(HashMap<String,ClientInterface> clients) throws RemoteException {
        try {
            friends = clients;
            printActiveClients();
        }catch (Exception e){
            Logger.error("Cannot receive friends");
        }
    }



    @Override
    public void receiveNewClient(ClientInterface client) throws RemoteException {
        System.out.println(client.getUsername() + " connected");

        friends.put(client.getUsername(), client);

        controller.friendConnected(client.getUsername());

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

        controller.friendDisconnected(clientUsername);

        printActiveClients();
    }

    @Override
    public void ping() throws RemoteException {}

    private void printActiveClients() throws RemoteException {
        System.out.println("\n-----Active friends (" + friends.size() + ")-----\n");
        System.out.print("[ ");
        for (ClientInterface c : friends.values()) {
            System.out.print(c.getUsername() + " ");
        }
        System.out.print("]\n\n");

        controller.showFriends();
    }

    public void sendMessage(Message message) {
        try {
            ClientInterface friendClient = friends.get(message.getReceiver());
            friendClient.receiveMessage(message.getContent(), message.getSender(), message.isText());
        }catch (RemoteException e){
            System.err.println(message.getSender() + " is not available right now. Try messaging him later");
            Logger.error("Cannot send message to " + message);
        }
    }

    public void sendRequest(String userNewFriend){
        for(String friend : friends.keySet()){
            if(friend.equals(userNewFriend)){
                Logger.error("You are already friend of "+ friend + ".");
                return;
            }
        }

        try {
           if (server.sendRequest(username, userNewFriend)){
               controller.addResquest(username, userNewFriend);

           }
        }catch (RemoteException e){
            System.err.println("Error connecting to server");
            Logger.error("Error connecting to server");
        }
    }

    public void addFriend(String friendName){
        try {
            System.out.println(friendName + " is now your friend");

            friends.put(friendName, server.getClient(friendName));

            controller.friendAdded(friendName);

            server.requestAcepted(friendName, username);

            printActiveClients();

        } catch (RemoteException e) {
            Logger.error("Cannot connect to server");
        }

    }

    @Override
    public void receiveFriendRequest(String requestSender) throws RemoteException {
        controller.addResquest(requestSender, username);
    }

    @Override
    public void receiveBDrequests(List<String> requestSenders) throws RemoteException {
        //recorro la lista de requestSenders y llamo a addRequest
        for(String requestSender : requestSenders){
            controller.addResquest(requestSender, username);
        }
    }

}
