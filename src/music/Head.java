package music;

import reaction.Mass;

import java.awt.*;
import java.util.ArrayList;

public class Head extends Mass {
    public Staff staff;
    public int line;  // line 0: top line, line 1: first space, line 2: next line.
    public Time time;

    public Head(Staff staff, int x, int y) {
        super("NOTE");
        this.staff = staff;
        this.time = staff.sys.getTime(x);
        line = staff.lineOfY(y);
        System.out.println("Head constructor line: " + line);
    }

    @Override
    public void show(Graphics g) {
        int H = staff.H();
        Glyph.HEAD_Q.showAt(g, H, time.x, staff.yTop() + line*H);
    }

    public int W() {return 24*staff.H()/10;}  // calculate width of single head

    //------------------------------------------List-------------------------------------------------------
    public static class List extends ArrayList<Head>{

    }
}
