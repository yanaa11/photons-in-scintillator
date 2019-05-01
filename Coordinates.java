public class Coordinates {
    public double x;
    public double y;
    public double z;

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
