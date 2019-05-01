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
            this.sizeH.setCoordinates(0.0, fullSize.y, fullSize.z);
        }
        if (side.equals("x - max")) {
            this.normal.setCoordinates(-1.0, 0.0, 0.0);
            this.sizeL.setCoordinates(fullSize.x, 0.0, 0.0);
            this.sizeH.setCoordinates(fullSize.x, fullSize.y, fullSize.z);
        }
        if (side.equals("y - min")) {
            this.normal.setCoordinates(0.0, 1.0, 0.0);
            this.sizeL.setCoordinates(0.0, 0.0, 0.0);
            this.sizeH.setCoordinates(fullSize.x, 0.0, fullSize.z);
        }
        if (side.equals("y - max")) {
            this.normal.setCoordinates(0.0, -1.0, 0.0);
            this.sizeL.setCoordinates(0.0, fullSize.y, 0.0);
            this.sizeH.setCoordinates(fullSize.x, fullSize.y, fullSize.z);
        }
        ;
        if (side.equals("z - min")) {
            this.normal.setCoordinates(0.0, 0.0, 1.0);
            this.sizeL.setCoordinates(0.0, 0.0, 0.0);
            this.sizeH.setCoordinates(fullSize.x, fullSize.y, 0.0);
        }
        ;
        if (side.equals("z - max")) {
            this.normal.setCoordinates(0.0, 0.0, -1.0);
            this.sizeL.setCoordinates(0.0, 0.0, fullSize.z);
            this.sizeH.setCoordinates(fullSize.x, fullSize.y, fullSize.z);
        }
        ;
    }

    boolean isOnTheWall(Coordinates point) {
        //проверять, попала ли точка на данную стенку
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
