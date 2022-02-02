import java.util.Scanner;
import java.math.BigInteger;


public class Main
{
    //key
    static BigInteger n = new BigInteger("19717277623555823968555502299731905161919099450883585997065862193190696922688640140068335881");
    
    static BigInteger public_key = new BigInteger("65537");
    //
    static String exit_code = "<exit>";
    static String separator = "  -  ";
    public static void main() throws Exception
    {
        Scanner sc = new Scanner(System.in);
        File_handler file_handler = new File_handler();
        int ct = 0;
        String message = "";
        String s = "";
        do
        {
            System.out.println("Type \"<exit>\" once complete.\nnumber of lines: " + ct);
            s = sc.nextLine();
            if(s.trim().compareTo(exit_code)==0)
            {
                break;
            }
            ct++;
            message += s + "\n";
            System.out.print("\f");
        }while(true);
        String file_name = file_handler.get_stamp();
        System.out.println("Enter filename: ");
        file_name=sc.nextLine()+separator+"encrypted at "+file_handler.get_stamp();
        String encrypted_message = File_encryption.encrypt(message,public_key,n,null);
        message = "";
        file_handler.create_file_and_write(file_name,"txt",encrypted_message);
    }
    public static void get_decrypted_file(String file_name,String file_extension,String key) throws Exception //creates a file containing decrypted form of file with file
    {
        File_handler file_handler = new File_handler();
        String encrypted_message = file_handler.get_data_in_file(file_name,file_extension); 
        Temp_tracker t = new Temp_tracker(encrypted_message.length(),"Decrypting");
        String message = File_encryption.decrypt(encrypted_message, new BigInteger(key), n, t);
        t.close();
        file_handler.create_file_and_write(file_name+separator+"decrypted at "+file_handler.get_stamp(),file_extension,message);
    }
    public static void get_encrypted_file(String file_name,String file_extension) throws Exception //creates a file containing encrypted form of file with file_name
    {
        File_handler file_handler = new File_handler();
        String message = file_handler.get_data_in_file(file_name,file_extension);
        Temp_tracker t = new Temp_tracker(message.length(),"Encrypting");
        String encrypted_message = File_encryption.encrypt(message,public_key,n,t);
        t.close();
        file_handler.create_file_and_write(file_name+separator+"encrypted at "+file_handler.get_stamp(),file_extension,encrypted_message);
    }
}
