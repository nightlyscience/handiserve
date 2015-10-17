package handserver;

import java.io.IOException;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

public class Handserve implements DirectionListener {

	HandSerialInterface ser; 
	
	private volatile float rot = 0, str = 0, ht = 0, ang = 0, grb = 0;
	private volatile float multiplier = 1f;
	private boolean changed = true;
	
	public Handserve() {
		try {
			
			ser = new HandSerialInterface("/dev/ttyUSB0", 9600);
			
			Thread serthread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					float i=0;
					while(true){
						
						if(changed){
							System.out.printf("%s %s %s %s %s\n", rot, str, ht, ang, grb);
						
							ser.write(uArmMessage((short)rot, (short)str, (short)ht, (short)ang, (grb >= 5)));
							changed = false;
						}
						
						try {
							Thread.sleep(45);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}	
				}
			});
			
			serthread.start();
			
			WebSocketServer websocket = new WebSocketServer(14444);
			websocket.addDirectionListener(this);
			websocket.run();
			
		} catch (NoSuchPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Handserve();
	}
	
	public static byte[] uArmMessage(short armRot, short armStr, short armHt, short handAng, boolean ctlData){
		byte[]
		msg
		=
		{(byte)
		0xFF,
		(byte)
		0xAA,
		(byte)
		((armRot
		>>
		8)
		&
		0xFF),
		(byte)
		(armRot
		&
		0xFF),
		(byte)
		((armStr
		>>
		8)
		&
		0xFF),
		(byte)
		(armStr
		&
		0xFF),
		(byte)
		((armHt
		>>
		8)
		&
		0xFF),
		(byte)
		(armHt
		&
		0xFF),
		(byte)
		((handAng
		>>
		8)
		&
		0xFF),
		(byte)
		(handAng
		&
		0xFF),
		(byte)
		(ctlData
		?
		1
		:
		2)};
		return
		msg;
		}

	@Override
	public void valuesChanged(float a, float b, float c, float d, float e) {
		rot += a; 
		str += b;
		ht += c;
		ang += d;
		grb += e;
		
		if(grb >= 10) grb = 0;
		
		if(rot > 90) rot = 90; else if(rot < -90) rot = -90;
		if(ht > 140) ht = 140; else if(ht < 5) ht = 5;
		if(str > 200) str = 200; else if(str < 5) str = 5;
		
		changed = true;
	}

	@Override
	public String getValues() {
		return "{ \"rot\":" + rot + ", \"str\":" + str + ", \"hgt\":" + ht + ", \"ang\":" + ang + ", \"grb\":" + grb;
	}

	@Override
	public float getMultiplier() {
		return multiplier;
	}

	
}
