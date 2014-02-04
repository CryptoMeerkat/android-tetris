package com.example.tetris;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	/**
	 * Called b the start game button.
	 * 
	 * @param v the view who called this method
	 */
	public void initializeGame(View v) {
		
		Intent gameIntent = new Intent(v.getContext(), GameActivity.class);
		startActivity(gameIntent);
		
	}
	
	

}
