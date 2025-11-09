package dasdwdjfhefhj.vechicleTypes.tram;
import dasdwdjfhefhj.vechicle.tram.*;

public class PesaSwing extends Tram {
    public PesaSwing() {
        // człony o długościach: (4 wózek na 3) + (5) + (2 wózek na 1) + (5) + (4 wózek na 1)
        // interpretacja: długość sekcji i jeden/dwa wózki z podanym offsetem w m
        TramSection s1 = new TramSection(4);
        s1.bogies.add(new Bogie(3)); // „wózek na 3”
        s1.bogies.add(new Bogie(1)); // drugi wózek – ustawmy jako near-front dla stabilizacji
        // Jeśli miało być 2 wózki na 3 – można dodać oba w offset 1 i 3; tu urealniamy z przodu/tyłu.

        TramSection s2 = new TramSection(5);
        // brak wózków → sekcja może być „zawieszona” na sprzęgach; dla uproszczenia dodamy 1 wózek centralnie (bardziej stabilny render)
        s2.bogies.add(new Bogie(2.5));

        TramSection s3 = new TramSection(2);
        s3.bogies.add(new Bogie(1)); // „wózek na 1” (jeden wózek)

        TramSection s4 = new TramSection(5);
        s4.bogies.add(new Bogie(2.5)); // jeden wózek pośrodku (dla stabilnego łącznika)

        TramSection s5 = new TramSection(4);
        s5.bogies.add(new Bogie(1)); // „wózek na 1”
        s5.bogies.add(new Bogie(3)); // drugi wózek

        sections.add(s1);
        sections.add(s2);
        sections.add(s3);
        sections.add(s4);
        sections.add(s5);
    }
}