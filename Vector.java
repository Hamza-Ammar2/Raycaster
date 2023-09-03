public class Vector {
    int x = 0;
    int y = 0;

    Vector(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public Vector add(Vector vector) {
        return new Vector(x + vector.x, y + vector.y);
    }


    public static int getSqrLength(int x, int y) {
        return x*x + y*y;
    }

    public Vector subtract(Vector vector) {
        return new Vector(x - vector.x, y - vector.y);
    }

    public Vector multiply(float num) {
        return new Vector((int) (x*num),(int) (y*num));
    }

    public float getLength() {
        return (float) Math.hypot((double) x, (double) y);
    }

    public void reset() {
        x = 0;
        y = 0;
    }

    public void equals(Vector vector) {
        x = vector.x;
        y = vector.y;
    }


    public void normalize() {
        float length = getLength();
        x /= length;
        y /= length;
    }

    public static double getAngle(int y, int x) {
        int X = x < 0 ? -x : x;
        int Y = y < 0 ? -y : y;

        double angle = Math.atan2(Y, X);
        if (x < 0)
            angle = Math.PI - angle;
        if (y < 0)
            angle *= -1;
        return angle;
    }

    public static Vector getUnity(Vector vector) {
        int x = 1;
        int y = 1;
        if (vector.y < 0)
            y = -1;
        if (vector.x < 0)
            x = -1;
        if (vector.x == 0)
            x = 0;
        if (vector.y == 0)
            y = 0;
        return new Vector(x, y);
    }


    public float dot(Vector vector) {
        return vector.x*x + vector.y*y;
    }
}
