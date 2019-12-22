public class TridiagonalMatrixAlgorithm {
    private final Double[][] A;
    private final Double[] B;
    private final int size;
    private Double[] result;

    public TridiagonalMatrixAlgorithm(Double[][] A, Double[] B) {
        this.A = A;
        this.B = B;
        size = A[0].length;
        result = new Double[size];

        doAlgorithm();
    }

    private void doAlgorithm() {
        Double[] alpha = new Double[size];
        Double[] betta = new Double[size];
        double z;

        alpha[1] = A[0][1] / A[0][0];
        betta[1] = B[0] / A[0][0];

        for (int i = 1; i < size - 1; i++) {
            z = A[i][i] + A[i][i - 1] * alpha[i];
            alpha[i + 1] = -A[i][i + 1] / z;
            betta[i + 1] = (B[i] - A[i][i - 1] * betta[i]) / z;
        }

        result[size - 1] = (B[size - 1] - A[size - 1][size - 2] * betta[size - 1])
                / (A[size - 1][size - 1] + A[size - 1][size - 2] * alpha[size - 1]);

        for (int i = size - 2; i >= 0; i--) {
            result[i] = alpha[i + 1] * result[i + 1] + betta[i + 1];
        }
    }

    public Double[] getResult() {
        return result;
    }
}
