package intermediateHost;

/*Iteration 1
 * Name: Ibrahim Ali fawaz
 * Student No. 100986043
 */

//IntermediateHost.java
//Intermediate Host receives packet from the client and forwards it to the server. It then
//receives a packet from the server and forwards to the client.

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import utilities.Utility;

public class IntermediateHost {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket, sendToClientSocket,sendReceiveSocket,receiveClientSocket;
	int clientPort; //client port where the request comes from
	private boolean flag;
	
	public IntermediateHost(){
		clientPort = 0;
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(23);
			sendReceiveSocket=new DatagramSocket();
		} catch (SocketException se) {
	         se.printStackTrace();
	         System.exit(1);
	    }
	}
	
	public void receiveSendPacket(){
		
			// Construct a DatagramPacket for receiving packets up 
		      // to 100 bytes long (the length of the byte array).

		      byte data[] = new byte[1400];
		      receivePacket = new DatagramPacket(data, data.length);
		      System.out.println("IntermediateHost: Waiting for Packet.\n");

		      // Block until a datagram packet is received from receiveSocket.
		      try {        
		         System.out.println("Waiting..."); // so we know we're waiting
		         receiveSocket.receive(receivePacket);
		      } catch (IOException e) {
		         System.out.print("IO Exception: likely:");
		         System.out.println("Receive Socket Timed Out.\n" + e);
		         e.printStackTrace();
		         System.exit(1);
		      }

		      clientPort = receivePacket.getPort();
		      // Process the received datagram.
		      System.out.println("IntermediateHost: Packet received:");
		      System.out.println("From host: " + receivePacket.getAddress());
		      System.out.println("Host port: " + receivePacket.getPort());
		      int len = receivePacket.getLength();
		      System.out.println("Length: " + len);
		      System.out.print("Containing: " );

		      // Form a String from the byte array.
		      String received = new String(data,0,len);   
		      System.out.println(received + "\n");
		      System.out.println("Containing Bytes: ");
		      System.out.println(Arrays.toString(Utility.getBytes(data,0, len)));
		      
		      // Slow things down (wait 5 seconds)
		      try {
		          Thread.sleep(1000);
		      } catch (InterruptedException e ) {
		          e.printStackTrace();
		          System.exit(1);
		      }
		 
		      // Send packet to server

		      try {
				sendPacket = new DatagramPacket(data, receivePacket.getLength(),
						  InetAddress.getLocalHost(), 69);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		      System.out.println( "IntermediateHost: Sending packet:");
		      System.out.println("To Server: " + sendPacket.getAddress());
		      System.out.println("Destination host port: " + sendPacket.getPort());
		      len = sendPacket.getLength();
		      System.out.println("Length: " + len);
		      System.out.print("Containing: ");
		      System.out.println(new String(sendPacket.getData(),0,len));
		      System.out.println("Containing Bytes: ");
		      System.out.println(Arrays.toString(Utility.getBytes(sendPacket.getData(),0, len)));
		      // Send the datagram packet to the client via the send socket. 
		     
		      try {
		         sendReceiveSocket.send(sendPacket);
		      } catch (IOException e) {
		         e.printStackTrace();
		         System.exit(1);
		      }

		      System.out.println("IntermediateHost: packet sent to Server");
		      
		      
		      
		      
		      //Receive packet from Server
		      while(flag){
		      data = new byte[516];
		      receivePacket = new DatagramPacket(data, data.length);
		      System.out.println("IntermediateHost: Waiting for Packet.\n");
		      	
		      // Block until a datagram packet is received from receiveSocket.
		      try {        
		         System.out.println("Waiting..."); // so we know we're waiting
		         sendReceiveSocket.receive(receivePacket);
		      } catch (IOException e) {
		         System.out.print("IO Exception: likely:");
		         System.out.println("Receive Socket Timed Out.\n" + e);
		         e.printStackTrace();
		         System.exit(1);
		      }

		    
		      // Process the received datagram.
		      System.out.println("IntermediateHost: Packet received:");
		      System.out.println("From host: " + receivePacket.getAddress());
		      System.out.println("Host port: " + receivePacket.getPort());
		      len = receivePacket.getLength();
		      System.out.println("Length: " + len);
		      System.out.print("Containing: " );
		      if((len!=4 && len!=516)||Utility.containsAzero(data, 4,len)){
		    	  flag=false;
		    	  
		    	  
		      }
		      // Form a String from the byte array.
		      received = new String(data,0,len);   
		      System.out.println(received + "\n");
		     
		      
		      // Slow things down (wait 5 seconds)
		      try {
		          Thread.sleep(1000);
		      } catch (InterruptedException e ) {
		          e.printStackTrace();
		          System.exit(1);
		      }
		      
		      
		      
		      
		      //Send packet to client
		      
		      try {
					sendPacket = new DatagramPacket(data, receivePacket.getLength(),
							  InetAddress.getLocalHost(), clientPort);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			      System.out.println( "IntermediateHost: Sending packet:");
			      System.out.println("To Client: " + sendPacket.getAddress());
			      System.out.println("Destination host port: " + sendPacket.getPort());
			      len = sendPacket.getLength();
			      System.out.println("Length: " + len);
			      System.out.print("Containing: ");
			      System.out.println(new String(sendPacket.getData(),0,len));
			      
			      // Send the datagram packet to the client via the send socket.
			      
			      try {
			    	  sendToClientSocket = new DatagramSocket();
			         sendToClientSocket.send(sendPacket);
			      } catch (IOException e) {
			         e.printStackTrace();
			         System.exit(1);
			      }

			      System.out.println("IntermediateHost: packet sent to Client");
			      data = new byte[516];
			      DatagramPacket receiveclientPacket = new DatagramPacket(data, data.length);
			      
			      try {        
			    	  receiveClientSocket=new DatagramSocket();
				         System.out.println("Waiting..."); // so we know we're waiting
				         sendToClientSocket.receive(receiveclientPacket);
				      } catch (IOException e) {
				         System.out.print("IO Exception: likely:");
				         System.out.println("Receive Socket Timed Out.\n" + e);
				         e.printStackTrace();
				         System.exit(1);
				      }

				      
				      // Process the received datagram.
				      System.out.println("IntermediateHost: Packet received:");
				      System.out.println("From host: " + receiveclientPacket.getAddress());
				      System.out.println("Host port: " + receiveclientPacket.getPort());
				      len = receiveclientPacket.getLength();
				      System.out.println("Length: " + len);
				      System.out.print("Containing: " );
				      if((len!=4 && len!=516)||Utility.containsAzero(data, 4,len)){
				    	  flag=false;
				    	  
				    	  
				      }
				      // Form a String from the byte array.
				       received = new String(receiveclientPacket.getData(),0,len);   
				      System.out.println(received + "\n");
				      
				      
				      // Slow things down (wait 5 seconds)
				      
				      try {
							sendPacket = new DatagramPacket(receiveclientPacket.getData(), receiveclientPacket.getLength(),
									  InetAddress.getLocalHost(),receivePacket.getPort());
						} catch (UnknownHostException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				      System.out.println( "IntermediateHost: Sending packet:");
				      System.out.println("To Server: " + receivePacket.getAddress());
				      System.out.println("Destination host port: " + receivePacket.getPort());
				      len = receiveclientPacket.getLength();
				      System.out.println("Length: " + len);
				      System.out.print("Containing: ");
				      System.out.println(new String(receiveclientPacket.getData(),0,len));
				      // Send the datagram packet to the client via the send socket. 
				      try {
				    	  
				         sendReceiveSocket.send(sendPacket);
				      } catch (IOException e) {
				         e.printStackTrace();
				         System.exit(1);
				      }

				      System.out.println("IntermediateHost: packet sent to Server");
				      
			      
		      
		 
		      
		      
		}
	}
	
	
	
	
	
}
