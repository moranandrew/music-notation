package music;

import reaction.Mass;

import java.awt.*;

public class Beam extends Mass {
    public Stem.List stems = new Stem.List();

    public Beam(Stem f, Stem l) {
        super ("NOTE");
        stems.addStem(f);
        stems.addStem(l);
    }

    public Stem first() {return stems.get(0);}

    public Stem last() {return stems.get(stems.size()-1);}

    public void deleteBeam() {for (Stem s: stems) {s.beam = null;}; deleteMass();}

    public void addStem(Stem s) {
        if (s.beam == null) {stems.add(s); s.beam = this; stems.sort();}
    }

    @Override
    public void show(Graphics g) {  // STUB

    }

    public void setMasterBeam() {
        mX1 = first().x(); mY1 = first().yBeamEnd(); mX2 = last().x(); mY2 = last().yBeamEnd();
    }

    // Math Functions

    public static int yOfX(int x, int x1, int y1, int x2, int y2) {
        int dY = y2 - y1, dX = x2 - x1;
        return (x - x1) * dY/dX + y1;
    }

    public static int mX1, mY1, mX2, mY2;  // coordinates for mater beam

    public static int yOfX(int x) {return yOfX(x,mX1, mY1, mX2, mY2);}

    public static void setMasterBeam(int x1, int y1, int x2, int y2) {
        mX1 = x1; mY1 = y1; mX2 = x2; mY2 = y2;
    }


}
