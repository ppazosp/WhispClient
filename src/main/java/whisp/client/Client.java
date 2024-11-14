package whisp.client;

import whisp.Logger;
import whisp.interfaces.ClientInterface;
import whisp.interfaces.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends UnicastRemoteObject implements ClientInterface {

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
    public void receiveMessage(String message, String senderName) throws RemoteException {
        System.out.println("-----New message received-----");
        System.out.println("[" + senderName + "]: " + message);
        System.out.println("------------------------------");

        controller.receiveMessage(new Message(senderName, message, username));
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
            friendClient.receiveMessage(message.getContent(), message.getSender());
        }catch (RemoteException e){
            System.err.println(message.getSender() + " is not available right now. Try messaging him later");
            Logger.error("Cannot send message to " + message);
        }
    }

    public void addFriend(String userNewFriend){
        //buscar en la lista de clientes activos
        for(String friend : friends.keySet()){
            if(friend.equals(userNewFriend)){
                Logger.error("You are already friend of "+ friend + ".");
                return;
            }
        }

        try {
            //llamar al servido consultando la lista de clientes activos
           if (server.sendRequest(username, userNewFriend)){
               //LOGICA DE CAMBIAR GUI
               controller.addResquest(username, userNewFriend);

           }
        }catch (RemoteException e){
            System.err.println("Error connecting to server");
            Logger.error("Error connecting to server");
        }
    }

    @Override
    public void receiveFriendRequest(String requestSender) throws RemoteException {
        //LOGICA DE CAMBIAR LA GUI
        controller.addResquest(requestSender, username);
    }
}
