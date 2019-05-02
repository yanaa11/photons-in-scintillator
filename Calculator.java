import static java.lang.Double.MAX_VALUE;

public class Calculator {


    static Coordinates intersectionPoint(Photon ph, Wall wall){
        //находит точку пересечения прямой - "траектории фотончика" с какой-то плоскостью. полученная тут координата может быть где-то на плоскоти за пределами стенки
        Coordinates point = new Coordinates();
        //проверить на параллельность. если угол с нормалью 90 (косинус = 0), то он летит параллельно стенке
        if (isEqualDouble(cosBetweenVectors(ph.direction, wall.normal),0)) {
            point.setCoordinates(MAX_VALUE, MAX_VALUE, MAX_VALUE); //эти максимальные значения - сомнительно, но я не придумала, как еще обозначить этот случай (бросать исключение? хм хм)
        } else {
            //тут надо посмотреть, с какой плоскостью пересекаем
            if (!isEqualDouble(wall.normal.getX(), 0.0)){
                point.setX(wall.sizeL.getX()); //попал на одну из стенок yz
                //считаем координаты точки пересечения с плоскостью
                point.setY(ph.direction.getY()*(wall.sizeL.getX() - ph.ph.getX())/ph.direction.getX() + ph.ph.getY());
                point.setZ(ph.direction.getZ()*(wall.sizeL.getX() - ph.ph.getX())/ph.direction.getX() + ph.ph.getZ());
            }
            if (!isEqualDouble(wall.normal.getY(), 0.0)){
                point.setY(wall.sizeL.getY()); //xz
                point.setX(ph.direction.getX()*(wall.sizeL.getY() - ph.ph.getY())/ph.direction.getY() + ph.ph.getX());
                point.setZ(ph.direction.getZ()*(wall.sizeL.getY() - ph.ph.getY())/ph.direction.getY() + ph.ph.getZ());
            }
            if (!isEqualDouble(wall.normal.getZ(), 0.0)){
                point.setZ(wall.sizeL.getZ()); //yx
                point.setY(ph.direction.getY()*(wall.sizeL.getZ() - ph.ph.getZ())/ph.direction.getZ() + ph.ph.getY());
                point.setX(ph.direction.getX()*(wall.sizeL.getZ() - ph.ph.getZ())/ph.direction.getZ() + ph.ph.getX());
            }
        }
        return point;
    }

    static double cosBetweenVectors(Coordinates v1, Coordinates v2){
        //косинус угла между векторами
        return (scalarProduct(v1, v2)/(vectorLength(v1)*vectorLength(v2)));
    }

    static double vectorLength(Coordinates v){
        return Math.sqrt(v.getX()*v.getX() + v.getY()*v.getY() + v.getZ()*v.getZ());
    }

    static double scalarProduct(Coordinates v1, Coordinates v2){
        //скалярное произведение двух векторов
        return (v1.getX() * v2.getX()+ v1.getY() * v2.getY() + v1.getZ() * v2.getZ());
    }

    static boolean isEqualDouble(double a, double b) {
        //хз, зачем написала (вроде с адекватной точностью в знака 4 и так норм сравнивает) но пусть будет
        return (Math.abs(a - b) <= 0.000001);
    }

    static Coordinates findWall(Photon photon, Crystal crystal){
        //ищем для фотончика стенку, куда попадет
        Coordinates inPoint = new Coordinates();
        for (int i = 0; i < crystal.walls.size(); i++){
            if (cosBetweenVectors(photon.direction, crystal.walls.get(i).normal) >= 0)continue; //если фотон летит от стенки (или вдоль), то нечего нам на неё и смотреть
            if (cosBetweenVectors(photon.direction, crystal.walls.get(i).normal) < 0){
                //а вот если летит на стенку, то надо посмотреть, в какой точке попадет
                Coordinates point = intersectionPoint(photon, crystal.walls.get(i));
                if (crystal.walls.get(i).isOnTheWall(point)){
                    inPoint = point; //если пересекается на стенке, а не где-то далекоооо, то это то, что нужно
                    //потому что если летит на 2 стенки, то пересечение траектории с какой-то одной из них будет выходить за границы стенки
                    break;
                }
            }
        }
        return inPoint;
    }

