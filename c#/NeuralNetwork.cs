using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.IO;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace digitRecognizerAndroidTrain
{
    class NeuralNetwork
    {
        List<NetworkLayer> network;
        String[] path;

        /*functions*/
        public NeuralNetwork(int nLayer, int[] nNeuronForLayer, int[] nInputForNeuron, String[] path)
        {
            this.path = path;
            network = new List<NetworkLayer>();
            for (int i = 0; i < nLayer; i++)
            {
                network.Add(new NetworkLayer(nNeuronForLayer[i], nInputForNeuron[i]));
            }
        }

        public void loadInput(float[] value)
        {
            //normalize data
            value = normalizeInput(value);

            //loading data into network
            for (int i = 0; i < value.Length; i++)
            {
                network[0].getNeuron(i).Output = value[i];
            }
        }

        public float[] normalizeInput(float[] values)
        {
            //preparing data in range 0.01 to 0.99
            for (int i = 0; i < values.Length; i++)
            {
                values[i] = (float)((0.98 * values[i]) / 255 + 0.01);
            }

            return values;
        }

        public void runNetwork()
        {
            for (int i = 0; i < network.Count - 1; i++)
            {
                for (int j = 0; j < network[i + 1].nNeuron; j++)
                {
                    for (int k = 0; k < network[i + 1].getNeuron(j).nInput; k++)
                    {
                        network[i + 1].getNeuron(j).setinputValue(k, network[i].getNeuron(k).Output);
                    }
                    network[i + 1].getNeuron(j).processInput();
                }
            }
        }

        public void train(int nEpoch, float learningRate, int nTrainingSet)
        {
            float[] desiderateOutput;
            TrainingExample trainingExample;


            for (int i = 0; i < nEpoch; i++)
            {
                for (int j = 0; j < nTrainingSet; j++)
                {
                    //forward data
                    trainingExample = readTrainingSet(j);
                    loadInput(trainingExample.getData());
                    runNetwork();

                    //MessageBox.Show(path[j] + "=" + trainingExample.Target.ToString());

                    //calculate errors
                    desiderateOutput = new float[network[network.Count - 1].nNeuron];
                    for (int k = 0; k < desiderateOutput.Length; k++)
                    {
                        desiderateOutput[k] = 0;
                    }
                    if(trainingExample.Target<=9)
                        desiderateOutput[trainingExample.Target] = 1;
                    updateErrors(desiderateOutput);

                    //fix weights
                    adjustsWeights(learningRate);

                    //save net
                    saveNet();
                }
            }
        }

        public void adjustsWeights(float learningRate)
        {
            float newValue;
            float partialDerivate;

            for (int i = network.Count - 1; i > 0; i--)
            {
                for (int j = 0; j < network[i].nNeuron; j++)
                {
                    for (int k = 0; k < network[i].getNeuron(j).nInput; k++)
                    {
                        float sum = 0;
                        for (int z = 0; z < network[i].getNeuron(j).nInput; z++)
                        {
                            sum += network[i].getNeuron(j).getWeightValue(z) * network[i - 1].getNeuron(z).Output;
                        }

                        partialDerivate = -network[i].getNeuron(j).Error *
                            derivedActivationFunction(sum) *
                            network[i - 1].getNeuron(k).Output;

                        newValue = network[i].getNeuron(j).getWeightValue(k) - (learningRate * partialDerivate);
                        network[i].getNeuron(j).setInputWeight(k, newValue);
                    }
                }
            }
        }

        public void updateErrors(float[] desiderateOutput)
        {
            float sumWeightsNeuron;

            //calculating output layer errors
            for (int i = 0; i < network[network.Count - 1].nNeuron; i++)
            {
                network[network.Count - 1].getNeuron(i).Error = (float)desiderateOutput[i] - network[network.Count - 1].getNeuron(i).Output;
            }

            //calculating other layers errors
            for (int i = network.Count - 2; i > 0; i--)
            {
                for (int j = 0; j < network[i].nNeuron; j++)
                {
                    network[i].getNeuron(j).Error = 0;

                    //loop to sum all weights neuron
                    sumWeightsNeuron = 0;
                    for (int z = 0; z < network[i + 1].nNeuron; z++)
                    {
                        sumWeightsNeuron += network[i + 1].getNeuron(z).getWeightValue(j);

                    }

                    //loop per prendere i singoli pesi
                    for (int z = 0; z < network[i + 1].nNeuron; z++)
                    {
                        network[i].getNeuron(j).Error += (network[i + 1].getNeuron(z).getWeightValue(j) / sumWeightsNeuron) * network[i + 1].getNeuron(z).Error;
                    }

                }
            }
        }

        private float derivedActivationFunction(float outputValue)
        {
            return ((float)(1 / (1 + (1 / Math.Pow(Math.E, outputValue))))
                * (1 - (float)(1 / (1 + (1 / Math.Pow(Math.E, outputValue))))));
        }

        public TrainingExample readTrainingSet(int index)
        {
            TrainingExample trainingExample;
            float[] values;

            Bitmap bmp = new Bitmap("png/" + path[index]);
            values = new float[28 * 28];
            for (int i = 0; i < 28; i++)
            {
                for (int j = 0; j < 28; j++)
                {
                    values[i * 28 + j] = float.Parse(bmp.GetPixel(i, j).R.ToString());
                }
            }

            trainingExample = new TrainingExample(values, int.Parse(path[index].Split(new char[] { '_','.'})[1].ToString()));

            return trainingExample;
        }

        public TrainingExample readTrainingSet(String path)
        {
            TrainingExample trainingExample;
            float[] values;

            Bitmap bmp = new Bitmap("png/" + path);
            values = new float[28 * 28];
            for (int i = 0; i < 28; i++)
            {
                for (int j = 0; j < 28; j++)
                {
                    values[i * 28 + j] = float.Parse(bmp.GetPixel(i, j).R.ToString());
                }
            }

            trainingExample = new TrainingExample(values, int.Parse(path.Split(new char[] { '_', '.' })[1].ToString()));

            return trainingExample;
        }

        public float[] softmaxFunction(float[] values)
        {
            float denominator = 0;
            float[] outputProbalities = new float[values.Length];

            //calculate denominator
            for (int i = 0; i < values.Length; i++)
            {
                denominator += (float)Math.Exp(values[i]);
            }

            //calculate function
            for (int i = 0; i < values.Length; i++)
            {
                outputProbalities[i] = (float)Math.Exp(values[i]) / (denominator);
            }

            return outputProbalities;
        }

        public void saveNet()
        {
            //creo la cartella salvataggi
            Directory.CreateDirectory("netBackup");
            String[] files = Directory.GetFiles("netBackup");

            using (FileStream fs = new FileStream("./netBackup/rete" + files.Length.ToString() + ".csv", FileMode.Create, FileAccess.Write))
            using (StreamWriter sw = new StreamWriter(fs))
            {

                //stampo tutti i pesi 
                for (int i = 0; i < network.Count; i++)
                {
                    for (int j = 0; j < network[i].nNeuron; j++)
                    {
                        for (int k = 0; k < network[i].getNeuron(j).nInput; k++)
                        {
                            sw.Write(network[i].getNeuron(j).getWeightValue(k) + ";");
                        }
                    }
                }
            }
        }

        public void loadSavedNet(String path)
        {
            //carica la rete da salvataggio precedente
            using (FileStream fs = new FileStream("./netBackup/" + path, FileMode.Open, FileAccess.Read))
            using (StreamReader sr = new StreamReader(fs))
            {
                string app;
                app = sr.ReadLine();
                String[] split = app.Split(';');
                int sum = 0;
                for (int i = 0; i < network.Count; i++)
                {
                    for (int j = 0; j < network[i].nNeuron; j++)
                    {
                        for (int k = 0; k < network[i].getNeuron(j).nInput; k++)
                        {
                            network[i].getNeuron(j).setInputWeight(k, float.Parse(split[sum]));
                            sum++;
                        }
                    }
                }
            }
        }

        public int[] getPrediction()
        {
            int[] prediction = new int[network[network.Count - 1].nNeuron];
            List<Neuron> list = new List<Neuron>();

            for (int i = 0; i < network[network.Count - 1].nNeuron; i++)
            {
                list.Add(network[network.Count - 1].getNeuron(i));
            }

            list = list.OrderBy(output => output.Output).ToList();
            list.Reverse();

            for (int i = 0; i < network[network.Count - 1].nNeuron; i++)
            {
                prediction[i] = list[i].getIndex();
            }

            return prediction;
        }

        /*get and set method*/
        public NetworkLayer getLayer(int index) { return network[index]; }
        public int getNLayer() { return network.Count; }
    }
}
