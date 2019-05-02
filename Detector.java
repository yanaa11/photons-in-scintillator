public class Detector {
    private double alpha; //вероятность выйти из окошка (альфа)
    private double nExt; //показатель преломления того, что снаружи (смазка или воздух)
    private Coordinates sizeL = new Coordinates(); //"нижние границы" диапазона координат на окошке (типа (а,y0,z0) для x=a)
    private Coordinates sizeH = new Coordinates(); //"верхние границы" (a,b0,c0)
    private Coordinates normal = new Coordinates(); //нормаль, пригодится чтоб отражать
    private int counter = 0; //считает фотончики
    private int mode; //1 - используем абсолютный альфа, 2 - считаем по Френелю
    private double nCrys;
    Detector(){}
    Detector(Coordinates sizeL, Coordinates sizeH, double a, double nExt){
        this.nExt = nExt;
        this.alpha = a;
        this.sizeL.setCoordinates(sizeL.getX(), sizeL.getY(), sizeL.getZ());
        this.sizeH.setCoordinates(sizeH.getX(), sizeH.getY(), sizeH.getZ());
        if (((sizeH.getX() - sizeL.getX()) == 0.0) & (this.sizeH.getX() == 0.0)){
            this.normal.setCoordinates(1.0, 0.0, 0.0);
        }
        if (((sizeH.getX() - sizeL.getX()) == 0.0) & (this.sizeH.getX() > 0.0)){
            this.normal.setCoordinates(-1.0, 0.0, 0.0);
        }
        if (((sizeH.getY() - sizeL.getY()) == 0.0) & (this.sizeH.getY() == 0.0)){
            this.normal.setCoordinates(0.0, 1.0, 0.0);
        }
        if (((sizeH.getY() - sizeL.getY()) == 0.0) & (this.sizeH.getY() > 0.0)){
            this.normal.setCoordinates(0.0, -1.0, 0.0);
        }
        if (((sizeH.getZ() - sizeL.getZ()) == 0.0) & (this.sizeH.getZ() == 0.0)){
            this.normal.setCoordinates(0.0, 0.0, 1.0);
        }
        if (((sizeH.getZ() - sizeL.getZ()) == 0.0) & (this.sizeH.getZ() > 0.0)){
            this.normal.setCoordinates(0.0, 0.0, -1.0);
        }
    }

    boolean isOnTheDetector(Coordinates point) {
        //проверять, попала ли точка на детектор
        return  (point.getX() >= this.sizeL.getX()) && (point.getX() <= this.sizeH.getX()) &&
                (point.getY() >= this.sizeL.getY()) && (point.getY() <= this.sizeH.getY()) &&
                (point.getZ() >= this.sizeL.getZ()) && (point.getZ() <= this.sizeH.getZ());
    }

    void setAlpha(double a){
        this.alpha = a;
    }

    void setNExt(double n2){
        this.nExt = n2;
    }

    void  setnCrys(double n1)
    {
        this.nCrys = n1;
    }

    double getAlpha(){
        return this.alpha;
    }

    double getnExt(){
        return this.nExt;
    }

    private void count(){
        this.counter++;
    }

    int getCounted(){
        return this.counter;
    }

    void setMode(int mode){
        this.mode = mode;
    }

    void interactionWithPhoton(Photon photon, Coordinates point){
//        Calculator calc = new Calculator();
        double probability = Math.random(); //будем ею определять шанс фотона выжить, вахаха
        if (this.mode == 1){
            //используем абсолютную альфа, альфа это вероятность всё-таки выйти
            if (probability <= this.alpha){
                //вышел! посчитали
                photon.beAbsorbed();
                this.count();
            } else {
                //живет и отражается обратно
                Coordinates reflected = Calculator.reflectLambertian(photon.direction, this.normal);
                photon.direction.setCoordinates(reflected.getX(), reflected.getY(), reflected.getZ());
                photon.ph.setCoordinates(point.getX(), point.getY(), point.getZ());
            }
        }
        if (this.mode == 2){
            double cosA = Math.abs(Calculator.cosBetweenVectors(photon.direction, this.normal));
            double tr = Calculator.trProbability(this.nCrys, this.nExt, cosA); //вероятность выйти
            if (probability <= tr) {
                //вышел
                photon.beAbsorbed();
                this.count();
            } else {
                //живет и отражается обратно
                Coordinates reflected = Calculator.reflectLambertian(photon.direction, this.normal);
                photon.direction.setCoordinates(reflected.getX(), reflected.getY(), reflected.getZ());
                photon.ph.setCoordinates(point.getX(), point.getY(), point.getZ());
            }
        }
    }
}
