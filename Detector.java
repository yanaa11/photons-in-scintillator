public class Detector {
    private double alpha; //вероятность выйти из окошка (альфа)
    private double nExt; //показатель преломления того, что снаружи (смазка или воздух)
    Coordinates sizeL = new Coordinates(); //"нижние границы" диапазона координат на окошке (типа (а,y0,z0) для x=a)
    Coordinates sizeH = new Coordinates(); //"верхние границы" (a,b0,c0)
    Coordinates normal = new Coordinates(); //нормаль, пригодится чтоб отражать
    private int counter = 0; //считает фотончики
    private int mode; //1 - используем абсолютный альфа, 2 - считаем по Френелю
    private double nCrys;
    Detector(){}
    Detector(Coordinates sizeL, Coordinates sizeH, double a, double nExt){
        this.nExt = nExt;
        this.alpha = a;
        this.sizeL.setCoordinates(sizeL.x, sizeL.y, sizeL.z);
        this.sizeH.setCoordinates(sizeH.x, sizeH.y, sizeH.z);
        if (((sizeH.x - sizeL.x) == 0.0) & (this.sizeH.x == 0.0)){
            this.normal.setCoordinates(1.0, 0.0, 0.0);
        }
        if (((sizeH.x - sizeL.x) == 0.0) & (this.sizeH.x > 0.0)){
            this.normal.setCoordinates(-1.0, 0.0, 0.0);
        }
        if (((sizeH.y - sizeL.y) == 0.0) & (this.sizeH.y == 0.0)){
            this.normal.setCoordinates(0.0, 1.0, 0.0);
        }
        if (((sizeH.y - sizeL.y) == 0.0) & (this.sizeH.y > 0.0)){
            this.normal.setCoordinates(0.0, -1.0, 0.0);
        }
        if (((sizeH.z - sizeL.z) == 0.0) & (this.sizeH.z == 0.0)){
            this.normal.setCoordinates(0.0, 0.0, 1.0);
        }
        if (((sizeH.z - sizeL.z) == 0.0) & (this.sizeH.z > 0.0)){
            this.normal.setCoordinates(0.0, 0.0, -1.0);
        }
    }

    boolean isOnTheDetector(Coordinates point) {
        //проверять, попала ли точка на детектор
        if (
                (point.x >= this.sizeL.x) & (point.x <= this.sizeH.x) &
                        (point.y >= this.sizeL.y) & (point.y <= this.sizeH.y) &
                        (point.z >= this.sizeL.z) & (point.z <= this.sizeH.z)
        ) {
            return true;
        } else {
            return false;
        }
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

    void count(){
        this.counter++;
    }

    int getCounted(){
        return this.counter;
    }

    void setMode(int mode){
        this.mode = mode;
    }

    void interactionWithPhoton(Photon photon, Coordinates point){
        Calculator calc = new Calculator();
        double probability = Math.random(); //будем ею определять шанс фотона выжить, вахаха
        if (this.mode == 1){
            //используем абсолютную альфа, альфа это вероятность всё-таки выйти
            if (probability <= this.alpha){
                //вышел! посчитали
                photon.beAbsorbed();
                this.count();
            } else {
                //живет и отражается обратно
                Coordinates reflected = calc.reflectLambertian(photon.direction, this.normal);
                photon.direction.setCoordinates(reflected.x, reflected.y, reflected.z);
                photon.ph.setCoordinates(point.x, point.y, point.z);
            }
        }
        if (this.mode == 2){
            double cosA = Math.abs(calc.cosBetweenVectors(photon.direction, this.normal));
            double tr = calc.trProbability(this.nCrys, this.nExt, cosA); //вероятность выйти
            if (probability <= tr) {
                //вышел
                photon.beAbsorbed();
                this.count();
            } else {
                //живет и отражается обратно
                Coordinates reflected = calc.reflectLambertian(photon.direction, this.normal);
                photon.direction.setCoordinates(reflected.x, reflected.y, reflected.z);
                photon.ph.setCoordinates(point.x, point.y, point.z);
            }
        }
    }
}
