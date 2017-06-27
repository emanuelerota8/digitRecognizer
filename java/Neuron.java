package com.emanuelerota.drawingtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Emanuele Rota on 27/02/2017.
 */

public class Neuron {
    float output;
    float error;
    int index;
    List<Float> inputWeights;
    List<Float> inputValues;

    /*functions*/
    public Neuron(int nInput)
    {
        inputValues=new ArrayList<>();
        inputWeights=new ArrayList<>();;
        for (int i = 0; i < nInput; i++)
        {
            inputValues.add(0f);
            inputWeights.add(0f);

        }
    }

    private void activationFunction(float outputValue)
    {
        output = (float)(1 / (1 + 1 / Math.pow(Math.E, outputValue)));
    }

    public void processInput()
    {
        float sum = 0;

        for(int i = 0; i < inputValues.size(); i++)
        {
            sum += inputValues.get(i) * inputWeights.get(i);
        }

        activationFunction(sum);
    }


    /*get and set function*/

    public void setIndex(int index) { this.index = index; }

    public int getIndex() { return index; }

    public void setInputWeight(int index, float newWeight)
    {
        inputWeights.set(index,newWeight);
    }

    public float getWeightValue(int index)
    {
        return inputWeights.get(index);
    }

    public float getInputValue(int index)
    {
        return inputValues.get(index);
    }

    public void setinputValue(int index, float newInputValues)
    {
        inputValues.set(index, newInputValues);
    }

    /*public float Error { get { return error; } set { error = value; } }*/

    public int nInput() { return inputValues.size();  }

    public float getOutput() { return output;}
    public void setOutput(float value){output=value;}
}

