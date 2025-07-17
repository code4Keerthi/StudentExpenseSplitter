import java.util.Arrays;
import java.util.*;

public class Expense {
    private String expenseId;
    private String name;
    private String description;
    private double amount;
    private String payer;
    private String[] participants;
    private String date;
    private String category;
    private String groupId;

    public Expense(String name, String description, double amount, String payer,
                   String[] participants, String date, String category, String groupId) {
        this.expenseId = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
        this.date = date;
        this.category = category;
        this.groupId = groupId;
    }

    // Getters
    public String getExpenseId() { return expenseId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getPayer() { return payer; }
    public String[] getParticipants() { return participants; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public String getGroupId() { return groupId; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    @Override
    public String toString() {
        return String.format(
                "Expense [ID: %s, Name: %s, Amount: %.2f, Payer: %s, Date: %s, Category: %s]",
                expenseId, name, amount, payer, date, category
        );
    }

    // In Expense.java
    public String getTitle() {
        return this.name;  // Or whatever field represents the title
    }

    public String getTimestamp() {
        return this.date;  // Or whatever field represents the timestamp
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return expenseId.equals(expense.expenseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expenseId);
    }
}
