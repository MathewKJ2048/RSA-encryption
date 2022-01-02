# RSA-encryption
An implementation of the simplest form of RSA encryption.

Author: Mathew K J  
Version: 2.0  
Date: 01-01-2022  

Features:
1) Handles all filetypes
2) Autogenerates date and time of encryption and decryption
3) Reads large files using a buffer

Requirements:
java version 8.0 or higher

Instructions for use:

I) SETUP
1) Obtain a pair of large prime numbers (https://bigprimes.org is quite useful). The two primes must around 50 digits long, with one longer than the other.
2) Run test() in class RSA and feed in the two prime numbers. n and φ(n) (the totient of n) are generated.
3) Enter a public key such that it divides φ(n). 65537 (2^16 + 1) is recommended, though any factor of the totient can be used. A smaller public key results in faster encryption and a larger private key, which in turn results in more security.
4) Enter the values thus obtained into the appropriate state variables in the source code of class File_encryption. This sets up the cryptosystem.

II) USE
1) Use get_encrypted_file() and get_decrypted_file() to encrypt and decrypt a file. The filename argument should not contain the extension. The extension argument should not contain the "." prefix.
2) Run the main() method in class File_encryption to generate an encrypted .txt file containing the text input. The exit code can be changed by changing the exit_code state variable in the source code of class File_encryption.

Warning: 
1) The encryption process is quite slow, which I plan to improve in upcoming versions.
2) Do not skip SETUP and use the provided key pair. The sk has been included as a comment for testing purposes and is publically available.

License:
GPL
