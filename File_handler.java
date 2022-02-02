import java.io.*;
import java.text.*;
import java.util.*;
public class File_handler
{
    private String date_format;
    private String path;
    public File_handler(String date_format)
    {
        this.date_format = date_format;
        this.path = get_path()+"//";
    }
    public File_handler()
    {
        this.date_format = "dd-MM-yyyy_";
        this.path = get_path()+"//";
    }
    private static String get_path()
    {
        Scanner sc = new Scanner("");
        try
        {
            sc = new Scanner(new File("program files//default path.txt"));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("path file not found");
        }
        return sc.nextLine();
    }
    public String get_stamp()//returns a String unique to time of creation
    {
        String date = new SimpleDateFormat(date_format).format(new Date());
        long unix_epoch = new Date().getTime();
        return date+unix_epoch;
    }
    public static String get_extension(String extension)
    {
        return "."+extension;
    }
    public void create_file_and_write(String file_name,String extension,String data)throws Exception  //operations are handled byte by byte
    {
        Temp_tracker t = new Temp_tracker((int)data.length(),"Writing");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path+file_name+get_extension(extension)),64*1024);
        byte b[]=new byte[data.length()];
        for(int i=0;i<b.length;i++)
        {
            b[i] = (byte)data.charAt(i);
            t.update(i);
        }
        t.close();
        bos.write(b);       
        bos.close();
    }
    public String get_data_in_file(String file_name,String extension)throws Exception
    {
        File f = new File(path+file_name+get_extension(extension));
        Temp_tracker t = new Temp_tracker((int)f.length(),"Reading");
        String data = "";
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f),64*1024);
        int d;
        int i = 0;
        while((d=bis.read()) != -1)
        {
            data+=((char)d);
            t.update(i);
            i++;
        }
        bis.close();
        t.close();
        return data;
    }
    
}
