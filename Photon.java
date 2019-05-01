import java.util.Random;

public class Photon {
    Coordinates ph = new Coordinates(); //текущие координаты фотона. по сути, меняются только на пересечении со стенкой, т.е. он не "шагает"
    Coordinates direction = new Coordinates(); //направляющий вектор
    private boolean alive = false; //есть ли он еще

    Photon (){
        Calculator calc = new Calculator();
        //по умолчанию - в случайном месте и со случайным направлением
        Random random = new Random(System.currentTimeMillis());
        this.ph.x = random.nextInt((int)Crystal.X_MAX + 1) + Math.random();
        this.ph.y = random.nextInt((int)Crystal.Y_MAX + 1) + Math.random();
        this.ph.z = random.nextInt((int)Crystal.Z_MAX + 1) + Math.random();
        double vx = random.nextInt();
        double vy = random.nextInt();
        double vz = random.nextInt();
        Coordinates v = new Coordinates(vx, vy, vz);
        double absv = calc.vectorLength(v);
        //посчитали рандомные координаты для направляющего вектора, а потом отнормировали
        this.direction.x = vx/absv;
        this.direction.y = vy/absv;
        this.direction.z = vz/absv;
        this.alive = true;
    }
    Photon(double x, double y, double z) {
        this.ph.x = x; this.ph.y = y; this.ph.z = z;
        Calculator calc = new Calculator();
        Random random = new Random(System.currentTimeMillis());
        double vx = random.nextInt();
        double vy = random.nextInt();
        double vz = random.nextInt();
        Coordinates v = new Coordinates(vx, vy, vz);
        double absv = calc.vectorLength(v);
        this.direction.x = vx/absv;
        this.direction.y = vy/absv;
        this.direction.z = vz/absv;
        this.alive = true;
    }

    Photon(double x, double y, double z, double dx, double dy, double dz){
        this.ph.x = x; this.ph.y = y; this.ph.z = z;
        this.direction.x = dx; this.direction.y = dy; this.direction.z = dz;
        this.alive = true;
    }

    void beAbsorbed(){
        this.alive = false;
    }

    boolean isAlive(){
        return this.alive;
    }
}

