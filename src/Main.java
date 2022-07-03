import javax.swing.*;

public class Main
{
    public static void main(String args[])
    {
        try
        {
            GUI();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

        public static void GUI() throws Exception
        {
            GUI_RSA.load_preferences();
            UIManager.setLookAndFeel(GUI_RSA.get_look_and_feel_location(GUI_RSA.get_look_and_feel()));
            UIManager.put("TextArea.font",GUI_RSA.get_console_font());
            GUI_RSA r = new GUI_RSA();
        }

}
