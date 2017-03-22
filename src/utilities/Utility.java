package utilities;

import java.net.DatagramPacket;
import java.net.InetAddress;

/*Assignment 1
 * Name: Sahaj Arora
 * Student No. 100961220
 */

//Utility.java
//Utility class to provide utility methods that can be used by any class.
public class Utility {
	public static boolean shutDown=false;
	private static DatagramPacket receivePacket;
	private static boolean received=false;
	private static byte[] data1;
	private static int lengthy;
	/**
	 * Trim a byte array from 0 to specified length
	 * @param data the byte array to trim
	 * @param len length of the trimmed array
	 * @return trimmed byte array
	 */
	
	//return the bytes of the given array 
	public static synchronized byte[] getBytes(byte[] data, int pos,int len) {
		
		 byte[] bytes = new byte[len-pos];
		 int j=0;

		 for (int i = pos; i<len; i++){
			bytes[j] = data[i];
			j++;
		}
		
		return bytes;
	}
	
	// check if the array contains a zero
	public static synchronized boolean  containsAzero(byte[] data2, int i, int j) {
		for(int k=i;k<j;k++){
			if(data2[k]==0) return true;
		}
		return false;
	}
	
	// to increment the array  
	public static synchronized int increment(byte array[]){
		int j,k,l;
		 boolean flag,flag2;
	
		flag=false;
		flag2=false;
		
		
		
		
		
		  
		 
		 	array[3]++;
		 	//when we reach to -128 we need to add 256 so we can keep counting up
		 	if(array[3]>=-128 && array[3]<=0){
		 		flag=true;
		 		
		 	}
		 	if(flag){
		 		
		 		k=array[3]+256;
		 	}else {
		 		k=array[3];
		 	}
		 	if(k==256){
		 		flag=false;
		 		k=0;
		 		array[2]+=1;
		 		array[3]=0;
		 	}
		 	//for the number of the left we also can reach -32768 so we have to add (2^16-1) 
		 	//so we keep counting up
		 	if((array[2]<<8)>=-32768 && (array[2]<<8)<0 ){
		 		flag2=true;
		 	}
		 	//comment
		 	if(flag2){
		 		l=(array[2]<<8)+k+65536;
		 	}else{
			
			   
			   l=(array[2]<<8)+k;
		 	}
			
			return l;
		}
	public static synchronized int getByteInt(byte array[]){
		int j,k,l;
		 boolean flag,flag2;
	
		flag=false;
		flag2=false;
		if(array[3]>=-128 && array[3]<=0){
	 		flag=true;
	 		
	 	}
	 	if(flag){
	 		
	 		k=array[3]+256;
	 	}else {
	 		k=array[3];
	 	}
	 	if(k==256){
	 		flag=false;
	 		k=0;
	 		array[2]+=1;
	 		array[3]=0;
	 	}
	 	//for the number of the left we also can reach -32768 so we have to add (2^16-1) 
	 	//so we keep counting up
	 	if((array[2]<<8)>=-32768 && (array[2]<<8)<0){
	 		flag2=true;
	 	}
	 	//comment
	 	if(flag2){
	 		l=(array[2]<<8)+k+65536;
	 	}else{
		
		   
		   l=(array[2]<<8)+k;
	 	}
		
		return l;
		
	}

	public static synchronized int getFirstZero(byte[] data1) {
		
		for(int j=0;j<data1.length;j++){
			if(data1[j]==0){
				return j;
			}
		}
		return 0;
	}
	public static synchronized void setShut(boolean a){
		shutDown=a;
	}
	public static synchronized boolean getShu(){
		return shutDown;
	}

	public static synchronized void setPacket(DatagramPacket receivePacket) {
		Utility.receivePacket=receivePacket;
		
	}

	public static synchronized void setReceived(boolean b) {
		Utility.received=b;
		
	}

	public static synchronized boolean getReceived() {
		
		return Utility.received;
	}

	public static synchronized DatagramPacket getPacket() {
		DatagramPacket packet=new DatagramPacket(receivePacket.getData(),receivePacket.getLength());
		
		return packet;
	}

	public static synchronized void setData(byte[] data) {
	data1=data;
		
	}

	public static  byte[] getData() {
		// TODO Auto-generated method stub
		byte copy[]=new byte[data1.length];
		System.arraycopy(data1,0, copy,0, data1.length);
		return copy;
	}

	

	public synchronized static int getLength() {
		// TODO Auto-generated method stub
		return lengthy;
	}

	public synchronized static void setLenght(int length) {
		lengthy=length;
		
	}

	public synchronized static int getPort() {
		// TODO Auto-generated method stub
		return receivePacket.getPort();
	}
	public synchronized static InetAddress getAddress(){
		return receivePacket.getAddress();
		
	}

}
