package com.example.mydormitory;

import android.net.Uri;

/**
 * Класс для хранения информации о пользователях приложения "Мое Общежитие"
 */
public class User {
    
    public enum UserType {
        RESIDENT("Проживающий в общежитии"),
        MODERATOR("Модератор");
        
        private final String displayName;
        
        UserType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String phone;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private UserType userType;
    private Uri documentUri;
    private boolean isApproved;
    private String approvedBy;
    private long registrationDate;
    private long approvalDate;
    
    // Конструктор для регистрации
    public User(String phone, String password, String firstName, String lastName, String middleName, Uri documentUri) {
        this.phone = phone;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.documentUri = documentUri;
        this.userType = UserType.RESIDENT; // По умолчанию - проживающий
        this.isApproved = false;
        this.registrationDate = System.currentTimeMillis();
    }
    
    // Конструктор для существующих пользователей
    public User(String id, String phone, String password, String firstName, String lastName, 
                String middleName, UserType userType, boolean isApproved, String approvedBy, 
                long registrationDate, long approvalDate) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.userType = userType;
        this.isApproved = isApproved;
        this.approvedBy = approvedBy;
        this.registrationDate = registrationDate;
        this.approvalDate = approvalDate;
    }
    
    // Геттеры
    public String getId() { return id; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public UserType getUserType() { return userType; }
    public Uri getDocumentUri() { return documentUri; }
    public boolean isApproved() { return isApproved; }
    public String getApprovedBy() { return approvedBy; }
    public long getRegistrationDate() { return registrationDate; }
    public long getApprovalDate() { return approvalDate; }
    
    // Сеттеры
    public void setId(String id) { this.id = id; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public void setUserType(UserType userType) { this.userType = userType; }
    public void setDocumentUri(Uri documentUri) { this.documentUri = documentUri; }
    public void setApproved(boolean approved) { this.isApproved = approved; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
    public void setRegistrationDate(long registrationDate) { this.registrationDate = registrationDate; }
    public void setApprovalDate(long approvalDate) { this.approvalDate = approvalDate; }
    
    // Вспомогательные методы
    public String getFullName() {
        return lastName + " " + firstName + " " + middleName;
    }
    
    public String getShortName() {
        return lastName + " " + firstName.charAt(0) + "." + middleName.charAt(0) + ".";
    }
    
    public boolean isModerator() {
        return userType == UserType.MODERATOR;
    }
    
    public boolean isResident() {
        return userType == UserType.RESIDENT;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", phone='" + phone + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", userType=" + userType.getDisplayName() +
                ", isApproved=" + isApproved +
                '}';
    }
}
