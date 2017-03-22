package Clientpackage;

import intermediateHost.IntermediateHost;
import utilities.Constants;

public class InterThread implements Runnable {

	@Override
	public void run() {
		
			IntermediateHost host = new IntermediateHost();
			host.receiveSendPacket();
		

	}

}
