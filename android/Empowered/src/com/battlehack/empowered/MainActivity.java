package com.battlehack.empowered;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String USERNAME_MESSAGE = "com.battlehack.USERNAME_MESSAGE";
	public final static String PASSWORD_MESSAGE = "com.battlehack.PASSWORD_MESSAGE";
	
	private EditText username;
	private EditText password;
	
	@Override
	 protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        username = (EditText) findViewById(R.id.etUserName );
        password = (EditText) findViewById(R.id.etPassword);
    }

    public void login(View view) {
    	Intent intent = new Intent(this,DashboardActivity.class);
    	intent.putExtra(USERNAME_MESSAGE, username.getText().toString());
    	intent.putExtra(PASSWORD_MESSAGE, password.getText().toString());
    	startActivity(intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
