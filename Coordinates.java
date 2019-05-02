public class Coordinates {
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    private double x;
    private double y;
    private double z;

    Coordinates(){
        this.x = 0.0; this.y = 0.0; this.z = 0.0;
    }
    Coordinates(double x, double y, double z){
        this.x = x; this.y = y; this.z = z;
    }

    void setCoordinates(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }
}
