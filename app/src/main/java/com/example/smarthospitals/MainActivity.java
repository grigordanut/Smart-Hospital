package com.example.smarthospitals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Action button NFC TEST
        Button buttonNFC = (Button) findViewById(R.id.btnNfc);
        buttonNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NFCReadCard.class));
            }
        });

        //Action button Login
        Button buttonLogIn = (Button)findViewById(R.id.btnLogInMain);
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginBy.class));
            }
        });

        //Action button Register
        Button buttonRegister = (Button) findViewById(R.id.btnRegisterMain);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sign = new Intent(MainActivity.this, CheckUniqueCode.class);
                startActivity(sign);
            }
        });
    }

    //user log out
    private void ContactUs(){
        finish();
        startActivity(new Intent(MainActivity.this, ContactUsForm.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == R.id.contactUs) {
            ContactUs();
        }

        return super.onOptionsItemSelected(item);
    }
}