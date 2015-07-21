/**********************************************
Travus Helmly
wthelmly
Chat Interface that our Teller console
must implement
*********************************************/

import java.rmi.Remote;
import java.rmi.RemoteException;


public interface TellerServerChat extends Remote
{
public String signIn(String           tellerName,
                     TellerClientChat clientStub)
                     throws           RemoteException;

public String signOut(String           tellerName,
                      TellerClientChat clientStub)
                      throws           RemoteException;

public void distributeAdminMessage(String message)
                                   throws RemoteException;
}
