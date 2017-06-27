package com.emanuelerota.drawingtest;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ProgressBar;

public class LoadingActivity extends AppCompatActivity {

    static NeuralNetwork net;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Thread th= new Thread(){
            @Override
            public void run(){
                net = new NeuralNetwork(3, new int[] { 28*28, 300, 10 }, new int[] { 1, 28*28, 300});
                net.loadWeights();
                startActivity(new Intent(getApplicationContext(),ActivityTestNet.class));
            }
        };
        th.start();
    }
}
