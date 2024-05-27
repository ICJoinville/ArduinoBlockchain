package eu.joaorodrigo.demos.blockchain;

import jssc.SerialPortEvent;

public class SerialReadEventHandler implements jssc.SerialPortEventListener {
	
	private static String a = "";
	private static String lastBytes = "";
	private static int b;
	
    public void serialEvent(jssc.SerialPortEvent evt) {
        if (evt.isRXCHAR())
        {
            try
            {
                byte[] singleData = BlockchainDemo.comPort.readBytes();
                
                a = a + new String(singleData).replace("\n", "");
                if(a.endsWith("=")) {
                	a = a.replace("=", "").replace("\n", "").trim();
                	b = Integer.parseInt(a);
                	a = "";
                	BlockchainDemo.sendNewValue(b);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
