using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;


namespace digitRecognizerAndroidTrain
{
    class Neuron
    {
        float output;
        float error;
        int index;
        List<float> inputWeights;
        List<float> inputValues;

        /*functions*/
        public Neuron(int nInput)
        {
            inputWeights = new List<float>(nInput);
            inputValues = new List<float>(nInput);

            Random rand = new Random(Guid.NewGuid().GetHashCode());
            Random r = new Random(Guid.NewGuid().GetHashCode());
            for (int i = 0; i < nInput; i++)
            {
                float val;
                int v;
                do
                {
                    val = (float)rand.NextDouble();

                } while (val > (1 / Math.Pow(nInput, 0.5)) || val == 0);

                v = r.Next(1, 50);
                if (v > 25) { inputWeights.Add(-val); }
                else { inputWeights.Add(val); }
                inputValues.Add(0f);

            }
        }

        private void activationFunction(float outputValue)
        {
            output = (float)(1 / (1 + 1 / Math.Pow(Math.E, outputValue)));
            //output = (float)Math.Tanh(outputValue);
        }

        public void processInput()
        {
            float sum = 0;

            for (int i = 0; i < inputValues.Count; i++)
            {
                sum += inputValues[i] * inputWeights[i];
            }

            activationFunction(sum);
        }

        /*get and set function*/

        public void setIndex(int index) { this.index = index; }

        public int getIndex() { return index; }

        public void setInputWeight(int index, float newWeight)
        {
            inputWeights[index] = newWeight;
        }

        public float getWeightValue(int index)
        {
            return inputWeights[index];
        }

        public float getInputValue(int index)
        {
            return inputValues[index];
        }

        public void setinputValue(int index, float newInputValues)
        {
            inputValues[index] = newInputValues;
        }

        public float Error { get { return error; } set { error = value; } }

        public int nInput { get { return inputValues.Count; } }

        public float Output { get { return output; } set { output = value; } }
    }
}