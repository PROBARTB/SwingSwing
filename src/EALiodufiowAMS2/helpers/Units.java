package EALiodufiowAMS2.helpers;

public final class Units {
    private Units() {}
    // Encja: 1 m = X pikseli. Możesz łatwo skalować scenę zmieniając tę wartość.
    public static final double METERS_TO_PIXELS = 25.0;

    public static double mToPx(double meters) {
        return meters * METERS_TO_PIXELS;
    }

    public static double pxToM(double px) {
        return px / METERS_TO_PIXELS;
    }
}

