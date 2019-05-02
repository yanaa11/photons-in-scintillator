import java.util.ArrayList;

public class Crystal {
    private double nCr; //показатель преломления самого кристалла
    static public double X_MAX = 0.0;
    static public double Y_MAX = 0.0;
    static public double Z_MAX = 0.0;
    public Coordinates FULL_SIZE = new Coordinates(X_MAX, Y_MAX, Z_MAX);
    private Detector detector = new Detector();
    ArrayList<Wall> walls = new ArrayList<>(); //массив стен, 1 minX, 2 maxX ...

    Crystal(double x, double y, double z){
        //принимает размеры по каждой оси, записывает их себе и создает стенки
        X_MAX = x;
        Y_MAX = y;
        Z_MAX = z;
        this.FULL_SIZE.setCoordinates(x, y, z);
        Wall xMin = new Wall(FULL_SIZE, "x - min"); this.walls.add(xMin);
        Wall xMax = new Wall(FULL_SIZE, "x - max"); this.walls.add(xMax);
        Wall yMin = new Wall(FULL_SIZE, "y - min"); this.walls.add(yMin);
        Wall yMax = new Wall(FULL_SIZE, "y - max"); this.walls.add(yMax);
        Wall zMin = new Wall(FULL_SIZE, "z - min"); this.walls.add(zMin);
        Wall zMax = new Wall(FULL_SIZE, "z - max"); this.walls.add(zMax);
    }

    void setnCr(double n1){
        this.nCr = n1;
    }
    void setBeta(double beta){
        for (int i = 0; i < 6; i++){
            this.walls.get(i).setBeta(beta);
        }
    }

    void adDetector(Coordinates detL, Coordinates detH, double alpha, double nExt){
        this.detector = new Detector(detL, detH, alpha, nExt);
        this.detector.setnCrys(this.nCr);
    }

    void setDetectorMode(int mode){
        this.detector.setMode(mode);
    }

    int getCounted(){
        return this.detector.getCounted();
    }


    Coordinates findWall(Photon photon){
        //ищет стенку, с которой пересечется фотончик
        Coordinates inPoint = new Coordinates();
        for (int i = 0; i < this.walls.size(); i++){
            if (Calculator.cosBetweenVectors(photon.direction, this.walls.get(i).normal) >= 0)continue;
            if (Calculator.cosBetweenVectors(photon.direction, this.walls.get(i).normal) < 0){
                Coordinates point = Calculator.intersectionPoint(photon, this.walls.get(i));
                if (this.walls.get(i).isOnTheWall(point)){
                    inPoint = point;
                    break;
                }
            }
        }
        return inPoint;
    }

    void interactionWithPhoton(Photon photon){
        Calculator calc = new Calculator();
        Coordinates point = this.findWall(photon);
        //если попадает на детектор
        if (this.detector.isOnTheDetector(point)){
            this.detector.interactionWithPhoton(photon, point);
        } else {
            if ( //если фотон попал в ребро, он пропадает, такие не буду отражать
                    ((point.getX() == 0.0) & (point.getY() == 0.0)) | ((point.getX() == 0.0) & (point.getZ() == 0.0)) | ((point.getY() == 0.0) & (point.getZ() == 0.0)) |
                            ((point.getX() == X_MAX) & (point.getY() == 0.0)) | ((point.getX() == X_MAX) & (point.getZ() == 0.0)) |
                            ((point.getY() == Y_MAX) & (point.getX() == 0.0)) | ((point.getY() == Y_MAX) & (point.getZ() == 0.0)) |
                            ((point.getZ() == Z_MAX) & (point.getY() == 0.0)) | ((point.getZ() == Z_MAX) & (point.getX() == 0.0)) |
                            ((point.getX() == X_MAX) & (point.getY() == Y_MAX)) | ((point.getX() == X_MAX) & (point.getZ() == Z_MAX)) | ((point.getZ() == Z_MAX) & (point.getY() == Y_MAX))
            ) {
                photon.beAbsorbed();

            } else {
                //попал в стенку, будем отражать
                Wall currentWall = null;
                //найдем стенку, в которую попал
                if (point.getX() == 0.0) {currentWall = this.walls.get(0);}
                if (point.getX() == X_MAX) {currentWall = this.walls.get(1);}
                if (point.getY() == 0.0) {currentWall = this.walls.get(2);}
                if (point.getY() == Y_MAX) {currentWall = this.walls.get(3);}
                if (point.getZ() == 0.0) {currentWall = this.walls.get(4);}
                if (point.getZ() == Z_MAX) {currentWall = this.walls.get(5);}

                double probability = Math.random();
                if (probability >= currentWall.getBeta()){
                    //бета это вероятность быть поглощенным
                    //не поглотился - отражаем
                    Coordinates reflected = Calculator.reflectLambertian(photon.direction, currentWall.normal);
                    photon.direction.setCoordinates(reflected.getX(), reflected.getY(), reflected.getZ());
                    photon.ph.setCoordinates(point.getX(), point.getY(), point.getZ());
                } else {
                    photon.beAbsorbed();
                }

            }
        }

    }

}
