package volungemv2.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class untuk password hashing dan verification
 */
public class PasswordUtils {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Hash password dengan SHA-256 (format MySQL SHA2 compatible)
     * Untuk kompatibilitas dengan database yang menggunakan SHA2(password, 256)
     */
    public static String hashPasswordSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            
            // Convert ke hex string lowercase (sama seperti MySQL SHA2)
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password with SHA-256", e);
        }
    }
    
    /**
     * Verify password against SHA-256 hash (format MySQL SHA2 compatible)
     */
    public static boolean verifyPasswordSHA256(String inputPassword, String storedHash) {
        if (inputPassword == null || storedHash == null) {
            return false;
        }
        
        try {
            // Hash input password
            String inputHash = hashPasswordSHA256(inputPassword);
            
            // Compare dengan stored hash (case insensitive untuk safety)
            boolean isMatch = inputHash.equalsIgnoreCase(storedHash.trim());
            
            // Debug log
            System.out.println("=== PASSWORD VERIFICATION DEBUG ===");
            System.out.println("Input password: '" + inputPassword + "'");
            System.out.println("Input hash: '" + inputHash + "'");
            System.out.println("Stored hash: '" + storedHash.trim() + "'");
            System.out.println("Match: " + isMatch);
            System.out.println("=======================================");
            
            return isMatch;
            
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Hash password dengan salt (format baru yang lebih aman)
     */
    public static String hashPasswordWithSalt(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes("UTF-8"));
            
            // Combine salt and hash
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);
            
            // Encode to Base64
            return Base64.getEncoder().encodeToString(saltAndHash);
            
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password with salt", e);
        }
    }
    
    /**
     * Verify password against salted hash
     */
    public static boolean verifyPasswordWithSalt(String password, String hashedPassword) {
        try {
            // Decode from Base64
            byte[] saltAndHash = Base64.getDecoder().decode(hashedPassword);
            
            // Extract salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltAndHash, 0, salt, 0, SALT_LENGTH);
            
            // Extract hash
            byte[] hash = new byte[saltAndHash.length - SALT_LENGTH];
            System.arraycopy(saltAndHash, SALT_LENGTH, hash, 0, hash.length);
            
            // Hash input password with same salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] testHash = md.digest(password.getBytes("UTF-8"));
            
            // Compare hashes
            return MessageDigest.isEqual(hash, testHash);
            
        } catch (Exception e) {
            System.err.println("Error verifying salted password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deteksi format hash password
     */
    public static boolean isSHA256HexFormat(String password) {
        // SHA-256 hex format: 64 karakter hexadecimal
        return password != null && 
               password.matches("^[a-fA-F0-9]{64}$");
    }
    
    /**
     * Deteksi format Base64 dengan salt
     */
    public static boolean isBase64SaltedFormat(String password) {
        try {
            if (password == null || password.isEmpty()) {
                return false;
            }
            
            // Coba decode Base64
            byte[] decoded = Base64.getDecoder().decode(password);
            
            // Harus minimal salt (16 bytes) + hash (32 bytes untuk SHA-256)
            return decoded.length >= (SALT_LENGTH + 32);
            
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Verify password dengan auto-detect format
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        if (inputPassword == null || storedPassword == null) {
            return false;
        }
        
        // Auto-detect format dan gunakan method yang sesuai
        if (isSHA256HexFormat(storedPassword)) {
            return verifyPasswordSHA256(inputPassword, storedPassword);
        } else if (isBase64SaltedFormat(storedPassword)) {
            return verifyPasswordWithSalt(inputPassword, storedPassword);
        } else {
            // Fallback: coba plain text comparison (untuk development/testing)
            System.out.println("WARNING: Using plain text password comparison!");
            return inputPassword.equals(storedPassword);
        }
    }
    
    /**
     * Generate random password
     */
    public static String generateRandomPassword(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        
        return password.toString();
    }
    
    /**
     * Validate password strength
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if ("!@#$%^&*()_+-=[]{}|;:,.<>?".indexOf(c) != -1) hasSpecial = true;
        }
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Get password strength score (0-5)
     */
    public static int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) return 0;
        
        int score = 0;
        
        // Length check
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // Character variety checks
        if (password.matches(".*[a-z].*")) score++; // lowercase
        if (password.matches(".*[A-Z].*")) score++; // uppercase
        if (password.matches(".*[0-9].*")) score++; // digits
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*")) score++; // special chars
        
        return Math.min(score, 5);
    }
    
    /**
     * Get password strength description
     */
    public static String getPasswordStrengthDescription(String password) {
        int strength = getPasswordStrength(password);
        
        switch (strength) {
            case 0:
            case 1:
                return "Very Weak";
            case 2:
                return "Weak";
            case 3:
                return "Fair";
            case 4:
                return "Good";
            case 5:
                return "Strong";
            default:
                return "Unknown";
        }
    }
}