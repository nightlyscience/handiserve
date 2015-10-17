package handserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public class HandSerialInterface {

	private static boolean connected;
	private Thread readerThread; 
	private String portName = null;
	private int baudRate = 0;
	
	private final InputStream in;
	private final OutputStream out;

	public HandSerialInterface(String portName, int baudRate) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {

		connected = true;

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
			in = null;
			out = null;
			return;
		} else {
			
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if ( commPort instanceof SerialPort ) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();
				System.out.printf("Connected serial device %s @ %s", serialPort.getName(), serialPort.getBaudBase());
				readerThread = new Thread(new Runnable() {
					public void run() {
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						while (connected){
							try {
								String dataIdf = "W: ";
								String sData = reader.readLine();
								System.out.println("raw> " + sData);
								sData = sData.trim();
								//if(sData.contains(dataIdf)) {
									//String parsedData = sData.substring(sData.indexOf(dataIdf) + dataIdf.length());
									//S.debug("> " + parsedData);
									//announceSensorChange(Float.parseFloat(parsedData));
								//}
							} catch (IOException e) {
								e.printStackTrace();
							} catch(NumberFormatException e2) {
								e2.printStackTrace();
							}
						}
					}
				});

				readerThread.start();
				
			} else {
				System.out.println("Error: Wryyyyyy!");
				in = null;
				out = null;
			}
		}     
	}

	public void stop(){
		connected = false;
	}
	
	public void write(byte[] data){
		if(data == null || data.length <= 0){
			return;
		} else {
			try {
				out.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
