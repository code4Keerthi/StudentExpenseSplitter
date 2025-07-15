public class Expense {
    private String title;
    private String description;
    private double amount;
    private String payer;
    private String[] participants;
    private String timestamp;

    public Expense(String title, String description, double amount,
                   String payer, String[] participants, String timestamp) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.payer = payer;
        this.participants = participants;
        this.timestamp = timestamp;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getPayer() { return payer; }
    public String[] getParticipants() { return participants; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s paid ₹%.2f for %s",
                timestamp, title, payer, amount, String.join(", ", participants));
    }
}