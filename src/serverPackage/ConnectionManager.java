package serverPackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;

import utilities.Constants;
import utilities.Utility;

/*
 * This thread is created for every client-connection. It takes care of the server-client transaction.
 */
public class ConnectionManager extends Thread{
	private String filepath;
	private DatagramPacket receivePacket, sendPacket;
	private DatagramSocket sendReceiveSocket, receiveSocket;
	private byte[] data;
	public static final byte READ = 1, WRITE = 2;
	private boolean isReadRequest, isWriteRequest;
	String filePath;
	boolean flag=true;
	byte[] opblock = new byte[4];
	public String mode;
	public int len;
	public String received;
	private boolean flag3=false;
	private final String FILEPATH="";
	private boolean errorFlag;
	private DatagramPacket errorPacket;
	public ConnectionManager( DatagramPacket receivedPacket, byte[] data,String filepath,String mode ){
		this.receivePacket = receivedPacket;
		
		this.filepath=FILEPATH+filepath;
		this.mode=mode;
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			sendReceiveSocket.setSoTimeout(10000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.data = data;
	}

	public void run(){
		// Process the received datagram.

		
		System.out.println("Server: request received:");
		//checking the mode if its verbose or Normal
		if(mode.equals(Constants.VERBOSE)){

			System.out.println("From host: " + receivePacket.getAddress());
			System.out.println("Host port: " + receivePacket.getPort());
			int len = receivePacket.getLength();
			System.out.println("Length: " + len);
			System.out.print("Containing: " );

			// Form a String from the byte array.
			String received = new String(data,0,len);   
			System.out.println(received + "\n");
			System.out.print("Containing Bytes: ");
			System.out.println(Arrays.toString(Utility.getBytes(receivePacket.getData(),0, len)));
		}

		//Validate Request
		
		if (isValidRequest(data)){
			//checking if it is read request 
			if (data[1] == READ){
				isReadRequest = true;
				isWriteRequest = false;
				
				// checking if it is write request 
			} else if (data[1] == WRITE){
				isReadRequest = false;
				isWriteRequest = true;
			}
		} else { // Throw an exception if the packet is an invalid request
			System.out.println("Packet is invalid.");
			receiveSocket.close();

			try {
				throw new Exception("Invalid Packet.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		//Slow things down (wait 1.5 seconds)
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e ) {
			e.printStackTrace();
			System.exit(1);
		}



		//Create a response byte array
		//0301 for a read request
		//0401 for a write request
		byte[] responseData = null;

		if (isReadRequest){
			// creating the response date which contains 512 data and 4 bytes of the op block
			responseData=new byte[516];
			responseData[0] = 0;
			responseData[1] = 3;
			responseData[2] = 0;
			responseData[3] = 0;
			BufferedInputStream in = null;
			
			
			try {
				in = new BufferedInputStream(new FileInputStream(filepath));
				
			
			} catch ( IOException e) {
				if(e instanceof FileNotFoundException){
					
					
					
				
				//if there is an error of code 1, we form an error packet and we send it to the client
				byte error[]=new byte[100];
				putError(error,1,"the file you are trying to read from does not exist");
				
				
				
				
				}else{
					
					//if there is an error of code 2, we form an error packet and we send it to the client
					byte error[]=new byte[100];
					putError(error,2,"the file you are trying to read from can not be accessed");
				}
				
				return;
			}
			
			
			
			

			// creating an array of data to send the data 
			
			byte[] data1 = new byte[512];
			byte[] ACK = new byte[4];

			int n;

			/* Read the file in 512 byte chunks. */
			try {
				while ((n = in.read(data1)) != -1) {
					/* 
					 * We just read "n" bytes into array data. 
					 * Now write them to the output file. 
					 */
					if(!Utility.containsAzero(data1,0,512)){
						//if we enter this condition it means that data1 doesn't contain a zero
						flag3=false;
						System.arraycopy(data1,0,responseData,4,data1.length);
					}else {
						//if data contains zero it means it is the last set of bytes we need to transefer so we have to create a packet
						//that is less than 516 bytes
						flag3=true;
						int j=Utility.getFirstZero(data1);
							byte copyArray[]=responseData;
						responseData=new byte[j+4];
						System.arraycopy(copyArray, 0,responseData,0,4);
						System.arraycopy(data1,0,responseData,4,j);
						
					}
					
					
					// creating a send packet
					DatagramPacket sendPacketDATA = new DatagramPacket(responseData, responseData.length,
							receivePacket.getAddress(), receivePacket.getPort());
					
					// printing the info of the sending data 
					System.out.println( "Server: Sending Data:");
					if(mode.equals(Constants.VERBOSE)){
						System.out.println("To host: " + sendPacketDATA.getAddress());
						System.out.println("Destination host port: " + sendPacketDATA.getPort());
						len = sendPacketDATA.getLength();
						System.out.println("Length: " + len);
						System.out.print("Containing: ");
						System.out.println(new String(sendPacketDATA.getData(),0,len));
						System.out.print("Containing Bytes: ");
						System.out.println("opcode: "+Arrays.toString(Utility.getBytes(sendPacketDATA.getData(),0,2)));

						System.arraycopy(responseData,0,opblock,0,4);
						System.out.println("block#: "+Utility.increment(opblock));
						System.out.println("data: "+Arrays.toString(Utility.getBytes(sendPacketDATA.getData(),4,len)));
					}
					//this will check for us if the last data transfer is full, we send a byte of zeros to the server.
					
					data1=new byte[512];
					// creating a packet to receive the acknowlegment  
					DatagramPacket receivePacketACK = new DatagramPacket(ACK, ACK.length);
					
					//send data
					System.arraycopy(opblock,0,responseData,0,4);
					
					// sending the data packet to the client 
					try {
						sendReceiveSocket.send(sendPacketDATA);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}

					System.out.println("data has been sent");
					//get acknowledge

					try {
						sendReceiveSocket.receive(receivePacketACK);
					} catch (SocketTimeoutException e) {
						System.out.println("can't receive from the client we are going to close the file transfer");
						return;
					}
					

					System.out.println("Server: Acknowledgement received:");
					
					if(mode.equals(Constants.VERBOSE)){
						System.out.println("From host: " + receivePacketACK.getAddress());
						System.out.println("Host port: " + receivePacketACK.getPort());
						len = receivePacketACK.getLength();
						System.out.println("Length: " + len);
						System.out.print("Containing: " );

						// Form a String from the byte array.
						received = new String(data,0,len);   
						System.out.println(received + "\n");
						System.out.print("Containing Bytes: ");
						System.out.println("opcode: "+ Arrays.toString(Utility.getBytes(receivePacketACK.getData(),0,2)));

						System.out.println("block#: "+ Utility.getByteInt(receivePacketACK.getData()));
					}





				}
				if(!flag3){
					/* 
					 * We just read "n" bytes into array data. 
					 * Now write them to the output file. 
					 */
					System.arraycopy(data1,0,responseData,4,data1.length);
					
					// creating a send packet
					DatagramPacket sendPacketDATA = new DatagramPacket(responseData, responseData.length,
							receivePacket.getAddress(), receivePacket.getPort());
					
					// printing the info of the sending data 
					System.out.println( "Server: Sending Data:");
					if(mode.equals(Constants.VERBOSE)){
						System.out.println("To host: " + sendPacketDATA.getAddress());
						System.out.println("Destination host port: " + sendPacketDATA.getPort());
						len = sendPacketDATA.getLength();
						System.out.println("Length: " + len);
						System.out.print("Containing: ");
						System.out.println(new String(sendPacketDATA.getData(),0,len));
						System.out.print("Containing Bytes: ");
						System.out.println("opcode: "+Arrays.toString(Utility.getBytes(sendPacketDATA.getData(),0,2)));

						System.arraycopy(responseData,0,opblock,0,4);
						System.out.println("block#: "+Utility.increment(opblock));
						System.out.println("data: "+Arrays.toString(Utility.getBytes(sendPacketDATA.getData(),4,len)));
					}
					
					
					// creating a packet to receive the acknowlegment  
					DatagramPacket receivePacketACK = new DatagramPacket(ACK, ACK.length);
					
					//send data
					System.arraycopy(opblock,0,responseData,0,4);
					
					// sending the data packet to the client 
					try {
						sendReceiveSocket.send(sendPacketDATA);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
					data1=new byte[512];
					System.out.println("data has been sent");
					//get acknowledge

					try {
						sendReceiveSocket.receive(receivePacketACK);
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(1);
					}
					

					System.out.println("Server: Acknowledgement received:");
					
					if(mode.equals(Constants.VERBOSE)){
						System.out.println("From host: " + receivePacketACK.getAddress());
						System.out.println("Host port: " + receivePacketACK.getPort());
						len = receivePacketACK.getLength();
						System.out.println("Length: " + len);
						System.out.print("Containing: " );

						// Form a String from the byte array.
						received = new String(data,0,len);   
						System.out.println(received + "\n");
						System.out.print("Containing Bytes: ");
						System.out.println("opcode: "+ Arrays.toString(Utility.getBytes(receivePacketACK.getData(),0,2)));

						System.out.println("block#: "+ Utility.getByteInt(receivePacketACK.getData()));

				}

			}
			
			
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			// checking if its write request 
		} else if (isWriteRequest){
	
			BufferedOutputStream out=null;
			//create a file with the file given by the client and catch the error if the file already exists
			File f = new File(filepath);
			
				try{
					
					if(!f.createNewFile()){
						byte error[]=new byte[100];
						putError(error,6,"the file you are trying to write to already exists");
						return;
						
					}
			   
				}catch (IOException e){
					//if there is an error of code 2, we form an error packet and we send it to the client
					
						byte error[]=new byte[100];
						putError(error,2,"the file you are trying to write to can not be accessed");
						
					
					return;
			   }
				
			try {
				out =
						new BufferedOutputStream(new FileOutputStream(filepath));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// creating a an array data to recive the data from the client 
			byte[] DATA = new byte[516];
			// creating an array to send ack
			byte[] ACK = {0,4,0,0};
			
			// creating a packet for the ack
			DatagramPacket sendPacketACK = new DatagramPacket(ACK, ACK.length, receivePacket.getAddress(), receivePacket.getPort());
			// creating packet to receive data 
			DatagramPacket receivePacketDATA = new DatagramPacket(DATA, DATA.length);

            // sending the ack packet 
			try {
				sendReceiveSocket.send(sendPacketACK);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			// printing the info for the ack 
			System.out.println( "Server: Sent Acknowledgement:");
			if(mode.equals(Constants.VERBOSE)){
				System.out.println("To host: " + receivePacket.getAddress());
				System.out.println("Destination host port: " + receivePacket.getPort());
				len = sendPacketACK.getLength();
				System.out.println("Length: " + len);
				System.out.print("Containing: ");
				System.out.println(new String(sendPacketACK.getData(),0,len));
				System.out.print("Containing Bytes: ");
				System.out.println("opcode: "+Arrays.toString(Utility.getBytes(sendPacketACK.getData(),0,2)));
				System.out.println("block#: 0");
			}
			

			//this while will keep looping until we get data less than 512 byte from the client
			while(flag){
		


				

				System.out.println("now waiting for new data");
				//get data
				try {
					sendReceiveSocket.receive(receivePacketDATA);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				if(Utility.containsAzero(receivePacketDATA.getData(),4,516)){
					flag=false;
				}
				if(receivePacketDATA.getLength()<516){
					flag=false;
				}
				System.out.println("Server: Data received:");
				if(mode.equals(Constants.VERBOSE));
				System.out.println("From host: " + receivePacket.getAddress());
				System.out.println("Host port: " + receivePacket.getPort());
				len = receivePacketDATA.getLength();
				System.out.println("Length: " + len);
				System.out.print("Containing: " );

				// Form a String from the byte array.
				String received1 = new String(receivePacketDATA.getData(),0,len);   
				System.out.println(received1 + "\n");
				System.out.print("Containing Bytes: ");
				System.out.println("opcode: "+Arrays.toString(Utility.getBytes(receivePacketDATA.getData(),0,2)));
				System.arraycopy(receivePacketDATA.getData(),0,opblock,0,4);
				System.out.println("block#: "+Utility.getByteInt(opblock));
				System.out.println("data: "+Arrays.toString(Utility.getBytes(receivePacketDATA.getData(),4,len)));

				try {
					out.write(receivePacketDATA.getData(),4,receivePacketDATA.getLength()-4);
				} catch (IOException e1) {
					byte error[]=new byte[100];
					putError(error,3,"no enough memory");
					
					return;
				}

				// get block number from the received block
				ACK[2] = receivePacketDATA.getData()[2];
				ACK[3] = receivePacketDATA.getData()[3];
				sendPacketACK = new DatagramPacket(ACK, ACK.length, receivePacket.getAddress(), receivePacket.getPort());

				try {
					sendReceiveSocket.send(sendPacketACK);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}

				System.out.println( "Server: Sent Acknowledgement:");
				if(mode.equals(Constants.VERBOSE)){
					System.out.println("To host: " + receivePacket.getAddress());
					System.out.println("Destination host port: " + receivePacket.getPort());
					len = sendPacketACK.getLength();
					System.out.println("Length: " + len);
					System.out.print("Containing: ");
					System.out.println(new String(sendPacketACK.getData(),0,len));
					System.out.print("Containing Bytes: ");
					System.out.println("opcode: "+Arrays.toString(Utility.getBytes(sendPacketACK.getData(),0,2)));
					System.out.println("block#: "+Utility.getByteInt(ACK));
				}
				System.out.println("ACKNOWLEDGEMENT SENT");

			}

			try {
				out.close();
			} catch (IOException e) {
				
				System.out.println("while closing the file that we are writing on the memory limit was exeeded");
				
				return;
			}


		}
		System.out.println("done with transeferring");
	}




	//this method takes a byte array, an error code and a string and it combines them all together
	private void putError(byte[] error, int i, String string) {
		byte msg[]=string.getBytes();
		error[0]=0;error[1]=5;error[2]=0;error[3]=(byte)i;
		System.arraycopy(msg, 0, error, 4, msg.length);
		errorPacket =new DatagramPacket(error,error.length,receivePacket.getAddress(),receivePacket.getPort());
		System.out.println(string);
		try {
			sendReceiveSocket.send(errorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("error packet sent");
		
		
	}

	/**
	 * Validate the request
	 * @param data the request data to validate
	 * @return false if request is not valid, true if request is valid
	 */
	// checks if the request is valid or not 
	public boolean isValidRequest(byte[] data){
		boolean isValid = true;

		if (data[0]!=0) return false; //First element should always be zero

		//2nd element can either be 1 or 2
		if (data[1]==1 || data[1] == 2) isValid = true;
		else {
			return false;
		}

		int count = 0;

		int fileNameEndingIndex = 0;

		boolean zeroSwitch = false; //true when iterator hits a zero

		for (int i = 2; i<receivePacket.getLength(); i++){

			if (data[i] != 0) count++;

			else if (data[i] == 0) {
				fileNameEndingIndex = i-1; //index of the last byte of the file name in the data array
				zeroSwitch = true; //zero found, turn the switch on
				break;
			}
		}

		//The count will be zero if there is no file name in the data
		if (count==0 || count > receivePacket.getLength()-3) return false;

		if (!zeroSwitch) return false;


		int modeEndingIndex = 0;
		count = 0;
		zeroSwitch = false;
		for (int j = fileNameEndingIndex + 2; j<receivePacket.getLength(); j++){
			if (data[j] != 0) count++;

			else if (data[j] == 0) {
				modeEndingIndex = j-1; //index of the last character of the "mode" in the data array
				zeroSwitch = true;
				break;
			}

		}

		if (count==0 || count == receivePacket.getLength()) return false; 
		if (!zeroSwitch) return false;
		else {
			if (receivePacket.getLength() - modeEndingIndex != 2) return false;
		}

		return true;
	}
}
