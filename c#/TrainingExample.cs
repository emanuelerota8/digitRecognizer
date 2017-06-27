namespace digitRecognizerAndroidTrain
{
    class TrainingExample
    {
        int target;
        float[] data;

        public TrainingExample(float[] values, int desiderateOutput)
        {
            data = values;
            target = desiderateOutput;
        }

        public int Target { get { return target; } set { target = value; } }

        public float[] getData() { return data; }
        public void setData(float[] values) { data = values; }
    }
}
