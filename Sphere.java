public class Sphere extends Object {
    Vec3 positon;
    int r;

    Sphere(int x, int y, int z, int r, int specular, float reflective, int[] color) {
        positon = new Vec3(x, y, z);
        this.r = r;
        this.reflective = reflective;
        this.specular = specular;
        rgb = new int[]{color[0], color[1], color[2]};
        type = 's';
    }
}
