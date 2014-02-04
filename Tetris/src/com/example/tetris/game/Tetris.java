package com.example.tetris.game;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;

public class Tetris extends Thread implements SensorEventListener {

	public static final byte light 	= (byte) 255;
	private boolean isRunning 		= false;	// the boolean to stop this thread running
	private final int SENSITIVITY 	= 50; // the greater number, the more the smartphone to turn when moving
	private final int GAME_SPEED 	= 300; // the lower the number the faster the game
	private final int START_OFFSET 	= 4000; // the start LED screen in ms (4 screens are shown)
	private final int END_OFFSET 	= 2500; // the end LED screen in ms (1 screen is shown)
	private int bluetoothPort 		= 16;
	private int matrixDimension 	= 24;
    private double angleZ 			= 0.0; // stores the z-axis angle of the smartphone
    private int orientation 		= 0; // stores how many times the user has tapped on his phone to turn a block
	private BluetoothAdapter btAdapter 	= null;
	private BluetoothSocket btSocket 	= null;
	private OutputStream btOut 			= null;
	//private static String address = "88:53:2E:E1:F1:8A"; // Sasmung ultrabook Bluetooth MAC
	private static String address 		= "5C:F3:70:02:D7:C7"; // TeCo Rasperry Pi Bluetooth MAC
	private Gamefield gamefield 		= null;
    private SensorManager sensorManager = null;
	public static final String name 	= "Tetris";
	
    /**
     * The game is created. A context to a available {@code Activity} must
     * be passed, to register the Sensor in this 
     * 
     * @param bluetoothAdapter the bluetooth adapter of the smartphone
     * @param context the context, to get the SensorManager
     */
	public Tetris(BluetoothAdapter bluetoothAdapter, Context context) {
		super(name);
		isRunning = true;
		this.btAdapter = bluetoothAdapter;
		gamefield = new Gamefield();
		
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
	}
	
	@Override
	public void run() {
		
		initGame();
		
		startAnimation();
		
		while (isRunning) {
			
			if (gamefield.isGameover()) {
				
				break;
				
			} else {
				// angle is concurrent read from Sensor
				int colOffset = (int) -Math.round(angleZ / SENSITIVITY);
				
				gamefield.moveActiveBrick(colOffset, orientation);
				orientation = gamefield.getOrientation().ordinal();
				
				writeGamefield(gamefield.getGamefield());
				
				try {
					sleep(GAME_SPEED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		
		}
		
		endAnimation();
		
		destroyGame();
		
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	
	private void writeGamefield(byte[][] ledMatrix) {
		
		byte[] matrixInMessage = new byte[matrixDimension * matrixDimension];

		Arrays.fill(matrixInMessage, (byte) 0);
		
		for (int i = 0; i < ledMatrix.length; i++) {
			for (int j = 0; j < ledMatrix[i].length; j++) {
				matrixInMessage[i * matrixDimension + j] = ledMatrix[i][j];
			}
		}
		
		try {
			btOut.write(matrixInMessage);
			Log.d("Bluetooth", "Sent led matrix to " + address + ".");
		} catch (IOException e) {
			Log.e("Bluetooth", "Exception during write.");
		}
		
	}
	
	/**
	 * Initializes bluetooth and registers the Accelerometer sensor listener.
	 */
	private void initGame() {

		try {
			// Get device object for the address.
			BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);
			
			// This is a workaround to get the socket using the Java reflection API.
			// Needed because the regular method apparently doesn't work on many devices.
			// The normal way to get the socket is:
			//
			//       btSocket = device.createRfcommSocketToServiceRecord(SERVICE_UUID);
			//
			// See: http://stackoverflow.com/questions/2853790/why-cant-htc-droid-running-ota-2-1-communicate-with-rfcomm
					
			Method m = btDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
			btSocket = (BluetoothSocket) m.invoke(btDevice, bluetoothPort);
		} catch (Exception e) {
			Log.e("Bluetooth", "Socket creation failed.", e);
		}

		// Connecting to devices while discovery is going on might result in problems, so it's
		// best to cancel it here.
		btAdapter.cancelDiscovery();
		
		try {
			// Blocking connect() call.
			btSocket.connect();
			Log.d("Bluetooth", "Connected to " + address + ". Sending data...");
		} catch (IOException e) {
			Log.e("Bluetooth", "Failed to open socket.", e);
		}
		
		// Create output stream to send data to the server.
		try {
			btOut = btSocket.getOutputStream();
		} catch (IOException e) {
			Log.e("Bluetooth", "Failed to create optupt stream.");
		}
		
		try {
			// Wait a moment to make sure devices are connected (Make sure stream is ready).
			sleep(200);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		// now register sensor listening
		Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		if (accel != null) {
			sensorManager.registerListener(this,
										   sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
										   SensorManager.SENSOR_DELAY_GAME);
		} else {
			Log.e("Sensor", "This game needs an accelerometer sensor for playing.");
			throw new RuntimeException();
		}
		
	}
	
	/**
	 * Clean up bluetooth and unregisters accelerometer sensor listener.
	 */
	private void destroyGame() {
		// send one last Matrix to clear the field
		
		
		Log.d("Bluetooth", "Bluetooth closing.");
		
		// close bluetooth
		try {
			btOut.close();
			btSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Log.d("Sensor", "Unregistering sensor.");
		
		// unregister from sensormanager
		sensorManager.unregisterListener(this);
	}

	private void startAnimation() {
		
		writeGamefield(StaticFields.START_1);
		
		try {
			sleep(START_OFFSET/4);
		} catch (InterruptedException e) {
			Log.i("Start", "Could not send thread to sleep for start matrix.");
		}
		
		writeGamefield(StaticFields.START_2);
		
		try {
			sleep(START_OFFSET/4);
		} catch (InterruptedException e) {
			Log.i("Start", "Could not send thread to sleep for start matrix.");
		}
		
		writeGamefield(StaticFields.START_3);
		
		try {
			sleep(START_OFFSET/4);
		} catch (InterruptedException e) {
			Log.i("Start", "Could not send thread to sleep for start matrix.");
		}
		
		writeGamefield(StaticFields.START_4);
		
		try {
			sleep(START_OFFSET/4);
		} catch (InterruptedException e) {
			Log.i("Start", "Could not send thread to sleep for start matrix.");
		}
		
		writeGamefield(StaticFields.CLEAR);
		
	}
	
	private void endAnimation() {
		
		try {
			sleep(END_OFFSET * 2 / 5);
		} catch (InterruptedException e) {
			Log.i("Start", "Could not send thread to sleep for start matrix.");
		}
		
		writeGamefield(StaticFields.END);
		
		try {
			sleep(END_OFFSET * 3 / 5);
		} catch (InterruptedException e) {
			Log.i("Start", "Could not send thread to sleep for start matrix.");
		}
		
		writeGamefield(StaticFields.CLEAR);
		
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    
	/**
	 * Calculates the z-axis angle of the smartphone. This is orientated on the
	 * coordinate system of Android: {@link http://developer.android.com/reference/android/hardware/SensorEvent.html}.
	 * <br />
	 * <br />
	 * The z-axis angle allows to determine the offset for the current
	 * active brick in the Tetris game.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		angleZ = Math.atan2(x, y) / (Math.PI/180);
	}

	/**
	 * When the display has been
	 */
	public boolean onTouchEvent(MotionEvent event) {
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			orientation++;
		}
		
		return true;
	}
}
