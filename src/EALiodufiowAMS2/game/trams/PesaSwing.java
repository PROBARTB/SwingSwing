package EALiodufiowAMS2.game.trams;

import EALiodufiowAMS2.helpers.Vec3;
import EALiodufiowAMS2.engine.rendering.TextureManager;
import EALiodufiowAMS2.game.vehicle.*;

import java.awt.image.BufferedImage;
import java.util.*;

public class PesaSwing extends Tram {
    public PesaSwing(String id, String currentTrackSegmentId, double posOnTrack) {
        super(id, createSections(), createJoints(), currentTrackSegmentId, posOnTrack);
    }

    private static List<Section> createSections() {
        final Vec3 size = new Vec3(31.5, 3.4, 2.4);
        final Vec3 bogieSize = new Vec3(1.0, 0.5, 2.4);

        List<Section> sections = new ArrayList<>(Arrays.asList(
                new Section(new Vec3(4.0, size.y, size.z), TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-1.png")),
                new Section(new Vec3(5.0, size.y, size.z), TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-2.png")),
                new Section(new Vec3(2.0, size.y, size.z), TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-3.png")),
                new Section(new Vec3(5.0, size.y, size.z), TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-4.png")),
                new Section(new Vec3(4.0, size.y, size.z), TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-section-5.png"))
        ));

        BufferedImage bogieTexture = TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-bogie.png");

        sections.get(0).addBogie(3.0, bogieSize, bogieTexture);
        sections.get(2).addBogie(1.0, bogieSize, bogieTexture);
        sections.get(4).addBogie(1.0, bogieSize, bogieTexture);

        return sections;
    }

    private static List<Joint> createJoints() {
        final Vec3 jointSize = new Vec3(0.3, 3.4, 2.2);
        final double maxBend = Math.toRadians(30.0);
        BufferedImage jointTexture = TextureManager.getTexture("assets\\vehicles\\trams\\PesaSwing\\120Na-joint.png");

        List<Joint> joints = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            joints.add(new Joint(jointSize, maxBend, jointTexture));
        }
        return joints;
    }

}
