
package com.eduzeb.connect;

public class SchoolPerformance {
    private String schoolName;
    private int overallRating; // 0-100
    private double passRate;
    private double bachelorPassRate;
    private int districtRank;
    private String[] strengths;
    private String[] improvements;
    private String achievements;
    
    public SchoolPerformance(String schoolName, int overallRating, double passRate, 
                           double bachelorPassRate, int districtRank, String[] strengths, 
                           String[] improvements, String achievements) {
        this.schoolName = schoolName;
        this.overallRating = overallRating;
        this.passRate = passRate;
        this.bachelorPassRate = bachelorPassRate;
        this.districtRank = districtRank;
        this.strengths = strengths;
        this.improvements = improvements;
        this.achievements = achievements;
    }
    
    // Getters
    public String getSchoolName() { return schoolName; }
    public int getOverallRating() { return overallRating; }
    public double getPassRate() { return passRate; }
    public double getBachelorPassRate() { return bachelorPassRate; }
    public int getDistrictRank() { return districtRank; }
    public String[] getStrengths() { return strengths; }
    public String[] getImprovements() { return improvements; }
    public String getAchievements() { return achievements; }
    
    public String getPerformanceGrade() {
        if (overallRating >= 90) return "A+";
        if (overallRating >= 80) return "A";
        if (overallRating >= 70) return "B";
        if (overallRating >= 60) return "C";
        return "D";
    }
    
    public String getPerformanceDescription() {
        if (overallRating >= 90) return "Outstanding Performance";
        if (overallRating >= 80) return "Excellent Performance";
        if (overallRating >= 70) return "Good Performance";
        if (overallRating >= 60) return "Satisfactory Performance";
        return "Needs Improvement";
    }
}
