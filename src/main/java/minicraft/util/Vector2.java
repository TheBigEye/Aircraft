package minicraft.util;

public class Vector2 {
    // The x and y coordinates of the vector
    public double x, y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // Takes a Vector2 object and returns a new Vector2 object that is the normalized version of the input.
    public static Vector2 normalize(Vector2 vec) {
        double max = Math.max(vec.x, vec.y);
        return new Vector2(vec.x / max, vec.y / max);
    }

    // Takes the x and y coordinates of a vector and returns the distance between the origin and the vector
    public static double calculateDistance(double x, double y) {
        return Math.sqrt(x * x + y * y);
    }
}