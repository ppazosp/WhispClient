package whisp.client;

import whisp.interfaces.ClientInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInterface {

    protected Client() throws RemoteException {
    }

    @Override
    public void receiveMessage(String message, String senderName) throws RemoteException {
        System.out.println(senderName + ": " + message);
    }
}
