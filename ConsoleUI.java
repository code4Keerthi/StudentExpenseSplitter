import java.util.*;
import java.time.*;
import java.time.format.*;

public class ConsoleUI {
    private Scanner sc;
    private ExpenseManager expenseManager;
    private List<User> users;
    private User currentUser;
    private DateTimeFormatter dtf;

    public ConsoleUI() {
        this.sc = new Scanner(System.in);
        this.dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // Load existing data
        Map<String, List<?>> data = FileManager.loadAllData();
        this.users = (List<User>) data.get("users");
        this.expenseManager = new ExpenseManager((List<Expense>) data.get("expenses"));

        // Add default users if none exist
        if (users.isEmpty()) {
            users.add(new User("user1", "pass1", "Alice", "alice@example.com"));
            users.add(new User("user2", "pass2", "Bob", "bob@example.com"));
        }
    }

    public void start() {
        System.out.println("\033[1;35m\n===== WELCOME TO SMARTSPLIT =====");

        while (true) {
            System.out.println("\033[1;36m\n1. Sign Up\n2. Login\n3. Exit");
            System.out.print("\033[1;37mChoose option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1: signUp(); break;
                case 2: if (login()) userMenu(); break;
                case 3: exit(); return;
                default: showError("Invalid choice!");
            }
        }
    }

    private void signUp() {
        System.out.println("\n\033[1;32m===== CREATE ACCOUNT =====");
        String username = getInput("Username: ");

        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            showError("Username already exists!");
            return;
        }

        User newUser = new User(
                username,
                getInput("Password: "),
                getInput("Full Name: "),
                getInput("Email: ")
        );

        users.add(newUser);
        showSuccess("Account created! Please login.");
    }

    private boolean login() {
        System.out.println("\n\033[1;32m===== LOGIN =====");
        String username = getInput("Username: ");
        String password = getInput("Password: ");

        Optional<User> user = users.stream()
                .filter(u -> u.getUsername().equals(username) && u.checkPassword(password))
                .findFirst();

        if (user.isPresent()) {
            currentUser = user.get();
            showSuccess("Welcome back, " + currentUser.getName() + "!");
            return true;
        }

        showError("Invalid credentials!");
        return false;
    }

    private void userMenu() {
        while (true) {
            System.out.println("\n\033[1;35m===== MAIN MENU =====");
            System.out.println("\033[1;36m1. Add Expense\n2. View Balances\n3. View History\n4. Logout");
            System.out.print("\033[1;37mChoose option: ");

            int choice = getIntInput();
            switch (choice) {
                case 1: addExpense(); break;
                case 2: expenseManager.showBalances(currentUser.getUsername()); break;
                case 3: expenseManager.showExpenseHistory(currentUser.getUsername()); break;
                case 4: logout(); return;
                default: showError("Invalid choice!");
            }
        }
    }

    private void addExpense() {
        System.out.println("\n\033[1;32m===== ADD EXPENSE =====");
        Expense expense = new Expense(
                getInput("Title (e.g. 'Dinner at Pizza Hut'): "),
                getInput("Description: "),
                getDoubleInput("Amount: "),
                currentUser.getUsername(),
                getInput("Participants (comma separated): ").split(","),
                dtf.format(LocalDateTime.now())
        );

        expenseManager.addExpense(expense);
        showSuccess("Expense added successfully!");
    }

    private void logout() {
        currentUser = null;
        showSuccess("Logged out successfully!");
    }

    private void exit() {
        FileManager.saveAllData(users, expenseManager.getExpenses());
        showSuccess("Data saved. Goodbye!");
        System.exit(0);
    }

    // Helper methods
    private String getInput(String prompt) {
        System.out.print("\033[0;35m" + prompt + "\033[0;37m");
        return sc.nextLine().trim();
    }

    private int getIntInput() {
        while (!sc.hasNextInt()) {
            sc.next();
            showError("Please enter a number!");
        }
        int input = sc.nextInt();
        sc.nextLine();
        return input;
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            System.out.print("\033[0;35m" + prompt + "\033[0;37m");
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                showError("Invalid amount! Please enter a number.");
            }
        }
    }

    private void showError(String message) {
        System.out.println("\033[1;31m[!] " + message + "\033[0m");
    }

    private void showSuccess(String message) {
        System.out.println("\033[1;32m[✓] " + message + "\033[0m");
    }

    public static void main(String[] args) {
        new ConsoleUI().start();
    }
}