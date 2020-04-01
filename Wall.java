public class Wall {
    Coordinates normal = new Coordinates(); //координаты нормали к плоскости
    private double beta; //вероятность поглотиться на стенке (бетта)
    Coordinates sizeL = new Coordinates(); //"нижние границы" диапазона координат на стенке (типа (а,0,0) для x=a)
    Coordinates sizeH = new Coordinates(); //"верхние границы" (a,b,c)
    private int reflection = 1; //тип отражения: по умолчанию 1 для диффузного (по Ламберту), 2 для зеркального (но в зеркальном еще добавлется чуть-чуть случайной величины, чтоб не начал "по кругу" ходить)

    Wall(Coordinates fullSize, String side) {
        if (side.equals("x - min")) {
            this.normal.setCoordinates(1.0, 0.0, 0.0);
            this.sizeL.setCoordinates(0.0, 0.0, 0.0);
            this.sizeH.setCoordinates(0.0, fullSize.getY(), fullSize.getZ());
        }
        if (side.equals("x - max")) {
            this.normal.setCoordinates(-1.0, 0.0, 0.0);
            this.sizeL.setCoordinates(fullSize.getX(), 0.0, 0.0);
            this.sizeH.setCoordinates(fullSize.getX(), fullSize.getY(), fullSize.getZ());
        }
        if (side.equals("y - min")) {
            this.normal.setCoordinates(0.0, 1.0, 0.0);
            this.sizeL.setCoordinates(0.0, 0.0, 0.0);
            this.sizeH.setCoordinates(fullSize.getX(), 0.0, fullSize.getZ());
        }
        if (side.equals("y - max")) {
            this.normal.setCoordinates(0.0, -1.0, 0.0);
            this.sizeL.setCoordinates(0.0, fullSize.getY(), 0.0);
            this.sizeH.setCoordinates(fullSize.getX(), fullSize.getY(), fullSize.getZ());
        }
        if (side.equals("z - min")) {
            this.normal.setCoordinates(0.0, 0.0, 1.0);
            this.sizeL.setCoordinates(0.0, 0.0, 0.0);
            this.sizeH.setCoordinates(fullSize.getX(), fullSize.getY(), 0.0);
        }
        if (side.equals("z - max")) {
            this.normal.setCoordinates(0.0, 0.0, -1.0);
            this.sizeL.setCoordinates(0.0, 0.0, fullSize.getZ());
            this.sizeH.setCoordinates(fullSize.getX(), fullSize.getY(), fullSize.getZ());
        }
    }

    boolean isOnTheWall(Coordinates point) {
        //проверять, попала ли точка на данную стенку
        return((point.getX() >= this.sizeL.getX()) && (point.getX() <= this.sizeH.getX()) &&
               (point.getY() >= this.sizeL.getY()) && (point.getY() <= this.sizeH.getY()) &&
               (point.getZ() >= this.sizeL.getZ()) && (point.getZ() <= this.sizeH.getZ()));
    }

    void setBeta(double b){
        this.beta = b;
    }

    double getBeta(){
        return this.beta;
    }

    void setReflection(int r){
        this.reflection = r;
    }

    int getReflection(){
        return this.reflection;
    }
}
