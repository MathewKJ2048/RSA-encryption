import javax.swing.*;
import java.awt.*;
public class Temp_tracker extends Tracker
{
    static Color BAR_FILLED = new Color(0,255,128);
    static Color BAR_UNFILLED = new Color(0,0,0);
    static Color BACKGROUND = new Color(128,128,128);
    JFrame f;
    JProgressBar p;
    Temp_tracker(int total, String title)
    {
        super(total);
        this.f = new JFrame();
        this.p = new JProgressBar(0,this.get_total());
        this.f.setVisible(true);
        this.f.setBackground(this.BACKGROUND);
        this.f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.f.setLayout(null);
        this.f.setTitle(title);
        this.f.setSize(512,96);
        this.p.setBounds(0,0,512,64);
        this.p.setForeground(this.BAR_FILLED);
        this.p.setBackground(this.BAR_UNFILLED);
        this.f.add(p);
    }
    public void update(int current)
    {
        this.p.setValue(current);
    }
    public void close()
    {
        this.f.dispose();
    }
}
