import java.math.BigInteger;
import java.util.Scanner;
public class RSA
{
    private static int number_of_digits_in(BigInteger x)
    {
        int ct=0;
        while(x.compareTo(BigInteger.valueOf(0))!=0)
        {
            x = x.divide(BigInteger.valueOf(10));
            ct++;
        }
        return ct;
    }
    private BigInteger gcd(BigInteger a,BigInteger b)
    {
        if(a.compareTo(BigInteger.valueOf(1)) == 0 || b.compareTo(BigInteger.valueOf(1)) == 0)
        {
            return BigInteger.valueOf(1);
        }
        else
        {
            BigInteger max = a.compareTo(b)>0 ? a : b;
            BigInteger min = a.compareTo(b)>0 ? b : a;
            return gcd(min,max.remainder(min));
        }
    }
    private static BigInteger totient(BigInteger p[])//p = array of distinct primes
    {
        BigInteger t = new BigInteger("1");
        for(int i=0;i<p.length;i++)
        {
            t=t.multiply(p[i].subtract(BigInteger.valueOf(1)));
        }     
        return t;
    }
    public static BigInteger encrypt(BigInteger data, BigInteger key, BigInteger n)
    {
        BigInteger encrypted_data = new BigInteger("1");
        for(BigInteger i = new BigInteger("0");i.compareTo(key)!=0;i=i.add(BigInteger.valueOf(1)))
        {
            encrypted_data = encrypted_data.multiply(data);
            encrypted_data = encrypted_data.remainder(n);
        }
        return encrypted_data;
    }
    public static BigInteger[] get_key_pair(BigInteger p[], BigInteger lower_limit, BigInteger upper_limit)//public key = -1 if unable to generate suitable value
    {
        BigInteger public_key = new BigInteger("-1");
        BigInteger product = totient(p).add(BigInteger.valueOf(1));
        BigInteger private_key = product.divide(public_key);
        if(lower_limit.compareTo(upper_limit)<=0)
        {
            for(BigInteger i = new BigInteger(lower_limit.toString());i.compareTo(upper_limit)!=0;i=i.add(BigInteger.valueOf(1)))
            {
                if(product.remainder(i).compareTo(BigInteger.valueOf(0))== 0)
                {
                    public_key = new BigInteger(i.toString());
                    private_key = product.divide(public_key);
                    break;
                }
            }
        }
        BigInteger key_pair[] = new BigInteger[2];
        key_pair[0] = public_key;
        key_pair[1] = private_key;
        return key_pair;
    }
    //ensure that totient+1 is not prime
    public static void main()
    {
        Scanner sc = new Scanner(System.in);
        //
        System.out.println("Enter no. of distinct primes:");
        int num = sc.nextInt();
        System.out.println("Enter primes:");
        BigInteger p[] = new BigInteger[num];
        BigInteger n = new BigInteger("1");
        for(int i=0;i<num;i++)
        {
            p[i] = new BigInteger(sc.next());
            n = n.multiply(p[i]);
        }
        BigInteger totient = totient(p);
        System.out.println("totient is:"+totient.toString());
        System.out.println("n is:"+n.toString());
        System.out.println("Enter sample message:");
        
        BigInteger m = new BigInteger(sc.next());//must be less than minimum of all p
        
        BigInteger lower_limit = new BigInteger("1");
        int power = number_of_digits_in(n)/number_of_digits_in(m);
        for(int i=1;i<=power;i++)lower_limit = lower_limit.multiply(BigInteger.valueOf(10));
        BigInteger upper_limit = lower_limit.add(BigInteger.valueOf(10000));
        System.out.println("lower limit is:"+lower_limit.toString());
        
        BigInteger[] key_pair = get_key_pair(p,lower_limit,upper_limit);
        System.out.println("Public key is:"+key_pair[0].toString());
        System.out.println("Private key is:"+key_pair[1].toString());
        BigInteger encrypted = encrypt(m,key_pair[0],n);
        System.out.println("Encrypted message is:"+encrypted);
        System.out.println("Decrypted message is:"+encrypt(encrypted,key_pair[1],n));        
    }
}
