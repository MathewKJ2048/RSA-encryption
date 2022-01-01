import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;
public class File_encryption
{
    //key
    static BigInteger n = new BigInteger("364052115966936022493842208845854663016163022850696051929824094409147132591584907267");
    static BigInteger public_key = new BigInteger("65537");
    //private key is 42400625618743956233814449549421069667552103634001735850795859797187521719443994577
    //
    static String exit_code = "<exit>";
    static String separator = "  -  ";
    public static void main() throws Exception
    {
        Scanner sc = new Scanner(System.in);
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
        String file_name = File_handler.get_stamp();
        System.out.println("Enter filename: ");
        file_name=sc.nextLine()+separator+"encrypted at "+File_handler.get_stamp();
        String encrypted_message = encrypt(message);
        message = "";
        File_handler.create_file_and_write(file_name,"txt",encrypted_message);
    }
    public static void get_decrypted_file(String file_name,String file_extension,String key) throws Exception //creates a file containing decrypted form of file with file
    {
        String encrypted_message = File_handler.get_data_in_file(file_name,file_extension); 
        String message = decrypt(encrypted_message, new BigInteger(key));
        File_handler.create_file_and_write(file_name+separator+"decrypted at "+File_handler.get_stamp(),file_extension,message);
    }
    public static void get_encrypted_file(String file_name,String file_extension) throws Exception //creates a file containing encrypted form of file with file_name
    {
        String message = File_handler.get_data_in_file(file_name,file_extension);
        String encrypted_message = encrypt(message);
        File_handler.create_file_and_write(file_name+separator+"encrypted at "+File_handler.get_stamp(),file_extension,encrypted_message);
    }
    
    //String encryption/decryption
    public static String encrypt(String message)//gives message encrypted as chunks with each chunk separated by '\n'
    {
        String encrypted_message = "";
        String s = "";
        int length = message.length();
        int limit = get_char_limit();
        for(int i=0;i<length;i++)
        {
            s = s+message.charAt(i);
            if(s.length() == limit)
            {
                encrypted_message += RSA.encrypt(process(s),public_key,n).toString() + "\n";
                s = "";
            }
        }
        encrypted_message += RSA.encrypt(process(s),public_key,n).toString() + "\n";
        return encrypted_message;
    }
    public static String decrypt(String encrypted_message, BigInteger key)
    {
        String message = "";
        String s = "0";
        int length = encrypted_message.length();
        for(int i=0;i<length;i++)
        {
            if(encrypted_message.charAt(i) == '\n')
            {
                message += process(RSA.encrypt(new BigInteger(s),key,n));
                s = "0";
            }
            else
            {
                s = s+encrypted_message.charAt(i);
            }
        }
        return message;
    }
    
    //String/BigInteger processing
    public static int get_char_limit()//returns maximum string length for encryption, original message broken into chunks of this size
    {
        int limit = -1;
        BigInteger test = new BigInteger("1");
        while(test.compareTo(n)<0)
        {
            test = test.multiply(BigInteger.valueOf(256));
            limit+=1;
        }
        return limit;
    }
    public static BigInteger process(String s)//characters' ASCII values used as digits in a number with base 256
    {
        BigInteger b = new BigInteger("0");
        BigInteger p = new BigInteger("1");
        for(int i=0;i<s.length();i++)
        {
            b=b.add(BigInteger.valueOf(((int)s.charAt(i))%256 + 1).multiply(p));
            p=p.multiply(BigInteger.valueOf(256));
        }
        return b;
    }
    public static String process(BigInteger b)//recovers string from associated number
    {
        String s = "";
        while(b.compareTo(BigInteger.valueOf(0)) != 0)
        {
            int det = b.remainder(BigInteger.valueOf(256)).intValue();
            det-=1;
            det+=256;
            det%=256;
            b = b.subtract(BigInteger.valueOf(det));
            s = s+((char)det);
            b = b.divide(BigInteger.valueOf(256));
        }
        return s;
    }
}
