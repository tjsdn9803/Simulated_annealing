

import java.util.ArrayList;
import java.util.Random;

public class SimulatedAnnealing {
    private int niter;
    public ArrayList<Double> a_hist;
    public ArrayList<Double> hist;

    public SimulatedAnnealing(int niter) {
        this.niter = niter;
        hist = new ArrayList<>();
        a_hist = new ArrayList<>();
    }

    public double[] solve(Problem p, double t, double ratio, double a_lower, double a_upper,double b_lower,double b_upper) {
        Random r = new Random();
        double a0 = r.nextDouble() * (a_upper - a_lower) + a_lower;
        double b0 = r.nextDouble() * (b_upper - b_lower) + b_lower;
        System.out.println("초기 값a: "+ a0 +" 초기값b: " + b0);
        return solve(p, t, ratio, a0,b0, a_lower, a_upper, b_lower, b_upper);
    }

    public double[] solve(Problem p, double t, double ratio, double a0, double b0,double a_lower, double a_upper, double b_lower, double b_upper) {
        Random r = new Random();
        double f0 = p.fit(a0,b0);
        hist.add(f0);
        a_hist.add(a0);
        for (int i=0; i<niter; i++) {
            int kt = (int) t;
            for(int j=0; j<kt; j++) {
                double a1 = (r.nextDouble() * (a_upper - a_lower) + a_lower);
                double b1 = (r.nextDouble() * (b_upper - b_lower) + b_lower);
                double f1 = p.fit(a1,b1);

                if(p.isNeighborBetter(f0, f1)) {//x1의 값 x0의 값보다 작을(더 적합할)경우
                    a0 = a1;
                    b0 = b1;
                    f0 = f1;
                    hist.add(f0);
                    a_hist.add(a0);
                } else {//x1이 더 안좋은 해여도 확률에 따라 바쀠게함
                    double d = Math.sqrt(Math.abs(f1 - f0));
                    double p0 = Math.exp(-d/t);
                    System.out.println("확률: "+p0+" 온도: "+t+" d: "+d);
                    if(r.nextDouble() < p0) {
                        a0 = a1;
                        b0 = b1;
                        f0 = f1;
                        hist.add(f0);
                        a_hist.add(a0);
                    }
                }
            }
            t *= ratio;
            System.out.println("온도: "+t);
        }
        double[] ab = new double[]{a0,b0};
        return ab;
    }
}