    static double sinCos(double cos){
        //ура тригонометрическому тождеству! п.с. работает только для углов "в прямоугольном треугольнике", т.е. без учета знаков
        //из синуса косинус и наоборт очевидно тоже может
        double sin;
        sin = Math.sqrt(1 - cos*cos);
        return sin;
    }

    static Coordinates reflectVector(Coordinates v, Coordinates normal){
        //отражает векктор зеркально
        //формула r = v - 2n*(v,n)/(n,n)
        //но у нормали длина единичка
        Coordinates r = new Coordinates();
        double vn = scalarProduct(v, normal);
        r.setX(v.getX() - normal.getX()*2*vn);
        r.setY(v.getY() - normal.getY()*2*vn);
        r.setZ(v.getZ() - normal.getZ()*2*vn);
        return r;
    }

    static double amplReflP(double n1, double n2, double cosA){
        //считает по формуле Френеля коэффициент отражения по АМПЛИТУДЕ для Р-волны
        double sinA = sinCos(cosA);
        double sinB = sinA*(n1/n2);
        double cosB = sinCos(sinB);
        double reflected = (n2*cosA - n1*cosB)/(n2*cosA + n1*cosB);
        return reflected;
    }
    static double amplReflS(double n1, double n2, double cosA){
        //считает по формуле Френеля коэффициент отражения по АМПЛИТУДЕ для S-волны
        double sinA = sinCos(cosA); //синус угла падения
        double sinB = sinA*(n1/n2); //синус угла преломления
        double cosB = sinCos(sinB); //косинус угла преломления
        double reflected = (n1*cosA - n2*cosB)/(n1*cosA + n2*cosB);
        return reflected;
    }

    static double transmittance(double amplRefl){
        //коэффициент пропускания по ИНТЕНСИВНОСТИ
        return (1 - amplRefl*amplRefl);
    }

    static double trProbability(double n1, double n2, double cosA){
        double rS = amplReflS(n1, n2, cosA);
        double rP = amplReflP(n1, n2, cosA);
        return (transmittance(rS) + transmittance(rP))/2;
    }

    static Coordinates reflectLambertian(Coordinates v, Coordinates normal){
        Coordinates rAbs = new Coordinates(); //отраженный в декартовых координатах "абсолютных", как всё в задачке
        Coordinates r = new Coordinates(); //отраженный в декартовых координатах "условных" - относительно нормали к стенке
        double phi = 2*Math.PI*Math.random(); //фи от 0 до 2пи
        double theta = Math.asin(Math.sqrt(Math.random())); //тетта классный как надо по распределению вероятности!
        r.setX(Math.sin(theta)*Math.cos(phi));
        r.setY(Math.sin(theta)*Math.sin(phi));
        r.setZ(Math.cos(theta));
        if (normal.getX() == 1.0){
            rAbs.setX(r.getZ());
            rAbs.setY(r.getY());
            rAbs.setZ(-1.0*r.getX());
        }
        if (normal.getX() == -1.0){
            rAbs.setX(-1.0*r.getZ());
            rAbs.setY(r.getY());
            rAbs.setZ(r.getX());
        }
        if (normal.getY() == 1.0){
            rAbs.setX(r.getY());
            rAbs.setY(r.getZ());
            rAbs.setZ(r.getX());
        }
        if (normal.getY() == -1.0){
            rAbs.setX(r.getX());
            rAbs.setY(-1.0*r.getZ());
            rAbs.setZ(r.getY());
        }
        if (normal.getZ() == 1.0){
            rAbs.setX(r.getX());
            rAbs.setY(r.getY());
            rAbs.setZ(r.getZ());
        }
        if (normal.getZ() == -1.0){
            rAbs.setX(-1.0*r.getX());
            rAbs.setY(r.getY());
            rAbs.setZ(-1.0*r.getZ());
        }
        return rAbs;

    }
}
