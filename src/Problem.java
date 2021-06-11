

public interface Problem {
    double fit(double a,double b);
    boolean isNeighborBetter(double f0, double f1);
}