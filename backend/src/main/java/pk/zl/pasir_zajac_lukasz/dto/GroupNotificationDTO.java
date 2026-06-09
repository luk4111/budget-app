package pk.zl.pasir_zajac_lukasz.dto;

public class GroupNotificationDTO {
    private String type;
    private Long groupId;
    private String groupName;
    private String title;
    private Double amount;
    private Double userShare;
    private String createdByEmail;
    private String message;

    public GroupNotificationDTO() {}

    public GroupNotificationDTO(String type, Long groupId, String groupName, String title,
                                Double amount, Double userShare, String createdByEmail, String message) {
        this.type = type;
        this.groupId = groupId;
        this.groupName = groupName;
        this.title = title;
        this.amount = amount;
        this.userShare = userShare;
        this.createdByEmail = createdByEmail;
        this.message = message;
    }

    // Gettery i Settery (zwykła Java, odporna na błędy kompilacji Lomboka)
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Double getUserShare() { return userShare; }
    public void setUserShare(Double userShare) { this.userShare = userShare; }
    public String getCreatedByEmail() { return createdByEmail; }
    public void setCreatedByEmail(String createdByEmail) { this.createdByEmail = createdByEmail; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}