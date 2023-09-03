import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Renderer {
    int d = 1;
    int Vh = 1;
    int Vw = 1;
    int SCREEN_HEIGHT = Game.SCREEN_HEIGHT;
    int SCREEN_WIDTH = Game.SCREEN_WIDTH;

    int depth = 1;
    int BigNum = Integer.MAX_VALUE;
    float phi = 0.0f;
    float theta = 0.0f;
    CameraControl cameraControl;
    int psize = 3;


    List<Light> lights = new ArrayList<>();
    List<Object> spheres = new ArrayList<>();
    int[] backgroundColor = {0, 0, 0};
    Vec3 camPos;

    Renderer() {
        camPos = new Vec3();
        cameraControl = new CameraControl(this);
        //loadModel("stall"); too slow
        spheres.add(
            new Triangle(
                new Vec3(-1, 0, 3), new Vec3(1, 0, 3), 
                new Vec3(0, 1, 3), 
                10, 0, new int[]{255, 0, 255}
            )
        );
        spheres.add(new Sphere(0, -1, 3, 1, 500, 0.2f, new int[]{255, 0, 0}));
        spheres.add(new Sphere(-2, 0, 4, 1, 10, 0.4f, new int[]{0, 255, 0}));
        spheres.add(new Sphere(2, 0, 4, 1, 500, 0.3f, new int[]{0, 0, 255}));
        spheres.add(new Sphere(0, -5001, 0, 5000, 1000, 0.5f, new int[]{255, 255, 0}));

        lights.add(new Light(null, 'a', 0.2f));
        lights.add(new Light(new int[]{2, 1, 0}, 'p', 0.6f));
        lights.add(new Light(new int[]{1, 4, 4}, 'd', 0.2f));
    }


    public void loadModel(String fileName) {
        FileReader fr = null;
        try {
            fr = new FileReader(new File("assets/" + fileName + ".obj"));
        } catch (Exception e) {}
        BufferedReader reader = new BufferedReader(fr);
        String line;
        List<Vec3> vertices = new ArrayList<>();

        try {
            while(true){
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vec3 vertex = new Vec3(
                        Float.parseFloat(currentLine[1]),
                        Float.parseFloat(currentLine[2]),
                        Float.parseFloat(currentLine[3])
                        );
                    vertices.add(vertex);
                }

                if (line.startsWith("f ")) {
                    break;
                }
            }

            
            int v0, v1, v2;
            while(line != null) {
                if (!line.startsWith("f ")) {
                    line = reader.readLine();
                    continue;
                } else
                    line = reader.readLine();

                String[] currentLine = line.split(" ");
                v0 = Integer.parseInt(currentLine[1].split("/")[0]) - 1;
                v1 = Integer.parseInt(currentLine[2].split("/")[0]) - 1;
                v2 = Integer.parseInt(currentLine[3].split("/")[0]) - 1;

                Triangle triangle = new Triangle(
                    vertices.get(v0), 
                    vertices.get(v1), 
                    vertices.get(v2), 
                    10, 0, 
                    new int[]{255, 255, 255}
                );
                spheres.add(triangle);
            }
        } catch (Exception e) {
        }
    }



    public void draw(Graphics g) {
        for (int x = -SCREEN_WIDTH/2; x < SCREEN_WIDTH/2; x += psize) {
            for (int y = -SCREEN_HEIGHT/2; y < SCREEN_HEIGHT/2; y += psize) {
                Vec3 D = rotate(canvasToViewport(x, y));
                int[] color = TraceRay(camPos, D, 1, BigNum, depth);
                putPixel(g, x, y, color);
            }
        }
    }



    public Vec3 rotate(Vec3 v) {
        float x = (float) (v.x*Math.cos(phi) - v.y*Math.sin(phi)*Math.sin(theta) + v.z*Math.sin(phi)*Math.cos(theta));
        float z = (float) (-v.x*Math.sin(phi) - v.y*Math.sin(theta)*Math.cos(phi) + v.z*Math.cos(phi)*Math.cos(theta));
        float y = (float) (v.y*Math.cos(theta) + v.z*Math.sin(theta));

        return new Vec3(x, y, z);
    }



    public float computeLighting(Vec3 P, Vec3 N, Vec3 V, int s) {
        float i = 0.0f;
        for (Light light : lights) {
            if (light.type == 'a')
                i += light.intensity;
            else {
                Vec3 L;
                float t_max;
                float t_min = 0.001f;

                if (light.type == 'p'){
                    L = light.position.subtract(P);
                    t_max = 1;
                } else {
                    L = light.position;
                    t_max = BigNum;
                }

                Object shadow_sphere = closest_obj(P, L, t_min, t_max, true).sphere;
                if (shadow_sphere != null)
                    continue;

                float N_dot_L = L.dot(N);
                if (N_dot_L <= 0) continue;
                
                i += light.intensity*(N_dot_L/(L.getLength()));
                if (s == -1) continue;
                
                Vec3 R = N.multiply(2*N_dot_L).subtract(L);
                float r_dot_v = R.dot(V);
                if (r_dot_v > 0) 
                    i += light.intensity*Math.pow((r_dot_v/(R.getLength()*V.getLength())), s);
            }   
        }

        return i;
    }



    public Vec3 canvasToViewport(int x, int y) {
        float fx = ((float) Vw)/((float) SCREEN_WIDTH);
        float fy = ((float) Vh)/((float) SCREEN_HEIGHT);

        fx *= x;
        fy *= y;

        return new Vec3(fx, fy, d);
    }

    private void putPixel(Graphics g, int x, int y, int[] color) {
        int Sx = (SCREEN_WIDTH/2) + x;
        int Sy = (SCREEN_HEIGHT/2) - y;

        g.setColor(new Color(color[0], color[1], color[2]));
        g.fillRect(Sx, Sy, psize, psize);
    }

    private int[] TraceRay(Vec3 O, Vec3 D, float t_min, float t_max, int depth) {
        Intersected intersected = closest_obj(O, D, t_min, t_max, false);
        Object closest_sphere = intersected.sphere;
        float closest_t = intersected.t;   
        
        if (closest_sphere == null) return backgroundColor;
    
        Vec3 P = O.add(D.multiply(closest_t));
        Vec3 N = closest_sphere.type == 's' ? P.subtract(((Sphere) closest_sphere).positon) : ((Triangle) closest_sphere).n;
        if (N.dot(D) <= 0)
            N.multiply(-1);
        
        N.normalize();
        float intensity = computeLighting(P, N, D.multiply(-1), closest_sphere.specular);

        int[] local_color = scaleColor(closest_sphere.rgb, intensity);
        float r = closest_sphere.reflective;
        if (depth <= 0 | r <= 0) 
            return normalizeColor(local_color);

        Vec3 R = reflect_ray(D.multiply(-1), N);
        int[] reflected_ray = TraceRay(P, R, 0.1f, BigNum, depth - 1);

        return normalizeColor(addColors(scaleColor(local_color, 1.0f - r), scaleColor(reflected_ray, r)));
    }


    private int[] normalizeColor(int[] c) {
        int r = c[0] > 255 ? 255 : c[0];
        int g = c[1] > 255 ? 255 : c[1];
        int b = c[2] > 255 ? 255 : c[2];

        return new int[]{r, g, b};
    }



    private int[] addColors(int[] c1, int[] c2) {
        return new int[]{c1[0] + c2[0], c1[1] + c2[1], c1[2] + c2[2]};
    }



    private int[] scaleColor(int[] c, float num) {
        return new int[]{(int) (c[0]*num), (int) (c[1]*num), (int) (c[2]*num)};
    }



    private Intersected closest_obj(Vec3 O, Vec3 D, float t_min, float t_max, boolean find_shadow) {
        float closest_t = BigNum;
        Object closest_sphere = null;
        for (Object sphere : spheres) {
            float[] intersections;
            if (sphere.type == 's')
                intersections = intersectRaySphere(O, D, (Sphere) sphere);
            else    
                intersections = intersectRayTriangle(O, D, (Triangle) sphere);

            float t1 = intersections[0];
            float t2 = intersections[1];

            if ((t1 > t_min && t1 < t_max) && t1 < closest_t) {
                closest_t = t1;
                closest_sphere = sphere;
                if (find_shadow)
                    break;
            }

            if ((t2 > t_min && t2 < t_max) && t2 < closest_t) {
                closest_t = t2;
                closest_sphere = sphere;
                if (find_shadow)
                    break;
            }
        }

        return new Intersected(closest_sphere, closest_t);
    }



    private Vec3 reflect_ray(Vec3 R, Vec3 N) {
        return N.multiply(2*(R.dot(N))).subtract(R);
    }



    private float[] intersectRaySphere(Vec3 O, Vec3 D, Sphere sphere) {
        Vec3 CO = O.subtract(sphere.positon);

        float a = D.dot(D);
        float b = 2*CO.dot(D);
        float c = CO.dot(CO) - sphere.r*sphere.r;

        float disc = b*b - 4*a*c;
        if (disc < 0) {
            return new float[]{BigNum, BigNum};
        }
        float t1 = (float) ((-b + Math.sqrt(disc))/(2*a));
        float t2 = (float) ((-b - Math.sqrt(disc))/(2*a));

        return new float[]{t1, t2};
    }



    private float[] intersectRayTriangle(Vec3 O, Vec3 D, Triangle triangle) {
        Vec3 edg1 = triangle.v1.subtract(triangle.v0);
        Vec3 edg2 = triangle.v2.subtract(triangle.v0);
        Vec3 h = Vec3.cross(D, edg2);
        float a, f, u, v;
        a = edg1.dot(h);

        if (a > -0.01f && a < 0.01f)
            return new float[]{BigNum, BigNum};
        
        f = 1.0f/a;
        Vec3 s = O.subtract(triangle.v0);
        u = f*s.dot(h);
        if (u < 0.0f || u > 1.0f) {
            return new float[]{BigNum, BigNum};
        }

        Vec3 q = Vec3.cross(s, edg1);
        v = f*D.dot(q);
        if (v < 0.0 || u + v > 1.0) {
            return new float[]{BigNum, BigNum};
        }

        float t = f*edg2.dot(q);
        return new float[]{t, t};
    }



    class Light {
        Vec3 position;
        char type;
        float intensity;
        Light(int[] pos, char type, float intensity) {
            if (type != 'a')
                position = new Vec3(pos[0], pos[1], pos[2]);
            this.type = type;
            this.intensity = intensity;
        }
    }

    class Intersected {
        Object sphere;
        float t;
        Intersected(Object sphere, float t) {
            this.sphere = sphere;
            this.t = t;
        }
    }
}
