import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Model {

	static final double l_rate = 0.01;

	int numLayers;
	int[] layerSizes;
	Matrix[] weights, bias;

	/*
	 * Assumes layerSizes[0] is the # of neurons in input layer and layerSizes[n-1]
	 * is the # of neurons in output layer
	 */
	public Model(int[] layerSizes) {
		// no input or output layer
		if (layerSizes.length < 2)
			throw new IllegalArgumentException("number of layers must be 2 or more");

		numLayers = layerSizes.length;
		this.layerSizes = new int[numLayers];
		for (int i = 0; i < numLayers; i++)
			this.layerSizes[i] = layerSizes[i];

		// initialize biases
		// biases are associated with neurons in hidden and output layers
		bias = new Matrix[numLayers - 1];
		for (int i = 1; i < numLayers; i++) {
			bias[i - 1] = new Matrix(layerSizes[i], 1);
			bias[i - 1].scramble(); // randomize values
		}

		// initialize weights
		// weights are associated between layers
		weights = new Matrix[numLayers - 1];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = new Matrix(layerSizes[i + 1], layerSizes[i]);
			weights[i].scramble(); // randomize values
		}
	}

	/*
	 * Initialize neural network from existing neural network
	 */
	public Model(int[] layerSizes, Matrix[] weights, Matrix[] bias) {
		// no input or output layer
		if (layerSizes.length < 2)
			throw new IllegalArgumentException("number of layers must be 2 or more");

		numLayers = layerSizes.length;
		this.layerSizes = new int[numLayers];
		for (int i = 0; i < numLayers; i++)
			this.layerSizes[i] = layerSizes[i];

		// copy weights and biases
		this.bias = new Matrix[numLayers - 1];
		this.weights = new Matrix[numLayers - 1];
		for (int i = 0; i < numLayers - 1; i++) {
			this.bias[i] = bias[i].copy();
			this.weights[i] = weights[i].copy();
		}
	}

	/*
	 * Reads in neural net from a text file
	 */
	public Model(String fileName) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			numLayers = Integer.parseInt(in.readLine());

			// reads in size of each layer
			layerSizes = new int[numLayers];
			StringTokenizer str = new StringTokenizer(in.readLine());
			for (int i = 0; i < numLayers; i++)
				layerSizes[i] = Integer.parseInt(str.nextToken());

			bias = new Matrix[numLayers - 1];
			weights = new Matrix[numLayers - 1];
			// reads in weights and biases for neurons
			for (int i = 0; i < numLayers - 1; i++) { // each layer
				int M = layerSizes[i + 1]; // # of neurons in this layer
				int K = layerSizes[i]; // # of neurons in previous layer
				double[][] lbiases = new double[M][1];
				double[][] lweights = new double[M][K];
				for (int j = 0; j < M; j++) { // each neuron
					str = new StringTokenizer(in.readLine());
					lbiases[j][0] = Double.parseDouble(str.nextToken()); // biases to this neuron
					for (int k = 0; k < K; k++) // weights to this neuron
						lweights[j][k] = Double.parseDouble(str.nextToken());
				}
				bias[i] = new Matrix(lbiases);
				weights[i] = new Matrix(lweights);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// predicts values in all layers of model
	private Matrix[] predictLayers(double[] input) {
		Matrix[] layers = new Matrix[numLayers];

		// input layer
		layers[0] = Matrix.fromArray(input);

		for (int i = 1; i < numLayers; i++) {
			layers[i] = Matrix.multiply(weights[i - 1], layers[i - 1]);
			layers[i].add(bias[i - 1]);
			layers[i].sigmoid();
		}

		return layers;
	}

	// returns only output layer after prediction
	public Matrix predict(double[] input) {
		return predictLayers(input)[numLayers - 1];
	}

	public void fit(double[][] X, double[][] Y, int epochs) {
		// train epoch number of samples
		for (int i = 0; i < epochs; i++) {
			// train random sample of data
			int sampleN = (int) (Math.random() * X.length);
			this.train(X[sampleN], Y[sampleN]);
		}
	}

	public void train(double[] X, double[] Y) {

		Matrix[] layers = predictLayers(X);
		Matrix target = Matrix.fromArray(Y);
		Matrix errors = Matrix.subtract(target, layers[numLayers - 1]);

		// back-propagation
		for (int i = numLayers - 1; i > 0; i--) {
			Matrix gradient = layers[i].dsigmoid();
			gradient.multiply(errors);
			gradient.multiply(l_rate);

			Matrix lTranspose = Matrix.transpose(layers[i - 1]); // transpose layer
			Matrix weightDelta = Matrix.multiply(gradient, lTranspose);

			weights[i - 1].add(weightDelta);
			bias[i - 1].add(gradient);

			// calculate error for next iteration
			Matrix wTranspose = Matrix.transpose(weights[i - 1]); // transpose weight connection matrix
			errors = Matrix.multiply(wTranspose, errors);
		}

	}

	/*
	 * Save neuron weights and biases in text file First line of text file is "N"
	 * where N is # of layers in this model
	 * 
	 * Second line of text file is "s0 s1 s2 ... sN" where "s" is the # of neurons
	 * in a particular layer
	 * 
	 * Next input will apply to N-1 layers starting with first hidden layer and
	 * ending with the output layer
	 * 
	 * For each layer: Each of the next K lines will be in the form
	 * "b w1 w2 w3 ... wM" where "b" is the bias of that neuron and w1 is a weight
	 * from the previous layer to the neuron
	 * 
	 */
	public void saveValuesToFile() {
		try {
			PrintWriter out = new PrintWriter(new File("nn.ylnn"));
			out.println(numLayers);
			for (int i = 0; i < numLayers; i++)
				out.print(layerSizes[i] + " ");
			out.println();
			for (int i = 0; i < numLayers - 1; i++) { // each layer
				for (int j = 0; j < layerSizes[i + 1]; j++) { // each neuron in that layer
					out.print(bias[i].data[j][0] + " ");
					for (int k = 0; k < layerSizes[i]; k++)
						out.print(weights[i].data[j][k] + " ");
					out.println();
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// gets exact replica of this neural network
	public Model copy() {
		return new Model(this.layerSizes, this.weights, this.bias);
	}

	// derive a single child from this neural network
	public Model deriveChild() {
		Model child = this.copy();
		for (int i = 0; i < numLayers - 1; i++) {
			child.bias[i].mutate();
			child.weights[i].mutate();
		}
		return child;
	}

	// derive multiple children from this neural network
	public Model[] deriveChildren(int numChildren) {
		Model[] children = new Model[numChildren];
		for (int i = 0; i < numChildren; i++) {
			children[i] = deriveChild();
		}
		return children;
	}

	// check if two neural networks have the same structure
	public static boolean sameStructure(Model a, Model b) {
		if (a.layerSizes.length == b.layerSizes.length) {
			for (int i = 0; i < a.layerSizes.length; i++)
				if (a.layerSizes[i] != b.layerSizes[i])
					return false;
			return true;
		}

		return false;
	}

	// derive a child from two neural networks
	public static Model deriveChild(Model p1, Model p2) {
		// check if two neural networks are same in structure
		if (!sameStructure(p1, p2))
			return null;

		Matrix[] childBiases = new Matrix[p1.numLayers - 1];
		Matrix[] childWeights = new Matrix[p1.numLayers - 1];

		for (int i = 0; i < p1.numLayers - 1; i++) {
			childBiases[i] = Matrix.breed(p1.bias[i], p2.bias[i]);
			childWeights[i] = Matrix.breed(p1.weights[i], p2.weights[i]);
			childBiases[i].mutate();
			childWeights[i].mutate();
		}
		return new Model(p1.layerSizes, childWeights, childBiases);
	}

	// derive a child from two neural networks
	public static Model[] deriveChildren(Model p1, Model p2, int numChildren) {
		// check if two neural networks are same in structure
		if (!sameStructure(p1, p2))
			return null;
		Model[] children = new Model[numChildren];

		for(int i= 0; i < numChildren; i++) {
			children[i] = deriveChild(p1, p2);
		}
		
		return children;
	}

}