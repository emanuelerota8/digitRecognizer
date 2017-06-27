package com.emanuelerota.drawingtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    int targetImage;
    String user;
    TextView target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user="roberta";
        if(Math.abs(new Random().nextInt())%10!=1){
            targetImage=Math.abs(new Random().nextInt()%10);
        }
        else{
            targetImage= Math.abs(new Random().nextInt()%(36-10)+10);
        }
        target =(TextView)findViewById(R.id.idTextViewTargetImage);
        target.setText("Disegna un "+indexToString(targetImage));

        //custom view objcet (touch event)
        final DrawingView drawingView = (DrawingView) findViewById(R.id.single_touch_view);

        //clear button click
        final ImageView buttonClear = (ImageView) findViewById(R.id.clear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                drawingView.cleanScreen();
            }
        });

        //save button click
        final ImageView buttonSave = (ImageView) findViewById(R.id.done);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //reading the bitmap
                Bitmap bitmap = Bitmap.createBitmap(drawingView.getWidth(), drawingView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawingView.draw(canvas);
                drawingView.cleanScreen();

                //send the bitmap to the webserver
                ImageUploader imageUploader= new ImageUploader(user,bitmap,targetImage);
                imageUploader.execute();

                //new target image
                if(Math.abs(new Random().nextInt())%10!=1){
                    targetImage=Math.abs(new Random().nextInt()%10);
                }
                else{
                    targetImage= Math.abs(new Random().nextInt()%(36-10)+10);
                }
                target.setText("Disegna un "+indexToString(targetImage));
            }
        });
    }

    public String indexToString(int index){
        String[] alphabet= {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F","G","H","scarabocchio","J","K","L","M","N","scarabocchio","P","Q","R","scarabocchio","T","U","V","W","X","Y","scarabocchio"};
        return alphabet[index];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.rcd:
                Intent iinent= new Intent(MainActivity.this,ActivityTestNet.class);
                startActivity(iinent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void msg(String text){
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}

class ImageUploader extends AsyncTask<Void, Void, Void> {

    private String user;
    private Bitmap bitmap;
    private int targetImage;

    public ImageUploader(String user,Bitmap bitmap, int targetImage){
        this.user=user;
        this.bitmap=bitmap;
        this.targetImage=targetImage;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            //encoding image
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            //normalize image
            bitmap=prepareImage(bitmap);

            //encoding image
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            String encodedImage= Base64.encodeToString(byteArrayOutputStream.toByteArray(),Base64.DEFAULT);

            //string to send
            String data = URLEncoder.encode("user", "UTF-8") + "=" + URLEncoder.encode(user, "UTF-8");
            data += "&" + URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(encodedImage, "UTF-8");
            data += "&" + URLEncoder.encode("targetImage", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(targetImage), "UTF-8");

            // Defined URL  where to send data
            URL url = new URL("http://rotascuola.altervista.org/project/handWrittenImages/saveImage.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();

            // Get the server response
            String text = "";
            BufferedReader reader=null;
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();
        }
        catch (Exception e){Log.d("mylog",e.toString());}
        return null;
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

        return img;
    }
}
