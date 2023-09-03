public class Triangle extends Object {
    Vec3 v0;
    Vec3 v1;
    Vec3 v2;
    Vec3 n;

    Triangle(Vec3 v0, Vec3 v1, Vec3 v2, int specular, float reflective, int[] color) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;

        this.reflective = reflective;
        this.specular = specular;
        rgb = new int[]{color[0], color[1], color[2]};
        type = 't';
        getNormal();
    }


    private void getNormal() {
        Vec3 edge1 = v0.subtract(v1);
        Vec3 edge2 = v0.subtract(v2);
        float x = edge1.y*edge2.z - edge1.z*edge2.y;
        float y = edge2.x*edge1.z - edge2.z*edge1.x;
        float z = edge1.x*edge2.y - edge1.y*edge2.x;
        n = new Vec3(x, y, z);
    }
}
