import java.util.*;
import java.util.function.Function;

public class Adams2 {
    private final static Double H = 0.05;
    private final static Integer b = 2;
    private final static Integer a = 1;
    private final static Integer N = (int) ((b - a) / H);

    static Function<Double, Double> realFunc = t -> t * Math.exp(t);
    static Function<Double, Double> derivative = t -> (t + 1) * Math.exp(t);

    /**
     * Мы преобразовали наше ДУ 2 порядка к системе из 2 ДУ, причем
     * u2 = (t^2-2t)/t^2*u2+(3t+2)/t^2*u1 = f2(t, u1, u2)
     * И обозначим u2 за g, u1 за y
     */

    static ThreeFunction f2 = (t, /* U1(t) */ y, /* U2(t) */ g) -> (1 - 2 / t) * g + (3 / t + 2 / (t * t)) * y;

    /**
     * Gn+1 = U2n+1 = Gn + h / 2 * (3 * Fn - Fn__1)
     * Yn+1 = U1n+1 = Yn + h / 2 * (3 * Gn - Gn__1)
     */

    static ThreeFunction gn_1 = (Gn, Fn, /* Fn-1 */ Fn__1) -> Gn + (H / 2) * (3 * Fn - Fn__1);
    static ThreeFunction yn_1 = (Yn, Gn, /* Gn-1 */ Gn__1) -> Yn + (H / 2) * (3 * Gn - Gn__1);

    public static void main(String[] args) {
        beautyPrint(algorithm());
    }

    private static Double[][] algorithm() {
        Double[][] statistics = new Double[5][N + 1];

        //тк метод 2 порядка, нам нужно знать Fn и Fn-1, Поэтому чтобы начать алгоритм адамса изначально нужно знать F0 = U2_0, F1 = U2_1
        //Обозначим y = u1; g = u2 = y';
        //Возьмем G1 и Y1 из предыдущего метода.

        Double Fi, Gi = 5.85765, /* Gi-1 */ Gi__1 = 2 * Math.E, Yi = 3.00030, /* Yi-1 */ Yi__1 = Math.E, t = 1D,/* Fi-1 */ Fi__1;

        statistics[0][0] = t;
        statistics[1][0] = Yi__1;
        statistics[2][0] = Gi__1;
        statistics[3][0] = realFunc.apply(t);
        statistics[4][0] = derivative.apply(t);

        //считаем F0
        Fi__1 = f2.apply(t, Yi__1, Gi__1);

        t = t + H;

        //F1
        Fi = f2.apply(t, Yi, Gi);

        statistics[0][1] = t;
        statistics[1][1] = Yi;
        statistics[2][1] = Gi;
        statistics[3][1] = realFunc.apply(t);
        statistics[4][1] = derivative.apply(t);

        for (int i = 2; i <= N; i++) {
            t = t + H;

            //нахожу Yn+1
            Yi = yn_1.apply(Yi, Gi, Gi__1);

            //Запоминаю Gi-1
            Gi__1 = Gi;

            //нахожу Gn+1
            Gi = gn_1.apply(Gi, Fi, Fi__1);

            //Запоминаю Fi-1
            Fi__1 = Fi;

            //Считаю Fn+1
            Fi = f2.apply(t, Yi, Gi);

            statistics[0][i] = t;
            statistics[1][i] = Yi;
            statistics[2][i] = Gi;
            statistics[3][i] = realFunc.apply(t);
            statistics[4][i] = derivative.apply(t);
        }
        return statistics;
    }

    private static void beautyPrint(Double[][] stat) {
        StringJoiner t = new StringJoiner(" ", "t    =    ", ";");
        StringJoiner u1 = new StringJoiner(" ", "u1(t)  =  ", ";");
        StringJoiner u2 = new StringJoiner(" ", "u2(t)  =  ", ";");
        StringJoiner u = new StringJoiner(" ", "u(t)   =  ", ";");
        StringJoiner u_ = new StringJoiner(" ", "u'(t)  =  ", ";");

        List<Double> errors = new ArrayList<>();
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

        System.out.println(t.toString());
        System.out.println(u1.toString());
        System.out.println(u2.toString());
        System.out.println(u.toString());
        System.out.println(u_.toString());
        System.out.println();
        System.out.println("Errors: ");
        String error = "max(|u-u1|,|u'-u2|) = " + String.format("%.5f", errors.stream().max(Double::compare).get());
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
