package org.literacybridge.acm.mobile;

import android.app.Activity;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class DebugActivity extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		
		final Button backButton = (Button) findViewById(R.id.btnRefresh);
		
		
	}
	
	
	public void returnToMenu(View view) {
		super.onBackPressed();
	 }

	    
}
