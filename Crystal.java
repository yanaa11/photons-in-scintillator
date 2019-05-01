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
        this.X_MAX = x;
        this.Y_MAX = y;
        this.Z_MAX = z;
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
        Calculator calc = new Calculator();
        Coordinates inPoint = new Coordinates();
        for (int i = 0; i < this.walls.size(); i++){
            if (calc.cosBetweenVectors(photon.direction, this.walls.get(i).normal) >= 0)continue;
            if (calc.cosBetweenVectors(photon.direction, this.walls.get(i).normal) < 0){
                Coordinates point = calc.intersectionPoint(photon, this.walls.get(i));
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
                    ((point.x == 0.0) & (point.y == 0.0)) | ((point.x == 0.0) & (point.z == 0.0)) | ((point.y == 0.0) & (point.z == 0.0)) |
                            ((point.x == X_MAX) & (point.y == 0.0)) | ((point.x == X_MAX) & (point.z == 0.0)) |
                            ((point.y == Y_MAX) & (point.x == 0.0)) | ((point.y == Y_MAX) & (point.z == 0.0)) |
                            ((point.z == Z_MAX) & (point.y == 0.0)) | ((point.z == Z_MAX) & (point.x == 0.0)) |
                            ((point.x == X_MAX) & (point.y == Y_MAX)) | ((point.x == X_MAX) & (point.z == Z_MAX)) | ((point.z == Z_MAX) & (point.y == Y_MAX))
            ) {
                photon.beAbsorbed();

            } else {
                //попал в стенку, будем отражать
                Wall currentWall = null;
                //найдем стенку, в которую попал
                if (point.x == 0.0) {currentWall = this.walls.get(0);}
                if (point.x == X_MAX) {currentWall = this.walls.get(1);}
                if (point.y == 0.0) {currentWall = this.walls.get(2);}
                if (point.y == Y_MAX) {currentWall = this.walls.get(3);}
                if (point.z == 0.0) {currentWall = this.walls.get(4);}
                if (point.z == Z_MAX) {currentWall = this.walls.get(5);}
                double probability = Math.random();
                if (probability >= currentWall.getBeta()){
                    //бета это вероятность быть поглощенным
                    //не поглотился - отражаем
                    Coordinates reflected = calc.reflectLambertian(photon.direction, currentWall.normal);
                    photon.direction.setCoordinates(reflected.x, reflected.y, reflected.z);
                    photon.ph.setCoordinates(point.x, point.y, point.z);
                } else {
                    photon.beAbsorbed();
                }

            }
        }

    }

}
