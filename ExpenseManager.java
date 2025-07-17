import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private List<Expense> expenses;
    private Map<String, Double> userNetBalances;
    private Map<String, Group> groups;
    private Map<String, Map<String, Double>> groupBalances;

    public ExpenseManager(List<Expense> expenses, List<Group> groups) {
        this.expenses = new ArrayList<>(expenses);
        this.userNetBalances = new HashMap<>();
        this.groups = new HashMap<>();
        this.groupBalances = new HashMap<>();

        for (Group group : groups) {
            this.groups.put(group.getGroupId(), group);
            this.groupBalances.put(group.getGroupId(), new HashMap<>());
        }
        calculateBalances();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        updateBalances(expense);
    }

    public void removeExpense(String expenseId) {
        expenses.removeIf(e -> e.getExpenseId().equals(expenseId));
        recalculateAllBalances();
    }

    public void updateExpense(Expense updatedExpense) {
        removeExpense(updatedExpense.getExpenseId());
        addExpense(updatedExpense);
    }

    public Expense getExpenseById(String expenseId) {
        return expenses.stream()
                .filter(e -> e.getExpenseId().equals(expenseId))
                .findFirst()
                .orElse(null);
    }

    public void createGroup(Group group) {
        groups.put(group.getGroupId(), group);
        groupBalances.put(group.getGroupId(), new HashMap<>());
    }

    public void updateGroup(Group group) {
        groups.put(group.getGroupId(), group);
        if (!groupBalances.containsKey(group.getGroupId())) {
            groupBalances.put(group.getGroupId(), new HashMap<>());
        }
        recalculateAllBalances();
    }

    public List<Group> getUserGroups(String username) {
        return groups.values().stream()
                .filter(g -> g.getMembers().contains(username))
                .collect(Collectors.toList());
    }

    public Group getGroupById(String groupId) {
        return groups.get(groupId);
    }

    public List<Expense> getGroupExpenses(String groupId) {
        return expenses.stream()
                .filter(e -> groupId.equals(e.getGroupId()))
                .collect(Collectors.toList());
    }

    public List<Expense> getMonthlyExpenses(String username, int month, int year) {
        return expenses.stream()
                .filter(e -> Arrays.asList(e.getParticipants()).contains(username))
                .filter(e -> {
                    String[] dateParts = e.getDate().split("-");
                    return Integer.parseInt(dateParts[1]) == month &&
                            Integer.parseInt(dateParts[0]) == year;
                })
                .collect(Collectors.toList());
    }

    private void calculateBalances() {
        userNetBalances.clear();
        groupBalances.values().forEach(Map::clear);
        expenses.forEach(this::updateBalances);
    }

    private void recalculateAllBalances() {
        calculateBalances();
    }

    private void updateBalances(Expense expense) {
        String payer = expense.getPayer();
        double amount = expense.getAmount();
        String[] participants = expense.getParticipants();
        double share = amount / participants.length;

        // Update personal balances
        userNetBalances.merge(payer, amount - share, Double::sum);
        for (String participant : participants) {
            if (!participant.equals(payer)) {
                userNetBalances.merge(participant, -share, Double::sum);
            }
        }

        // Update group balances if applicable
        if (expense.getGroupId() != null) {
            Map<String, Double> groupBalance = groupBalances.get(expense.getGroupId());
            if (groupBalance != null) {
                groupBalance.merge(payer, amount - share, Double::sum);
                for (String participant : participants) {
                    if (!participant.equals(payer)) {
                        groupBalance.merge(participant, -share, Double::sum);
                    }
                }
            }
        }
    }

    public void showUserSummary(String username) {
        double balance = userNetBalances.getOrDefault(username, 0.0);
        System.out.printf("\033[1;36mNet Balance: %s₹%.2f\n",
                balance >= 0 ? "You are owed " : "You owe ", Math.abs(balance));

        System.out.println("\033[1;33mDetailed Balances:");
        userNetBalances.forEach((user, amount) -> {
            if (!user.equals(username) && amount != 0) {
                System.out.printf("\033[0;37m%s: %s₹%.2f\n",
                        user, amount > 0 ? "owes you " : "you owe ", Math.abs(amount));
            }
        });
    }

    public void showGroupSummary(String groupId, String currentUser) {
        Map<String, Double> balances = groupBalances.getOrDefault(groupId, new HashMap<>());
        double userBalance = balances.getOrDefault(currentUser, 0.0);

        System.out.printf("\033[1;35m=== GROUP BALANCE SUMMARY ===\n");
        System.out.printf("\033[1;36mYour net balance: %s₹%.2f\n\n",
                userBalance >= 0 ? "+" : "", userBalance);

        System.out.println("\033[1;33mDetailed Balances:");
        balances.forEach((user, amount) -> {
            if (!user.equals(currentUser) && amount != 0) {
                System.out.printf("\033[0;37m%s: %s₹%.2f\n",
                        user, amount > 0 ? "owes you " : "you owe ", Math.abs(amount));
            }
        });
    }

    public void showExpenseHistory(String username) {
        List<Expense> userExpenses = expenses.stream()
                .filter(e -> Arrays.asList(e.getParticipants()).contains(username))
                .collect(Collectors.toList());

        if (userExpenses.isEmpty()) {
            System.out.println("\033[1;31mNo expenses found");
            return;
        }

        System.out.println("\n\033[1;33m=== EXPENSE HISTORY ===");
        userExpenses.forEach(System.out::println);
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return expenses.stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // Make sure all methods have proper return statements


}
