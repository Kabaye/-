import java.util.*;
import java.util.function.Function;

public class Pryamougolniki {

    private final static Double H = 0.05;
    private final static Double H_RUNGE = H * 2;
    private final static Integer b = 2;
    private final static Integer a = 1;
    private final static Integer N = (int) ((b - a) / H);
    private final static Integer N_RUNGE = (int) ((b - a) / H_RUNGE);

    static Function<Double, Double> realFunc = t -> t * Math.exp(t);
    static Function<Double, Double> derivative = t -> (t + 1) * Math.exp(t);

    /**
     * Мы преобразовали наше ДУ 2 порядка к системе из 2 ДУ, причем
     * u2 = (t^2-2t)/t^2*u2+(3t+2)/t^2*u1 = f2(t, u1, u2)
     * И обозначим u2 за g, u1 за y
     */

    static ThreeFunction f2 = (t, y, g) -> (1 - 2 / t) * g + (3 / t + 2 / (t * t)) * y;

    /**
     * U2n+1/2 обозначим как Gn+1/2 = Gn+h/2*Fn
     */
    static ThreeFunction gn_1_2 = (Gn, Fn, h) -> Gn + (h / 2) * Fn;

    /**
     * U1n+1/2 обозначим как Yn+1/2 = Yn+h/2*Gn
     */

    static ThreeFunction yn_1_2 = (Yn, Gn, h) -> Yn + (h / 2) * Gn;

    /**
     * Gn+1 = U2n+1 = Gn+h * Fn+1/2
     * Yn+1 = Yn+h * Gn+1/2
     */

    static ThreeFunction gn_1 = (Gn, Fn_1_2, h) -> Gn + h * Fn_1_2;
    static ThreeFunction yn_1 = (Yn, Gn_1_2, h) -> Yn + h * Gn_1_2;

    public static void main(String[] args) {
        Double[][] statistics = algorithm(H, N);

        Double[][] statisticsRunge = algorithm(H_RUNGE, N_RUNGE);

        beautyPrint(statistics, statisticsRunge);

    }

    private static Double[][] algorithm(Double h, Integer n) {
        Double[][] statistics = new Double[5][n + 1];
        Double Fi, Gi = 2 * Math.E, Yi = Math.E, Gi_1_2, Yi_1_2, Fi_1_2, t = 1D;
        for (int i = 0; i <= n; i++) {
            /* Считаем Fi*/
            Fi = f2.apply(t, Yi, Gi);
            Gi_1_2 = gn_1_2.apply(Gi, Fi, h);
            Yi_1_2 = yn_1_2.apply(Yi, Gi, h);
            Fi_1_2 = f2.apply(t + h / 2, Yi_1_2, Gi_1_2);
            statistics[0][i] = t;
            statistics[1][i] = Yi;
            statistics[2][i] = Gi;
            statistics[3][i] = realFunc.apply(t);
            statistics[4][i] = derivative.apply(t);
            Yi = yn_1.apply(Yi, Gi_1_2, h);
            Gi = gn_1.apply(Gi, Fi_1_2, h);
            t = t + h;
        }
        return statistics;
    }

    private static void beautyPrint(Double[][] stat, Double[][] statRunge) {
        StringJoiner t = new StringJoiner(" ", "t    =    ", ";");
        StringJoiner u1 = new StringJoiner(" ", "u1(t)  =  ", ";");
        StringJoiner u2 = new StringJoiner(" ", "u2(t)  =  ", ";");
        StringJoiner u = new StringJoiner(" ", "u(t)   =  ", ";");
        StringJoiner u_ = new StringJoiner(" ", "u'(t)  =  ", ";");

        List<Double> errors = new ArrayList<>();
        List<Double> rungeErrors = new ArrayList<>();
        for (int i = 0; i < stat[0].length; i++) {
            String[] s = beautifyStrings(String.format("%.2f", stat[0][i]),
                    String.format("%.5f", stat[1][i]),
                    String.format("%.5f", stat[2][i]),
                    String.format("%.5f", stat[3][i]),
                    String.format("%.5f", stat[4][i]));
            t.add(s[0]);
            u1.add(s[1]);
            u2.add(s[2]);
            u.add(s[3]);
            u_.add(s[4]);
            errors.add(Math.max(Math.abs(stat[1][i] - stat[3][i]), Math.abs(stat[2][i] - stat[4][i])));
        }

        for (int i = 0; i < N_RUNGE; i++) {
            rungeErrors.add(Math.abs(statRunge[1][i] - stat[1][i * 2]) / 3);
        }

        System.out.println(t.toString());
        System.out.println(u1.toString());
        System.out.println(u2.toString());
        System.out.println(u.toString());
        System.out.println(u_.toString());
        System.out.println();
        System.out.println("Errors: ");
        String error = "max(|u-u1|,|u'-u2|) = " + String.format("%.5f",errors.stream().max(Double::compare).get());
        String errorRunge = "RungeError      =     " + String.format("%.5f",rungeErrors.stream().max(Double::compare).get());
        System.out.println(errorRunge);
        System.out.println(error);
    }

    private static String[] beautifyStrings(String... args) {
        String[] s = new String[args.length];

        System.arraycopy(args, 0, s, 0, args.length);

        int max = Arrays.stream(s).map(String::length).max(Comparator.naturalOrder()).get();
        for (int j = 0; j < args.length; j++) {
            for (int i = 0; i < max - args[j].length(); i++) {
                s[j] += " ";
            }
        }
        return s;
    }

}