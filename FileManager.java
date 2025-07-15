import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private static final String EXPENSE_FILE = "expenses.txt";
    private static final String USER_FILE = "users.txt";

    // Initialize data directory
    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.out.println("Error creating data directory: " + e.getMessage());
        }
    }

    // Save all data
    public static void saveAllData(List<User> users, List<Expense> expenses) {
        saveUsers(users);
        saveExpenses(expenses);
    }

    // Save expenses
    private static void saveExpenses(List<Expense> expenses) {
        try (PrintWriter writer = new PrintWriter(DATA_DIR + EXPENSE_FILE)) {
            for (Expense e : expenses) {
                writer.println(String.join("|",
                        e.getTitle(),
                        e.getDescription(),
                        String.valueOf(e.getAmount()),
                        e.getPayer(),
                        String.join(",", e.getParticipants()),
                        e.getTimestamp()
                ));
            }
        } catch (IOException e) {
            System.out.println("Error saving expenses: " + e.getMessage());
        }
    }

    // Save users
    private static void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(DATA_DIR + USER_FILE)) {
            for (User u : users) {
                writer.println(String.join("|",
                        u.getUsername(),
                        u.getPassword(),
                        u.getName(),
                        u.getEmail()
                ));
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // Load all data
    public static Map<String, List<?>> loadAllData() {
        Map<String, List<?>> data = new HashMap<>();
        data.put("users", loadUsers());
        data.put("expenses", loadExpenses());
        return data;
    }

    // Load expenses
    public static List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATA_DIR + EXPENSE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    expenses.add(new Expense(
                            parts[0], parts[1], Double.parseDouble(parts[2]),
                            parts[3], parts[4].split(","), parts[5]
                    ));
                }
            }
        } catch (IOException e) {
            System.out.println("No existing expense data found. Starting fresh.");
        }
        return expenses;
    }

    // Load users
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATA_DIR + USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    users.add(new User(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("No existing user data found. Starting fresh.");
        }
        return users;
    }
}