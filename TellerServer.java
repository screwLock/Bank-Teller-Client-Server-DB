/* *************************************************
Travus Helmly
wthelmly
A teller interface.  We should expect any teller class
to implement the following methods.

**************************************************/
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TellerServer extends Remote, TellerServerChat
{
public String openNewAccount(String  accountType,
                             String  customerName)
                             throws  RemoteException;

public String showAccount   (Integer accountNumber)
                             throws  RemoteException;

public String showAccounts  (String  customerName)
                             throws  RemoteException;

public String processAccount(String  processingType,
                             Integer accountNumber,
                             Double  amount)
                             throws  RemoteException;
}