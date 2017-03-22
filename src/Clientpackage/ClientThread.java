package Clientpackage;

import intermediateHost.IntermediateHost;
import utilities.Constants;

/*
 * this class is a thread that will contain the clients that we create
 */
		
public  class ClientThread implements Runnable{
	private String reqType,filepath,filewritepath,readFilePath,vqMode,tnMode; 
	
	public ClientThread(String reqType,String filepath,String filewritepath,String readFilePath,String vqMode,String tnMode){
		this.reqType=reqType;
		this.filepath=filepath;
		this.filewritepath=filewritepath;
		this.readFilePath=readFilePath;
		this.vqMode=vqMode;
		this.tnMode=tnMode;
		//hi
	}
	
	@Override
	public void run() {
		Client c=new Client();
		
		c.sendAndReceive(reqType, filepath, filewritepath, readFilePath, vqMode, tnMode);
		
		
	}
	
	
	
	
	
	
}