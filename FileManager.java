import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileManager {
    private static final String DATA_DIR = "data/";
    private static final String EXPENSE_FILE = "expenses.txt";
    private static final String USER_FILE = "users.txt";
    private static final String GROUP_FILE = "groups.txt";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static {
        new File(DATA_DIR).mkdirs();
    }

    public static void saveAllData(List<User> users, List<Expense> expenses, List<Group> groups) {
        saveUsers(users);
        saveExpenses(expenses);
        saveGroups(groups);
    }

    public static void saveUsers(List<User> users) {
        try (PrintWriter writer = new PrintWriter(DATA_DIR + USER_FILE)) {
            for (User user : users) {
                writer.println(user.getUsername() + "|" +
                        user.getName() + "|" +
                        user.getEmail() + "|" +
                        user.getCurrency() + "|" +
                        String.join(",", user.getCategories()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveExpenses(List<Expense> expenses) {
        try (PrintWriter writer = new PrintWriter(DATA_DIR + EXPENSE_FILE)) {
            for (Expense expense : expenses) {
                writer.println(expense.getTitle() + "|" +
                        expense.getDescription() + "|" +
                        expense.getAmount() + "|" +
                        expense.getPayer() + "|" +
                        String.join(",", expense.getParticipants()) + "|" +
                        expense.getTimestamp() + "|" +
                        expense.getCategory() + "|" +
                        (expense.getGroupId() != null ? expense.getGroupId() : ""));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveGroups(List<Group> groups) {
        try (PrintWriter writer = new PrintWriter(DATA_DIR + GROUP_FILE)) {
            for (Group group : groups) {
                writer.println(group.getGroupId() + "|" +
                        group.getGroupName() + "|" +
                        group.getDescription() + "|" +
                        group.getCreator() + "|" +
                        String.join(",", group.getMembers()) + "|" +
                        dtf.format(group.getCreatedAt()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(DATA_DIR + USER_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\\|");
                if (parts.length >= 4) {
                    User user = new User(parts[0], parts[1], parts[2], parts[3]);
                    if (parts.length >= 5) user.setCurrency(parts[4]);
                    if (parts.length >= 6) {
                        for (String category : parts[5].split(",")) {
                            if (!category.isEmpty()) user.addCategory(category);
                        }
                    }
                    users.add(user);
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return users;
    }

    public static List<Expense> loadExpenses() {
        List<Expense> expenses = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(DATA_DIR + EXPENSE_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\\|");
                if (parts.length >= 7) {
                    String groupId = parts.length >= 8 && !parts[7].isEmpty() ? parts[7] : null;
                    expenses.add(new Expense(
                            parts[0], parts[1], Double.parseDouble(parts[2]),
                            parts[3], parts[4].split(","), parts[5],
                            parts[6], groupId
                    ));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return expenses;
    }

    public static List<Group> loadGroups() {
        List<Group> groups = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(DATA_DIR + GROUP_FILE))) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split("\\|");
                if (parts.length == 6) {
                    groups.add(new Group(
                            parts[1], parts[2], parts[3],
                            new HashSet<>(Arrays.asList(parts[4].split(",")))
                    ));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
        }
        return groups;
    }
}
