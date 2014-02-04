package com.example.tetris;

import com.example.tetris.game.Tetris;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.Toast;

public class GameActivity extends Activity {

	private BluetoothAdapter bluetoothAdapter = null;
	private Tetris game = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	/**
	 * Initializes the bluetooth at the beginning. onResume is called in this order: <br />
	 * OnCreate -> OnStart -> OnResume <br />
	 * This method is always called when reinitializing the app.
	 */
	@Override
	public void onResume() {
		super.onResume();
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (bluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		// Make sure bluetooth is enabled on the device.
		if (!bluetoothAdapter.isEnabled()) {
			Toast.makeText(this, "Please enable your BT and re-run this program.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		game = new Tetris(bluetoothAdapter, this);
		game.start();
		
	}
	
	
	public void onPause() {
		super.onPause();
		game.setRunning(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return game.onTouchEvent(event);
	}
	
}
