import java.util.Arrays;

public class Matrix {

	double[][] data;
	int rows, cols;

	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		data = new double[rows][cols];
	}

	// create a matrix from linear array
	public Matrix(double[] x) {
		this(x.length, 1);
		for (int i = 0; i < rows; i++)
			data[i][0] = x[i];
	}

	// create a matrix from a 2D array
	public Matrix(double[][] x) {
		this(x.length, x[0].length);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				data[i][j] = x[i][j];
	}

	// randomize a single weight or bias value
	public double randomize() {
		return Math.random() * 2 - 1;
	}

	// initialize matrix with random doubles
	// between -1.0 and 1.0 exclusive
	public void scramble() {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				data[i][j] = randomize();
	}

	// add scalar quantity (bias)
	public void add(double scalar) {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				data[i][j] += scalar;
	}

	// add another matrix to this matrix
	public void add(Matrix m) {
		if (cols != m.cols || rows != m.rows) {
			System.out.println("Shape Mismatch");
			return;
		}

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				data[i][j] += m.data[i][j];
	}

	// element wise multiplication with matrix a
	// (Hadamard product)
	public void multiply(Matrix a) {
		if (a.rows > rows || a.cols > cols) {
			System.out.println("Shape Mismatch");
			return;
		}

		for (int i = 0; i < a.rows; i++)
			for (int j = 0; j < a.cols; j++)
				data[i][j] *= a.data[i][j];
	}

	// multiply by scalar
	public void multiply(double a) {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				data[i][j] *= a;
	}

	// pass every element of matrix into sigmoid function
	public void sigmoid() {
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				data[i][j] = 1 / (1 + Math.exp(-data[i][j]));
	}

	// return new matrix containing derivative of sigmoid
	public Matrix dsigmoid() {
		Matrix ret = new Matrix(rows, cols);
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				ret.data[i][j] = data[i][j] * (1 - data[i][j]);
		return ret;
	}

	// return matrix as a linear array
	public double[] toArray() {
		double[] ret = new double[rows * cols];
		int pos = 0;

		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				ret[pos++] = data[i][j];
		return ret;
	}

	/*
	 * Mutate the matrix with a probability of a mutation happening as 1/s where s
	 * is the number of cells in the matrix
	 */
	public void mutate() {
		double threshold = (1 + 0.0) / (rows * cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// mutation occurs
				if (Math.random() < threshold) {
					data[i][j] = randomize();
				}
			}
		}
	}

	// returns a copy of this Matrix
	public Matrix copy() {
		return new Matrix(this.data);
	}

	public String toString() {
		if (cols == 1)
			return Arrays.toString(data[0]);
		return Arrays.deepToString(data);
	}

	/*
	 * 
	 * Static methods
	 * 
	 */

	// return matrix a - matrix b
	// returns null if operation is invalid
	public static Matrix subtract(Matrix a, Matrix b) {
		if (b.rows > a.rows || b.cols > a.cols)
			return null;
		Matrix ret = new Matrix(a.rows, a.cols);
		for (int i = 0; i < a.rows; i++)
			for (int j = 0; j < a.cols; j++)
				ret.data[i][j] = a.data[i][j] - b.data[i][j];
		return ret;
	}

	// transpose matrix
	public static Matrix transpose(Matrix a) {
		Matrix ret = new Matrix(a.cols, a.rows);
		for (int i = 0; i < a.rows; i++)
			for (int j = 0; j < a.cols; j++)
				ret.data[j][i] = a.data[i][j];
		return ret;
	}

	// return dot product of matrix a and matrix b
	public static Matrix multiply(Matrix a, Matrix b) {
		if (a.cols != b.rows)
			return null;
		Matrix ret = new Matrix(a.rows, b.cols);

		for (int i = 0; i < ret.rows; i++)
			for (int j = 0; j < ret.cols; j++)
				for (int k = 0; k < a.cols; k++)
					ret.data[i][j] += a.data[i][k] * b.data[k][j];

		return ret;
	}

	public static Matrix fromArray(double[] x) {
		Matrix temp = new Matrix(x.length, 1);
		for (int i = 0; i < x.length; i++)
			temp.data[i][0] = x[i];
		return temp;
	}

	/*
	 * Get the child of two parent matrices
	 * 
	 * The two matrices must have the same number of rows and columns
	 * 
	 * Each cell of the child matrix will have equal chance of receiving a value
	 * from parent 1 or parent 2
	 */
	public static Matrix breed(Matrix p1, Matrix p2) {
		if (p1.rows != p2.rows || p1.cols != p2.cols)
			return null;

		Matrix child = new Matrix(p1.rows, p1.cols);
		for (int i = 0; i < child.rows; i++)
			for (int j = 0; j < child.cols; j++)
				child.data[i][j] = Math.random() < 0.5 ? p1.data[i][j] : p2.data[i][j];
		
		return child;
	}

}
