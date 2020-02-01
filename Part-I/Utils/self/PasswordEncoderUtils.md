

!FILENAME PasswordEncoderUtils.java
```java
@SuppressWarnings("deprecation")
public class PasswordEncoderUtils {

    public static final String BCRYPT  = "bcrypt";
    public static final String LDAP    = "ldap";
    public static final String MD4     = "MD4";
    public static final String MD5     = "MD5";
    public static final String NOOP    = "noop";
    public static final String PBKDF2  = "pbkdf2";
    public static final String SCRYPT  = "scrypt";
    public static final String SHA_1   = "SHA-1";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA256  = "sha256";

    private static Map<String, PasswordEncoder> encoders = new HashMap<>();

    static {
        encoders.put(BCRYPT, new BCryptPasswordEncoder());
        encoders.put(LDAP, new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put(MD4, new org.springframework.security.crypto.password.Md4PasswordEncoder());
        encoders.put(MD5, new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
        encoders.put(NOOP, org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        encoders.put(PBKDF2, new Pbkdf2PasswordEncoder());
        encoders.put(SCRYPT, new SCryptPasswordEncoder());
        encoders.put(SHA_1, new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
        encoders.put(SHA_256, new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
        encoders.put(SHA256, new org.springframework.security.crypto.password.StandardPasswordEncoder());
    }   

    private PasswordEncoderUtils() {
    }

    public static String encode(String idForEncode, CharSequence rawPassword) {
        DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder(idForEncode, encoders);
        return encoder.encode(rawPassword);
    }

}
```