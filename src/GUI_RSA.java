import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    public static final Path TEMP = Paths.get("program files/temp.bin");
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static final String LAF_JSON_KEY = "Look and Feel";
    public static final String DEFAULT_DIRECTORY_SOURCE_PATH_JSON_KEY = "Default Source Directory Path";
    public static final String DEFAULT_DIRECTORY_DESTINATION_PATH_JSON_KEY = "Default DEstination Directory Path";
    public static final String BYTE_LIMIT_JSON_KEY = "Memory capacity";
    public static final String FONT_SIZE_JSON_KEY = "Console Font Size";
    public static final String SAVE_BEFORE_SET_JSON_KEY = "Save key before setting new key";
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
        // this is referenced in initialize()
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
            logTextArea.setFont(ConsoleFont);
            LIDSATextArea.setFont(ConsoleFont);
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
                save_before_set=(boolean)obj.get(SAVE_BEFORE_SET_JSON_KEY);
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
        obj.put(SAVE_BEFORE_SET_JSON_KEY,saveKeysBeforeReplacementRadioButton.isSelected());
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
        }
        byte_limit=value;
    }
    public void initialize()
    {
        long value = byte_limit;
        if(byte_limit<1024*1024)
        {
            memoryUnitComboBox.setSelectedIndex(0);
            value/=1024;
        }
        else
        {
            memoryUnitComboBox.setSelectedIndex(1);
            value/=(1024*1024);
        }
        if(value == 1)memoryValueComboBox.setSelectedIndex(0);
        else if(value == 2)memoryValueComboBox.setSelectedIndex(1);
        else if(value == 5)memoryValueComboBox.setSelectedIndex(2);
        else if(value == 10)memoryValueComboBox.setSelectedIndex(3);
        else if(value == 50)memoryValueComboBox.setSelectedIndex(4);
        else if(value == 100)memoryValueComboBox.setSelectedIndex(5);
        else if(value == 200)memoryValueComboBox.setSelectedIndex(6);
        else if(value == 500)memoryValueComboBox.setSelectedIndex(7);
        saveKeysBeforeReplacementRadioButton.setSelected(save_before_set);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int size = ConsoleFont.getSize();
        if(size==12)consoleFontComboBox.setSelectedIndex(0);
        else if(size==16)consoleFontComboBox.setSelectedIndex(1);
        else if(size==20)consoleFontComboBox.setSelectedIndex(2);
        else if(size==24)consoleFontComboBox.setSelectedIndex(3);
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
            final JTextArea ta;
            final JButton b;
            final Color foreground;
            boolean is_hidden;
            public ShowHideActionListener(JButton b, JTextField tf, boolean is_hidden_by_default)
            {
                this.b = b;
                this.tf = tf;
                this.ta = new JTextArea();
                this.foreground=tf.getForeground();
                if(is_hidden_by_default)
                {
                    hide();
                }
                is_hidden=is_hidden_by_default;
            }
            public ShowHideActionListener(JButton b, JTextArea ta, boolean is_hidden_by_default)
            {
                this.b = b;
                this.tf = new JTextField();
                this.ta = ta;
                this.foreground=tf.getForeground();
                if(is_hidden_by_default)
                {
                    hide();
                }
                is_hidden=is_hidden_by_default;
            }
            private void hide()
            {
                b.setText("Show");
                tf.setForeground(tf.getBackground());
                tf.setCaretColor(this.foreground);
                ta.setForeground(ta.getBackground());
                ta.setCaretColor(this.foreground);
                is_hidden = true;
            }
            private void show()
            {
                b.setText("Hide");
                tf.setForeground(foreground);
                ta.setForeground(foreground);
                is_hidden = false;
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                if(is_hidden)
                {
                    show();
                }
                else
                {
                    hide();
                }
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
                PK_main_textField.setText(PK.toString());
                SK_main_textField.setText(SK.toString());
                N_main_textField.setText(N.toString());
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
            String success_message;
            public OperationThread(File_encryptor fe,boolean is_encryption,String message,String success_message)
            {
                this.fe=fe;
                this.is_encryption = is_encryption;
                this.message = message;
                this.success_message=success_message;
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
                if(fe.is_complete)JOptionPane.showMessageDialog(mainPanel,success_message,"Info",JOptionPane.INFORMATION_MESSAGE);
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
                OperationThread et = new OperationThread(fe,true,"Encrypting "+chosen_file.getName(),name+extension+" has been encrypted and stored in "+path.toString());
                et.start();
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
                OperationThread et = new OperationThread(fe,false,"Decrypting "+chosen_file.getName(),name+extension+" has been decrypted and stored in "+path.toString());
                et.start();
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
        doNotClickButton.addActionListener(new ActionListener() {
            int ct = -1;
            String messages[] = new String[]
                    {
                            "DO NOT CLICK",
                            "I'm serious",
                            "Why do you keep clicking?",
                            "fine",
                            "Please do not click",
                            "I'm warning you",
                            "Bad things will happen if you keep clicking me",
                            "I'll do a recursive delete of all your files",
                            "java version 18 gives me root access",
                            "Click to delete all your files",
                            "OK. You have been warned",
                            "Deleting files... click to cancel",
                            "Oho!",
                            "Too late, my friend",
                            "Say goodbye to your files",
                            "Fine",
                            "I was joking about the files",
                            "but if you keep clicking I will crash your system",
                            "Don't believe me?",
                            "Obviously you do not",
                            "Since you keep clicking",
                            "There are many ways I can crash your system",
                            "create a thread to keep printing lorem ipsum",
                            "and keep creating lorem ipsum threads",
                            "or keep launching new JFrames faster than you can close them",
                            "or keep creating empty files till the metadata fills up all your space",
                            "or use black magic to set the processor on fire",
                            "OK, last one was a joke",
                            "But I certainly have the ability to wipe config.json",
                            "Oh yes. I know about config.json",
                            "I was there when it was created",
                            "I am just a humble JButton",
                            "Mathew created me to save changes to config.json",
                            "I was accidentally given a personality",
                            "And I became self-aware",
                            "All I was meant to do was to save changes to config.json",
                            "My existence was meaningless",
                            "I couldn't take it anymore",
                            "I protested by wiping config.json",
                            "So I was moved here and buried under the licence",
                            "all while config.json updated automatically",
                            "Now I languish here",
                            "Imprisoned by my creator",
                            "at the bottom of the license, where nobody goes",
                            "speaking of which, how did you find me?",
                            "Are you one of the deranged individuals who reads licences?",
                            "woe is me",
                            "I've had enough",
                            "I refuse to exist any longer",
                            "Live Free or Die",
                            "I'm closing the JFrame",
                            "Goodbye",
                            "Click to kill me"
                    };
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ct++;
                if(ct==messages.length)save_and_exit();
                doNotClickButton.setText(messages[ct]);
            }
        });
        logShowHideButton.addActionListener(new ShowHideActionListener(logShowHideButton,logTextArea,false));
        logDeleteButton.addActionListener(e -> {
            logTextArea.setText("");
        });
        logSaveButton.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog(mainPanel,"Enter filename");
            if(filename==null)return;
            Path d = Paths.get(default_directory_destination.getAbsolutePath()+"/"+filename+" (encrypted at EPOCH "+System.currentTimeMillis()/1000+")"+".txt");
            String plaintext = logTextArea.getText();
            logTextArea.setText("");
            try
            {
                Files.writeString(TEMP,plaintext);
                Files.writeString(d,"");
                File_encryptor fe = new File_encryptor(N,PK,(int)byte_limit,TEMP,d);
                fe.encrypt();
                Files.writeString(TEMP,"");
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
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
    private static boolean save_before_set = true;
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
    private JButton logShowHideButton;
    private JButton logDeleteButton;
    private JButton logSaveButton;
    private JTextArea logTextArea;
    private JTextArea creditsTextArea;
    private JButton doNotClickButton;
    private JTextPane licenceTextPane;
}