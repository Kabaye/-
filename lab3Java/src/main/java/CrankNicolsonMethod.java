import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CrankNicolsonMethod {
    final private Double h, tau;
    final private Integer N1, N2;
    final private BiFunction<Double, Double, Double> f;
    final private Function<Double, Double> u0, u1, u2;

    //result matrix
    private Double[][] rm;

    public CrankNicolsonMethod(Double h, Double tau, Map.Entry<Double, Double> dimension, BiFunction<Double, Double, Double> f,
                               Function<Double, Double> u0, Function<Double, Double> u1, Function<Double, Double> u2) {
        this.h = h;
        this.tau = tau;
        this.f = f;
        this.u0 = u0;
        this.u1 = u1;
        this.u2 = u2;

        N1 = (int) (dimension.getKey() / h) + 1;
        N2 = (int) (dimension.getValue() / tau) + 1;
        rm = new Double[N2][N1];

        doAlgorithm();
    }

    private void doAlgorithm() {
        for (int i = 0; i < N1; i++) {
            rm[N2 - 1][i] = u0.apply(i * h);
        }

        for (int i = 1; i < N2; i++) {
            rm[N2 - 1 - i][0] = u1.apply(i * tau);
            rm[N2 - 1 - i][N1 - 1] = u2.apply(i * tau);
        }

        for (int i = N2 - 2; i >= 0; i--) {
            rm[i] = doDiagonalMatrixAlgorithm(i);
        }
    }

    private Double[] doDiagonalMatrixAlgorithm(int i) {
        Double[][] A = new Double[N1][N1];
        Double[] B = new Double[N1];

        A[0][0] = 1.0;
        A[0][1] = 0.0;
        B[0] = rm[i][0];

        A[N1 - 1][N1 - 1] = 1.0;
        A[N1 - 1][N1 - 2] = 0.0;
        B[N1 - 1] = rm[i][N1 - 1];

        double r = 1 / (h * h);
        double k = 1 / tau + r;
        for (int j = 1; j < N1 - 1; j++) {
            A[j][j - 1] = -r / 2;
            A[j][j] = k;
            A[j][j + 1] = -r / 2;

            B[j] = f.apply(j * h, (N2 - i - 1) * tau) + tau / 2 * (-6 * (N2 - i - 1) * tau) + rm[i + 1][j] / tau +
                    r / 2 * (rm[i + 1][j + 1] - 2 * rm[i + 1][j] + rm[i + 1][j - 1]);
        }
        final TridiagonalMatrixAlgorithm result = new TridiagonalMatrixAlgorithm(A, B);
        return result.getResult();
    }

    public Double[][] getResult() {
        return rm;
    }
}
