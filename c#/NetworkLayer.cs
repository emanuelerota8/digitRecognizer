using System.Collections.Generic;

namespace digitRecognizerAndroidTrain
{
    class NetworkLayer
    {
        List<Neuron> layer;

        /*Functions*/
        public NetworkLayer(int nNeuron, int nInput)
        {
            layer = new List<Neuron>();
            for (int i = 0; i < nNeuron; i++)
            {
                Neuron n = new Neuron(nInput);
                n.setIndex(i);
                layer.Add(n);
            }
        }

        /*get and set function*/
        public Neuron getNeuron(int index)
        {
            return layer[index];
        }

        public int nNeuron { get { return layer.Count; } }

    }
}
