
package com.eduzeb.connect;

public class StudentProfile {
    private int userId;
    private double averageGrade;
    private String[] preferredSubjects;
    private String[] extracurricular;
    private String district;
    private double latitude;
    private double longitude;
    
    public StudentProfile(int userId, double averageGrade, String[] preferredSubjects,
                         String[] extracurricular, String district, 
                         double latitude, double longitude) {
        this.userId = userId;
        this.averageGrade = averageGrade;
        this.preferredSubjects = preferredSubjects;
        this.extracurricular = extracurricular;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Getters
    public int getUserId() { return userId; }
    public double getAverageGrade() { return averageGrade; }
    public String[] getPreferredSubjects() { return preferredSubjects; }
    public String[] getExtracurricular() { return extracurricular; }
    public String getDistrict() { return district; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    
    // Setters
    public void setAverageGrade(double averageGrade) { 
        this.averageGrade = averageGrade; 
    }
    
    public void setPreferredSubjects(String[] subjects) { 
        this.preferredSubjects = subjects; 
    }
    
    public void setExtracurricular(String[] activities) { 
        this.extracurricular = activities; 
    }
    
    public void setDistrict(String district) { 
        this.district = district; 
    }
    
    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    // Helper methods
    public String getGradeLevel() {
        if (averageGrade >= 80) return "Distinction";
        if (averageGrade >= 70) return "Merit";
        if (averageGrade >= 60) return "Upper Credit";
        if (averageGrade >= 50) return "Credit";
        return "Pass";
    }
    
    public boolean hasStrongAcademics() {
        return averageGrade >= 70;
    }
    
    public String getSubjectsString() {
        return String.join(", ", preferredSubjects);
    }
    
    public String getExtracurricularString() {
        return String.join(", ", extracurricular);
    }
}
