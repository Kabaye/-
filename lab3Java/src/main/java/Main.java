import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Main {
    private static final Double X_SIZE = 1.0;
    private static final Double T_SIZE = 0.5;
    private static final Double H = 0.1;

    static BiFunction<Double, Double, Double> f = (x, t) -> -(2 + 3 * t * t); //F
    static Function<Double, Double> u0 = x -> x * x;  //u0
    static Function<Double, Double> u1 = t -> -t * t * t;  //u1
    static Function<Double, Double> u2 = t -> 1 - t * t * t; //u1
    static BiFunction<Double, Double, Double> exactSolution = (x, t) -> x * x - t * t * t; //Exact solution
    static Map.Entry<Double, Double> dimension = Map.entry(X_SIZE, T_SIZE); //dimension

    public static void main(String[] args) {
        Double[][] exactSolutionMatrix = initialiseExactSolutionMatrix(H, H, dimension);

        ExplicitMethod firstExplicitMethod = new ExplicitMethod(H, H, dimension, f, u0, u1, u2);
        Double[][] rm1 = firstExplicitMethod.getResult();

        System.out.println(String.format("Exact error (unstable explicit method): %.5f", countError(rm1, exactSolutionMatrix)));

        Double[][] exactSolutionMatrixSpecial = initialiseExactSolutionMatrix(H, H * H / 2, dimension);
        ExplicitMethod secondExplicitMethod = new ExplicitMethod(H, H * H / 2, dimension, f, u0, u1, u2);
        Double[][] rm2 = secondExplicitMethod.getResult();

        System.out.println(String.format("Exact error (stable explicit method): %.5f", countError(rm2, exactSolutionMatrixSpecial)));

        ImplicitMethod implicitMethod = new ImplicitMethod(H, H, dimension, f, u0, u1, u2);
        Double[][] rm3 = implicitMethod.getResult();

        System.out.println(String.format("Exact error (implicit method): %.5f", countError(rm3, exactSolutionMatrix)));

        CrankNicolsonMethod crankNicolsonMethod = new CrankNicolsonMethod(H, H, dimension, f, u0, u1, u2);
        Double[][] rm4 = crankNicolsonMethod.getResult();

        System.out.println(String.format("Exact error (Crank-Nicolson method): %.5f", countError(rm4, exactSolutionMatrix)));

        for (int i = 0; i < 11; i++) {
            System.out.println(String.format("%.4f", rm2[1][i]));
        }
        System.out.println();
        for (int i = 0; i < 11; i++) {
            System.out.println(String.format("%.4f", exactSolutionMatrixSpecial[1][i]));
        }
    }

    private static Double[][] initialiseExactSolutionMatrix(Double h, Double tau, Map.Entry<Double, Double> dimension) {
        int n1 = (int) (dimension.getKey() / h) + 1;
        int n2 = (int) (dimension.getValue() / tau) + 1;
        Double[][] rm = new Double[n2][n1];

        for (int i = 0; i < rm.length; i++) {
            for (int j = 0; j < rm[i].length; j++) {
                rm[i][j] = exactSolution.apply(j * h, (n2 - 1 - i) * tau);
            }
        }
        return rm;
    }

    private static double countError(Double[][] resultMatrix, Double[][] exactSolutionMatrix) {
        double error = 0d;
        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix[i].length; j++) {
                error = Math.max(error, Math.abs(resultMatrix[i][j] - exactSolutionMatrix[i][j]));
            }
        }
        return error;
    }
}
