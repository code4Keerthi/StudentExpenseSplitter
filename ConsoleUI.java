import java.util.*;
import java.time.*;
import java.time.format.*;
import java.util.regex.Pattern;

public class ConsoleUI {
    private Scanner sc;
    private ExpenseManager expenseManager;
    private List<User> users;
    private User currentUser;
    private DateTimeFormatter dtf;

    public ConsoleUI() {
        this.sc = new Scanner(System.in);
        this.dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.users = FileManager.loadUsers();
        List<Expense> expenses = FileManager.loadExpenses();
        List<Group> groups = FileManager.loadGroups();
        this.expenseManager = new ExpenseManager(expenses, groups);
    }


    public void start() {
        System.out.println("\033[1;35m\n===== WELCOME TO SMARTSPLIT =====");

        while (true) {
            System.out.println("\033[1;36m\n1. Login\n2. Sign Up\n3. Exit");
            System.out.print("\033[1;37mChoose option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    if (login()) {
                        showMainMenu();
                    }
                    break;
                case "2":
                    signUp();
                    break;
                case "3":
                    exit();
                    return;
                default:
                    System.out.println("\033[1;31mInvalid choice!");
            }
        }
    }

    private boolean login() {
        System.out.println("\n\033[1;32m===== LOGIN =====");
        System.out.print("\033[0;35mUsername: \033[0;37m");
        String username = sc.nextLine();
        System.out.print("\033[0;35mPassword: \033[0;37m");
        String password = sc.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                currentUser = user;
                System.out.println("\033[1;32m\nWelcome, " + user.getName() + "!");
                showUserSummary();
                return true;
            }
        }
        System.out.println("\033[1;31mInvalid credentials!");
        return false;
    }

    private void showUserSummary() {
        System.out.println("\n\033[1;35m=== YOUR SUMMARY ===");
        expenseManager.showUserSummary(currentUser.getUsername());
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private String getValidatedEmail(Scanner sc) {
        while (true) {
            System.out.print("\033[0;35mEmail: \033[0;37m");
            String email = sc.nextLine().trim();

            if (isValidEmail(email)) {
                return email;
            } else {
                System.out.println("\033[1;31mInvalid email format. Please try again (example: user@example.com)");
            }
        }
    }
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    private void signUp() {


        System.out.println("\n\033[1;32m===== CREATE ACCOUNT =====");

        System.out.print("\033[0;35mFull Name: \033[0;37m");
        String name = sc.nextLine();
        System.out.print("\033[0;35mEmail: \033[0;37m");
        String email = getValidatedEmail(sc);
        System.out.print("\033[0;35mUsername: \033[0;37m");
        String username = sc.nextLine();

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("\033[1;31mUsername already exists!");
                return;
            }
        }

        System.out.print("\033[0;35mPassword: \033[0;37m");
        String password = sc.nextLine();
        users.add(new User(username, password, name, email));
        FileManager.saveUsers(users);
        System.out.println("\033[1;32mAccount created successfully!");
    }

    private void showMainMenu() {
        while (true) {
            System.out.println("\n\033[1;35m===== MAIN MENU =====");
            System.out.println("\033[1;36m1. Expense Management");
            System.out.println("2. Group Management");
            System.out.println("3. Balance & Settlements");
            System.out.println("4. Reports & Analytics");
            System.out.println("5. User Profile");
            System.out.println("6. Exit");
            System.out.print("\033[1;37mChoose option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    showExpenseManagementMenu();
                    break;
                case "2":
                    showGroupManagementMenu();
                    break;
                case "3":
                    showBalanceSettlementMenu();
                    break;
                case "4":
                    showReportsMenu();
                    break;
                case "5":
                    showUserProfileMenu();
                    break;
                case "6":
                    exit();
                    return;
                default:
                    System.out.println("\033[1;31mInvalid choice!");
            }
        }
    }

    private void showExpenseManagementMenu() {
        while (true) {
            System.out.println("\n\033[1;35m===== EXPENSE MANAGEMENT =====");
            System.out.println("\033[1;36m1. Add New Expense");
            System.out.println("2. View Expense History");
            System.out.println("3. Edit Expense");
            System.out.println("4. Delete Expense");
            System.out.println("5. Back to Main Menu");
            System.out.print("\033[1;37mChoose option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    addExpense();
                    break;
                case "2":
                    viewExpenseHistory();
                    break;
                case "3":
                    editExpense();
                    break;
                case "4":
                    deleteExpense();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("\033[1;31mInvalid choice!");
            }
        }
    }

    private void addExpense() {
        System.out.println("\n\033[1;32m===== ADD EXPENSE =====");
        System.out.print("\033[0;35mExpense Name: \033[0;37m");
        String name = sc.nextLine();
        System.out.print("\033[0;35mDescription: \033[0;37m");
        String description = sc.nextLine();
        System.out.print("\033[0;35mAmount: \033[0;37m");
        double amount = Double.parseDouble(sc.nextLine());

        // Show available categories
        System.out.println("\n\033[1;33mAvailable Categories:");
        List<String> categories = currentUser.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            System.out.printf("%d. %s\n", i+1, categories.get(i));
        }
        System.out.print("\033[0;35mSelect Category (number): \033[0;37m");
        int catChoice = Integer.parseInt(sc.nextLine()) - 1;
        String category = categories.get(catChoice);

        System.out.print("\033[0;35mParticipants (comma separated): \033[0;37m");
        String[] participants = sc.nextLine().split(",");

        Expense expense = new Expense(
                name,
                description,
                amount,
                currentUser.getUsername(),
                participants,
                dtf.format(LocalDateTime.now()),
                category,
                null
        );

        expenseManager.addExpense(expense);
        FileManager.saveExpenses(expenseManager.getExpenses());
        System.out.println("\033[1;32mExpense added successfully!");
    }

    private void viewExpenseHistory() {
        System.out.println("\n\033[1;35m=== EXPENSE HISTORY ===");
        expenseManager.showExpenseHistory(currentUser.getUsername());
    }

    private void editExpense() {
        System.out.println("\n\033[1;32m===== EDIT EXPENSE =====");
        viewExpenseHistory();
        System.out.print("\033[0;35mEnter expense ID to edit: \033[0;37m");
        String expenseId = sc.nextLine();

        // Get the expense to edit
        Expense expense = expenseManager.getExpenseById(expenseId);
        if (expense == null || !expense.getPayer().equals(currentUser.getUsername())) {
            System.out.println("\033[1;31mInvalid expense ID or you don't have permission to edit this expense");
            return;
        }

        // Get new values from user
        System.out.print("\033[0;35mNew name (leave blank to keep current): \033[0;37m");
        String name = sc.nextLine();
        System.out.print("\033[0;35mNew description (leave blank to keep current): \033[0;37m");
        String description = sc.nextLine();
        System.out.print("\033[0;35mNew amount (leave blank to keep current): \033[0;37m");
        String amountStr = sc.nextLine();

        // Update expense fields if provided
//        if (!name.isEmpty()) expense.setName(name);
//        if (!description.isEmpty()) expense.setDescription(description);
//        if (!amountStr.isEmpty()) expense.setAmount(Double.parseDouble(amountStr));

        // Save changes
        expenseManager.updateExpense(expense);
        FileManager.saveExpenses(expenseManager.getExpenses());
        System.out.println("\033[1;32mExpense updated successfully!");
    }

    private void deleteExpense() {
        System.out.println("\n\033[1;32m===== DELETE EXPENSE =====");
        viewExpenseHistory();
        System.out.print("\033[0;35mEnter expense ID to delete: \033[0;37m");
        String expenseId = sc.nextLine();

        Expense expense = expenseManager.getExpenseById(expenseId);
        if (expense == null || !expense.getPayer().equals(currentUser.getUsername())) {
            System.out.println("\033[1;31mInvalid expense ID or you don't have permission to delete this expense");
            return;
        }

        System.out.print("\033[0;35mAre you sure you want to delete this expense? (y/n): \033[0;37m");
        String confirm = sc.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            expenseManager.removeExpense(expenseId);
            FileManager.saveExpenses(expenseManager.getExpenses());
            System.out.println("\033[1;32mExpense deleted successfully!");
        }
    }

    private void showGroupManagementMenu() {
        while (true) {
            System.out.println("\n\033[1;35m===== GROUP MANAGEMENT =====");
            System.out.println("\033[1;36m1. Create New Group");
            System.out.println("2. View My Groups");
            System.out.println("3. Add Members");
            System.out.println("4. View Group Expenses");
            System.out.println("5. Back to Main Menu");
            System.out.print("\033[1;37mChoose option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    createGroup();
                    break;
                case "2":
                    viewMyGroups();
                    break;
                case "3":
                    addGroupMembers();
                    break;
                case "4":
                    viewGroupExpenses();
                    break;
                case "5":
                    return;
                default:
                    System.out.println("\033[1;31mInvalid choice!");
            }
        }
    }

    private void addGroupMembers() {
        List<Group> userGroups = expenseManager.getUserGroups(currentUser.getUsername());
        if (userGroups.isEmpty()) {
            System.out.println("\033[1;31mYou're not in any groups!");
            return;
        }

        System.out.println("\n\033[1;33mSelect a group:");
        for (int i = 0; i < userGroups.size(); i++) {
            System.out.printf("\033[1;36m%d. %s\n", i+1, userGroups.get(i));
        }
        System.out.print("\033[0;35mEnter group number: \033[0;37m");
        int choice = Integer.parseInt(sc.nextLine()) - 1;

        if (choice >= 0 && choice < userGroups.size()) {
            Group group = userGroups.get(choice);
            System.out.print("\033[0;35mEnter usernames to add (comma separated): \033[0;37m");
            String[] newMembers = sc.nextLine().split(",");

            for (String member : newMembers) {
                member = member.trim();
                if (!group.getMembers().contains(member)) {
                    group.addMember(member);
                }
            }

            expenseManager.updateGroup(group);
            FileManager.saveGroups(expenseManager.getUserGroups(currentUser.getUsername()));
            System.out.println("\033[1;32mMembers added successfully!");
        }
    }

    private void viewGroupExpenses() {
        List<Group> userGroups = expenseManager.getUserGroups(currentUser.getUsername());
        if (userGroups.isEmpty()) {
            System.out.println("\033[1;31mYou're not in any groups!");
            return;
        }

        System.out.println("\n\033[1;33mSelect a group:");
        for (int i = 0; i < userGroups.size(); i++) {
            System.out.printf("\033[1;36m%d. %s\n", i+1, userGroups.get(i));
        }
        System.out.print("\033[0;35mEnter group number: \033[0;37m");
        int choice = Integer.parseInt(sc.nextLine()) - 1;

        if (choice >= 0 && choice < userGroups.size()) {
            String groupId = userGroups.get(choice).getGroupId();
            List<Expense> groupExpenses = expenseManager.getGroupExpenses(groupId);

            System.out.println("\n\033[1;35m=== GROUP EXPENSES ===");
            for (Expense expense : groupExpenses) {
                System.out.println(expense);
            }
        }
    }

    private void createGroup() {
        System.out.println("\n\033[1;32m===== CREATE GROUP =====");
        System.out.print("\033[0;35mGroup Name: \033[0;37m");
        String name = sc.nextLine();
        System.out.print("\033[0;35mDescription: \033[0;37m");
        String description = sc.nextLine();

        Set<String> members = new HashSet<>();
        members.add(currentUser.getUsername());

        Group group = new Group(name, description, currentUser.getUsername(), members);
        expenseManager.createGroup(group);
        FileManager.saveGroups(new ArrayList<>(expenseManager.getUserGroups(currentUser.getUsername())));
        System.out.println("\033[1;32mGroup created successfully!");
    }

    private void viewMyGroups() {
        List<Group> userGroups = expenseManager.getUserGroups(currentUser.getUsername());
        if (userGroups.isEmpty()) {
            System.out.println("\033[1;31mYou're not in any groups yet!");
            return;
        }

        System.out.println("\n\033[1;33m=== YOUR GROUPS ===");
        for (int i = 0; i < userGroups.size(); i++) {
            System.out.printf("\033[1;36m%d. %s\n", i+1, userGroups.get(i));
        }
    }

    private void showBalanceSettlementMenu() {
        while (true) {
            System.out.println("\n\033[1;35m===== BALANCES & SETTLEMENTS =====");
            System.out.println("\033[1;36m1. View Personal Balances");
            System.out.println("2. View Group Balances");
            System.out.println("3. Record Settlement");
            System.out.println("4. Back to Main Menu");
            System.out.print("\033[1;37mChoose option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    expenseManager.showUserSummary(currentUser.getUsername());
                    break;
                case "2":
                    viewGroupBalances();
                    break;
                case "3":
                    recordSettlement();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("\033[1;31mInvalid choice!");
            }
        }
    }

    private void recordSettlement() {
        System.out.println("\n\033[1;32m===== RECORD SETTLEMENT =====");
        System.out.print("\033[0;35mEnter username of person you're settling with: \033[0;37m");
        String otherUser = sc.nextLine();
        System.out.print("\033[0;35mAmount: \033[0;37m");
        double amount = Double.parseDouble(sc.nextLine());
        System.out.print("\033[0;35mDescription: \033[0;37m");
        String description = sc.nextLine();

        // Create a settlement expense
        Expense settlement = new Expense(
                "Settlement with " + otherUser,
                description,
                amount,
                currentUser.getUsername(),
                new String[]{otherUser},
                dtf.format(LocalDateTime.now()),
                "Settlement",
                null
        );

        expenseManager.addExpense(settlement);
        FileManager.saveExpenses(expenseManager.getExpenses());
        System.out.println("\033[1;32mSettlement recorded successfully!");
    }
    private void viewGroupBalances() {
        List<Group> userGroups = expenseManager.getUserGroups(currentUser.getUsername());
        if (userGroups.isEmpty()) {
            System.out.println("\033[1;31mYou're not in any groups!");
            return;
        }

        System.out.println("\n\033[1;33mSelect a group:");
        for (int i = 0; i < userGroups.size(); i++) {
            System.out.printf("\033[1;36m%d. %s\n", i+1, userGroups.get(i));
        }
        System.out.print("\033[0;35mEnter group number: \033[0;37m");
        int choice = Integer.parseInt(sc.nextLine()) - 1;

        if (choice >= 0 && choice < userGroups.size()) {
            expenseManager.showGroupSummary(userGroups.get(choice).getGroupId(), currentUser.getUsername());
        }
    }

    private void showReportsMenu() {
        while (true) {
            System.out.println("\n\033[1;35m===== REPORTS =====");
            System.out.println("\033[1;36m1. Category-wise Spending");
            System.out.println("2. Monthly Summary");
            System.out.println("3. Back to Main Menu");
            System.out.print("\033[1;37mChoose option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    showCategoryReport();
                    break;
                case "2":
                    showMonthlyReport();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("\033[1;31mInvalid choice!");
            }
        }
    }

    private void showMonthlyReport() {
        System.out.println("\n\033[1;35m=== MONTHLY SUMMARY ===");
        System.out.print("\033[0;35mEnter month and year (MM/yyyy): \033[0;37m");
        String monthYear = sc.nextLine();

        try {
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth yearMonth = YearMonth.parse(monthYear, inputFormat);

            List<Expense> monthlyExpenses = expenseManager.getMonthlyExpenses(
                    currentUser.getUsername(),
                    yearMonth.getMonthValue(),
                    yearMonth.getYear()
            );

            double total = monthlyExpenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.printf("\033[1;36mTotal for %s: \033[0;37m₹%.2f (%d expenses)\n",
                    yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), total, monthlyExpenses.size());

            // Show by category
            Map<String, Double> byCategory = new HashMap<>();
            for (Expense expense : monthlyExpenses) {
                byCategory.merge(expense.getCategory(), expense.getAmount(), Double::sum);
            }

            System.out.println("\n\033[1;33mBy Category:");
            byCategory.forEach((category, amount) ->
                    System.out.printf("\033[1;36m%s: \033[0;37m₹%.2f\n", category, amount));

        } catch (DateTimeParseException e) {
            System.out.println("\033[1;31mInvalid date format! Please use MM/yyyy format.");
        }
    }

    private void showCategoryReport() {
        System.out.println("\n\033[1;35m=== CATEGORY-WISE SPENDING ===");
        List<String> categories = currentUser.getCategories();
        for (String category : categories) {
            List<Expense> expenses = expenseManager.getExpensesByCategory(category);
            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            System.out.printf("\033[1;36m%s: \033[0;37m₹%.2f (%d expenses)\n",
                    category, total, expenses.size());
        }
    }

    private void showUserProfileMenu() {
        while (true) {
            System.out.println("\n\033[1;35m===== USER PROFILE =====");
            System.out.println("\033[1;36m1. View Profile");
            System.out.println("2. Change Password");
            System.out.println("3. Set Currency");
            System.out.println("4. Back to Main Menu");
            System.out.print("\033[1;37mChoose option: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewProfile();
                    break;
                case "2":
                    changePassword();
                    break;
                case "3":
                    setCurrency();
                    break;
                case "4":
                    return;
                default:
                    System.out.println("\033[1;31mInvalid choice!");
            }
        }
    }

    private void changePassword() {
        System.out.println("\n\033[1;32m===== CHANGE PASSWORD =====");
        System.out.print("\033[0;35mCurrent Password: \033[0;37m");
        String currentPassword = sc.nextLine();
        System.out.print("\033[0;35mNew Password: \033[0;37m");
        String newPassword = sc.nextLine();

        try {
            if (currentUser.changePassword(currentPassword, newPassword)) {
                System.out.println("\033[1;32mPassword changed successfully!");
                FileManager.saveUsers(users);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\033[1;31mError: " + e.getMessage());
        }
    }

    private void setCurrency() {
        System.out.println("\n\033[1;32m===== SET CURRENCY =====");
        System.out.print("\033[0;35mEnter currency symbol (e.g., ₹, $, €): \033[0;37m");
        String currency = sc.nextLine();

        currentUser.setCurrency(currency);
        System.out.println("\033[1;32mCurrency preference updated!");
        FileManager.saveUsers(users);
    }

    private void viewProfile() {
        System.out.println("\n\033[1;35m=== YOUR PROFILE ===");
        System.out.printf("\033[1;36mUsername: \033[0;37m%s\n", currentUser.getUsername());
        System.out.printf("\033[1;36mName: \033[0;37m%s\n", currentUser.getName());
        System.out.printf("\033[1;36mEmail: \033[0;37m%s\n", currentUser.getEmail());
        System.out.printf("\033[1;36mCurrency: \033[0;37m%s\n", currentUser.getCurrency());
    }


    private void exit() {
        FileManager.saveAllData(users, expenseManager.getExpenses(), expenseManager.getUserGroups(currentUser.getUsername()));
        System.out.println("\033[1;32mData saved. Goodbye!");
        System.exit(0);
    }
}
