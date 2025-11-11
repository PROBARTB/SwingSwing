//package EALiodufiowAMS2.vehicles.trams;
//
//import EALiodufiowAMS2.vehicle.*;
//
//public class PesaSwing extends Tram {
//    public PesaSwing() {
//        super();
//        // Sekcje: [1: 4m z wózkiem na 3m], [2: 5m], [3: 2m z wózkiem na 1m], [4: 5m], [5: 4m z wózkiem na 1m]
//    // Section length, height (meters)
//    Section s1 = new Section(4.0, 2.5);
//    Section s2 = new Section(5.0, 2.5);
//    Section s3 = new Section(2.0, 2.5);
//    Section s4 = new Section(5.0, 2.5);
//    Section s5 = new Section(4.0, 2.5);
//
//        // Wózki wewnątrz sekcji
//    // add bogies with offset and height
//    s1.addBogie(3.0, 1.0, 1.0); // prowadzący
//    s3.addBogie(1.0, 1.0, 1.0);
//    s5.addBogie(1.0, 1.0, 1.0);
//
//        sections.add(s1);
//        sections.add(s2);
//        sections.add(s3);
//        sections.add(s4);
//        sections.add(s5);
//
//        // Jointy między sekcjami (4 szt.)
//        double gap = 0.3;
//        double maxBend = Math.toRadians(15.0);
//        for (int i = 0; i < 4; i++) joints.add(new Joint(gap, maxBend));
//
//        // Kamera domyślnie na sekcji 1
//        setCameraSection(s1);
//    }
//}
