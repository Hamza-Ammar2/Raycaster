public class Vec3 {
    float x = 0;
    float y = 0;
    float z = 0;

    Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec3 add(Vec3 vector) {
        return new Vec3(x + vector.x, y + vector.y, z + vector.z);
    }


    public static int getSqrLength(int x, int y, int z) {
        return x*x + y*y + z*z;
    }

    public Vec3 subtract(Vec3 vector) {
        return new Vec3(x - vector.x, y - vector.y, z - vector.z);
    }

    public Vec3 multiply(float num) {
        return new Vec3((x*num),(y*num), (z*num));
    }

    public float getLength() {
        return (float) Math.hypot((double) x, Math.hypot(z, y));
    }

    public void reset() {
        x = 0;
        y = 0;
        z = 0;
    }

    public void equals(Vec3 vector) {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }


    public void normalize() {
        float length = getLength();
        x /= length;
        y /= length;
        z /= length;
    }


    public float dot(Vec3 vector) {
        return vector.x*x + vector.y*y + vector.z*z;
    }


    public static Vec3 cross(Vec3 v1, Vec3 v2) {
        float x = v1.y*v2.z - v1.z*v2.y;
        float y = v2.x*v1.z - v2.z*v1.x;
        float z = v1.x*v2.y - v1.y*v2.x;

        return new Vec3(x, y, z);
    }
}
