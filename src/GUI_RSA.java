import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import encryption.File_encryptor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class GUI_RSA extends JFrame
{
    public static final Path TEST = Paths.get("program files/test.bin");
    public static final Path TEST_ENCRYPTED = Paths.get("program files/test-encrypted.bin");
    public static final Path TEST_DECRYPTED = Paths.get("program files/test-decrypted.bin");
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static final String LAF_JSON_KEY = "Look and Feel";
    public static final String DEFAULT_DIRECTORY_SOURCE_PATH_JSON_KEY = "Default Source Directory Path";
    public static final String DEFAULT_DIRECTORY_DESTINATION_PATH_JSON_KEY = "Default DEstination Directory Path";
    public static final String BYTE_LIMIT_JSON_KEY = "Memory capacity";
    public static final String FONT_SIZE_JSON_KEY = "Console Font Size";
    public static final String PK_JSON_KEY = "PK";
    public static final String SK_JSON_KEY = "SK";
    public static final String N_JSON_KEY = "N";
    public static final HashMap<String,String> LOOK_AND_FEEL = get_all_looks_and_feels();
    public static final HashMap<String,Integer> FONT_SIZES = get_font_sizes();
    private static HashMap<String,Integer> get_font_sizes()
    {
        HashMap<String,Integer> fs = new HashMap<>();
        fs.put("Small",12);
        fs.put("Medium",16);
        fs.put("Large",20);
        fs.put("Huge",24);
        return fs;
    }
    private static HashMap<String,String> get_all_looks_and_feels()
    {
        HashMap<String,String> laf = new HashMap<>();
        laf.put("Acryl","com.jtattoo.plaf.acryl.AcrylLookAndFeel");
        laf.put("Aero","com.jtattoo.plaf.aero.AeroLookAndFeel");
        laf.put("Aluminum","com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
        laf.put("Bernstein","com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
        laf.put("Fast","com.jtattoo.plaf.fast.FastLookAndFeel");
        laf.put("Graphite","com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
        laf.put("HiFi","com.jtattoo.plaf.hifi.HiFiLookAndFeel");
        laf.put("Luna","com.jtattoo.plaf.luna.LunaLookAndFeel");
        laf.put("McWin","com.jtattoo.plaf.mcwin.McWinLookAndFeel");
        laf.put("Metal","javax.swing.plaf.metal.MetalLookAndFeel");
        laf.put("Michaelsoft Binbows","com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        laf.put("Mint","com.jtattoo.plaf.mint.MintLookAndFeel");
        laf.put("Motif","com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        laf.put("Nimbus","javax.swing.plaf.nimbus.NimbusLookAndFeel");
        laf.put("Noire","com.jtattoo.plaf.noire.NoireLookAndFeel");
        laf.put("Smart","com.jtattoo.plaf.smart.SmartLookAndFeel");
        laf.put("Texture","com.jtattoo.plaf.texture.TextureLookAndFeel");
        laf.put("Default","javax.swing.plaf.nimbus.NimbusLookAndFeel");
        return laf;
    }
    public static String get_look_and_feel_location(String look_and_feel) throws Exception
    {
        String location = LOOK_AND_FEEL.get(look_and_feel);
        if(location==null)throw new Exception("Look and Feel not found");
        return location;
    }
    public void set_look_and_feel()
    {
        try
        {
            UIManager.setLookAndFeel(get_look_and_feel_location(look_and_feel));
            System.out.println(ConsoleFont.getSize());
            //TODO solve this issue (font)
            //UIManager.put("TextArea.font", GUI_RISCV.ConsoleFont);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(main,ex.getStackTrace(),"Look and Feel not found",JOptionPane.ERROR_MESSAGE);
        }
        SwingUtilities.updateComponentTreeUI(main);
        main.pack();
    }
    public static void set_key(BigInteger pk, BigInteger sk, BigInteger n, boolean save_previous) throws Exception
    {
        if(save_previous)
        {
            JSONParser jp = new JSONParser();
            JSONObject obj = (JSONObject) jp.parse(new FileReader("program files/key.json"));
            BigInteger n_old = new BigInteger((String)obj.get(N_JSON_KEY));
            BigInteger pk_old = new BigInteger((String)obj.get(PK_JSON_KEY));
            BigInteger sk_old = new BigInteger((String)obj.get(SK_JSON_KEY));
            String key = "" + "\n----\nDate (saved) : " +
                    "\n----\nDate: "+df.format(new Date())+
                    "\nUnix Epoch: "+(System.currentTimeMillis()/1000)+
                    df.format(new Date()) +
                    "\nn: " + n_old +
                    "\npk: " + pk_old +
                    "\nsk: " + sk_old;
            Files.writeString(Paths.get("program files/history.txt"), key,StandardOpenOption.APPEND);
        }
        JSONObject obj = new JSONObject();
        obj.put(PK_JSON_KEY,pk.toString());
        obj.put(SK_JSON_KEY,sk.toString());
        obj.put(N_JSON_KEY,n.toString());
        Files.writeString(Paths.get("program files/key.json"), obj.toString());
    }
    public void load_key() throws Exception
    {
        JSONParser jp = new JSONParser();
        JSONObject obj = (JSONObject) jp.parse(new FileReader("program files/key.json"));
        N = new BigInteger((String)obj.get(N_JSON_KEY));
        PK = new BigInteger((String)obj.get(PK_JSON_KEY));
        SK = new BigInteger((String)obj.get(SK_JSON_KEY));
    }
    public static void load_preferences()
    {
        /*
        try to open config.json
        if not found, ask if one can be created.
        else
        String file type
        String default path (check if it exists, if not use system default)
        String Look and Feel (only name, not address, check if the combobox contains it)
         */
        JSONParser jp = new JSONParser();
        try
        {
            JSONObject obj = (JSONObject) jp.parse(new FileReader("program files/config.json"));
            try
            {
                Path default_directory_source_path = Paths.get((String)obj.get(DEFAULT_DIRECTORY_SOURCE_PATH_JSON_KEY));
                if(!(Files.isDirectory(default_directory_source_path) && Files.isWritable(default_directory_source_path)))throw new Exception("Invalid path for default source directory");
                default_directory_source = default_directory_source_path.toFile();
                Path default_directory_destination_path = Paths.get((String)obj.get(DEFAULT_DIRECTORY_DESTINATION_PATH_JSON_KEY));
                if(!(Files.isDirectory(default_directory_destination_path) && Files.isWritable(default_directory_destination_path)))throw new Exception("Invalid path for default destination directory");
                default_directory_destination = default_directory_destination_path.toFile();
                String laf = (String)obj.get(LAF_JSON_KEY);
                if(LOOK_AND_FEEL.containsKey(laf))look_and_feel=laf;
                else
                {
                    look_and_feel = "Default";
                    throw new Exception("look and feel not rcognized, using default look and feel");

                }
                int FontSize = (int)((long)obj.get(FONT_SIZE_JSON_KEY));
                ConsoleFont = ConsoleFont.deriveFont((float)(FontSize));
                byte_limit = (long)obj.get(BYTE_LIMIT_JSON_KEY);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(),e.getMessage()+"\nconfig.json has been corrupted, default settings will be used","Warning",JOptionPane.WARNING_MESSAGE);
            }
        }
        catch(Exception ex)
        {
            JOptionPane.showMessageDialog(new JFrame(),"config.json not found, using default settings","Warning",JOptionPane.WARNING_MESSAGE);
        }
    }
    public void save_and_exit()
    {
        main.dispose();
        JSONObject obj = new JSONObject();
        obj.put(DEFAULT_DIRECTORY_SOURCE_PATH_JSON_KEY,default_directory_source.getAbsolutePath());
        obj.put(DEFAULT_DIRECTORY_DESTINATION_PATH_JSON_KEY,default_directory_destination.getAbsolutePath());
        obj.put(LAF_JSON_KEY,look_and_feel);
        obj.put(FONT_SIZE_JSON_KEY,ConsoleFont.getSize());
        obj.put(BYTE_LIMIT_JSON_KEY,byte_limit);
        try
        {
            Files.writeString(Paths.get("program files/config.json"), obj.toString());
            set_key(PK,SK,N,saveKeysBeforeReplacementRadioButton.isSelected());
        }
        catch(Exception ex){
            ex.printStackTrace();}//no error message here, it will be jarring to see one after closing
        System.exit(0);
    }
    public void set_byte_limit()
    {
        String order = memoryUnitComboBox.getSelectedItem().toString();
        int value = Integer.parseInt(memoryValueComboBox.getSelectedItem().toString());
        switch (order) {
            case "KB" -> value *= 1024;
            case "MB" -> value *= 1024 * 1024;
            case "GB" -> value *= 1024 * 1024 * 1024;
        }
        byte_limit=value;
    }
    public void initialize()
    {

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        try
        {
            load_key();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(new JFrame(),e.getMessage()+"\nkey.json has been corrupted, no key is present","Warning",JOptionPane.WARNING_MESSAGE);
            PK = BigInteger.ONE;
            SK = BigInteger.ONE;
            N = new BigInteger("1024");
        }
        this.PK_main_textField.setText(PK.toString());
        this.SK_main_textField.setText(SK.toString());
        this.N_main_textField.setText(N.toString());
        this.sourceDirectoryTextField.setText(default_directory_source.getAbsolutePath());
        this.destinationDirectoryTextField.setText(default_directory_destination.getAbsolutePath());
        this.LFComboBox.setSelectedItem(look_and_feel);
    }
    public GUI_RSA()
    {
        super("RSA encryption");
        ImageIcon icon = new ImageIcon("program files/icon.png");
        this.setIconImage(icon.getImage());
        initialize();
        main.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                save_and_exit();
            }
        });
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        LFComboBox.addActionListener(e -> {
            look_and_feel = LFComboBox.getSelectedItem().toString();
            set_look_and_feel();
        });
        consoleFontComboBox.addActionListener(e -> {
            int size = FONT_SIZES.get(consoleFontComboBox.getSelectedItem());
            ConsoleFont = ConsoleFont.deriveFont((float) (size));
            set_look_and_feel();
        });
        sourceDirectoryChangeButton.addActionListener(e -> {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rv = fc.showOpenDialog(GUI_RSA.this);
            if(rv == JFileChooser.APPROVE_OPTION)
            {
                default_directory_source = fc.getSelectedFile();
                sourceDirectoryTextField.setText(default_directory_source.getAbsolutePath());
                fc.setCurrentDirectory(default_directory_source);
            }
        });
        destinationDirectoryChangeButton.addActionListener(e -> {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rv = fc.showOpenDialog(GUI_RSA.this);
            if(rv == JFileChooser.APPROVE_OPTION)
            {
                default_directory_destination = fc.getSelectedFile();
                destinationDirectoryTextField.setText(default_directory_destination.getAbsolutePath());
                fc.setCurrentDirectory(default_directory_destination);
            }
        });
        class ShowHideActionListener implements ActionListener
        {
            final JTextField tf;
            final JButton b;
            final Color foreground;
            boolean is_hidden;
            public ShowHideActionListener(JButton b,JTextField tf, boolean is_hidden_by_default)
            {
                this.b = b;
                is_hidden=is_hidden_by_default;
                this.tf=tf;
                this.foreground=tf.getForeground();
                if(is_hidden)
                {
                    b.setText("Show");
                    tf.setForeground(tf.getBackground());
                    tf.setCaretColor(this.foreground);
                    tf.repaint();
                }
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                if(is_hidden)
                {
                    b.setText("Hide");
                    tf.setForeground(foreground);
                }
                else
                {
                    b.setText("Show");
                    tf.setForeground(tf.getBackground());
                    tf.setCaretColor(this.foreground);
                }
                tf.repaint();
                is_hidden=!is_hidden;
            }
        }
        PshowButton.addActionListener(new ShowHideActionListener(PshowButton,PtextField,false));
        QshowButton.addActionListener(new ShowHideActionListener(QshowButton,QtextField,false));
        SKshowButton.addActionListener(new ShowHideActionListener(QshowButton,SKtextField,false));
        generateButton.addActionListener(e -> {
            try
            {
                p = new BigInteger(PtextField.getText());
                q = new BigInteger(QtextField.getText());
                pk = new BigInteger(PKtextField.getText());
                sk = encryption.RSA.get_private_key(new BigInteger[]{p,q},pk);
                if(sk.compareTo(new BigInteger("-1"))==0)throw new Exception("Unable to generate a secret key");
                n = p.multiply(q);
                NtextField.setText(n.toString());
                SKtextField.setText(sk.toString());
                saveButton.setEnabled(true);
                testButton.setEnabled(true);
                JOptionPane.showMessageDialog(keyPanel,"Key successfully generated","Info",JOptionPane.INFORMATION_MESSAGE);
            }catch(Exception ex)
            {
                JOptionPane.showMessageDialog(keyPanel,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        saveButton.addActionListener(e -> {
            /*
            use the following format:
            ----
            Date:
            Unix Epoch:
            p:
            q:
            n:
            pk:
            sk:
             */
            StringBuilder key = new StringBuilder();
            key.append("\n----\nDate: ").append(df.format(new Date()));
            key.append("\nUnix Epoch: ").append(System.currentTimeMillis()/1000);
            key.append("\np: ").append(p.toString());
            key.append("\nq: ").append(q.toString());
            key.append("\nn: ").append(n.toString());
            key.append("\npk: ").append(pk.toString());
            key.append("\nsk: ").append(sk.toString());
            try
            {
                Files.writeString(Paths.get("program files/history.txt"),key, StandardOpenOption.APPEND);
                JOptionPane.showMessageDialog(keyPanel,"key stored in history.txt","Info",JOptionPane.INFORMATION_MESSAGE);
            }catch(Exception ex)
            {
                JOptionPane.showMessageDialog(keyPanel,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        testButton.addActionListener(e -> {

        });
        setButton.addActionListener(e -> {
            try
            {
                PK = pk;
                SK = sk;
                N = n;
                PKtextField.setText(PK.toString());
                SKtextField.setText(SK.toString());
                NtextField.setText(N.toString());
                set_key(pk,sk,n,saveKeysBeforeReplacementRadioButton.isSelected());
                JOptionPane.showMessageDialog(keyPanel,"key updated in key.json","Info",JOptionPane.INFORMATION_MESSAGE);
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(keyPanel,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        class ChangeBigIntegerActionListener implements ActionListener
        {
            final String big_int_name;
            final JTextField display;
            public ChangeBigIntegerActionListener(String big_int_name,JTextField display)
            {
                this.big_int_name = big_int_name;
                this.display=display;
            }
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    BigInteger big_int = new BigInteger(JOptionPane.showInputDialog("Enter the new value:"));
                    switch (big_int_name) {
                        case "N" -> N = big_int;
                        case "PK" -> PK = big_int;
                        case "SK" -> SK = big_int;
                    }
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(mainPanel,ex.getMessage()+"\nchange unsuccessful","Error",JOptionPane.ERROR_MESSAGE);
                }
            }

        }
        PK_main_changeButton.addActionListener(new ChangeBigIntegerActionListener("PK",PK_main_textField));
        SK_main_changeButton.addActionListener(new ChangeBigIntegerActionListener("SK",SK_main_textField));
        N_main_changeButton.addActionListener(new ChangeBigIntegerActionListener("N",N_main_textField));
        showButton.addActionListener(new ShowHideActionListener(showButton,SK_main_textField,true));
        memoryValueComboBox.addActionListener(e -> set_byte_limit());
        memoryUnitComboBox.addActionListener(e -> set_byte_limit());
        clearHistoryButton.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(keyPanel,"Are you sure you want to clear history.txt?\nThis action is irreversible")==JOptionPane.YES_OPTION)
            {
                try
                {
                    Files.writeString(Paths.get("program files/history.txt"),"");
                    JOptionPane.showMessageDialog(keyPanel,"history cleared successfully","Info",JOptionPane.INFORMATION_MESSAGE);
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(keyPanel,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        class OperationThread extends Thread
        {
            File_encryptor fe;
            boolean is_encryption;
            String message;
            public OperationThread(File_encryptor fe,boolean is_encryption,String message)
            {
                this.fe=fe;
                this.is_encryption = is_encryption;
                this.message = message;
            }
            @Override
            public void run()
            {
                ProgressWindow p = new ProgressWindow(fe,message);
                try
                {
                    if(is_encryption)fe.encrypt();
                    else fe.decrypt();
                }catch (Exception e)
                {
                    JOptionPane.showMessageDialog(mainPanel,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
                p.save_and_exit();
            }
        }
        // file.ex becomes file (encrypted at EPOCH).ex and is stored in destination directory
        encryptButton.addActionListener(e -> {
            try
            {
                String name = chosen_file.getName();
                int i = name.lastIndexOf('.');
                String extension = "";
                if(i!=-1)extension = name.substring(i);
                name = name.substring(0,name.lastIndexOf('.'));
                Path path = Paths.get(default_directory_destination.getAbsolutePath()+"/"+name+" (encrypted at EPOCH "+System.currentTimeMillis()/1000+")"+extension);
                Files.writeString(path,"");
                File_encryptor fe = new File_encryptor(N,PK,(int)byte_limit,Paths.get(chosen_file.getAbsolutePath()),path);
                OperationThread et = new OperationThread(fe,true,"Encrypting "+chosen_file.getName());
                et.start();
                JOptionPane.showMessageDialog(mainPanel,name+extension+" has been encrypted and stored in "+path.toString(),"Info",JOptionPane.INFORMATION_MESSAGE);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        decryptButton.addActionListener(e -> {
            try
            {
                String name = chosen_file.getName();
                int i = name.lastIndexOf('.');
                String extension = "";
                if(i!=-1)extension = name.substring(i);
                name = name.substring(0,name.lastIndexOf('.'));
                Path path = Paths.get(default_directory_destination.getAbsolutePath()+"/"+name+" (decrypted at EPOCH "+System.currentTimeMillis()/1000+")"+extension);
                Files.writeString(path,"");
                File_encryptor fe = new File_encryptor(N,SK,(int)byte_limit,Paths.get(chosen_file.getAbsolutePath()),path);
                OperationThread et = new OperationThread(fe,false,"Decrypting "+chosen_file.getName());
                et.start();
                JOptionPane.showMessageDialog(mainPanel,name+extension+" has been decrypted and stored in "+path.toString(),"Info",JOptionPane.INFORMATION_MESSAGE);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        });
        loadFileButton.addActionListener(e -> {
            JFileChooser filechooser = new JFileChooser();
            filechooser.setCurrentDirectory(default_directory_source);
            int rv = filechooser.showOpenDialog(GUI_RSA.this);
            if(rv == JFileChooser.APPROVE_OPTION)
            {
                chosen_file = filechooser.getSelectedFile();
                chosenFileNameTextField.setText(chosen_file.getAbsolutePath());
                encryptButton.setEnabled(true);
                decryptButton.setEnabled(true);
            }
        });
        sourceDirectoryChangeButton.addActionListener(e -> {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int rv = fc.showOpenDialog(GUI_RSA.this);
            if(rv == JFileChooser.APPROVE_OPTION)
            {
                default_directory_source = fc.getSelectedFile();
                sourceDirectoryTextField.setText(default_directory_source.getAbsolutePath());
                fc.setCurrentDirectory(default_directory_source);
            }
        });
        destinationDirectoryChangeButton.addActionListener(e -> {
            int rv = fc.showOpenDialog(GUI_RSA.this);
            if(rv == JFileChooser.APPROVE_OPTION)
            {
                default_directory_destination = fc.getSelectedFile();
                destinationDirectoryTextField.setText(default_directory_destination.getAbsolutePath());
            }
        });
        testButton.addActionListener(e -> {
            try
            {
                Files.writeString(TEST_ENCRYPTED,"");
                Files.writeString(TEST_DECRYPTED,"");
                File_encryptor fee = new File_encryptor(n,pk,(int)byte_limit,TEST,TEST_ENCRYPTED);
                fee.encrypt();
                System.out.println("Encryption complete");
                File_encryptor fed = new File_encryptor(n,sk,(int)byte_limit,TEST_ENCRYPTED,TEST_DECRYPTED);
                fed.decrypt();
                System.out.println("Decryption complete");
                byte[] b0 = Files.readAllBytes(TEST);
                byte[] b1 = Files.readAllBytes(TEST_DECRYPTED);
                if(b0.length!=b1.length)throw new Exception("Error detected: length mismatch");
                for(int i =0;i<b0.length;i++)if(b0[i]!=b1[i])throw new Exception("Error detected");
                JOptionPane.showMessageDialog(mainPanel,"Test successful","Info",JOptionPane.INFORMATION_MESSAGE);
            }
            catch(Exception ex)
            {
                JOptionPane.showMessageDialog(mainPanel,"Test failed","Error",JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }

    private final JFrame main = this;
    private final JFileChooser fc = new JFileChooser();
    private static File default_directory_source = new JFileChooser().getCurrentDirectory();
    private static File default_directory_destination = new JFileChooser().getCurrentDirectory();
    private static File chosen_file = null;
    private static String look_and_feel = "Nimbus";
    private static Font ConsoleFont = new Font("Consolas",Font.PLAIN,16);
    public static Font get_console_font()
    {
        return ConsoleFont;
    }
    public static String get_look_and_feel()
    {
        return look_and_feel;
    }
    private static BigInteger p = null; // these are used only for setting key
    private static BigInteger q =null;
    private static BigInteger pk = null;
    private static BigInteger sk = null;
    private static BigInteger n = null;
    private static BigInteger PK = null; // these are used for encryption and decryption
    private static BigInteger SK = null;
    private static BigInteger N = null;
    private static long byte_limit = 1024*1024;
    private JTabbedPane tabbedPane1;
    private JPanel mainPanel;
    private JPanel settingsTab;
    private JTextField sourceDirectoryTextField;
    private JButton sourceDirectoryChangeButton;
    private JComboBox LFComboBox;
    private JComboBox consoleFontComboBox;
    private JButton destinationDirectoryChangeButton;
    private JTextField destinationDirectoryTextField;
    private JTextField PtextField;
    private JTextField QtextField;
    private JTextField SKtextField;
    private JTextField PKtextField;
    private JTextField NtextField;
    private JButton PshowButton;
    private JButton QshowButton;
    private JButton SKshowButton;
    private JButton generateButton;
    private JButton saveButton;
    private JPanel keyPanel;
    private JButton testButton;
    private JButton setButton;
    private JTabbedPane tabbedPane2;
    private JTextArea LIDSATextArea;
    private JButton ipsumButton;
    private JRadioButton dolorRadioButton;
    private JComboBox comboBox1;
    private JTextField PK_main_textField;
    private JTextField SK_main_textField;
    private JTextField N_main_textField;
    private JButton PK_main_changeButton;
    private JButton SK_main_changeButton;
    private JButton N_main_changeButton;
    private JButton showButton;
    private JTextField chosenFileNameTextField;
    private JButton loadFileButton;
    private JButton encryptButton;
    private JButton decryptButton;
    private JComboBox memoryUnitComboBox;
    private JComboBox memoryValueComboBox;
    private JButton clearHistoryButton;
    private JRadioButton saveKeysBeforeReplacementRadioButton;
}
