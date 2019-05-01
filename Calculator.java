import static java.lang.Double.MAX_VALUE;

public class Calculator {
    Coordinates intersectionPoint(Photon ph, Wall wall){
        //находит точку пересечения прямой - "траектории фотончика" с какой-то плоскостью. полученная тут координата может быть где-то на плоскоти за пределами стенки
        Coordinates point = new Coordinates();
        //проверить на параллельность. если угол с нормалью 90 (косинус = 0), то он летит параллельно стенке
        if (isEqualDouble(cosBetweenVectors(ph.direction, wall.normal),0)) {
            point.setCoordinates(MAX_VALUE, MAX_VALUE, MAX_VALUE); //эти максимальные значения - сомнительно, но я не придумала, как еще обозначить этот случай (бросать исключение? хм хм)
        } else {
            //тут надо посмотреть, с какой плоскостью пересекаем
            if (!isEqualDouble(wall.normal.x, 0.0)){
                point.x = wall.sizeL.x; //попал на одну из стенок yz
                //считаем координаты точки пересечения с плоскостью
                point.y = ph.direction.y*(wall.sizeL.x - ph.ph.x)/ph.direction.x + ph.ph.y;
                point.z = ph.direction.z*(wall.sizeL.x - ph.ph.x)/ph.direction.x + ph.ph.z;
            }
            if (!isEqualDouble(wall.normal.y, 0.0)){
                point.y = wall.sizeL.y; //xz
                point.x = ph.direction.x*(wall.sizeL.y - ph.ph.y)/ph.direction.y + ph.ph.x;
                point.z = ph.direction.z*(wall.sizeL.y - ph.ph.y)/ph.direction.y + ph.ph.z;
            }
            if (!isEqualDouble(wall.normal.z, 0.0)){
                point.z= wall.sizeL.z; //yx
                point.y = ph.direction.y*(wall.sizeL.z - ph.ph.z)/ph.direction.z + ph.ph.y;
                point.x = ph.direction.x*(wall.sizeL.z - ph.ph.z)/ph.direction.z + ph.ph.x;
            }
        }
        return point;
    }

    double cosBetweenVectors(Coordinates v1, Coordinates v2){
        //косинус угла между векторами
        return (scalarProduct(v1, v2)/(vectorLength(v1)*vectorLength(v2)));
    }

    double vectorLength(Coordinates v){
        return Math.sqrt(v.x*v.x + v.y*v.y + v.z*v.z);
    }

    double scalarProduct(Coordinates v1, Coordinates v2){
        //скалярное произведение двух векторов
        return (v1.x * v2.x+ v1.y * v2.y + v1.z * v2.z);
    }

    boolean isEqualDouble(double a, double b) {
        //хз, зачем написала (вроде с адекватной точностью в знака 4 и так норм сравнивает) но пусть будет
        return (Math.abs(a - b) <= 0.000001);
    }

    Coordinates findWall(Photon photon, Crystal crystal){
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

    double sinCos(double cos){
        //ура тригонометрическому тождеству! п.с. работает только для углов "в прямоугольном треугольнике", т.е. без учета знаков
        //из синуса косинус и наоборт очевидно тоже может
        double sin;
        sin = Math.sqrt(1 - cos*cos);
        return sin;
    }

    Coordinates reflectVector(Coordinates v, Coordinates normal){
        //отражает векктор зеркально
        //формула r = v - 2n*(v,n)/(n,n)
        //но у нормали длина единичка
        Coordinates r = new Coordinates();
        double vn = scalarProduct(v, normal);
        r.x = v.x - normal.x*2*vn;
        r.y = v.y - normal.y*2*vn;
        r.z = v.z - normal.z*2*vn;
        return r;
    }

    double amplReflP(double n1, double n2, double cosA){
        //считает по формуле Френеля коэффициент отражения по АМПЛИТУДЕ для Р-волны
        double sinA = sinCos(cosA);
        double sinB = sinA*(n1/n2);
        double cosB = sinCos(sinB);
        double reflected = (n2*cosA - n1*cosB)/(n2*cosA + n1*cosB);
        return reflected;
    }
    double amplReflS(double n1, double n2, double cosA){
        //считает по формуле Френеля коэффициент отражения по АМПЛИТУДЕ для S-волны
        double sinA = sinCos(cosA); //синус угла падения
        double sinB = sinA*(n1/n2); //синус угла преломления
        double cosB = sinCos(sinB); //косинус угла преломления
        double reflected = (n1*cosA - n2*cosB)/(n1*cosA + n2*cosB);
        return reflected;
    }

    double transmittance(double amplRefl){
        //коэффициент пропускания по ИНТЕНСИВНОСТИ
        return (1 - amplRefl*amplRefl);
    }

    double trProbability(double n1, double n2, double cosA){
        double rS = this.amplReflS(n1, n2, cosA);
        double rP = this.amplReflP(n1, n2, cosA);
        return (transmittance(rS) + transmittance(rP))/2;
    }

    Coordinates reflectLambertian(Coordinates v, Coordinates normal){
        Coordinates rAbs = new Coordinates(); //отраженный в декартовых координатах "абсолютных", как всё в задачке
        Coordinates r = new Coordinates(); //отраженный в декартовых координатах "условных" - относительно нормали к стенке
        double phi = 2*Math.PI*Math.random(); //фи от 0 до 2пи
        double theta = Math.asin(Math.sqrt(Math.random())); //тетта классный как надо по распределению вероятности!
        r.x = Math.sin(theta)*Math.cos(phi);
        r.y = Math.sin(theta)*Math.sin(phi);
        r.z = Math.cos(theta);
        if (normal.x == 1.0){
            rAbs.x = r.z;
            rAbs.y = r.y;
            rAbs.z = -1.0*r.x;
        }
        if (normal.x == -1.0){
            rAbs.x = -1.0*r.z;
            rAbs.y = r.y;
            rAbs.z = r.x;
        }
        if (normal.y == 1.0){
            rAbs.x = r.y;
            rAbs.y = r.z;
            rAbs.z = r.x;
        }
        if (normal.y == -1.0){
            rAbs.x = r.x;
            rAbs.y = -1.0*r.z;
            rAbs.z = r.y;
        }
        if (normal.z == 1.0){
            rAbs.x = r.x;
            rAbs.y = r.y;
            rAbs.z = r.z;
        }
        if (normal.z == -1.0){
            rAbs.x = -1.0*r.x;
            rAbs.y = r.y;
            rAbs.z = -1.0*r.z;
        }
        return rAbs;

    }
}
