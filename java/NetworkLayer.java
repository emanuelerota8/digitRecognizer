package com.emanuelerota.drawingtest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emanuele Rota on 27/02/2017.
 */

public class NetworkLayer {
    List<Neuron> layer;

    /*Functions*/
    public NetworkLayer(int nNeuron, int nInput)
    {
        layer=new ArrayList<>();
        for(int i = 0; i < nNeuron; i++)
        {
            Neuron n = new Neuron(nInput);
            n.setIndex(i);
            layer.add(n);
        }
    }

    /*get and set function*/
    public Neuron getNeuron(int index)
    {
        return layer.get(index);
    }

    public int nNeuron() {  return layer.size();}
}

