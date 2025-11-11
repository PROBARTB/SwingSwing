package dasdwdjfhefhj.vechicleTypes.tram;
import dasdwdjfhefhj.vechicle.tram.*;

public class PesaSwing extends Tram {
    public PesaSwing() {
        TramSection s1 = new TramSection(4);
        s1.bogies.add(new Bogie(3));

        TramSection s2 = new TramSection(5);

        TramSection s3 = new TramSection(2);
        s3.bogies.add(new Bogie(1));

        TramSection s4 = new TramSection(5);

        TramSection s5 = new TramSection(4);
        s5.bogies.add(new Bogie(1));

        sections.add(s1);
        sections.add(s2);
        sections.add(s3);
        sections.add(s4);
        sections.add(s5);
    }
}
