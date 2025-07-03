package volungemv2.utils;

import volungemv2.models.Users;

/**
 * SessionManager untuk mengelola session user yang sedang login
 */
public class SessionManager {
    private static Users currentUser;
    private static long loginTime;
    
    /**
     * Set current user yang sedang login
     */
    public static void setCurrentUser(Users user) {
        currentUser = user;
        loginTime = System.currentTimeMillis();
    }
    
    /**
     * Get current user yang sedang login
     */
    public static Users getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check apakah ada user yang sedang login
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Get username dari current user
     */
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }
    
    /**
     * Get role dari current user
     */
    public static String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Get email dari current user
     */
    public static String getCurrentUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }
    
    /**
     * Check apakah current user memiliki role tertentu
     */
    public static boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equalsIgnoreCase(role);
    }
    
    /**
     * Check apakah current user adalah admin
     */
    public static boolean isAdmin() {
        return hasRole("admin");
    }
    
    /**
     * Check apakah current user adalah organizer
     */
    public static boolean isOrganizer() {
        return hasRole("organizer");
    }
    
    /**
     * Check apakah current user adalah volunteer
     */
    public static boolean isVolunteer() {
        return hasRole("volunteer");
    }
    
    /**
     * Get login time
     */
    public static long getLoginTime() {
        return loginTime;
    }
    
    /**
     * Get session duration dalam milliseconds
     */
    public static long getSessionDuration() {
        return System.currentTimeMillis() - loginTime;
    }
    
    /**
     * Logout - clear session
     */
    public static void logout() {
        currentUser = null;
        loginTime = 0;
    }
    
    /**
     * Check apakah session masih valid (optional - untuk session timeout)
     */
    public static boolean isSessionValid() {
        // Implement session timeout logic if needed
        // Misalnya: session valid selama 8 jam
        long sessionTimeout = 8 * 60 * 60 * 1000; // 8 hours in milliseconds
        return isLoggedIn() && (getSessionDuration() < sessionTimeout);
    }
}