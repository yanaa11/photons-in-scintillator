import java.util.Random;

public class Photon {
    Coordinates ph; //текущие координаты фотона. по сути, меняются только на пересечении со стенкой, т.е. он не "шагает"
    Coordinates direction;//направляющий вектор
    private boolean alive; //есть ли он еще

    boolean isAlive(){
        return this.alive;
    }

    Photon (){
        //по умолчанию - в случайном месте и со случайным направлением
        Random random = new Random(System.currentTimeMillis());
        this.ph = new Coordinates(random.nextInt((int)Crystal.X_MAX + 1) + Math.random(),
                                  random.nextInt((int)Crystal.Y_MAX + 1) + Math.random(),
                                  random.nextInt((int)Crystal.Z_MAX + 1) + Math.random());
        double vx = random.nextInt();
        double vy = random.nextInt();
        double vz = random.nextInt();
        Coordinates v = new Coordinates(vx, vy, vz);
        double absv = Calculator.vectorLength(v);
        //посчитали рандомные координаты для направляющего вектора, а потом отнормировали
        this.direction = new Coordinates(vx/absv, vy/absv, vz/absv);
        this.alive = true;
    }

    Photon(double x, double y, double z) {
        this.ph = new Coordinates(x, y, z);
        Random random = new Random(System.currentTimeMillis());
        double vx = random.nextInt();
        double vy = random.nextInt();
        double vz = random.nextInt();
        Coordinates v = new Coordinates(vx, vy, vz);

        double absv = Calculator.vectorLength(v);
        this.direction = new Coordinates(vx/absv, vy/absv, vz/absv);
        this.alive = true;
    }

    Photon(double x, double y, double z, double dx, double dy, double dz){
        this.ph = new Coordinates(x, y, z);
        this.direction = new Coordinates(dx, dy, dz);
        this.alive = true;
    }

    void beAbsorbed(){
        this.alive = false;
    }
}

