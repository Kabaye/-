package by.kulich.fpm;

public class Main {
    private final static double A = -1D;
    private final static double B = 1D;

    private final static double MU0 = 0.9625;
    private final static double SIGMA0 = 0.025 * Math.exp(-1F / 3);

    private final static double MU1 = 0.0375;
    private final static double SIGMA1 = -0.025 * Math.exp(1F / 3);

    private final static double h1 = 0.05;
    private final static int SIZE1 = (int) ((B - A) / h1) + 1;

    private final static double h2 = 0.025;
    private final static int SIZE2 = (int) ((B - A) / h2) + 1;

    public static void main(String[] args) {
        double[][] a = matrixGenerator(h1, SIZE1);

        double[][] b = matrixGenerator(h2, SIZE2);

        double[] answer1 = doProgonka(a, SIZE1);

        double[] answer2 = doProgonka(b, SIZE2);

        for (int i = 0; i < SIZE1; i++) {
            System.out.println("Y" + i + " = " + String.format("%.5f", answer1[i]));
        }

        rungeError(answer2, SIZE2, answer1, SIZE1);
    }

    public static double[] doProgonka(double[][] matrix, int size) {
        /* Прямая прогонка */

        double[] y = new double[size];
        double[] alpha = new double[size];
        double[] betta = new double[size];

        y[0] = matrix[0][0];
        alpha[1] = -matrix[0][1] / y[0];
        betta[1] = matrix[0][size] / y[0];

        for (int i = 1; i < size - 1; i++) {
            y[i] = matrix[i][i] + matrix[i][i - 1] * alpha[i-1];
            alpha[i] = -matrix[i][i + 1] / y[i];
            betta[i] = (matrix[i][size] - matrix[i][i - 1] * betta[i - 1]) / y[i];
        }

        y[size - 1] = matrix[size - 1][size - 1] + matrix[size - 1][size - 2] * alpha[size - 1];
        betta[size - 1] = (matrix[size - 1][size] - matrix[size - 1][size - 2] * betta[size - 1]) / y[size - 1];

        /*Обратный ход*/

        double[] x = new double[size];
        x[size - 1] = betta[size - 1];
        for (int i = size - 2; i > 1; i--) {
            x[i] = alpha[i] * x[i + 1] + betta[i];
        }

        return x;
    }

    public static void rungeError(double[] answer1, int size1, double[] answer2, int size2) {
        double max = 0;
        for (int i = 0; i < size2; i++) {
            max = Math.max(max, Math.abs(answer2[i] - answer1[2 * i]) / 3);
        }

        System.out.println("RungeError max(|answer1(xi)-answer2(xi)|/3)     =     " + max);
    }

    public static double[][] matrixGenerator(Double h, int size) {

        double[][] matrix = new double[size][size + 1];

        matrix[0][0] = -SIGMA0 - 1 / h;
        matrix[0][1] = 1 / h;

        matrix[0][size] = MU0;

        double xi = -1D + h;
        int i;
        for (i = 1; i < size - 1; i++) {
            matrix[i][i - 1] = -1 / (h * h) - xi / (2 * h);
            matrix[i][i] = 2 / h + Math.exp(xi / 3);
            matrix[i][i + 1] = -1 / (h * h) + xi / (2 * h);

            matrix[i][size] = xi / 2 + 1;

            xi += h;
        }

        matrix[size - 1][size - 2] = -1 / h;
        matrix[size - 1][size - 1] = -SIGMA1 + 1 / h;

        matrix[size - 1][size] = MU1;

        return matrix;
    }
}
