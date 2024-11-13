package whisp.client;

import whisp.Logger;
import whisp.interfaces.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
        System.out.println("-----New message received-----");
        System.out.println("[" + senderName + "]: " + message);
        System.out.println("------------------------------");
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


    private void menuOptions(){
        System.out.println("1. Send message");
        System.out.println("2. Add friend");
        System.out.println("3. Remove friend");
        System.out.println("4. Show active friends");
        System.out.println("5. Exit");
    }

    public void menuImplementation(){
        System.out.println("Welcome " + username + "!!!");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        while(option != 5){
            menuOptions();
            option = scanner.nextInt();
            switch(option){
                case 1:
                    showFriendsListPATATA();
                    break;
                case 2:
                    addFriend();
                    break;
                case 3:
                    removeFriend();
                    break;
                case 4:
                    try{
                        printActiveClients();
                    }catch(RemoteException e){
                        System.out.println("Error showing active friends");
                    }
                    break;
                case 5:
                    System.out.println("See you soon!");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void removeFriend() {

    }

    private void addFriend() {
    }

    private void sendMessage(ClientInterface friend, String message) {
        try {
            friend.receiveMessage(message, this.username);
        }catch (RemoteException e){
            System.err.println(friend + " is not available right now. Try messaging him later");
            Logger.error("Cannot send message to " + message);
        }
    }

    private void showFriendsListPATATA() {

        for(String friend : friends.keySet()){
            System.out.print(friend + "\t");
        }
        System.out.println("Select a friend to send a message");
        Scanner scanner = new Scanner(System.in);
        String friend = scanner.nextLine();
        System.out.println("Write your message");
        String message = scanner.nextLine();
        sendMessage(friends.get(friend), message);

    }
}
