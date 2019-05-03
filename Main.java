public class Main {

    public static void main(String[] args) {
        System.out.println("experiment started!");

        Crystal crystal = new Crystal(10.0, 10.0, 10.0);
        crystal.setnCr(1.8);
        Coordinates detL = new Coordinates(0.0, 2.0, 2.0);
        Coordinates detH = new Coordinates(0.0, 8.0, 8.0);
        crystal.adDetector(detL, detH, 0.8, 1.0);
        crystal.setBeta(0.3);
        crystal.setDetectorMode(2);

        for (int i = 0; i < 1000000; i++){
            Photon photon = new Photon();
            while (photon.isAlive()){
                crystal.interactionWithPhoton(photon);
            }
        }
        System.out.println(crystal.getCounted());
        System.out.println(crystal.getCounted()/1000000.0);
    }
}
