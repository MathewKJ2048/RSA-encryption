import java.io.*;
import java.text.*;
import java.util.*;
public class File_handler
{
    static String date_format = "dd-MM-yyyy_";
    public static String get_stamp()//returns a String unique to time of creation
    {
        String date = new SimpleDateFormat(date_format).format(new Date());
        long unix_epoch = new Date().getTime();
        return date+unix_epoch;
    }
    public static String get_extension(String extension)
    {
        return "."+extension;
    }
    public static void create_file_and_write(String file_name,String extension,String data)throws Exception  //operations are handled byte by byte
    {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file_name+get_extension(extension)));
        byte b[]=new byte[data.length()];
        for(int i=0;i<b.length;i++)
        {
            b[i] = (byte)data.charAt(i);
        }
        bos.write(b);       
        bos.close();
    }
    public static String get_data_in_file(String file_name,String extension)throws Exception
    {
        String data = "";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file_name+get_extension(extension)));
        int d;
        while((d=bis.read()) != -1)
        {
            data+=((char)d);
        }
        bis.close();
        return data;
    }
}
