package serverPackage;


// This class is the server side of assignment 1.
// The server receives from the intermediate host a packet containing a read/write/invalid request,
// reads and validates it, and sends a response back to the intermediate host
// Last edited 16th July, 2016

import java.io.*;
import java.net.*;
import utilities.Constants;
import utilities.Utility;

public class Server {

	public DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	private boolean isReadRequest, isWriteRequest;
	public static boolean notShotDown=true;
	public static String mode=Constants.VERBOSE;
	public static boolean received=false;
	public static boolean shutdown;




	public Server()
	{
		try {
			// Construct a datagram socket and bind it to any available 
			// port on the local host machine. This socket will be used to
			// send UDP Datagram packets.


			// Construct a datagram socket and bind it to port 69 
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(69);

			// to test socket timeout (2 seconds)
			//receiveSocket.setSoTimeout(2000);
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		} 
		try {
			receiveSocket.setSoTimeout(10000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	/**
	 * Receive request, validate it, and send a response back.
	 * @throws Exception
	 */
	public void receiveAndRespond() throws Exception
	{
		
		while (notShotDown){
			int checker=0;//to check if we received or not
			// receiving a packet from the client  
			
			//if(Utility.getShu()){
				//System.out.println("Server shot down");
				//System.exit(1);
			//}
			
			
		
			//if(Utility.getReceived()){
				//Utility.setReceived(false);
				byte data[] = new byte[1400];
				receivePacket = new DatagramPacket(data, data.length);
				//receivePacket.setData(Utility.getData());
				//receivePacket.setLength(Utility.getLength());
				//receivePacket.setPort(Utility.getPort());
				//receivePacket.setAddress(Utility.getAddress());
				//System.out.println("hi");
				//receivePacket=Utility.getPacket();
			//System.out.println(""+receivePacket.getData());
		
			// Block until a datagram packet is received from receiveSocket.
			
			
            // creating a thread that deals with a the receiving packet 
			
			try {        
				
				receiveSocket.receive(receivePacket);
			} catch (SocketTimeoutException e) {
				checker=1;
				
			}
			if(checker==0){
			ConnectionManager connectionManagerThread = new ConnectionManager( receivePacket,receivePacket.getData(),getPath(receivePacket.getData()),mode);
			connectionManagerThread.start();}else{
				
			}
		//}
			//try{
			//Thread.sleep(5000);
			//}catch(Exception e){
				//e.printStackTrace();
				//System.exit(0);
			//}
		}
		System.out.println("System is shot down");
	}


    //this method  returns  the path of the file  
	private String getPath(byte[] data) {
		byte stringForm[]=new byte[100];
		int j=0;
		for(int i=2;i<100;i++){
			if(data[i]==0){
				break;
			} 
			stringForm[j]=data[i];
			j++;

		}
		byte stringForm2[]=new byte[j];
		System.arraycopy(stringForm, 0,stringForm2,0,j);
		return new String(stringForm2,0,j);
	}

	public static void main( String args[] )
	{
		Server c = new Server();
		//Thread waitingThread=new WaitingThread();
		//waitingThread.start();
		Thread su=new ServerUI();
		su.start();
		try {
			c.receiveAndRespond();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void kill() {
		System.exit(1);
		
	}
}

/*
 * This class provides user interface for the server administrator to interact with the system.
 */
class ServerUI extends Thread
{
	/**
	 * The text area where this thread's output will be displayed.
	 */


	public ServerUI() {

	}
//run method checks for commands, if the user changes mode it changes it, and if the user wants to shutdown the server it shuts it down 
	public void run() {
		BufferedReader br = null;
		String mode=Constants.VERBOSE;
		try{
			br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {

				System.out.println("Default Mode is " + Constants.VERBOSE);
				System.out.println();
				System.out.println("Type help for a list of available commands.");
				System.out.println();
				System.out.println("Enter command: ");
				String input = br.readLine();
				if (input.equals(Constants.CMD_HELP)){
					System.out.println();
					System.out.println("List of Commands:");
					System.out.println("mode - Display the current mode (verbose/quiet)");
					System.out.println("changeMode - Change the mode (verbose/quiet)");
					System.out.println("shutdown - Shut down Server");
					System.out.println();
				}else if (input.equals(Constants.CMD_MODE)) {
					System.out.println("Current mode: " +mode );
					System.out.println();
				}else if (input.equals(Constants.CMD_CHANGE_MODE)){
					System.out.print("Set logging mode (verbose/quiet): ");
					input = br.readLine();
					while (!(input.equals(Constants.VERBOSE) || (input.equals(Constants.QUIET)))){
						System.out.print("Please enter correct mode (verbose or quiet): ");
						input = br.readLine();
					}

					mode = input;
					Server.mode=input;
					System.out.println("Mode set to " + input);
					System.out.println();

				}
				else if(input.equals(Constants.CMD_SHUTDOWN)){
					Server.notShotDown=false;
					
					
					//System.exit(0);
				}else{
					System.out.println("Command not recognized. Please try again.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}


