import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;
public class File_encryption
{
    //key
    static BigInteger n = new BigInteger("835586163981545225127736337227399172548174195744666123151235223401771546673148886692994518172834583144481");
    static BigInteger public_key = new BigInteger("65537");
    // private_key = 413324263606111556955474870382193667327869666398553887968644570271419186046529395457384581012743805273473
    // prime 1 = 232015641148467178379124681607892174979481
    // prime 2 = 3601421696595240782412944634084439747505399112193100441489965001
    static String exit_code = "<exit>";
    static String date_format = "dd-MM-yyyy_";
    static String separator = "  -  ";
    public static void main() throws IOException
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
        String encrypted_message = encrypt(message);
        message = "";
        create_file_and_write(get_file_name(),encrypted_message);
    }
    public static void get_decrypted_file(String file_name,String key) throws IOException,FileNotFoundException //creates a file containing decrypted form of file with file
    {
        String encrypted_message = get_data_in_file(file_name); 
        String message = decrypt(encrypted_message, new BigInteger(key));
        create_file_and_write(file_name+separator+"decrypted at "+get_file_name(),message);
    }
    public static void get_encrypted_file(String file_name) throws IOException,FileNotFoundException //creates a file containing encrypted form of file with file_name
    {
        String message = get_data_in_file(file_name);
        String encrypted_message = encrypt(message);
        create_file_and_write(file_name+separator+"encrypted at "+get_file_name(),encrypted_message);
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
            b=b.add(BigInteger.valueOf(((int)s.charAt(i))%256).multiply(p));
            p=p.multiply(BigInteger.valueOf(256));
        }
        return b;
    }
    public static String process(BigInteger b)//recovers string from associated number
    {
        String s = "";
        while(b.compareTo(BigInteger.valueOf(0)) != 0)
        {
            s = s+((char)(b.remainder(BigInteger.valueOf(256)).intValue()));
            b = b.divide(BigInteger.valueOf(256));
        }
        return s;
    }
    
    //file manipulation
    public static String get_file_name()//returns a String unique to time of creation
    {
        String date = new SimpleDateFormat(date_format).format(new Date());
        long unix_epoch = new Date().getTime();
        return date+unix_epoch;
    }
    public static void create_file_and_write(String file_name,String s)throws IOException
    {
        File f = new File(file_name+".txt");
        if(!f.createNewFile())
        {
            System.out.print("error");
        }
        FileWriter fw = new FileWriter(f.getName());
        fw.write(s);
        fw.close();
    }
    public static String get_data_in_file(String file_name) throws FileNotFoundException
    {
        String data = "";
        File f = new File(file_name+".txt");
        Scanner sc=new Scanner(f);
        while(sc.hasNextLine())
        {
          data+=sc.nextLine()+"\n";
        }
        return data;
    }
}
