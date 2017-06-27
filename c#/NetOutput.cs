using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace digitRecognizerAndroidTrain
{
    class NetOutput
    {
        public int neuron;
        public float outputNeuron;
        public float normalizedOutput;
        public float percentage;

        public NetOutput(int neuron, float outputNeuron, float normalizedOutput, float percentage)
        {
            this.neuron = neuron;
            this.outputNeuron = outputNeuron;
            this.normalizedOutput = normalizedOutput;
            this.percentage = percentage;
        }
    }
}
