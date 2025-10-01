package com.eduzeb.connect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EduZebConnect.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseHelper";

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_SCHOOLS = "schools";
    private static final String TABLE_APPLICATIONS = "applications";
    private static final String TABLE_DOCUMENTS = "documents";
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String TABLE_RECOMMENDATIONS = "recommendations";

    // Users Table Columns
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "name";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_TYPE = "user_type"; // student, admin
    private static final String USER_PHONE = "phone";
    private static final String USER_CREATED_AT = "created_at";
    private static final String USER_PROFILE_COMPLETE = "profile_complete";

    // Schools Table Columns
    private static final String SCHOOL_ID = "school_id";
    private static final String SCHOOL_NAME = "school_name";
    private static final String SCHOOL_TYPE = "school_type"; // public, private
    private static final String SCHOOL_LOCATION = "location";
    private static final String SCHOOL_RATING = "rating";
    private static final String SCHOOL_FEES = "fees";
    private static final String SCHOOL_DESCRIPTION = "description";
    private static final String SCHOOL_REQUIREMENTS = "requirements";
    private static final String SCHOOL_CONTACT = "contact_info";

    // Applications Table Columns
    private static final String APP_ID = "app_id";
    private static final String APP_USER_ID = "user_id";
    private static final String APP_SCHOOL_ID = "school_id";
    private static final String APP_STATUS = "status"; // pending, approved, rejected, under_review
    private static final String APP_SUBMISSION_DATE = "submission_date";
    private static final String APP_LAST_UPDATED = "last_updated";
    private static final String APP_NOTES = "notes";

    // Documents Table Columns
    private static final String DOC_ID = "doc_id";
    private static final String DOC_USER_ID = "user_id";
    private static final String DOC_NAME = "document_name";
    private static final String DOC_TYPE = "document_type"; // report, certificate, testimonial
    private static final String DOC_PATH = "file_path";
    private static final String DOC_UPLOADED_AT = "uploaded_at";
    private static final String DOC_VERIFIED = "is_verified";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHOOLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOMMENDATIONS);
        onCreate(db);
    }

    private void createTables(SQLiteDatabase db) {
        // Create Users table
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USER_NAME + " TEXT NOT NULL,"
                + USER_EMAIL + " TEXT UNIQUE NOT NULL,"
                + USER_PASSWORD + " TEXT NOT NULL,"
                + USER_TYPE + " TEXT NOT NULL,"
                + USER_PHONE + " TEXT,"
                + USER_PROFILE_COMPLETE + " INTEGER DEFAULT 0,"
                + USER_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(createUsersTable);

        // Create Schools table
        String createSchoolsTable = "CREATE TABLE " + TABLE_SCHOOLS + "("
                + SCHOOL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SCHOOL_NAME + " TEXT NOT NULL,"
                + SCHOOL_TYPE + " TEXT NOT NULL,"
                + SCHOOL_LOCATION + " TEXT NOT NULL,"
                + SCHOOL_RATING + " REAL DEFAULT 0.0,"
                + SCHOOL_FEES + " INTEGER DEFAULT 0,"
                + SCHOOL_DESCRIPTION + " TEXT,"
                + SCHOOL_REQUIREMENTS + " TEXT,"
                + SCHOOL_CONTACT + " TEXT"
                + ")";
        db.execSQL(createSchoolsTable);

        // Create Applications table
        String createApplicationsTable = "CREATE TABLE " + TABLE_APPLICATIONS + "("
                + APP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + APP_USER_ID + " INTEGER NOT NULL,"
                + APP_SCHOOL_ID + " INTEGER NOT NULL,"
                + APP_STATUS + " TEXT DEFAULT 'pending',"
                + APP_SUBMISSION_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + APP_LAST_UPDATED + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + APP_NOTES + " TEXT,"
                + "FOREIGN KEY(" + APP_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + "),"
                + "FOREIGN KEY(" + APP_SCHOOL_ID + ") REFERENCES " + TABLE_SCHOOLS + "(" + SCHOOL_ID + ")"
                + ")";
        db.execSQL(createApplicationsTable);

        // Create Documents table
        String createDocumentsTable = "CREATE TABLE " + TABLE_DOCUMENTS + "("
                + DOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DOC_USER_ID + " INTEGER NOT NULL,"
                + DOC_NAME + " TEXT NOT NULL,"
                + DOC_TYPE + " TEXT NOT NULL,"
                + DOC_PATH + " TEXT,"
                + DOC_VERIFIED + " INTEGER DEFAULT 0,"
                + DOC_UPLOADED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(" + DOC_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + USER_ID + ")"
                + ")";
        db.execSQL(createDocumentsTable);

        // Create Notifications table
        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + "notification_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "title TEXT NOT NULL,"
                + "message TEXT NOT NULL,"
                + "is_read INTEGER DEFAULT 0,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(" + USER_ID + ")"
                + ")";
        db.execSQL(createNotificationsTable);

        // Create Recommendations table
        String createRecommendationsTable = "CREATE TABLE " + TABLE_RECOMMENDATIONS + "("
                + "recommendation_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "school_id INTEGER NOT NULL,"
                + "match_score REAL DEFAULT 0.0,"
                + "reason TEXT,"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(user_id) REFERENCES " + TABLE_USERS + "(" + USER_ID + "),"
                + "FOREIGN KEY(school_id) REFERENCES " + TABLE_SCHOOLS + "(" + SCHOOL_ID + ")"
                + ")";
        db.execSQL(createRecommendationsTable);
    }

    private void insertSampleData(SQLiteDatabase db) {
        // Insert sample admin user
        ContentValues adminValues = new ContentValues();
        adminValues.put(USER_NAME, "Admin User");
        adminValues.put(USER_EMAIL, "admin@eduzeb.com");
        adminValues.put(USER_PASSWORD, "admin123");
        adminValues.put(USER_TYPE, "admin");
        adminValues.put(USER_PHONE, "+1234567890");
        db.insert(TABLE_USERS, null, adminValues);

        // Insert sample student user
        ContentValues studentValues = new ContentValues();
        studentValues.put(USER_NAME, "John Doe");
        studentValues.put(USER_EMAIL, "student@example.com");
        studentValues.put(USER_PASSWORD, "student123");
        studentValues.put(USER_TYPE, "student");
        studentValues.put(USER_PHONE, "+0987654321");
        db.insert(TABLE_USERS, null, studentValues);

        // Insert sample schools
        insertSampleSchool(db, "Springfield High School", "public", "Springfield", 4.2, 0, 
            "Excellent public high school with strong academic programs");
        insertSampleSchool(db, "Oakwood Academy", "private", "Oakwood", 4.7, 15000,
            "Premium private academy with advanced curriculum");
        insertSampleSchool(db, "Riverside High", "public", "Riverside", 4.0, 0,
            "Community-focused school with diverse programs");
        insertSampleSchool(db, "Elite Prep School", "private", "Downtown", 4.8, 25000,
            "Top-tier preparatory school for university admission");
    }

    private void insertSampleSchool(SQLiteDatabase db, String name, String type, String location, 
                                   double rating, int fees, String description) {
        ContentValues values = new ContentValues();
        values.put(SCHOOL_NAME, name);
        values.put(SCHOOL_TYPE, type);
        values.put(SCHOOL_LOCATION, location);
        values.put(SCHOOL_RATING, rating);
        values.put(SCHOOL_FEES, fees);
        values.put(SCHOOL_DESCRIPTION, description);
        values.put(SCHOOL_REQUIREMENTS, "Good academic record, Character reference, Interview");
        values.put(SCHOOL_CONTACT, "info@" + name.toLowerCase().replace(" ", "") + ".edu");
        db.insert(TABLE_SCHOOLS, null, values);
    }

    // User Authentication
    public User authenticateUser(String email, String password, String userType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{USER_ID, USER_NAME, USER_EMAIL, USER_TYPE, USER_PHONE},
                USER_EMAIL + "=? AND " + USER_PASSWORD + "=? AND " + USER_TYPE + "=?",
                new String[]{email, password, userType}, null, null, null);

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    // Dashboard Statistics
    public int getActiveApplicationsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICATIONS 
                + " WHERE " + APP_USER_ID + "=? AND " + APP_STATUS + " IN ('pending', 'under_review')",
                new String[]{String.valueOf(userId)});
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getRecommendationsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECOMMENDATIONS 
                + " WHERE user_id=?", new String[]{String.valueOf(userId)});
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getUnreadNotificationsCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS 
                + " WHERE user_id=? AND is_read=0", new String[]{String.valueOf(userId)});
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // School Management
    public List<School> getAllSchools() {
        List<School> schools = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SCHOOLS + " ORDER BY " + SCHOOL_RATING + " DESC", null);
        
        if (cursor.moveToFirst()) {
            do {
                School school = new School(
                    cursor.getInt(0), // school_id
                    cursor.getString(1), // name
                    cursor.getString(2), // type
                    cursor.getString(3), // location
                    cursor.getDouble(4), // rating
                    cursor.getInt(5), // fees
                    cursor.getString(6), // description
                    cursor.getString(7), // requirements
                    cursor.getString(8)  // contact
                );
                schools.add(school);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schools;
    }

    public List<School> searchSchools(String query, String type, String location) {
        List<School> schools = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = "1=1";
        List<String> selectionArgs = new ArrayList<>();
        
        if (query != null && !query.isEmpty()) {
            selection += " AND (" + SCHOOL_NAME + " LIKE ? OR " + SCHOOL_DESCRIPTION + " LIKE ?)";
            selectionArgs.add("%" + query + "%");
            selectionArgs.add("%" + query + "%");
        }
        
        if (type != null && !type.equals("all")) {
            selection += " AND " + SCHOOL_TYPE + "=?";
            selectionArgs.add(type);
        }
        
        if (location != null && !location.isEmpty()) {
            selection += " AND " + SCHOOL_LOCATION + " LIKE ?";
            selectionArgs.add("%" + location + "%");
        }
        
        Cursor cursor = db.query(TABLE_SCHOOLS, null, selection, 
                selectionArgs.toArray(new String[0]), null, null, SCHOOL_RATING + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                School school = new School(
                    cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getDouble(4), cursor.getInt(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8)
                );
                schools.add(school);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return schools;
    }

    // Application Management
    public boolean submitApplication(int userId, int schoolId, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if application already exists
        Cursor cursor = db.query(TABLE_APPLICATIONS, null,
                APP_USER_ID + "=? AND " + APP_SCHOOL_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(schoolId)},
                null, null, null);
        
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return false; // Application already exists
        }
        cursor.close();
        
        ContentValues values = new ContentValues();
        values.put(APP_USER_ID, userId);
        values.put(APP_SCHOOL_ID, schoolId);
        values.put(APP_STATUS, "pending");
        values.put(APP_NOTES, notes);
        
        long result = db.insert(TABLE_APPLICATIONS, null, values);
        db.close();
        
        if (result != -1) {
            // Create notification
            createNotification(userId, "Application Submitted", 
                "Your application has been submitted successfully and is under review.");
        }
        
        return result != -1;
    }

    public List<Application> getUserApplications(int userId) {
        List<Application> applications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT a.*, s." + SCHOOL_NAME + ", s." + SCHOOL_LOCATION + 
                      " FROM " + TABLE_APPLICATIONS + " a " +
                      "JOIN " + TABLE_SCHOOLS + " s ON a." + APP_SCHOOL_ID + " = s." + SCHOOL_ID +
                      " WHERE a." + APP_USER_ID + "=? ORDER BY a." + APP_SUBMISSION_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        if (cursor.moveToFirst()) {
            do {
                Application app = new Application(
                    cursor.getInt(0), // app_id
                    cursor.getInt(1), // user_id
                    cursor.getInt(2), // school_id
                    cursor.getString(3), // status
                    cursor.getString(4), // submission_date
                    cursor.getString(5), // last_updated
                    cursor.getString(6), // notes
                    cursor.getString(7), // school_name
                    cursor.getString(8)  // school_location
                );
                applications.add(app);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return applications;
    }

    // Document Management
    public boolean uploadDocument(int userId, String docName, String docType, String filePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DOC_USER_ID, userId);
        values.put(DOC_NAME, docName);
        values.put(DOC_TYPE, docType);
        values.put(DOC_PATH, filePath);
        
        long result = db.insert(TABLE_DOCUMENTS, null, values);
        db.close();
        return result != -1;
    }

    public List<Document> getUserDocuments(int userId) {
        List<Document> documents = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_DOCUMENTS, null, DOC_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, DOC_UPLOADED_AT + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Document doc = new Document(
                    cursor.getInt(0), // doc_id
                    cursor.getInt(1), // user_id
                    cursor.getString(2), // document_name
                    cursor.getString(3), // document_type
                    cursor.getString(4), // file_path
                    cursor.getInt(5) == 1, // is_verified
                    cursor.getString(6)  // uploaded_at
                );
                documents.add(doc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return documents;
    }

    // Notification Management
    public boolean createNotification(int userId, String title, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("title", title);
        values.put("message", message);
        
        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        db.close();
        return result != -1;
    }

    public List<Notification> getUserNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, "created_at DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification(
                    cursor.getInt(0), // notification_id
                    cursor.getInt(1), // user_id
                    cursor.getString(2), // title
                    cursor.getString(3), // message
                    cursor.getInt(4) == 1, // is_read
                    cursor.getString(5)  // created_at
                );
                notifications.add(notification);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notifications;
    }

    public boolean markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_read", 1);
        
        int result = db.update(TABLE_NOTIFICATIONS, values, "notification_id=?",
                new String[]{String.valueOf(notificationId)});
        db.close();
        return result > 0;
    }

    // Recommendations
    public List<SchoolRecommendation> getUserRecommendations(int userId) {
        List<SchoolRecommendation> recommendations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT r.*, s." + SCHOOL_NAME + ", s." + SCHOOL_TYPE + ", s." + SCHOOL_LOCATION +
                      ", s." + SCHOOL_RATING + ", s." + SCHOOL_FEES + ", s." + SCHOOL_DESCRIPTION +
                      " FROM " + TABLE_RECOMMENDATIONS + " r " +
                      "JOIN " + TABLE_SCHOOLS + " s ON r.school_id = s." + SCHOOL_ID +
                      " WHERE r.user_id=? ORDER BY r.match_score DESC";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        if (cursor.moveToFirst()) {
            do {
                SchoolRecommendation rec = new SchoolRecommendation(
                    cursor.getInt(0), // recommendation_id
                    cursor.getInt(1), // user_id
                    cursor.getInt(2), // school_id
                    cursor.getDouble(3), // match_score
                    cursor.getString(4), // reason
                    cursor.getString(5), // created_at
                    cursor.getString(6), // school_name
                    cursor.getString(7), // school_type
                    cursor.getString(8), // school_location
                    cursor.getDouble(9), // school_rating
                    cursor.getInt(10), // school_fees
                    cursor.getString(11) // school_description
                );
                recommendations.add(rec);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recommendations;
    }

    // User Management
    public boolean updateUserProfile(int userId, String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, name);
        values.put(USER_PHONE, phone);
        values.put(USER_PROFILE_COMPLETE, 1);
        
        int result = db.update(TABLE_USERS, values, USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
        return result > 0;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
        
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                cursor.getInt(0), // user_id
                cursor.getString(1), // name
                cursor.getString(2), // email
                cursor.getString(3), // user_type
                cursor.getString(4)  // phone
            );
        }
        cursor.close();
        db.close();
        return user;
    }

    // Generate ML-based recommendations (simplified version)
    public void generateRecommendations(int userId) {
        // This is a simplified ML algorithm - in a real app, you'd use more sophisticated ML
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Clear existing recommendations
        db.delete(TABLE_RECOMMENDATIONS, "user_id=?", new String[]{String.valueOf(userId)});
        
        // Get all schools
        List<School> schools = getAllSchools();
        
        // Simple scoring algorithm based on school rating and fees
        for (School school : schools) {
            double matchScore = calculateMatchScore(school);
            String reason = generateRecommendationReason(school, matchScore);
            
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("school_id", school.getId());
            values.put("match_score", matchScore);
            values.put("reason", reason);
            
            db.insert(TABLE_RECOMMENDATIONS, null, values);
        }
        db.close();
    }

    private double calculateMatchScore(School school) {
        double score = 0.0;
        
        // Factor in school rating (40% weight)
        score += (school.getRating() / 5.0) * 0.4;
        
        // Factor in affordability (30% weight) - lower fees = higher score
        double affordabilityScore = school.getFees() == 0 ? 1.0 : Math.max(0, 1.0 - (school.getFees() / 30000.0));
        score += affordabilityScore * 0.3;
        
        // Factor in school type preference (30% weight)
        // This could be based on user preferences - for now, slight preference for public schools
        score += (school.getType().equals("public") ? 0.3 : 0.25);
        
        return Math.min(1.0, score);
    }

    private String generateRecommendationReason(School school, double matchScore) {
        StringBuilder reason = new StringBuilder();
        
        if (school.getRating() >= 4.5) {
            reason.append("Excellent academic rating. ");
        } else if (school.getRating() >= 4.0) {
            reason.append("Good academic reputation. ");
        }
        
        if (school.getFees() == 0) {
            reason.append("No tuition fees. ");
        } else if (school.getFees() < 10000) {
            reason.append("Affordable fees. ");
        }
        
        if (matchScore >= 0.8) {
            reason.append("Perfect match for your profile!");
        } else if (matchScore >= 0.6) {
            reason.append("Great fit based on your preferences.");
        } else {
            reason.append("Consider exploring this option.");
        }
        
        return reason.toString().trim();
    }

    // Admin-specific methods
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USER_ID}, 
                USER_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean createUser(String name, String email, String phone, String password, String userType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME, name);
        values.put(USER_EMAIL, email);
        values.put(USER_PHONE, phone);
        values.put(USER_PASSWORD, password);
        values.put(USER_TYPE, userType);
        
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public int getPendingApplicationsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_APPLICATIONS + 
                " WHERE " + APP_STATUS + "=?", new String[]{"pending"});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getTotalSchoolsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_SCHOOLS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getTotalStudentsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS + 
                " WHERE " + USER_TYPE + "=?", new String[]{"student"});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public List<Application> getAllApplicationsForAdmin() {
        List<Application> applications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String query = "SELECT a.*, s." + SCHOOL_NAME + ", s." + SCHOOL_LOCATION + 
                      " FROM " + TABLE_APPLICATIONS + " a " +
                      "JOIN " + TABLE_SCHOOLS + " s ON a." + APP_SCHOOL_ID + " = s." + SCHOOL_ID +
                      " ORDER BY a." + APP_SUBMISSION_DATE + " DESC";
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            do {
                Application app = new Application(
                    cursor.getInt(0), // app_id
                    cursor.getInt(1), // user_id
                    cursor.getInt(2), // school_id
                    cursor.getString(3), // status
                    cursor.getString(4), // submission_date
                    cursor.getString(5), // last_updated
                    cursor.getString(6), // notes
                    cursor.getString(7), // school_name
                    cursor.getString(8)  // school_location
                );
                applications.add(app);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return applications;
    }

    public String getStudentName(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{USER_NAME}, 
                USER_ID + "=?", new String[]{String.valueOf(userId)}, null, null, null);
        
        String name = "Unknown Student";
        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return name;
    }

    public boolean updateApplicationStatus(int applicationId, String status, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APP_STATUS, status);
        values.put(APP_NOTES, notes);
        values.put(APP_LAST_UPDATED, "datetime('now')");
        
        int result = db.update(TABLE_APPLICATIONS, values, 
                APP_ID + "=?", new String[]{String.valueOf(applicationId)});
        db.close();
        return result > 0;
    }

    public int bulkApproveQualifiedApplications() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Simple qualification check - approve pending applications
        // In a real app, this would have more sophisticated criteria
        ContentValues values = new ContentValues();
        values.put(APP_STATUS, "approved");
        values.put(APP_NOTES, "Bulk approved - meets requirements");
        values.put(APP_LAST_UPDATED, "datetime('now')");
        
        int result = db.update(TABLE_APPLICATIONS, values, 
                APP_STATUS + "=?", new String[]{"pending"});
        db.close();
        return result;
    }

    // School Management for Admin
    public boolean addSchool(String name, String type, String location, double rating, 
                           int fees, String description, String requirements, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCHOOL_NAME, name);
        values.put(SCHOOL_TYPE, type);
        values.put(SCHOOL_LOCATION, location);
        values.put(SCHOOL_RATING, rating);
        values.put(SCHOOL_FEES, fees);
        values.put(SCHOOL_DESCRIPTION, description);
        values.put(SCHOOL_REQUIREMENTS, requirements);
        values.put(SCHOOL_CONTACT, contact);
        
        long result = db.insert(TABLE_SCHOOLS, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateSchool(int schoolId, String name, String type, String location, 
                              double rating, int fees, String description, String requirements, String contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCHOOL_NAME, name);
        values.put(SCHOOL_TYPE, type);
        values.put(SCHOOL_LOCATION, location);
        values.put(SCHOOL_RATING, rating);
        values.put(SCHOOL_FEES, fees);
        values.put(SCHOOL_DESCRIPTION, description);
        values.put(SCHOOL_REQUIREMENTS, requirements);
        values.put(SCHOOL_CONTACT, contact);
        
        int result = db.update(TABLE_SCHOOLS, values, 
                SCHOOL_ID + "=?", new String[]{String.valueOf(schoolId)});
        db.close();
        return result > 0;
    }

    public boolean deleteSchool(int schoolId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // First check if there are any applications for this school
        Cursor cursor = db.query(TABLE_APPLICATIONS, null, 
                APP_SCHOOL_ID + "=?", new String[]{String.valueOf(schoolId)}, 
                null, null, null);
        
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return false; // Cannot delete school with existing applications
        }
        cursor.close();
        
        int result = db.delete(TABLE_SCHOOLS, SCHOOL_ID + "=?", 
                new String[]{String.valueOf(schoolId)});
        db.close();
        return result > 0;
    }

    public School getSchoolById(int schoolId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCHOOLS, null, SCHOOL_ID + "=?",
                new String[]{String.valueOf(schoolId)}, null, null, null);
        
        School school = null;
        if (cursor.moveToFirst()) {
            school = new School(
                cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getDouble(4), cursor.getInt(5),
                cursor.getString(6), cursor.getString(7), cursor.getString(8)
            );
        }
        cursor.close();
        db.close();
        return school;
    }

    // Additional Document Management Methods
    public boolean deleteDocument(int documentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DOCUMENTS, DOC_ID + "=?", 
                new String[]{String.valueOf(documentId)});
        db.close();
        return result > 0;
    }

    public boolean renameDocument(int documentId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DOC_NAME, newName);
        
        int result = db.update(TABLE_DOCUMENTS, values, DOC_ID + "=?", 
                new String[]{String.valueOf(documentId)});
        db.close();
        return result > 0;
    }

    public boolean verifyDocument(int documentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DOC_VERIFIED, 1);
        
        int result = db.update(TABLE_DOCUMENTS, values, DOC_ID + "=?", 
                new String[]{String.valueOf(documentId)});
        db.close();
        return result > 0;
    }

    public int getDocumentCountByType(int userId, String documentType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DOCUMENTS + 
                " WHERE " + DOC_USER_ID + "=? AND " + DOC_TYPE + "=?",
                new String[]{String.valueOf(userId), documentType});
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public List<Document> getDocumentsByType(String documentType) {
        List<Document> documents = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_DOCUMENTS, null, DOC_TYPE + "=?",
                new String[]{documentType}, null, null, DOC_UPLOADED_AT + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Document doc = new Document(
                    cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getInt(5) == 1,
                    cursor.getString(6)
                );
                documents.add(doc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return documents;
    }

    public boolean hasRequiredDocuments(int userId) {
        // Check if user has uploaded required documents
        String[] requiredTypes = {"transcript", "certificate", "reference"};
        
        for (String type : requiredTypes) {
            int count = getDocumentCountByType(userId, type);
            if (count == 0) {
                return false;
            }
        }
        return true;
    }

    // File Provider helper methods
    public Document getDocumentById(int documentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_DOCUMENTS, null, DOC_ID + "=?",
                new String[]{String.valueOf(documentId)}, null, null, null);
        
        Document document = null;
        if (cursor.moveToFirst()) {
            document = new Document(
                cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getInt(5) == 1,
                cursor.getString(6)
            );
        }
        cursor.close();
        db.close();
        return document;
    }
}
