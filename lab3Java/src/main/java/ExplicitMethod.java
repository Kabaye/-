import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExplicitMethod {
    final private Double h, tau;
    final private Integer N1, N2;
    final private BiFunction<Double, Double, Double> f;
    final private Function<Double, Double> u0, u1, u2;

    //result matrix
    private Double[][] rm;

    public ExplicitMethod(Double h, Double tau, Map.Entry<Double, Double> dimension, BiFunction<Double, Double, Double> f,
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

        double r = 1 / (h * h);
        double k = 1 / tau - 2 * r;

        for (int i = N2 - 2; i >= 0; i--) {
            for (int j = 1; j < N1 - 1; j++) {
                rm[i][j] = tau * (k * rm[i + 1][j] + r * rm[i + 1][j + 1] + r * rm[i + 1][j - 1] + f.apply(j * h, (N2 - i - 1) * tau));
            }
        }
    }

    public Double[][] getResult() {
        return rm;
    }
}
