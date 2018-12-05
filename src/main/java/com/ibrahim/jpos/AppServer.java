package com.ibrahim.jpos;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOServer;
import org.jpos.iso.ISOSource;
import org.jpos.iso.ServerChannel;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

/**
 * Created By Ibrahim Manorek Project Jpos
 */
//http://iso8583tutorial.blogspot.com/2014/10/implementasi-menggunakan-jpos.html
public class AppServer implements ISORequestListener {

	public boolean process(ISOSource isoSrc, ISOMsg isoMsg) {
		try {
			System.out.println("Server menerima koneksi dari [" + ((BaseChannel) isoSrc).getSocket().getInetAddress().getHostAddress() + "]");
			// pack the ISO 8583 Message
	        byte[] bIsoMsg = isoMsg.pack();
	 
	        // output ISO 8583 Message String
	        String isoMessage = "";
	        for (int i = 0; i < bIsoMsg.length; i++) {
	            isoMessage += (char) bIsoMsg[i];
	        }
	        System.out.println("Packed ISO8385 Message = '"+isoMessage+"'");
	        
	        // last, print the unpacked ISO8583
	        System.out.println("MTI='"+isoMsg.getMTI()+"'");
	        for(int i=1; i<=isoMsg.getMaxField(); i++){
	            if(isoMsg.hasField(i))
	                System.out.println(i+"='"+isoMsg.getString(i)+"'");
	        }
	        
			if (isoMsg.getMTI().equalsIgnoreCase("0200")) {
				acceptNetworkMsg(isoSrc, isoMsg);
			}
		} catch (IOException ex) {
			System.out.println(ex);
		} catch (ISOException ex) {
			System.out.println(ex);
		}
		return false;
	}

	private void acceptNetworkMsg(ISOSource isoSrc, ISOMsg isoMsg) throws ISOException, IOException {
		System.out.println("Accepting Network Management Request");
//		ISOMsg reply = (ISOMsg) isoMsg.clone();
		ISOMsg reply = new ISOMsg();
		reply.setMTI("0810");
		reply.set(39, "00");
		reply.set(63, "Balikin Gann");

		isoSrc.send(reply);
	}

	public static void main(String[] args) throws Exception {
		String hostname = "localhost";
		int portNumber = 12345;

		// membuat sebuah packager
		ISOPackager packager = new GenericPackager("packager/fields.xml");
		// membuat channel
		ServerChannel channel = new ASCIIChannel(hostname, portNumber, packager);
		// membuat server
		ISOServer server = new ISOServer(portNumber, channel, null);
		server.addISORequestListener(new AppServer());

		new Thread(server).start();

		System.out.println("Server siap menerima koneksi pada port [" + portNumber + "]");
	}

}
