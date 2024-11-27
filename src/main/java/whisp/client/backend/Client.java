package whisp.client.backend;

import whisp.client.ClientApplication;
import whisp.utils.Logger;
import whisp.interfaces.ClientInterface;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client extends UnicastRemoteObject implements ClientInterface, Serializable {

    //*******************************************************************************************
    //* ATTRIBUTES
    // *******************************************************************************************

    public final String username;
    private HashMap<String, ClientInterface> friends;
    private ClientApplication mainApp;



    //*******************************************************************************************
    //* CONSTRUCTORS
    // *******************************************************************************************

    public Client(String username, ClientApplication mainApp) throws RemoteException {
        super();
        this.username = username;
        this.mainApp = mainApp;
    }



    //*******************************************************************************************
    //* OVERRIDE METHODS
    // *******************************************************************************************

    /**
     * Obtiene el nombre de usuario del cliente actual
     *
     * @return el nombre de usuario del cliente
     * @throws RemoteException si ocurre un error de comunicación remota
     */
    @Override
    public String getUsername() throws RemoteException {
        return username;
    }

    /**
     * Recibe un mensaje de otro cliente y lo pasa a la GUI para mostrarlo
     *
     * @param message    el contenido del mensaje
     * @param senderName el nombre de usuario del remitente
     * @param isText     indica si el mensaje es un mensaje en texto plano o una imagen
     * @throws RemoteException si ocurre un error de comunicación remota
     */
    @Override
    public void receiveMessage(String message, String senderName, boolean isText) throws RemoteException {
        Logger.info("Received message from " + senderName);

        mainApp.receiveMessage(message, senderName, isText);
    }

    /**
     * Recibe la lista de amigos conectados desde el servidor al iniciar sesión
     *
     * @param clients un Hasmap que contiene los nombres de usuario como clave y sus interfaces correspondientes como valores
     * @throws RemoteException si ocurre un error de comunicación remota
     */
    @Override
    public void receiveActiveClients(HashMap<String, ClientInterface> clients) throws RemoteException {
        Logger.info("received " + clients.size() + " clients from server...");
        Logger.info("settings friends on backend...");
        friends = clients;
        mainApp.setFriends(clients);
    }

    /**
     * Maneja el evento de un nuevo amigo que se conecta al servidor.
     * Añade el cliente a la lista de amigos y se lo pasa a la GUI para que haga lo apropiado
     *
     * @param client la referencia al amigo que se ha conectado.
     * @throws RemoteException si ocurre un error de comunicación remota.
     */
    @Override
    public void receiveNewClient(ClientInterface client) throws RemoteException {
        Logger.info("User " + client.getUsername() + " received from server");

        Logger.info("updating backend...");
        friends.put(client.getUsername(), client);

        mainApp.friendConnected(client.getUsername());
    }

    /**
     * Maneja la desconexión de un amigo. Halla su nombre a través del Hashmap amigos antes de eliminarlo
     * e informa a la GUI
     *
     * @param client la referencia al amigo que se ha desconectado.
     * @throws RemoteException si ocurre un error de comunicación remota.
     */
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

    /**
     * Recibe una solicitud de amistad de otro usuario y se la pasa a la GUI para que la muestre
     *
     * @param requestSender el nombre del usuario que envió la solicitud.
     * @throws RemoteException si ocurre un error de comunicación remota.
     */
    @Override
    public void receiveFriendRequest(String requestSender) throws RemoteException {
        Logger.info("Friend request from " + requestSender + "received from server");
        mainApp.addReceivedRequest(requestSender);
    }

    /**
     * Recibe la lista de solicitudes de amistad recibidas y enviadas por el usuario desde el servidor.
     *
     * @param sentRequests lista de nombres de usuarios que han recibido una solicitud de amistad por parte del usuario de la sesión actual.
     * @param receivedRequests lista de nombres de usuarios que le han enviado al usuario de la sesión actual una solicitud de amistad.
     * @throws RemoteException si ocurre un error de comunicación remota.
     */
    @Override
    public void receiveRequests(List<String> sentRequests, List<String> receivedRequests) throws RemoteException {
        Logger.info("Received " + sentRequests.size() + " sent requests and " + receivedRequests.size() + " received requests from server db...");

        for(String requestReceiver : sentRequests){
            mainApp.addSentRequest(requestReceiver);
        }

        for(String requestSender : receivedRequests){
            mainApp.addReceivedRequest(requestSender);
        }

    }

    /**
     * Recibe la cancelación de una solicitud de amistad por parte de otro usuario.
     *
     * @param receiverName el nombre del usuario que canceló la solicitud.
     * @throws RemoteException si ocurre un error de comunicación remota.
     */
    @Override
    public void receiveRequestCancelled(String receiverName) throws RemoteException {
        Logger.info("Received a request cancellation from server from " + receiverName);
        mainApp.removeResquest(receiverName);
    }

    /**
     * Método de ping para verificar la conectividad con el cliente.
     *
     * @throws RemoteException si ocurre un error de comunicación remota.
     */
    @Override
    public void ping() throws RemoteException {}


    //*******************************************************************************************
    //* MORE METHODS
    // *******************************************************************************************

    /**
     * Envía un mensaje recibido desde la GUI al amigo indicado. Toma la referencia del amigo
     * del Hashmap friends utilizando el propio nombre del amigo
     *
     * @param message mensaje que se va a enviar
     * @param receiver nombre del usuario q va a recibir el mensaje
     * @param isText {@code true} si el mensaje es texto plano, {@code false} en otro caso
     */
    public void sendMessage(String message, String receiver, boolean isText) {
        try {
            Logger.info("Sending message to " + receiver + "...");
            ClientInterface friendClient = friends.get(receiver);
            friendClient.receiveMessage(message, username, isText);
            Logger.info("Message sended");
        }catch (RemoteException e){
            Logger.error(receiver + " is not available right now. Try messaging him later");
            ClientApplication.showErrorWindow(receiver + " is not available");
        }
    }

    /**
     * Comprueba si un usuario es amigo del usuario de la sesión actual.
     *
     * @param username nombre del usuario a comprobar
     */
    public boolean isFriend(String username){
        return friends.get(username)!= null;
    }
}
