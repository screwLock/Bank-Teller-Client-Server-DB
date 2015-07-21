/********************************************************
Travus Helmly
wthelmly
Interface for chat that the client console must implement

*********************************************************/

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface TellerClientChat extends Remote
{
public void displayAdminMessage(String message) throws RemoteException; 
}