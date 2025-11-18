package EALiodufiowAMS2.vehicles.trams;

import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.vehicle.*;

public class PesaSwing extends Tram {
    public PesaSwing(String id) {
        super(id);
        // [1: 4m z wózkiem na 3m], [2: 5m], [3: 2m z wózkiem na 1m], [4: 5m], [5: 4m z wózkiem na 1m]
        setSize(new Vec3(31.5, 3.4, 2.4));
        Section s1 = new Section(new Vec3(4.0, getSize().y, getSize().z));
        Section s2 = new Section(new Vec3(5.0, getSize().y, getSize().z));
        Section s3 = new Section(new Vec3(2.0, getSize().y, getSize().z));
        Section s4 = new Section(new Vec3(5.0, getSize().y, getSize().z));
        Section s5 = new Section(new Vec3(4.0, getSize().y, getSize().z));

        s1.addBogie(3.0, new Vec3(1.0, 0.5, getSize().z));
        s3.addBogie(1.0, new Vec3(1.0, 0.5, getSize().z));
        s5.addBogie(1.0, new Vec3(1.0, 0.5, getSize().z));

        sections.add(s1);
        sections.add(s2);
        sections.add(s3);
        sections.add(s4);
        sections.add(s5);

        double gap = 0.3;
        double maxBend = Math.toRadians(30.0);
        for (int i = 0; i < 4; i++) joints.add(new Joint(gap, getSize().y, maxBend));

    }
}
