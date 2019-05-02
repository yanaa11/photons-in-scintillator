import java.util.Random;

public class Photon {
    Coordinates ph = new Coordinates(); //текущие координаты фотона. по сути, меняются только на пересечении со стенкой, т.е. он не "шагает"
    Coordinates direction = new Coordinates(); //направляющий вектор
    private boolean alive = false; //есть ли он еще

    Photon (){
        Calculator calc = new Calculator();
        //по умолчанию - в случайном месте и со случайным направлением
        Random random = new Random(System.currentTimeMillis());
        this.ph.setX(random.nextInt((int)Crystal.X_MAX + 1) + Math.random());
        this.ph.setY(random.nextInt((int)Crystal.Y_MAX + 1) + Math.random());
        this.ph.setZ(random.nextInt((int)Crystal.Z_MAX + 1) + Math.random());
        double vx = random.nextInt();
        double vy = random.nextInt();
        double vz = random.nextInt();
        Coordinates v = new Coordinates(vx, vy, vz);
        double absv = calc.vectorLength(v);
        //посчитали рандомные координаты для направляющего вектора, а потом отнормировали
        this.direction.setX(vx/absv);
        this.direction.setY(vy/absv);
        this.direction.setX(vz/absv);
        this.alive = true;
    }
    Photon(double x, double y, double z) {
        this.ph.setX(x);
        this.ph.setY(y);
        this.ph.setZ(z);
        Calculator calc = new Calculator();
        Random random = new Random(System.currentTimeMillis());
        double vx = random.nextInt();
        double vy = random.nextInt();
        double vz = random.nextInt();
        Coordinates v = new Coordinates(vx, vy, vz);
        double absv = calc.vectorLength(v);
        this.direction.setX(vx/absv);
        this.direction.setY(vy/absv);
        this.direction.setZ(vz/absv);
        this.alive = true;
    }

    Photon(double x, double y, double z, double dx, double dy, double dz){
        this.ph.setX(x);
        this.ph.setY(y);
        this.ph.setZ(z);

        this.direction.setX(dx);
        this.direction.setY(dy);
        this.direction.setZ(dz);
        this.alive = true;
    }

    void beAbsorbed(){
        this.alive = false;
    }

    boolean isAlive(){
        return this.alive;
    }
}

