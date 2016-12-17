package com.example.os.positionin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void selectDrawMapTest(View pView){
        mIntent = new Intent(this,DrawMap.class);
        startActivity(mIntent);
    }

    public void selectStaticMapTest(View pView){
        mIntent = new Intent(this,StaticMapTestActivity.class);
        startActivity(mIntent);
    }

    public void selectDynamicMapTest(View pView){
        mIntent = new Intent(this,DynamicMapTestActivity.class);
        startActivity(mIntent);
    }

}
