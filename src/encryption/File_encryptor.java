package encryption;

import java.nio.file.*;
import java.math.*;
import java.util.*;
import java.io.*;

public class File_encryptor
{
    /*
    A unique File_encryptor object is made for each file
    One object carries out only one process before deactiviating
    the destination path and source path must be specified beforehand
    the process can be terminated at any time by calling deactivate()
     */
    private BigInteger n;   // n used to encrypt/decrypt
    private BigInteger key; // key used to encrypt/decrypt
    public int memory_capacity; //proportional to max RAM usage
    private int byte_limit; // number of bytes per chunk
    private volatile long bytes_processed;
    private volatile long total_bytes;
    public long get_bytes_processed()
    {
        return bytes_processed;
    }
    public long get_total_bytes()
    {
        return total_bytes;
    }
    private volatile boolean is_active = true;
    public void deactivate(){is_active=false;}
    public boolean is_complete(){return is_complete;}
    public volatile boolean is_complete = false;
    private volatile Path p;
    private volatile Path d;
    public File_encryptor(BigInteger n, BigInteger key, int mc, Path p, Path d)
    {
        this.n = n;
        this.key = key;
        this.memory_capacity = mc;
        byte_limit = process(n).size() - 1;
        this.p = p;
        this.d = d;
        total_bytes = p.toFile().length();
    }
    private static boolean is_valid(char ch)
    {
        if(ch <= '9' && ch >= '0')return true;
        return false;
    }
    public void encrypt() throws Exception
    {
        if(!is_active)throw new Exception("Operation complete/aborted");
        int mc = memory_capacity - memory_capacity%byte_limit; //ensures that memory capacity is a multiple of byte limit
        if(mc==0)throw new Exception("memory capacity too low");
        byte[] b = new byte[mc];
        InputStream ins = Files.newInputStream(p);
        int k = 0;
        while(is_active)
        {
            int l = ins.readNBytes(b,0,mc);
            List<Byte> b_c = new ArrayList<>();
            List<String> encrypted_numbers = new ArrayList<>();
            for(int i=0;i<l;i++)
            {
                bytes_processed = mc*k+i;
                if(b_c.size() == byte_limit)
                {
                    BigInteger enc = RSA.encrypt(process(b_c),key,n);
                    b_c.clear();
                    encrypted_numbers.add(enc.toString());
                }
                b_c.add(b[i]);
            }
            BigInteger enc = RSA.encrypt(process(b_c),key,n);
            b_c.clear();
            encrypted_numbers.add(enc.toString());
            
            Files.write(d,encrypted_numbers,StandardOpenOption.WRITE,StandardOpenOption.APPEND);
            if(l<mc)
            {
                is_complete = true;
                break;
            }
            k++;
        }
        if(!is_complete)Files.writeString(d,"");
        is_active = false;
    }
    public void decrypt() throws Exception
    {
        if(!is_active)throw new Exception("Operation complete/aborted");
        total_bytes = p.toFile().length();
        bytes_processed = 0;
        int mc = memory_capacity - memory_capacity%byte_limit; //ensures that memory capacity is a multiple of byte limit
        if(mc==0)throw new Exception("memory capacity too low");
        byte[] b = new byte[mc];
        StringBuilder number = new StringBuilder();
        List<Byte> data = new ArrayList<Byte>();
        InputStream ins = Files.newInputStream(p);
        int k = 0;
        while(is_active)
        {
            int l = ins.readNBytes(b,0,mc);
            boolean exists_number_to_process = false;
            for(int i=0;i<l;i++)
            {
                bytes_processed = mc*k+i;
                char ch = (char)b[i];
                if(is_valid(ch))
                {
                    exists_number_to_process = true;
                    number.append(ch);
                }
                else if(exists_number_to_process)//simple check for '\n' does not work since different OS have different strings to signify nextline
                {
                    BigInteger num = new BigInteger(number.toString());
                    data.addAll(process(RSA.encrypt(num,key,n)));
                    number.setLength(0);
                    exists_number_to_process = false;
                }
            }
            byte[] data_primitive = new byte[data.size()];
            for(int i=0;i<data.size();i++)data_primitive[i] = data.get(i);
            data.clear();
            Files.write(d,data_primitive,StandardOpenOption.WRITE,StandardOpenOption.APPEND);
            if(l<mc)
            {
                is_complete = true;
                break;
            }
            k++;
        }
        if(!is_complete)Files.writeString(d,"");
        is_active = false;
    }
    public static BigInteger process(List<Byte> data)//byte-set converted into BigInteger 
    {
        BigInteger b = new BigInteger("0");
        int s = data.size();
        for(int i=0;i!=s;i++)
        {
            b=b.add(BigInteger.valueOf(((int)data.get(i)+256)%256 + 1).shiftLeft(8*i)); //b = b + f(det)*256^i;
        }
        return b;
    }
    public static List<Byte> process(BigInteger b)//recovers byte-set from associated BigInteger
    {
        List<Byte> data = new ArrayList<Byte>();
        while(b.compareTo(BigInteger.ZERO) != 0)
        {
            int det = ((b.remainder(BigInteger.valueOf(256)).intValue())+255)%256;
            b = b.subtract(BigInteger.valueOf(det));
            data.add((byte)det);
            b = b.shiftRight(8); //b = b/256;
        }
        return data;
    }
}
