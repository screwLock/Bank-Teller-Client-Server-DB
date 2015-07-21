/**********************************************
Travus Helmly
wthelmly
Program to create console for a Bank Teller
**********************************************/


import java.awt.event.*;
import java.awt.*;
import java.io.Serializable;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;


public class Teller extends UnicastRemoteObject implements ActionListener, WindowListener,TellerClientChat, Serializable
{
public static void main(String[] args)
 {
	System.out.println("***********Teller Console***************");

	if (args.length ==0)
	   {
		System.out.println("Command line arguments missing.");
	   }
	if (args.length >2)
	   {
	   System.out.println("Two command line input. But it's okay");

	   }
	   try {
	 new TellerClient(args[0], args[1]);
     }
 catch(Exception e)
     {
     System.out.println(e);
	 }
 }

private TellerServer server;
private Vector<String> messages = new Vector<String>();
private int currentVectorIndex;
private String  theTellerName;
private String latestServerReply;

//OBJECT STUFF --------------------------------------------------------
private JFrame     tellerWindow          = new JFrame();
private JButton    depositButton         = new JButton("Deposit");
private JButton    withdrawButton        = new JButton("Withdraw");
private JButton    closeButton           = new JButton("Close");
private JButton    clearButton           = new JButton("Clear");
private JButton    showByNumberButton    = new JButton("ShowByNumber");
private JButton    showByNameButton      = new JButton("ShowByName");
private JButton    openNewSavingsButton  = new JButton("OpenNewSavings");
private JButton    openNewCheckingButton = new JButton("OpenNewChecking");
private JButton    sendChatButton        = new JButton("Send Message");
private JButton    previousChatButton     = new JButton ("Previous");
private JButton    nextChatButton         = new JButton ("Next");
private JButton    clearChatButton        = new JButton ("Clear Message");
private JTextField accountTextField      = new JTextField(16);
private JTextField amountTextField       = new JTextField(16);
private JTextField customerNameTextField = new JTextField(16);
private JTextField sendChatTextField	 = new JTextField(30);
private JTextField receiveChatTextField  = new JTextField(40);
private JLabel     accountLabel          = new JLabel("Account # =>",SwingConstants.RIGHT);
private JLabel     amountLabel           = new JLabel("Amount =>",SwingConstants.RIGHT);
private JLabel     customerNameLabel     = new JLabel("CustomerName Last,First =>",SwingConstants.RIGHT);
private JTextArea  displayTextArea       = new JTextArea(10,60);
private JScrollPane displayScrollPane    = new JScrollPane(displayTextArea);
private JPanel     topPanel              = new JPanel();
private JPanel     bottomPanel           = new JPanel();
private JPanel     bottom2Panel			 = new JPanel();
private JPanel     bottom3Panel          = new JPanel();
private JPanel     southPanel			 =new JPanel(new GridLayout(3,1));

private JDialog closeAccountWindow       =new JDialog(tellerWindow, true);
private JLabel  dialogLabel     		 =new JLabel("", JLabel.CENTER);
private JButton  confirmCloseButton 	 =new JButton("Confirm Close");
private JButton cancelCloseButton		 =new JButton ("Cancel Close");

//=========================================================
public Teller(String serverAddress, String tellerName) throws Exception // CONSTRUCTOR
															//Catches different kinds of exceptions
    {
	server = (TellerServer)Naming.lookup(
            "rmi://" + serverAddress + "/TellerServices");
	String serverReply=server.signIn(tellerName,this); //send teller name & _Stub to server
	System.out.println(serverReply);


	theTellerName=tellerName; //using tellerName as instance variable (to use as title in JFrame,...)
	tellerWindow.setTitle("Teller Station for "+ theTellerName);

	tellerWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // Build GUI (layout)
	topPanel.setLayout(new GridLayout(1,8));
	topPanel.add(accountLabel);
	topPanel.add(accountTextField);
	topPanel.add(showByNumberButton);
	topPanel.add(amountLabel);
	topPanel.add(amountTextField);
	topPanel.add(depositButton);
	topPanel.add(withdrawButton);
	topPanel.add(closeButton);
	tellerWindow.getContentPane().add(topPanel,BorderLayout.NORTH);

	tellerWindow.getContentPane().add(displayScrollPane,BorderLayout.CENTER);

	bottomPanel.setLayout(new GridLayout(1,6));
	bottomPanel.add(clearButton);
	bottomPanel.add(customerNameLabel);
	bottomPanel.add(customerNameTextField);
	bottomPanel.add(showByNameButton);
	bottomPanel.add(openNewSavingsButton);
	bottomPanel.add(openNewCheckingButton);

	bottom2Panel.add(sendChatTextField);
	bottom2Panel.add(sendChatButton);

	bottom3Panel.add(receiveChatTextField);
	bottom3Panel.add(previousChatButton);
	bottom3Panel.add(clearChatButton);
	bottom3Panel.add(nextChatButton);

	southPanel.add(bottomPanel);
	southPanel.add(bottom2Panel);
	southPanel.add(bottom3Panel);
	tellerWindow.getContentPane().add(southPanel, BorderLayout.SOUTH);

	closeAccountWindow.add(confirmCloseButton,"North");
	closeAccountWindow.add(dialogLabel,"Center");
	closeAccountWindow.add(cancelCloseButton,"South");
	closeAccountWindow.setSize(250, 120);
	closeAccountWindow.setLocation(300,300);
	closeAccountWindow.setTitle("Account Close Request");
	closeAccountWindow.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

	// set GUI attributes
	displayTextArea.setEditable(false); // keep cursor out!
	displayTextArea.setFont(new Font("default", Font.BOLD, 20));
	accountTextField.setFont(new Font("default", Font.BOLD, 20));
	amountTextField.setFont(new Font("default", Font.BOLD, 20));
	customerNameTextField.setFont(new Font("default", Font.BOLD, 20));
	sendChatTextField.setFont(new Font("default", Font.PLAIN, 20));
	sendChatTextField.setForeground(Color.GRAY);
	sendChatTextField.setText("Type here your message to server");
	receiveChatTextField.setEditable(false);
	// set colors
    showByNumberButton.setBackground(Color.cyan);
	showByNameButton.setBackground(Color.cyan);
	clearButton.setBackground(Color.pink);
	closeButton.setBackground(Color.red);
	openNewSavingsButton.setBackground(Color.green);
	openNewCheckingButton.setBackground(Color.green);
	depositButton.setBackground(Color.yellow);
	withdrawButton.setBackground(Color.yellow);

	// Request event notification (CALL ME!)
	showByNumberButton.addActionListener(this);
	showByNameButton.addActionListener(this);
	depositButton.addActionListener(this);
	withdrawButton.addActionListener(this);
	openNewCheckingButton.addActionListener(this);
	openNewSavingsButton.addActionListener(this);
	clearButton.addActionListener(this);
	closeButton.addActionListener(this);
	sendChatButton.addActionListener(this);
	previousChatButton.addActionListener(this);
	clearChatButton.addActionListener(this);
	nextChatButton.addActionListener(this);
	tellerWindow.addWindowListener(this);


	confirmCloseButton.addActionListener(this);
	cancelCloseButton.addActionListener(this);
	// show window
	tellerWindow.setSize(1000,500); // across, down
	tellerWindow.setVisible(true);

	System.out.println("Client connected to the server.");
	displayTextArea.setText(serverReply);
   }

//========================================================
public void actionPerformed(ActionEvent ae)
     {
	try{
     if (ae.getSource() == showByNameButton)     showAccounts  ();
     if (ae.getSource() == showByNumberButton)   showAccount   ();
     if (ae.getSource() == depositButton)        processAccount(Bank.DEPOSIT);
     if (ae.getSource() == withdrawButton)       processAccount(Bank.WITHDRAW);
     if (ae.getSource() == openNewSavingsButton) openNewAccount(Bank.SAVINGS);
     if (ae.getSource() == openNewCheckingButton)openNewAccount(Bank.CHECKING);
     if (ae.getSource() == clearButton)          clear();
     if (ae.getSource()== previousChatButton)
    	 {

    	  showPreviousChat();

    	 }
     if (ae.getSource()== nextChatButton)
	 	{


    	 showNextChat();
	 	}
     if(ae.getSource()==clearChatButton)
     	{
    	 receiveChatTextField.setText(" ");
     	}
     if(ae.getSource()==sendChatButton)
     {
    	 sendChatToServer();
     }
     if (ae.getSource() == closeButton)
     {
    	 processAccount(Bank.CLOSE);

    	 String lastServerMessage=latestServerReply;

    	if(lastServerMessage.startsWith("Error: "))
    	{
    		System.out.println("Can't close.");
    		return;
    	}
    	if(!lastServerMessage.startsWith("Error: "))
    	{
    		System.out.println("Can close.");
    		displayTextArea.setText("");//erase any previous err msg
    		dialogLabel.setText(lastServerMessage);
    		closeAccountWindow.setVisible(true);

    	}
      }
    		if(ae.getSource()==confirmCloseButton)
    		{
    			processAccount(Bank.CONFIRM);
    		}
    		if(ae.getSource()==cancelCloseButton)
    		{
    			processAccount(Bank.CANCEL);
    		}
	}
	catch(Exception e)
	{
		displayTextArea.setText(e.getMessage());
	}

  }
//========================================================
private void sendChatToServer() throws Exception{

	String tellerMessage=sendChatTextField.getText();
	sendChatTextField.setText(" ");
	sendChatTextField.requestFocus();
	System.out.println("Sent "+"'"+tellerMessage+"'"+" to the server. ");

	//call a server method
	server.distributeAdminMessage(theTellerName+" says: "+tellerMessage);

}

//========================================================
private void showPreviousChat() {

	currentVectorIndex--; //decrement each time the button is pressed

	if(currentVectorIndex<0)
		{
		receiveChatTextField.setText("No more messages");
		currentVectorIndex=-1; //Provide a fixed value (min index-1) to stop further decrementing
		return; 			   //if this return was omitted, the diplayTextArea would show the last statement.
		}
	receiveChatTextField.setText(messages.elementAt(currentVectorIndex));

}

//========================================================
private void showNextChat() {

	currentVectorIndex++;//increment each time the button is pressed

	if(currentVectorIndex>=messages.size())
		{
		receiveChatTextField.setText("No more messages");
		currentVectorIndex=messages.size();//Provide a fixed value (max index+1) to stop further incrementing
		return; 						   //if this return was omitted, the diplayTextArea would show the last statement.
		}
	receiveChatTextField.setText(messages.elementAt(currentVectorIndex));

}
//========================================================
private void clear()
 {
 accountTextField.setText("");
 amountTextField.setText("");
 customerNameTextField.setText("");
 displayTextArea.setText("");
 }

//========================================================
private void openNewAccount(String accountType) throws Exception
 {
 String customerName  = getName();

 System.out.println("Opening a new " + accountType
		          + " account for "  + customerName);

//call a server method
 String serverReply = server.openNewAccount(accountType, customerName);
 displayTextArea.setText(serverReply);
 }

//======================================================
private void processAccount(String processType) throws Exception
 {
 int accountNumber = getAccount();
 Double amount        = getAmount();

 if (processType.equals(Bank.CLOSE))
    {
	System.out.println("Closing account " + accountNumber);
	String serverReply = server.processAccount(processType, accountNumber, amount);
	latestServerReply=serverReply;
	System.out.println("Close command: "+serverReply);
	return;
	}
 else if (processType.equals(Bank.CONFIRM))
 {
	 System.out.println("Closing confirmed for " + accountNumber);
	 dialogLabel.setText(" ");
	 closeAccountWindow.setVisible(false);
	 //call a server method
	 String serverReply = server.processAccount(processType, accountNumber, amount);
	 displayTextArea.setText(serverReply);
	 return;
 }
 else if (processType.equals(Bank.CANCEL))
 {
	 System.out.println("Closing cancelled for " + accountNumber);
	 dialogLabel.setText(" ");
	 closeAccountWindow.setVisible(false);
	 return;
 }
  else
    {
    System.out.println("Doing a "     + processType
	                 + " of "         + amount
		             + " on account " + accountNumber);
    }

 	//call a server method
 	String serverReply = server.processAccount(processType, accountNumber, amount);
 	displayTextArea.setText(serverReply);
 }
//========================================================
private void showAccount() throws Exception
  {
  String accountNumberText = accountTextField.getText();
  int accountNumber=Integer.parseInt(accountNumberText);
  System.out.println("Showing account " + accountNumber);

  //call a server method
  String serverReply = server.showAccount(accountNumber);
  displayTextArea.setText(serverReply);
  }

//=========================================================
private void showAccounts() throws Exception
  {
  String customerName = customerNameTextField.getText();
  System.out.println("Showing all accounts for a customer whose name starts with '" + customerName+"'.");

  //call a server method
  String serverReply = server.showAccounts(customerName);
  displayTextArea.setText(serverReply);
  }

//================="Getter" methods==================
private int getAccount() throws IllegalArgumentException
  {
	String accountText;
	int accountNumber=0;
	accountText=accountTextField.getText();
	accountText=accountText.trim();
	accountText=accountText.replaceFirst("^0*", ""); //Eliminate leading zeros
	Boolean isInteger=true;

	try{
		accountNumber=Integer.parseInt(accountText);

		if(accountNumber<0)
		    throw new IllegalArgumentException("Error: Account number is a negative number.");
		if(accountNumber>Integer.MAX_VALUE)
			throw new IllegalArgumentException("Error: Account number is a bigger than allowable integer value.");
		}
	catch(NumberFormatException nfe)
		{
		isInteger=false;
		}
	if(accountText.length()==0)
		throw new IllegalArgumentException("Error: Account number is missing.");
	else if(!isInteger)
		throw new IllegalArgumentException("Error: Entered account number is not an integer.");
	else
		return accountNumber;

  }
private double getAmount() throws IllegalArgumentException
  {
	String amountText;
	double amount=0.0;
	amountText=amountTextField.getText();
	amountText=amountText.trim();
	Boolean isDouble=true;

	int decimalIndex=amountText.indexOf('.'); //locating a period
	int decimalPlaces=amountText.length()-1-decimalIndex; //counting decimal places

	try{
		amount=Double.parseDouble(amountText);

		if(amount<0)
		    throw new IllegalArgumentException("Error: Amount is a negative.");
		}
	catch(NumberFormatException nfe)
		{
		isDouble=false;
		}
	if(decimalPlaces!=2)
		throw new IllegalArgumentException("Error: Not two decimal places exactly in the entered decimal amount.");
	else if(amountText.length()==0)
		throw new IllegalArgumentException("Error: Amount not entered.");
	else if(!isDouble)
		throw new IllegalArgumentException("Error: Entered amount is not a correct type.");
	else
		return amount;
  }
private String getName() throws IllegalArgumentException
  {
	String nameText;
	int commaOccurence;

	nameText=customerNameTextField.getText();
	commaOccurence=nameText.length()-nameText.replaceAll(",","").length();
	nameText=nameText.trim(); //remove ending blank

	if(nameText.length()==0)
		throw new IllegalArgumentException("Error: Customer name missing.");
	else if(nameText.contains(" "))
		throw new IllegalArgumentException("Error: Contains imbedded blank.");
	else if(commaOccurence==0)
		throw new IllegalArgumentException("Error: Missing comma between lastname and firstname.");
	else if(commaOccurence>1)
		throw new IllegalArgumentException("Error: More than one comma used.");
	else if(nameText.startsWith(","))
		throw new IllegalArgumentException("Error: Name strarting with a comma.");
	else if(nameText.endsWith(","))
		throw new IllegalArgumentException("Error: Name ending with a comma.");
	else
		return nameText;

  }

//========================================================
public void displayAdminMessage(String message) throws RemoteException {

	receiveChatTextField.setText(message);
	messages.add(message);
	currentVectorIndex=messages.size()-1;//Reset and make the current position be the last-in msg

}

//========================================================
/*
WindowListener interface requires that we implement the following methods,
even if we do not use them.
*/

@Override
public void windowOpened(WindowEvent e) {
	// TODO Auto-generated method stub

}

public void windowClosing(WindowEvent e){

	try
	{
	//call a server method
	String serverReply=server.signOut(theTellerName,this);
	System.out.println(serverReply);

	} catch (RemoteException e1) {

		e1.printStackTrace();
	}
}

@Override
public void windowClosed(WindowEvent e) {
	// TODO Auto-generated method stub

}

@Override
public void windowIconified(WindowEvent e) {
	// TODO Auto-generated method stub

}

@Override
public void windowDeiconified(WindowEvent e) {
	// TODO Auto-generated method stub

}

@Override
public void windowActivated(WindowEvent e) {
	// TODO Auto-generated method stub

}

@Override
public void windowDeactivated(WindowEvent e) {
	// TODO Auto-generated method stub
}


}
