package com.eduzeb.connect;

public class User {
    private int id;
    private String name;
    private String email;
    private String userType;
    private String phone;
    private boolean profileComplete;
    private String createdAt;

    // Constructor for login authentication
    public User(int id, String name, String email, String userType, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.phone = phone;
    }

    // Full constructor
    public User(int id, String name, String email, String userType, String phone, 
                boolean profileComplete, String createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.phone = phone;
        this.profileComplete = profileComplete;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUserType() { return userType; }
    public String getPhone() { return phone; }
    public boolean isProfileComplete() { return profileComplete; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUserType(String userType) { this.userType = userType; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}

// School Model Class
class School {
    private int id;
    private String name;
    private String type;
    private String location;
    private double rating;
    private int fees;
    private String description;
    private String requirements;
    private String contact;

    public School(int id, String name, String type, String location, double rating, 
                  int fees, String description, String requirements, String contact) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.rating = rating;
        this.fees = fees;
        this.description = description;
        this.requirements = requirements;
        this.contact = contact;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public double getRating() { return rating; }
    public int getFees() { return fees; }
    public String getDescription() { return description; }
    public String getRequirements() { return requirements; }
    public String getContact() { return contact; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setLocation(String location) { this.location = location; }
    public void setRating(double rating) { this.rating = rating; }
    public void setFees(int fees) { this.fees = fees; }
    public void setDescription(String description) { this.description = description; }
    public void setRequirements(String requirements) { this.requirements = requirements; }
    public void setContact(String contact) { this.contact = contact; }
}

// Application Model Class
class Application {
    private int id;
    private int userId;
    private int schoolId;
    private String status;
    private String submissionDate;
    private String lastUpdated;
    private String notes;
    private String schoolName;
    private String schoolLocation;

    public Application(int id, int userId, int schoolId, String status, String submissionDate,
                      String lastUpdated, String notes, String schoolName, String schoolLocation) {
        this.id = id;
        this.userId = userId;
        this.schoolId = schoolId;
        this.status = status;
        this.submissionDate = submissionDate;
        this.lastUpdated = lastUpdated;
        this.notes = notes;
        this.schoolName = schoolName;
        this.schoolLocation = schoolLocation;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getSchoolId() { return schoolId; }
    public String getStatus() { return status; }
    public String getSubmissionDate() { return submissionDate; }
    public String getLastUpdated() { return lastUpdated; }
    public String getNotes() { return notes; }
    public String getSchoolName() { return schoolName; }
    public String getSchoolLocation() { return schoolLocation; }

    // Status helper methods
    public boolean isPending() { return "pending".equals(status); }
    public boolean isApproved() { return "approved".equals(status); }
    public boolean isRejected() { return "rejected".equals(status); }
    public boolean isUnderReview() { return "under_review".equals(status); }

    public String getStatusDisplayText() {
        switch (status.toLowerCase()) {
            case "pending": return "Pending Review";
            case "under_review": return "Under Review";
            case "approved": return "Approved";
            case "rejected": return "Rejected";
            default: return "Unknown Status";
        }
    }
}

// Document Model Class
class Document {
    private int id;
    private int userId;
    private String name;
    private String type;
    private String filePath;
    private boolean isVerified;
    private String uploadedAt;

    public Document(int id, int userId, String name, String type, String filePath, 
                   boolean isVerified, String uploadedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.filePath = filePath;
        this.isVerified = isVerified;
        this.uploadedAt = uploadedAt;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getFilePath() { return filePath; }
    public boolean isVerified() { return isVerified; }
    public String getUploadedAt() { return uploadedAt; }

    public String getTypeDisplayText() {
        switch (type.toLowerCase()) {
            case "report": return "Academic Report";
            case "certificate": return "Certificate";
            case "testimonial": return "Testimonial";
            case "transcript": return "Transcript";
            default: return "Document";
        }
    }
}

// Notification Model Class
class Notification {
    private int id;
    private int userId;
    private String title;
    private String message;
    private boolean isRead;
    private String createdAt;

    public Notification(int id, int userId, String title, String message, 
                       boolean isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public String getCreatedAt() { return createdAt; }

    // Setters
    public void setRead(boolean read) { isRead = read; }
}

// School Recommendation Model Class
class SchoolRecommendation {
    private int id;
    private int userId;
    private int schoolId;
    private double matchScore;
    private String reason;
    private String createdAt;
    private String schoolName;
    private String schoolType;
    private String schoolLocation;
    private double schoolRating;
    private int schoolFees;
    private String schoolDescription;

    public SchoolRecommendation(int id, int userId, int schoolId, double matchScore, 
                               String reason, String createdAt, String schoolName, 
                               String schoolType, String schoolLocation, double schoolRating,
                               int schoolFees, String schoolDescription) {
        this.id = id;
        this.userId = userId;
        this.schoolId = schoolId;
        this.matchScore = matchScore;
        this.reason = reason;
        this.createdAt = createdAt;
        this.schoolName = schoolName;
        this.schoolType = schoolType;
        this.schoolLocation = schoolLocation;
        this.schoolRating = schoolRating;
        this.schoolFees = schoolFees;
        this.schoolDescription = schoolDescription;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getSchoolId() { return schoolId; }
    public double getMatchScore() { return matchScore; }
    public String getReason() { return reason; }
    public String getCreatedAt() { return createdAt; }
    public String getSchoolName() { return schoolName; }
    public String getSchoolType() { return schoolType; }
    public String getSchoolLocation() { return schoolLocation; }
    public double getSchoolRating() { return schoolRating; }
    public int getSchoolFees() { return schoolFees; }
    public String getSchoolDescription() { return schoolDescription; }

    public int getMatchPercentage() {
        return (int) (matchScore * 100);
    }

    public String getMatchLevel() {
        if (matchScore >= 0.8) return "Excellent Match";
        else if (matchScore >= 0.6) return "Good Match";
        else if (matchScore >= 0.4) return "Fair Match";
        else return "Consider";
    }
}
