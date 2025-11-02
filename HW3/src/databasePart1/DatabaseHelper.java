package databasePart1;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import application.User;


/**
 * Enhanced DatabaseHelper class with support for multiple roles, user management,
 * and one-time passwords for password recovery.
 * It allows proper connection management to prevent "connection closed" errors.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		// Updated user table with additional fields
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "firstName VARCHAR(255), "
				+ "lastName VARCHAR(255), "
				+ "email VARCHAR(255), "
				+ "oneTimePassword VARCHAR(255), "
				+ "mustChangePassword BOOLEAN DEFAULT FALSE)";
		statement.execute(userTable);
		
		// User roles table for multiple roles support
		String userRolesTable = "CREATE TABLE IF NOT EXISTS cse360userroles ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255), "
				+ "role VARCHAR(50), "
				+ "FOREIGN KEY (userName) REFERENCES cse360users(userName) ON DELETE CASCADE, "
				+ "UNIQUE(userName, role))";
		statement.execute(userRolesTable);
		
		// Enhanced invitation codes table with deadline
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE, "
				+ "createdBy VARCHAR(255), "
				+ "deadline TIMESTAMP, "
				+ "usedBy VARCHAR(255), "
				+ "usedAt TIMESTAMP)";
		statement.execute(invitationCodesTable);
	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		ensureConnection(); // Make sure connection is valid
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Enhanced user registration with full user information
	public void register(User user) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		
		// Insert basic user information
		String insertUser = "INSERT INTO cse360users (userName, password, firstName, lastName, email, oneTimePassword, mustChangePassword) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getFirstName());
			pstmt.setString(4, user.getLastName());
			pstmt.setString(5, user.getEmail());
			pstmt.setString(6, user.getOneTimePassword());
			pstmt.setBoolean(7, user.getMustChangePassword());
			pstmt.executeUpdate();
		}
		
		// Insert user roles
		for (String role : user.getRoles()) {
			addRoleToUser(user.getUserName(), role);
		}
	}

	// Legacy login method for backward compatibility
	public boolean login(User user) throws SQLException {
		return login(user.getUserName(), user.getPassword(), user.getRole());
	}
	
	// Enhanced login method
	public boolean login(String userName, String password, String role) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		
		// First check regular password
		String query = "SELECT * FROM cse360users u JOIN cse360userroles r ON u.userName = r.userName " +
				"WHERE u.userName = ? AND u.password = ? AND r.role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return true;
				}
			}
		}
		
		// Check one-time password
		String otpQuery = "SELECT * FROM cse360users u JOIN cse360userroles r ON u.userName = r.userName " +
				"WHERE u.userName = ? AND u.oneTimePassword = ? AND r.role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(otpQuery)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, password);
			pstmt.setString(3, role);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Get full user information
	public User getUser(String userName) throws SQLException {
		ensureConnection(); // Make sure connection is valid
		String query = "SELECT * FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					User user = new User(
						rs.getString("userName"),
						rs.getString("password"),
						rs.getString("firstName"),
						rs.getString("lastName"),
						rs.getString("email"),
						getUserRoles(userName)
					);
					user.setOneTimePassword(rs.getString("oneTimePassword"));
					user.setMustChangePassword(rs.getBoolean("mustChangePassword"));
					return user;
				}
			}
		}
		return null;
	}
	
	// Get all user roles
	public Set<String> getUserRoles(String userName) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		Set<String> roles = new HashSet<>();
		String query = "SELECT role FROM cse360userroles WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					roles.add(rs.getString("role"));
				}
			}
		}
		return roles;
	}
	
	// Legacy method for backward compatibility
	public String getUserRole(String userName) {
		try {
			Set<String> roles = getUserRoles(userName);
			return roles.isEmpty() ? null : roles.iterator().next();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Check if user exists
	public boolean doesUserExist(String userName) {
		try {
			ensureConnection(); // Ensure connection is valid
			String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, userName);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// Get all users for admin management
	public List<User> getAllUsers() throws SQLException {
		ensureConnection(); // Ensure connection is valid
		List<User> users = new ArrayList<>();
		String query = "SELECT * FROM cse360users ORDER BY userName";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					String userName = rs.getString("userName");
					String firstName = rs.getString("firstName");
					String lastName = rs.getString("lastName");
					String email = rs.getString("email");
					
					// Debug logging
					System.out.println("=== LOADING USER FROM DATABASE ===");
					System.out.println("Username: " + userName);
					System.out.println("FirstName from DB: '" + firstName + "'");
					System.out.println("LastName from DB: '" + lastName + "'");
					System.out.println("Email from DB: '" + email + "'");
					
					User user = new User(
						userName,
						rs.getString("password"),
						firstName,
						lastName,
						email,
						getUserRoles(userName)
					);
					user.setOneTimePassword(rs.getString("oneTimePassword"));
					user.setMustChangePassword(rs.getBoolean("mustChangePassword"));
					
					// Debug: Check what the User object contains after creation
					System.out.println("After User creation:");
					System.out.println("  User.getFirstName(): '" + user.getFirstName() + "'");
					System.out.println("  User.getLastName(): '" + user.getLastName() + "'");
					System.out.println("  User.getEmail(): '" + user.getEmail() + "'");
					System.out.println("  User.getFullName(): '" + user.getFullName() + "'");
					System.out.println("=== END USER LOADING ===");
					
					users.add(user);
				}
			}
		}
		return users;
	}
	
	// Add role to user
	public void addRoleToUser(String userName, String role) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		
		// Check if role already exists first
		String checkQuery = "SELECT COUNT(*) FROM cse360userroles WHERE userName = ? AND role = ?";
		try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
			checkStmt.setString(1, userName);
			checkStmt.setString(2, role.toLowerCase());
			ResultSet rs = checkStmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return; // Role already exists, no need to add
			}
		}
		
		// Insert new role
		String query = "INSERT INTO cse360userroles (userName, role) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, role.toLowerCase());
			pstmt.executeUpdate();
		}
	}
	
	// Remove role from user
	public void removeRoleFromUser(String userName, String role) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		String query = "DELETE FROM cse360userroles WHERE userName = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, role.toLowerCase());
			pstmt.executeUpdate();
		}
	}
	
	// Delete user (with admin protection)
	public boolean deleteUser(String userName, String adminUserName) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		
		// Prevent admin from deleting themselves
		if (userName.equals(adminUserName)) {
			return false;
		}
		
		// Check if this is the last admin
		if (isLastAdmin(userName)) {
			return false;
		}
		
		String query = "DELETE FROM cse360users WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			int rowsAffected = pstmt.executeUpdate();
			return rowsAffected > 0;
		}
	}
	
	// Check if user is the last admin
	private boolean isLastAdmin(String userName) throws SQLException {
		String query = "SELECT COUNT(*) FROM cse360userroles WHERE role = 'admin'";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					int adminCount = rs.getInt(1);
					if (adminCount <= 1) {
						// Check if this user is an admin
						return hasRole(userName, "admin");
					}
				}
			}
		}
		return false;
	}
	
	// Check if user has specific role
	public boolean hasRole(String userName, String role) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		String query = "SELECT COUNT(*) FROM cse360userroles WHERE userName = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
			pstmt.setString(2, role.toLowerCase());
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}
	
	// Set one-time password for user
	public void setOneTimePassword(String userName, String oneTimePassword) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		String query = "UPDATE cse360users SET oneTimePassword = ?, mustChangePassword = TRUE WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, oneTimePassword);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}
	}
	
	// Update user password and clear one-time password
	public void updatePassword(String userName, String newPassword) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		String query = "UPDATE cse360users SET password = ?, oneTimePassword = NULL, mustChangePassword = FALSE WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, newPassword);
			pstmt.setString(2, userName);
			pstmt.executeUpdate();
		}
	}
	
	// Update user information
	public void updateUser(User user) throws SQLException {
		ensureConnection(); // Ensure connection is valid
		String query = "UPDATE cse360users SET firstName = ?, lastName = ?, email = ? WHERE userName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getFirstName());
			pstmt.setString(2, user.getLastName());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getUserName());
			pstmt.executeUpdate();
		}
	}

	// Enhanced invitation code generation with deadline
	public String generateInvitationCode(String createdBy, int daysValid) {
		try {
			ensureConnection(); // Ensure connection is valid
			String code = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
			LocalDateTime deadline = LocalDateTime.now().plusDays(daysValid);
			
			System.out.println("=== GENERATING INVITATION CODE ===");
			System.out.println("Code: " + code);
			System.out.println("Created by: " + createdBy);
			System.out.println("Days valid: " + daysValid);
			System.out.println("Deadline: " + deadline);
			
			String query = "INSERT INTO InvitationCodes (code, createdBy, deadline, isUsed) VALUES (?, ?, ?, FALSE)";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, code);
				pstmt.setString(2, createdBy);
				pstmt.setTimestamp(3, Timestamp.valueOf(deadline));
				int rowsInserted = pstmt.executeUpdate();
				System.out.println("Rows inserted: " + rowsInserted);
				System.out.println("=== INVITATION CODE GENERATION COMPLETE ===");
				return code;
			}
		} catch (SQLException e) {
			System.err.println("Error generating invitation code: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	// Legacy method for backward compatibility
	public String generateInvitationCode() {
		return generateInvitationCode("system", 7); // Default 7 days
	}
	
	// Enhanced invitation code validation with proper connection management
	public boolean validateInvitationCode(String code) {
		try {
			ensureConnection(); // Ensure connection is valid
			System.out.println("=== VALIDATING INVITATION CODE ===");
			System.out.println("Input code: '" + code + "'");
			System.out.println("Current timestamp: " + new Timestamp(System.currentTimeMillis()));
			
			String query = "SELECT * FROM InvitationCodes WHERE code = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, code);
				ResultSet rs = pstmt.executeQuery();
				
				if (rs.next()) {
					System.out.println("CODE FOUND IN DATABASE:");
					System.out.println("  Code: '" + rs.getString("code") + "'");
					System.out.println("  IsUsed: " + rs.getBoolean("isUsed"));
					System.out.println("  CreatedBy: " + rs.getString("createdBy"));
					Timestamp deadline = rs.getTimestamp("deadline");
					System.out.println("  Deadline: " + deadline);
					System.out.println("  Current time: " + new Timestamp(System.currentTimeMillis()));
					
					boolean isUsed = rs.getBoolean("isUsed");
					boolean isExpired = deadline != null && deadline.before(new Timestamp(System.currentTimeMillis()));
					
					System.out.println("  Used: " + isUsed);
					System.out.println("  Expired: " + isExpired);
					
					boolean isValid = !isUsed && !isExpired;
					System.out.println("  VALIDATION RESULT: " + isValid);
					System.out.println("=== VALIDATION COMPLETE ===");
					return isValid;
				} else {
					System.out.println("CODE NOT FOUND IN DATABASE");
					System.out.println("=== VALIDATION COMPLETE ===");
					return false;
				}
			}
		} catch (SQLException e) {
			System.err.println("Error validating invitation code: " + e.getMessage());
			e.printStackTrace();
			System.out.println("=== VALIDATION COMPLETE (ERROR) ===");
			return false;
		}
	}
	
	// NEW: Separate method to mark invitation code as used after successful registration
	public void useInvitationCode(String code, String usedBy) {
		try {
			ensureConnection(); // Ensure connection is valid
			System.out.println("=== MARKING INVITATION CODE AS USED ===");
			System.out.println("Code: " + code);
			System.out.println("Used by: " + usedBy);
			markInvitationCodeAsUsed(code, usedBy);
			System.out.println("=== MARK AS USED COMPLETE ===");
		} catch (SQLException e) {
			System.err.println("Error ensuring connection for useInvitationCode: " + e.getMessage());
		}
	}
	
	// Mark invitation code as used
	private void markInvitationCodeAsUsed(String code, String usedBy) {
		try {
			String query = "UPDATE InvitationCodes SET isUsed = TRUE, usedBy = ?, usedAt = CURRENT_TIMESTAMP WHERE code = ?";
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, usedBy);
				pstmt.setString(2, code);
				int rowsUpdated = pstmt.executeUpdate();
				System.out.println("Rows updated when marking as used: " + rowsUpdated);
			}
		} catch (SQLException e) {
			System.err.println("Error marking invitation code as used: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Check if connection is still valid
	public boolean isConnectionValid() {
		try {
			return connection != null && !connection.isClosed() && connection.isValid(3);
		} catch (SQLException e) {
			return false;
		}
	}
	
	// Reconnect if connection is invalid
	public void ensureConnection() throws SQLException {
		if (!isConnectionValid()) {
			System.out.println("Database connection lost, reconnecting...");
			connectToDatabase();
		}
	}

	public void closeConnection() {
		// Only close when explicitly requested (like application shutdown)
		// Don't close on page navigation to prevent "connection closed" errors
		System.out.println("Connection close requested - but keeping connection open for page navigation");
	}
}