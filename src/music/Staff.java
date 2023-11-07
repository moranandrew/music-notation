package music;

import graphicsLib.UC;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;
import java.util.ArrayList;

import static music.AppMusicEd.PAGE;

public class Staff extends Mass {
    public Sys sys;
    public int iStaff;  // staff index
    public Staff.Fmt fmt;

    public Staff(int iStaff, Staff.Fmt staffFmt){
        super("BACK");
        this.iStaff = iStaff;
        fmt = staffFmt;

        addReaction(new Reaction("S-S") {  // Build Bar Line
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
                if (x < PAGE.margins.left || x > PAGE.margins.right + UC.barToMarginSnap) {
                    return UC.noBid;
                }
                int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
                return (d < 40)? d + UC.barToMarginSnap:UC.noBid;  // allow cycleBarType to outbid this.
            }

            @Override
            public void act(Gesture g) {
                new Bar(Staff.this.sys, g.vs.xM());

            }
        });

        addReaction(new Reaction("S-S") {  //
            @Override
            public int bid(Gesture g) {
                if (Staff.this.sys.iSys != 0) {return UC.noBid;}
                int y1 = g.vs.yL(), y2 = g.vs.yH();
                int iStaff = Staff.this.iStaff;
                if (iStaff == PAGE.sysFmt.size() - 1) {return UC.noBid;}
                if (Math.abs(y1 - Staff.this.yBot()) > 20) {return UC.noBid;}
                Staff nextStaff = sys.staffs.get(iStaff + 1);
                if (Math.abs(y2 - nextStaff.yTop()) > 20) {return UC.noBid;}
                return 10;
            }

            @Override
            public void act(Gesture g) {
                PAGE.sysFmt.get(Staff.this.iStaff).toggleBarContinues();
            }
        });
    }

    public int sysOff() {return sys.fmt.staffOffset.get(iStaff);}

    public int yTop() {return sys.yTop() + sysOff();}

    public int yBot() {return yTop() + fmt.height();}

    @Override
    public void show(Graphics g) {}


    //------------------------------------------Fmt------------------------------------------------

    public static class Fmt{
        public int nLines = 5, H = 8;
        public boolean barContinues = false;

        public void toggleBarContinues() {barContinues = !barContinues;}

        public int height() {return 2 * H * (nLines - 1);}

        public void showAt(Graphics g, int y) {
            int LEFT = PAGE.margins.left, RIGHT = PAGE.margins.right;
            for (int i = 0; i < nLines; i++) {
                g.drawLine(LEFT, y + 2 * H * i, RIGHT, y + 2 * H * i);
            }
        }
    }

    //------------------------------------------List-------------------------------------------------
    public static class List extends ArrayList<Staff> {

    }
}
