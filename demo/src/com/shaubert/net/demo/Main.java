package com.shaubert.net.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
    }
    
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        
        findViewById(R.id.dialog_example_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main.this, DialogExample.class));
            }
        });
        
        findViewById(R.id.table_example_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main.this, RequestTableExample.class));
            }
        });
    }
}
