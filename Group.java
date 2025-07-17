import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Group {
    private String groupId;
    private String groupName;
    private String description;
    private String creator;
    private Set<String> members;
    private LocalDateTime createdAt;

    public Group(String groupName, String description, String creator, Set<String> members) {
        this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.description = description;
        this.creator = creator;
        this.members = new HashSet<>(members);
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public String getDescription() { return description; }
    public String getCreator() { return creator; }
    public Set<String> getMembers() { return members; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void addMember(String username) {
        members.add(username);
    }

    @Override
    public String toString() {
        return String.format("%s (%d members)", groupName, members.size());
    }
}