package com.emanuelerota.drawingtest;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Emanuele Rota on 27/02/2017.
 */

public class NeuralNetwork {
    List<NetworkLayer> network;

    public NeuralNetwork(int nLayer, int[] nNeuronForLayer, int[] nInputForNeuron )
    {
        network=new ArrayList<>();
        for(int i = 0; i < nLayer; i++)
        {
            network.add(new NetworkLayer(nNeuronForLayer[i], nInputForNeuron[i]));
        }

    }

    public void loadInput(float[] value)
    {
        //normalize data
        value = normalizeInput(value);

        //loading data into network
        for(int i = 0; i < value.length; i++)
        {
            network.get(0).getNeuron(i).setOutput( value[i]);
        }
    }

    public float[] normalizeInput(float[] values)
    {
        //preparing data in range 0.01 to 0.99
        for (int i = 0; i < values.length; i++)
        {
            values[i] = (float)((0.98 * values[i]) / 255 + 0.01);
        }

        return values;
    }

    public void runNetwork()
    {
        for(int i = 0; i < network.size()-1; i++)
        {
            for(int j = 0; j < network.get(i+1).nNeuron(); j++)
            {
                for(int k = 0; k < network.get(i + 1).getNeuron(j).nInput(); k++)
                {
                    network.get(i + 1).getNeuron(j).setinputValue(k, network.get(i).getNeuron(k).getOutput());
                }
                network.get(i + 1).getNeuron(j).processInput();
            }
        }
    }

    public void loadWeights(){
        NetDownloader downloader= new NetDownloader();
        downloader.execute();
        String net=downloader.getNet();
        String[] split;
        while(net==null)
            net=downloader.getNet();
        split = net.split(";");
        int sum = 0;
        for (int i = 0; i < network.size(); i++)
        {
            for (int j = 0; j < network.get(i).nNeuron(); j++)
            {
                for (int k = 0; k < network.get(i).getNeuron(j).nInput(); k++)
                {
                    split[sum]=split[sum].replace(',','.');
                    network.get(i).getNeuron(j).setInputWeight(k, Float.parseFloat(split[sum]));
                    sum++;
                }
            }
        }
    }

    public float[] getPrediction()
    {
        float[] prediction = new float[network.get(network.size() - 1).nNeuron()];

        for(int i=0;i< network.get(network.size() - 1).nNeuron(); i++)
        {
            prediction[i] = network.get(network.size()-1).getNeuron(i).getOutput();
        }

        return prediction;
    }

    public int getNNeuronOutput(){
        return network.get(network.size()-1).nNeuron();
    }
}

class NetDownloader extends AsyncTask<Void, Void, Void> {

    private String net;
    public NetDownloader(){}

    public String getNet(){
        return net;
    }
    @Override
    protected Void doInBackground(Void... params) {
        try {
            // Defined URL  where to send data
            URL url = new URL("http://rotascuola.altervista.org/project/handWrittenImages/apiReadImages.php?action=downloadNet&user=emanuele");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

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
            net=text;
            Log.d("mylog","lungCarattFile="+String.valueOf(text.length()));
        }
        catch (Exception e){Log.d("mylog",e.toString());}
        return null;
    }

}
