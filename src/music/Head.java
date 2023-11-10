package music;

import reaction.Mass;

import java.awt.*;

public class Head extends Mass {
    public Staff staff;
    public int line;  // line 0: top line, line 1: first space, line 2: next line.
    public Time time;

    public Head(Staff staff, int x, int y) {
        super("NOTE");
        this.staff = staff;
        this.time = staff.sys.getTime(x);
        int H = staff.H();
        int top = staff.yTop() - H;
        line = (y - top + H/2)/H - 1;
        System.out.println("Head constructor line: " + line);
    }

    @Override
    public void show(Graphics g) {
        int H = staff.H();
        Glyph.HEAD_Q.showAt(g, H, time.x, staff.yTop() + line*H);
    }
}
