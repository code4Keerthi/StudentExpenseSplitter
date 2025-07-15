import java.util.*;

public class ExpenseManager {
    private List<Expense> expenses;
    private Map<String, Map<String, Double>> detailedBalances;

    public ExpenseManager(List<Expense> expenses) {
        this.expenses = new ArrayList<>(expenses);
        this.detailedBalances = new HashMap<>();
        calculateBalances();
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
        updateBalances(expense);
    }

    private void calculateBalances() {
        for (Expense e : expenses) {
            updateBalances(e);
        }
    }

    private void updateBalances(Expense expense) {
        String payer = expense.getPayer();
        double amount = expense.getAmount();
        String[] participants = expense.getParticipants();
        double share = amount / participants.length;

        // Initialize payer's balance map if not exists
        detailedBalances.putIfAbsent(payer, new HashMap<>());

        for (String participant : participants) {
            if (!participant.equals(payer)) {
                // Initialize participant's balance map if not exists
                detailedBalances.putIfAbsent(participant, new HashMap<>());

                // Update balances
                detailedBalances.get(payer).merge(participant, share, Double::sum);
                detailedBalances.get(participant).merge(payer, -share, Double::sum);
            }
        }
    }

    public void showBalances(String username) {
        System.out.println("\n\033[1;35m=== YOUR BALANCE SUMMARY ===");

        double netBalance = 0;
        Map<String, Double> userBalances = detailedBalances.getOrDefault(username, new HashMap<>());

        // Calculate net balance
        for (Map.Entry<String, Double> entry : userBalances.entrySet()) {
            netBalance += entry.getValue();
        }

        System.out.printf("\033[1;36mNet Balance: %s%.2f\n",
                netBalance >= 0 ? "You are owed ₹" : "You owe ₹",
                Math.abs(netBalance));

        System.out.println("\n\033[1;33m=== DETAILED BALANCES ===");
        if (userBalances.isEmpty()) {
            System.out.println("No balances to show");
            return;
        }

        userBalances.forEach((person, amount) -> {
            if (amount > 0) {
                System.out.printf("\033[0;32m%s owes you ₹%.2f\n", person, amount);
            } else if (amount < 0) {
                System.out.printf("\033[0;31mYou owe %s ₹%.2f\n", person, Math.abs(amount));
            }
        });
    }

    public void showExpenseHistory(String username) {
        System.out.println("\n\033[1;35m=== EXPENSE HISTORY ===");
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet");
            return;
        }

        System.out.println("\033[1;36mTotal expenses: " + expenses.size());
        expenses.forEach(expense -> {
            boolean isInvolved = Arrays.asList(expense.getParticipants()).contains(username);
            boolean isPayer = expense.getPayer().equals(username);

            if (isInvolved) {
                System.out.printf("\n\033[1;33m[%s] %s - %s paid ₹%.2f\n",
                        expense.getTimestamp(), expense.getTitle(), expense.getPayer(), expense.getAmount());

                System.out.println("\033[0;37mDescription: " + expense.getDescription());

                if (isPayer) {
                    System.out.printf("\033[0;32mYou paid for: %s\n",
                            String.join(", ", expense.getParticipants()));
                } else {
                    double share = expense.getAmount() / expense.getParticipants().length;
                    System.out.printf("\033[0;33mYour share: ₹%.2f (to %s)\n",
                            share, expense.getPayer());
                }
            }
        });
    }

    public List<Expense> getExpenses() {
        return expenses;
    }
}