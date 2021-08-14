# RSA-encryption
An implementation of the simplest form of RSA encryption.

class RSA has all the functions necessary to generate a public key/private key pair given appropriate primes.
RSA.test() generates a key pair with two primes and allows one to confirm that they work.
-1 indicates a failed attempt at finding a private key for a given public key.
The message to be encrypted is a BigInteger 0<=m<n.
Entry of -1 for m ends the execution of test().

class File_encryption provides necessary tools for encrypting text files of arbitrary length and decrypting files encrypted by it.
The keys used can be changed by initilializing appropriate values to state variables.
Only ASCII characters are supported. characters are truncated at 8 bits/1 byte.
main() does not encrypt sentence by sentence. It merely allows input to be sentence by sentence without the entire message shown on screen.
the message broken into chunks and encrypted.
"\n" is used as a delimiter to separate encrypted chunks.
filenames generated using the Unix epoch, to ensure file creation does not cause errors.
All files are created or accessed from working dircetory.

The secret key to the given key has been listed as a comment for testing purposes.
Those who intend to use my code are advised to generate a different key pair.

