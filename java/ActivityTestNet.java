package com.emanuelerota.drawingtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityTestNet extends AppCompatActivity {

    NeuralNetwork net;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_net);
        textView=(TextView)findViewById(R.id.textView);

        //setup the network
        net = LoadingActivity.net;

        //custom view objcet (touch event)
        final DrawingView drawingView = (DrawingView) findViewById(R.id.single_touch_view);

        //set up the timer
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //runNet(drawingView);
            }
        },1000,1000);

        //clear button click
        final ImageView buttonClear = (ImageView) findViewById(R.id.clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                drawingView.cleanScreen();
                textView.setText("");
            }
        });

        //run button click
        final ImageView buttonSave = (ImageView) findViewById(R.id.done);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                runNet(drawingView);
            }
        });
    }

    public void runNet(DrawingView drawingView){
        try{
            //reading the bitmap
            Bitmap bitmap = Bitmap.createBitmap(drawingView.getWidth(), drawingView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawingView.draw(canvas);
            //drawingView.cleanScreen();

            //normalize image
            bitmap=prepareImage(bitmap);

            //reading the bitmpa
            float[] values = new float[28*28];
            for(int i=0;i<28;i++){
                for(int j=0;j<28;j++){
                    values[i*28+j]=Color.red(bitmap.getPixel(i,j));
                }
            }

            //loading the data into the net
            net.loadInput(values);

            //run the net
            net.runNetwork();

            //get prediction
            float[] p=net.getPrediction();
            int indexMax=0;
            Log.d("myLog","here"+String.valueOf(net.getNNeuronOutput()));
            for(int i=0;i<net.getNNeuronOutput();i++){
                Log.d("mylog",String.valueOf(i)+"="+String.valueOf(p[i]));
                if(p[i]>p[indexMax])
                    indexMax=i;
            }
            textView=(TextView)findViewById(R.id.textView);
            if(p[indexMax]*100<20){
                textView.setText("Cifra non riconosciuta");
            }
            else if(p[indexMax]*100<50){
                textView.setText("Penso potrebbe essere un "+ indexToString(indexMax)+" ("+String.format("%.0f", p[indexMax]*100)+"%)");
            }
            else{
                textView.setText("Penso sia un "+indexToString(indexMax)+" ("+String.format("%.0f", p[indexMax]*100)+"%)");
            }
        }
        catch (Exception e){
            textView=(TextView)findViewById(R.id.textView);
            textView.setText("The bitmap is empty");
            Log.d("mylog","errore");
        }
    }

    public String indexToString(int index){
        String[] alphabet= {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","scarabocchio","J","K","L","M","N","scarabocchio","P","Q","R","scarabocchio","T","U","V","W","X","Y","scarabocchio"};
        Log.d("myLog","ciao sono rrrr"+String.valueOf(alphabet.length));
        return alphabet[index];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                Intent iinent= new Intent(ActivityTestNet.this,MainActivity.class);
                startActivity(iinent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Bitmap prepareImage(Bitmap bitmap){
        //resize image
        bitmap=Bitmap.createScaledBitmap(bitmap, 28, 28, false);

        //monocramtica
        for(int i=0;i<bitmap.getWidth();i++){
            for(int j=0;j<bitmap.getHeight();j++){
                if(Color.red(bitmap.getPixel(i,j))!=0){
                    //se non è nero
                    bitmap.setPixel(i,j,Color.rgb(255,255,255));
                }
            }
        }

        //centering
        bitmap=centerImage(bitmap);

        return bitmap;
    }

    private Bitmap centerImage(Bitmap img)
    {
        int a, b, c, d;
        a = 0;
        b = 0;
        c = 0;
        d = 0;
        //A
        boolean stop = false;
        for(int i = 0; i < 28 && !stop; i++)
        {
            for(int j = 0; j < 28 && !stop; j++)
            {
                if (Color.red(img.getPixel(i, j)) != 0)
                {
                    stop = true;
                    a = i;
                }

            }
        }

        //B
        stop = false;
        for (int i = 27; i >= 0 && !stop; i--)
        {
            for (int j = 0; j < 28 && !stop; j++)
            {
                if (Color.red(img.getPixel(i, j)) != 0)
                {
                    stop = true;
                    b = i;
                }

            }
        }

        //C
        stop = false;
        for (int i = 0; i < 28 && !stop; i++)
        {
            for (int j = 0; j < 28 && !stop; j++)
            {
                if (Color.red(img.getPixel(j, i)) != 0)
                {
                    stop = true;
                    c = i;
                }
            }
        }

        //D
        stop = false;
        for (int i = 27; i >= 0 && !stop; i--)
        {
            for (int j = 0; j < 28 && !stop; j++)
            {
                if (Color.red(img.getPixel(j, i)) != 0)
                {
                    stop = true;
                    d = i;
                }

            }
        }

        a -= 2;
        b += 2;
        c -= 2;
        d += 2;
        if(a>0 && b>0 && c>0 && d>0){
            Bitmap ris = Bitmap.createBitmap(b-a, d-c, Bitmap.Config.ARGB_8888);
            for(int i = a; i < b; i++)
            {
                for(int j = c; j < d; j++)
                {
                    ris.setPixel(i - a, j - c, Color.rgb(Color.red(img.getPixel(i, j)), Color.green(img.getPixel(i, j)), Color.blue(img.getPixel(i, j))));
                }
            }

            img =  Bitmap.createBitmap(28, 28, Bitmap.Config.ARGB_8888);
            //set black background
            for(int i=0;i<28;i++){
                for(int j=0;j<28;j++){
                    img.setPixel(i,j,Color.BLACK);
                }
            }

            for(int i = (28-(b-a))/2; i < ris.getWidth() + (28 - (b - a)) / 2; i++)
            {
                for(int j = (28-(d-c))/2; j < ris.getHeight() + (28 - (d - c)) / 2; j++)
                {
                    img.setPixel(i, j, Color.rgb(Color.red(ris.getPixel(i - (28 - (b - a)) / 2, j - (28 - (d - c)) / 2)), Color.green(ris.getPixel(i - (28 - (b - a)) / 2, j - (28 - (d - c)) / 2)), Color.blue(ris.getPixel(i - (28 - (b - a)) / 2, j - (28 - (d - c)) / 2))));
                }
            }
        }
        return img;
    }
}
