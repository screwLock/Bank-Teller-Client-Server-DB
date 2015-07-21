///Travus Helmly wthelmly 

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class BankServerDB extends UnicastRemoteObject implements TellerServer, Runnable{

	private ConcurrentHashMap <String,TellerClientChat> tellers = 
			new ConcurrentHashMap<String,TellerClientChat>(); 
	private ConcurrentHashMap <Integer,CashAccount> accounts = 
			new ConcurrentHashMap<Integer,CashAccount>();
	Vector<String> messages= new Vector<String>();
	TellerClientChat tellerStub;
private CashAccount ca;
private String ThenewLine = System.getProperty("line.separator");

private Connection connection;
private Statement selectAllStatement;
private PreparedStatement insertStatement;
private PreparedStatement updateStatement;
private PreparedStatement deleteStatement;
	
	//===========================================================
	public String openNewAccount(String accountType, String customerName)
	 {		
		if(accountType.equals(Bank.CHECKING))
		{
			try 
			{ 
				ca = new CheckingAccount(customerName);				
				
			} 
			catch (IOException e)
				{
				System.out.println("CheckingAccount() Exeption: "+e.getMessage());
				return "Error: The TellerServer cannot create new accounts. Call the IT department."+e.toString();
				}				
		}
		else if(accountType.equals(Bank.SAVINGS))
		{
			try {
				ca = new SavingsAccount(customerName);
				} 
			catch (IOException e) 
			{
				System.out.println("SavingAccount() Exeption: "+e.getMessage());
				return "Error: The Server cannot create new accounts. Call the IT department for: "+e.toString() ;
			}
		}
		else
			return "Account type "+accountType+ " not recognized by the server. Call the IT departement.";		
		
		accounts.put(ca.getAccountNumber(),ca);
		try {
		    insertStatement.setInt   (1, ca.getAccountNumber());
		    insertStatement.setString(2, accountType);
		    insertStatement.setString(3, customerName);
		    insertStatement.setDouble(4, 0); 
		    insertStatement.executeUpdate();
		    }
		catch(SQLException sqle)
		    {
		    return "Error: Unable to add new account to the data base."
		          + sqle.toString();
		    }
		
		return ca.toString();
	 
	 }

	//====================================================	
	public String showAccount(Integer accountNumber)  {
		
	CashAccount ca = accounts.get(accountNumber);
   	if (ca == null)
   		 return "Error: account " + accountNumber + " not found.";
   	else
   		 return ca.toString();
		
	}

	//====================================================	
	public String showAccounts(String customerName)  {
				
	     Collection<CashAccount>listOfAccounts=accounts.values();
         
	     TreeSet<String> hitList = new TreeSet<String>();
	     
	     String hitListString = ""; 
         	     
         boolean ShowingAllAccounts;
	     if (customerName.length() == 0||customerName.equals(" "))
             ShowingAllAccounts = true;
	      else
	    	 ShowingAllAccounts = false;
	     String enteredName = customerName.toUpperCase();
	     for (CashAccount ca : listOfAccounts)
	    	 {
	    	 String accountCustomerName = ca.getCustomerName().toUpperCase();
	    	 if (accountCustomerName.startsWith(enteredName) || ShowingAllAccounts)
	    		 hitList.add(ca.toString());
	    	 }
	     if (hitList.isEmpty())
	    	 return "No accounts starting with '" + customerName+"' have been found. ";
	     
	    	 for (String account : hitList)
	    		 hitListString +=account+ThenewLine; 
	    	 return hitListString;
	    
	}

	//==============================================	
	public String processAccount(String processingType, Integer accountNumber,
			Double amount) {
		CashAccount ca = accounts.get(accountNumber);
	   	if (ca == null)
	   		 	return "Error: account " + accountNumber + " not found.";
	   	else{
				if (processingType.equals(Bank.CLOSE))
				{
					double currentBalance=ca.getBalance();
					if(currentBalance==0.0)
						return ca.getCustomerName(); 
					else
						return "Error: account " + accountNumber
								+ " does not have a zero balance and therefore can't be closed.";
				}
				if (processingType.equals(Bank.CANCEL))			
					return "Closing "+accountNumber+" has been cancelled.";		
				if (processingType.equals(Bank.CONFIRM))
				{
					accounts.remove(accountNumber);
					
					try {
					    deleteStatement.setInt(1, accountNumber);
					    deleteStatement.executeUpdate();
					    }
					catch(SQLException sqle)
					    {
					    return "Error: Server is unable to delete account from the data base."
					         + sqle.toString();
					    }
					
			         return "The account " + accountNumber + " has been closed.";
				}
				if(processingType.equals(Bank.DEPOSIT))
				{
					ca.deposit(amount);
					try {
					    updateStatement.setDouble (1, ca.getBalance());
					    updateStatement.setInt    (2, ca.getAccountNumber());
					    updateStatement.executeUpdate();
					    }
					catch(SQLException sqle)
					    {
					    return "ERROR: Server is unable to update account in the data base."
					         + sqle.toString();
					    }
					
				   	return ca.toString();					
				}
				if(processingType.equals(Bank.WITHDRAW))
				{
					try {
						ca.withdraw(amount); 
					    } 
					catch (InsufficientFundsException e) 
					    {	
						System.out.println(e.getMessage());
						return "Sorry, insufficient funds";
					    }
					
					try {
					    updateStatement.setDouble (1, ca.getBalance());
					    updateStatement.setInt    (2, ca.getAccountNumber());
					    updateStatement.executeUpdate();
					    }
					catch(SQLException sqle)
					    {
					    return "ERROR: Server is unable to update account in the data base."
					         + sqle.toString();
					    }
					
				   	return ca.toString();					
				}				
				return "Process type "+processingType+" is not recognized by the server. Call the IT departement.";
		}
	}
	
	//================================================
	public static void main(String[] args) throws Exception
	{
		System.out.println("Travus Helmly wthelmly");
		System.out.println("**************");
		System.out.println("SERVER CONSOLE");
		System.out.println("Cmd:serverAdrress yourName");
		try {
			new BankServerDB();
			} 
		catch (RemoteException | MalformedURLException | UnknownHostException e) 
			{
			e.printStackTrace();
			}		
	}	
	//==============================================
	public BankServerDB() throws RemoteException, MalformedURLException, UnknownHostException, Exception
	{
		super(); 
	    Naming.rebind("TellerServices", this);
	    System.out.println("I'm up "
				+ InetAddress.getLocalHost().getHostAddress()
				+ " on TellerServices");
	    System.out.println("###########################");
	    	    
	    //Load the DB driver and konnect to the DB
	    Class.forName("com.ibm.db2j.jdbc.DB2jDriver");
		System.out.println("The database driver is loaded!");
		
		//The path depends on user's preference 
		connection = DriverManager.getConnection(
		 	    			"jdbc:db2j:C:\\database\\QuoteDB");
		
		System.out.println("The database is opened!");
		
		insertStatement = connection.prepareStatement(
			     "INSERT INTO BANK_ACCOUNTS "
			   + "(ACCOUNT_NUMBER, ACCOUNT_TYPE, CUSTOMER_NAME, BALANCE) "
			   + "VALUES (?,?,?,?)");
				
		updateStatement = connection.prepareStatement(
                "UPDATE BANK_ACCOUNTS "
              + "SET BALANCE = ? "
              + "WHERE ACCOUNT_NUMBER = ?");
				
		deleteStatement = connection.prepareStatement(
                "DELETE FROM BANK_ACCOUNTS "
              + "WHERE ACCOUNT_NUMBER = ?");
				
		selectAllStatement = connection.createStatement();
		ResultSet rs = selectAllStatement.executeQuery(
	              "SELECT * FROM BANK_ACCOUNTS");
		while (rs.next())
		  {
		  int    accountNumber = rs.getInt   ("ACCOUNT_NUMBER");
		  String accountType   = rs.getString("ACCOUNT_TYPE");
		  String customerName  = rs.getString("CUSTOMER_NAME");
		  double balance       = rs.getDouble("BALANCE");
		  		 	  
		  System.out.println(" acct#="    + accountNumber
	                 + " acctType=" + accountType
	                 + " custName=" + customerName
	                 + " balance="  + balance);
		  }
		
	    new Thread(this).start();	 
	}
	
	//===========================================
	public void run() { 
		
		  while (true)
		    {
			  if (messages.isEmpty()) 
		       {
		       synchronized(this) 
		         {
		         try { 
		        	 wait(); 
		        	 }
		         catch(InterruptedException ie) {} 
		         }
		       }		 
			  
			  String message = messages.remove(0);
			  Collection<TellerClientChat> tellerStubList = tellers.values();
			  Set<String> keySet = tellers.keySet();
			  String[] tellerNames = keySet.toArray(new String[0]); 
			  for (String tellerName : tellerNames)
			    {
			        tellerStub = tellers.get(tellerName);
			    if (tellerStub == null) 
			        continue;          
			    try { 
			        tellerStub.displayAdminMessage(message);
			        System.out.println("Received from the client: '"+ message+ "'");
			        }
			    catch(RemoteException re)      
			        {                          
			        tellers.remove(tellerName);
			        }
			    }
		    }
	}
	
	//========================================================
	public String signIn(String tellerName, TellerClientChat clientStub)
			throws RemoteException {
		
		tellerName=tellerName.toUpperCase(); 
		tellers.put(tellerName, clientStub);  
		System.out.println("Connected teller name: "+tellerName);
		
		return "Welcome "+tellerName+"!";
	}

	//========================================================
	public String signOut(String tellerName, TellerClientChat clientStub)
			throws RemoteException {
		
		tellerName=tellerName.toUpperCase();		
		System.out.println(tellerName+"'s left.");
					
		return "Good day, "+tellerName+"!";
	}

	//========================================================
	public synchronized void distributeAdminMessage(String message) throws RemoteException {
		
		messages.add(message + " [" + new Date()+"]"); 
		notify();
	}	

}
