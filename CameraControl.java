public class CameraControl {
    // up down left right
    boolean[] keys = {false, false, false, false};
    Vec3 velocity;
    float speed = 0.1f;
    Vec3 sideVel;
    float angVel = 0.1f;

    Renderer renderer;
    CameraControl(Renderer renderer) {
        this.renderer = renderer;
        velocity = new Vec3(0, 0, speed);
        velocity = renderer.rotate(velocity);
        sideVel = rotate(velocity, 0, (float) (Math.PI/2));
    }



    public Vec3 rotate(Vec3 v, float theta, float phi) {
        float x = (float) (v.x*Math.cos(phi) - v.y*Math.sin(phi)*Math.sin(theta) + v.z*Math.sin(phi)*Math.cos(theta));
        float z = (float) (-v.x*Math.sin(phi) - v.y*Math.sin(theta)*Math.cos(phi) + v.z*Math.cos(phi)*Math.cos(theta));
        float y = (float) (v.y*Math.cos(theta) + v.z*Math.sin(theta));

        return new Vec3(x, y, z);
    }



    public void changeAngle(boolean isVertical, boolean isPositive) {
        int f = isPositive ? 1 : -1;
        if (isVertical)
            renderer.theta += f*angVel;
        else
            renderer.phi += f*angVel;
        
        velocity = new Vec3(0, 0, speed);
        sideVel = rotate(velocity, 0, (float) (renderer.phi + Math.PI/2));
        velocity = renderer.rotate(velocity);
    }



    public void changeVelocity(boolean isVertical, boolean isPositive, boolean isPressed) {
        if (isVertical) {
            if (isPositive)
                keys[0] = isPressed;
            else
                keys[1] = isPressed;
        } else {
            if (isPositive)
                keys[2] = isPressed;
            else
                keys[3] = isPressed;
        }
    }




    public void update() {
        if (keys[0])
            renderer.camPos = renderer.camPos.add(velocity);
        if (keys[1])
            renderer.camPos = renderer.camPos.add(velocity.multiply(-1));
        if (keys[2])
            renderer.camPos = renderer.camPos.add(sideVel);
        if (keys[3])
            renderer.camPos = renderer.camPos.add(sideVel.multiply(-1));
    }
}