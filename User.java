import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    private String username;
    private String password;
    private String name;
    private String email;
    private String currency;
    private List<String> activityLog;
    private List<String> categories;

    public User(String username, String password, String name, String email) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.currency = "₹";
        this.activityLog = new ArrayList<>();
        this.categories = new ArrayList<>(List.of("Food", "Rent", "Travel", "Utilities", "Entertainment"));
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCurrency() { return currency; }
    public String getUsername() { return username; }
    public List<String> getActivityLog() { return activityLog; }
    public List<String> getCategories() { return categories; }

    // Setters
    public void setCurrency(String currency) {
        this.currency = currency;
        logActivity("Currency changed to: " + currency);
    }

    public boolean checkPassword(String inputPassword) {
        if (inputPassword == null || inputPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return password.equals(inputPassword);
    }

    public boolean changePassword(String currentPassword, String newPassword) {
        if (!checkPassword(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        if (newPassword.equals(currentPassword)) {
            throw new IllegalArgumentException("New password must be different");
        }
        this.password = newPassword;
        logActivity("Password changed");
        return true;
    }

    public void addCategory(String category) {
        categories.add(category);
        logActivity("Added category: " + category);
    }

    public void removeCategory(String category) {
        categories.remove(category);
        logActivity("Removed category: " + category);
    }

    public void logActivity(String activity) {
        activityLog.add(java.time.LocalDateTime.now() + " - " + activity);
    }
}
