package me.leefeng.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import me.leefeng.viewlibrary.PEditTextView;

public class MainActivity extends AppCompatActivity {

    private String TAG="main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PEditTextView editext = findViewById(R.id.pe);
        editext.setListener(new PEditTextView.PEditTextFinishListener() {
            @Override
            public void callBack(String result) {
                Log.i(TAG, "callBack: "+result);
            }
        });

    }
}
