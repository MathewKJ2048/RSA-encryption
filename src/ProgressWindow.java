import encryption.File_encryptor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProgressWindow extends JFrame
{
    public static final int frame_rate = 10; // time between successive frames in milliseconds
    private File_encryptor e;

    private void createUIComponents() {
        this.progressBar = new JProgressBar(0,1024);
        this.progressBar.setValue(1000);
    }

    static class Control
    {
        boolean is_in_progress;
        public Control()
        {
            is_in_progress = true;
        }
    }
    Control c = new Control();

    public void initialize()
    {
        class BarUpdater extends Thread
        {
            @Override
            public void run()
            {
                while(c.is_in_progress)
                {
                    progressBar.setValue((int)(1024*e.get_bytes_processed()/e.get_total_bytes()));
                    System.out.println(e.get_bytes_processed()/1024);
                    try
                    {
                        Thread.sleep(frame_rate);
                    }
                    catch(Exception ex)
                    {

                    }
                }
            }
        }
        final BarUpdater b = new BarUpdater();
        b.start();
    }
    public void save_and_exit()
    {
        e.is_active = false;
        c.is_in_progress = false;
        this.dispose();
        System.out.println("close");
    }
    public ProgressWindow(File_encryptor e,String Message)
    {
        super("Progress");
        this.e=e;
        initialize();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                save_and_exit();
            }
        });
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.messageLabel.setText(Message);
        this.pack();
        System.out.println("Constructor");
        abortButton.addActionListener(e1 -> {
            save_and_exit();
        });
        this.setVisible(true);
    }
    private JProgressBar progressBar;
    private JButton abortButton;
    private JLabel messageLabel;
    private JPanel mainPanel;
    private JFrame main=this;
}
