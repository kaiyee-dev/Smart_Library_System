import LibraryItem.*;
import Transaction.*;
import User.*;
import helper.*;

import java.util.Scanner;

import FineCheck.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

// Main class - Entry point for Library Management System
public class Main {
    
    // Manager instances
    public static CatalogManager catalogManager;
    public static UserManager userManager;
    public static TransactionManager transactionManager;
    public static FineCalculator fineCalculator;
    public static Scanner scanner;
    
    // File paths
    public static final String DATA_DIR = "data/";
    public static final String CATALOG_FILE = DATA_DIR + "catalog.txt";
    public static final String USERS_FILE = DATA_DIR + "users.txt";
    public static final String BORROWS_FILE = DATA_DIR + "borrows.txt";
    public static final String RETURNS_FILE = DATA_DIR + "returns.txt";
    public static final String FINES_FILE = DATA_DIR + "fines.txt";
    public static final String PUBLIC_USER_ID = "PUBLIC001";
    
    // Main entry point
    public static void main(String[] args) {
        createDataDirectory();
        
        // Initialize all managers
        catalogManager = new CatalogManager(CATALOG_FILE);
        userManager = new UserManager(USERS_FILE);
        transactionManager = new TransactionManager(BORROWS_FILE, RETURNS_FILE, catalogManager);
        fineCalculator = new FineCalculator(FINES_FILE);
        scanner = new Scanner(System.in);
        
        // Initialize InputHelper
        InputHelper.init(scanner);
        
        // Create default public member account if not exists
        if (userManager.readUserById(PUBLIC_USER_ID) == null) {
            userManager.createPublicMember(PUBLIC_USER_ID, "Guest", "guest");
        }
        
        MenuHelper.printHeader("WELCOME TO LIBRARY SYSTEM");
        
        // Main application loop
        while (true) {
            if (userManager.readCurrentUser() == null) {
                handleLogin();
            } else {
                handleMain();
            }
        }
    }
    
    //  LOGIN & REGISTER 
    
    public static void handleLogin() {
        MenuHelper.showLoginMenu();
        int choice = InputHelper.getIntInput();
        if (choice == -1) return;
        
        switch (choice) {
            case 1: login(); break;
            case 2: register(); break;
            case 3: userManager.loginAsPublicMember(PUBLIC_USER_ID); break;
            case 0: System.out.println("Thank you!"); System.exit(0); break;
            default: System.out.println("Invalid choice!");
        }
    }
    
    public static void login() {
        MenuHelper.showLoginTitle();
        System.out.print("User ID: ");
        String id = scanner.nextLine();
        System.out.print("Password: ");
        String pwd = scanner.nextLine();
        userManager.login(id, pwd);
    }
    
    public static void register() {
        String username = null;
        String password = null;
        String email = null;
        String phone = null;
        String role = null;
        String loginId = null;
        String secondField = null;
        int step = 1;
        
        while (true) {
            switch (step) {
                case 1:
                    MenuHelper.showRegisterTitle();
                    System.out.print("Username: ");
                    username = scanner.nextLine();
                    if (!Validation.validUsername(username)) {
                        System.out.println(Validation.msgUsername());
                        if (!InputHelper.askToContinue("Try again")) return;
                    } else {
                        step = 2;
                    }
                    break;
                    
                case 2:
                    System.out.print("Password: ");
                    password = scanner.nextLine();
                    if (!Validation.validPassword(password)) {
                        System.out.println(Validation.msgPassword());
                        if (!InputHelper.askToContinue("Try again")) return;
                    } else {
                        step = 3;
                    }
                    break;
                    
                case 3:
                    System.out.print("Email: ");
                    email = scanner.nextLine();
                    if (!Validation.validEmail(email)) {
                        System.out.println(Validation.msgEmail());
                        if (!InputHelper.askToContinue("Try again")) return;
                    } else {
                        step = 4;
                    }
                    break;
                    
                case 4:
                    System.out.print("Phone (10-11 digits): ");
                    phone = scanner.nextLine();
                    if (!Validation.validPhone(phone)) {
                        System.out.println(Validation.msgPhone());
                        if (!InputHelper.askToContinue("Try again")) return;
                    } else {
                        step = 5;
                    }
                    break;
                    
                case 5:
                    System.out.print("Role (1. Student | 2. Faculty | 3. Staff): ");
                    String roleChoice = scanner.nextLine();
                    switch (roleChoice) {
                        case "1": role = "student"; step = 6; break;
                        case "2": role = "faculty"; step = 6; break;
                        case "3": role = "staff"; step = 6; break;
                        default:
                            System.out.println("Invalid choice! Please enter 1, 2, or 3");
                            if (!InputHelper.askToContinue("Try again")) return;
                    }
                    break;
                    
                case 6:
                    switch (role) {
                        case "student":
                            System.out.print("Student ID: ");
                            loginId = scanner.nextLine();
                            if (loginId.isEmpty()) {
                                System.out.println("Student ID cannot be empty!");
                                if (!InputHelper.askToContinue("Try again")) return;
                                break;
                            }
                            System.out.print("Major: ");
                            secondField = scanner.nextLine();
                            if (secondField.isEmpty()) {
                                System.out.println("Major cannot be empty!");
                                if (!InputHelper.askToContinue("Try again")) return;
                                break;
                            }
                            break;
                        case "faculty":
                            System.out.print("Faculty ID: ");
                            loginId = scanner.nextLine();
                            if (loginId.isEmpty()) {
                                System.out.println("Faculty ID cannot be empty!");
                                if (!InputHelper.askToContinue("Try again")) return;
                                break;
                            }
                            System.out.print("Department: ");
                            secondField = scanner.nextLine();
                            if (secondField.isEmpty()) {
                                System.out.println("Department cannot be empty!");
                                if (!InputHelper.askToContinue("Try again")) return;
                                break;
                            }
                            break;
                        case "staff":
                            System.out.print("Staff ID: ");
                            loginId = scanner.nextLine();
                            if (loginId.isEmpty()) {
                                System.out.println("Staff ID cannot be empty!");
                                if (!InputHelper.askToContinue("Try again")) return;
                                break;
                            }
                            System.out.print("Position: ");
                            secondField = scanner.nextLine();
                            if (secondField.isEmpty()) {
                                System.out.println("Position cannot be empty!");
                                if (!InputHelper.askToContinue("Try again")) return;
                                break;
                            }
                            break;
                    }
                    step = 7;
                    break;
                    
                case 7:
                    if (isLoginIdExists(loginId)) {
                        System.out.println("Login ID already exists!");
                        if (!InputHelper.askToContinue("Try again")) return;
                        step = 6;
                        break;
                    }
                    step = 8;
                    break;
                    
                case 8:
                    String systemId = "";
                    switch (role) {
                        case "student": systemId = generateSystemId("STU"); break;
                        case "faculty": systemId = generateSystemId("FAC"); break;
                        case "staff": systemId = generateSystemId("STA"); break;
                    }
                    
                    System.out.println();
                    System.out.println("========================================");
                    System.out.println("REGISTRATION SUCCESSFUL!");
                    System.out.println("Your LOGIN ID: " + loginId);
                    System.out.println("Your SYSTEM ID: " + systemId);
                    System.out.println("Your Password: " + password);
                    System.out.println("========================================");
                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                    
                    if (userManager.createUser(systemId, username, password, role, email, phone, loginId, secondField)) {
                        return;
                    }
                    step = 1;
                    break;
            }
        }
    }
    
    public static String generateSystemId(String prefix) {
        int max = 0;
        for (User user : userManager.readAllUsers()) {
            if (user.getUserId().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(user.getUserId().substring(3));
                    if (num > max) max = num;
                } catch (Exception e) {}
            }
        }
        return prefix + String.format("%03d", max + 1);
    }
    
    public static boolean isLoginIdExists(String loginId) {
        for (User user : userManager.readAllUsers()) {
            String[] parts = user.getUniqueDataForFile().split("\\|");
            if (parts.length > 0 && parts[0].equalsIgnoreCase(loginId)) return true;
        }
        return false;
    }
    
    //  HANDLERS 
    
    public static void handleMain() {
        User user = userManager.readCurrentUser();
        MenuHelper.printHeader("Welcome " + user.getUsername() + " (" + user.getRole() + ")");
        
        if (user.isStaff()) {
            handleStaff();
        } else if (user.isPublicMember()) {
            handlePublic();
        } else {
            handleUser();
        }
    }
    
    public static void handleStaff() {
        MenuHelper.showStaffMenu();
        int choice = InputHelper.getIntInput();
        if (choice == -1) return;
        
        switch (choice) {
            case 1: manageItems(); break;
            case 2: manageUsers(); break;
            case 3: manageBorrows(); break;
            case 4: manageFines(); break;
            case 0: userManager.logout(); break;
            default: System.out.println("Invalid choice!");
        }
    }
        
    public static void handleUser() {
        User user = userManager.readCurrentUser();
        boolean isPublic = user.isPublicMember();
        
        MenuHelper.showUserMenu(isPublic);
        int choice = InputHelper.getIntInput();
        if (choice == -1) return;
        
        if (isPublic) {
            switch (choice) {
                case 1: viewBooks(); break;
                case 0: userManager.logout(); break;
                default: System.out.println("Invalid choice!");
            }
        } else {
            switch (choice) {
                case 1: viewBooks(); break;
                case 2: myBorrows(); break;
                case 3: myFines(); break;
                case 0: userManager.logout(); break;
                default: System.out.println("Invalid choice!");
            }
        }
    }
    
    public static void handlePublic() {
        MenuHelper.showPublicMenu();
        int choice = InputHelper.getIntInput();
        if (choice == -1) return;
        
        switch (choice) {
            case 1: catalogManager.displayAllItems(); break;
            case 0: userManager.logout(); break;
            default: System.out.println("Invalid choice!");
        }
    }
    
    //  STAFF: ITEMS 
    
    public static void manageItems() {
        while (true) {
            MenuHelper.displayItems(catalogManager.readAllItems());
            MenuHelper.showItemsSubMenu();
            int choice = InputHelper.getIntInput();
            if (choice == -1) continue;
            
            switch (choice) {
                case 0: return;
                case 1: addItem(); break;
                case 2: updateItem(); break;
                case 3: deleteItem(); break;
                default: System.out.println("Invalid choice!");
            }
            if (!InputHelper.askToContinue("Continue")) break;
        }
    }
    
    public static void addItem() {
        MenuHelper.showAddItemHeader();
        
        String isbn = InputHelper.getValidIsbnInput();
        if (isbn == null) return;
        String title = InputHelper.getValidTitleInput();
        if (title == null) return;
        String author = InputHelper.getValidAuthorInput();
        if (author == null) return;
        String genre = InputHelper.getValidGenreInput();
        if (genre == null) return;
        System.out.print("Page Count: ");
        int pageCount = InputHelper.getIntInput();
        if (pageCount <= 0) pageCount = 0;
        
        catalogManager.createItem(new Book(isbn, title, author, genre, pageCount));
    }
    
    public static void updateItem() {
        MenuHelper.displayItems(catalogManager.readAllItems());
        
        LibraryItem item = InputHelper.getValidItem(catalogManager.readAllItems());
        if (item == null) return;
        
        MenuHelper.showCurrentItemInfo(item);
        
        String newTitle = InputHelper.getValidNewTitleWithDefault(item.getTitle());
        String newAuthor = InputHelper.getValidNewAuthorWithDefault(item.getAuthor());
        String newGenre = InputHelper.getValidNewGenreWithDefault(item.getGenre());
        
        Integer newPageCount = null;
        if (item instanceof Book) {
            MenuHelper.promptNewPageCount(((Book) item).getPageCount());
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                try {
                    newPageCount = Integer.parseInt(input);
                } catch (Exception e) {}
            }
        }
        
        catalogManager.updateItem(item.getIsbn(), newTitle, newAuthor, newGenre, newPageCount);
        
        if (!item.getStatus().equals("On Loan")) {
            MenuHelper.showUpdateStatusMenu();
            int statusChoice = InputHelper.getIntInput();
            
            if (statusChoice == -1) {
                System.out.println("Status update skipped.");
            } else {
                switch (statusChoice) {
                    case 1: catalogManager.updateItemStatus(item.getIsbn(), "Available"); break;
                    case 2: catalogManager.updateItemStatus(item.getIsbn(), "Damaged"); break;
                    case 3: catalogManager.updateItemStatus(item.getIsbn(), "Lost"); break;
                    default: break;
                }
            }
        } else {
            System.out.println("\nStatus cannot be changed because item is On Loan.");
        }
        
        System.out.println("Item updated successfully!");
    }
    
    public static void deleteItem() {
        MenuHelper.displayItems(catalogManager.readAllItems());
        
        LibraryItem item = InputHelper.getValidItem(catalogManager.readAllItems());
        if (item == null) return;
        
        MenuHelper.showDeleteItemConfirmation(item.getTitle());
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            catalogManager.deleteItem(item.getIsbn());
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    //  STAFF: USERS 
    public static void manageUsers() {
        while (true) {
            // Show PUBLIC section
            MenuHelper.displayUsers(userManager.readAllUsers(), true);
            // Show USER section  
            MenuHelper.displayUsers(userManager.readAllUsers(), false);
            
            MenuHelper.showUsersSubMenu();
            int choice = InputHelper.getIntInput();
            if (choice == -1) continue;
            
            switch (choice) {
                case 0: return;
                case 1: updateUser(); break;
                case 2: deleteUser(); break;
                default: System.out.println("Invalid choice!");
            }
            if (!InputHelper.askToContinue("Continue")) break;
        }
    }
    
    public static void updateUser() {
        MenuHelper.displayUsers(userManager.readAllUsers(), false);
        
        User user = InputHelper.getValidUser(userManager.readAllUsers());
        if (user == null) return;
        
        System.out.println("\nCURRENT USER INFO");
        System.out.println("========================================");
        System.out.println("User ID: " + user.getUserId());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + (user.getEmail() == null ? "none" : user.getEmail()));
        System.out.println("Phone: " + (user.getPhone() == null ? "none" : user.getPhone()));
        
        if (user instanceof Student) {
            System.out.println("Major: " + ((Student) user).getMajor());
        } else if (user instanceof Faculty) {
            System.out.println("Department: " + ((Faculty) user).getDepartment());
        } else if (user instanceof Staff) {
            System.out.println("Position: " + ((Staff) user).getPosition());
        }
        System.out.println("========================================");
        
        System.out.print("New Username (Enter to keep: " + user.getUsername() + "): ");
        String name = scanner.nextLine();
        if (name.isEmpty()) name = user.getUsername();
        
        System.out.print("New Email (Enter to keep: " + (user.getEmail() == null ? "none" : user.getEmail()) + "): ");
        String email = scanner.nextLine();
        if (email.isEmpty()) email = user.getEmail();
        
        System.out.print("New Phone (Enter to keep: " + (user.getPhone() == null ? "none" : user.getPhone()) + "): ");
        String phone = scanner.nextLine();
        if (phone.isEmpty()) phone = user.getPhone();
        
        System.out.print("New Password (Enter to keep): ");
        String pwd = scanner.nextLine();
        if (pwd.isEmpty()) pwd = user.getPassword();
        
        String major = null, dept = null, pos = null;
        
        if (user instanceof Student) {
            System.out.print("New Major (Enter to keep: " + ((Student) user).getMajor() + "): ");
            major = scanner.nextLine();
            if (major.isEmpty()) major = ((Student) user).getMajor();
        } else if (user instanceof Faculty) {
            System.out.print("New Department (Enter to keep: " + ((Faculty) user).getDepartment() + "): ");
            dept = scanner.nextLine();
            if (dept.isEmpty()) dept = ((Faculty) user).getDepartment();
        } else if (user instanceof Staff) {
            System.out.print("New Position (Enter to keep: " + ((Staff) user).getPosition() + "): ");
            pos = scanner.nextLine();
            if (pos.isEmpty()) pos = ((Staff) user).getPosition();
        }
        
        userManager.updateUser(user.getUserId(), name, email, phone, pwd, major, dept, pos);
    }
    
    public static void deleteUser() {
        MenuHelper.displayUsers(userManager.readAllUsers(), false);
        
        User user = InputHelper.getValidUser(userManager.readAllUsers());
        if (user == null) return;
        
        MenuHelper.showDeleteUserConfirmation(user.getUsername());
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            userManager.deleteUser(user.getUserId());
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    //  STAFF: BORROWS 
    
    public static void manageBorrows() {
        while (true) {
            MenuHelper.displayAllBorrowRecords(transactionManager.readAllBorrowRecords());
            MenuHelper.showBorrowsSubMenu();
            int choice = InputHelper.getIntInput();
            if (choice == -1) continue;
            
            switch (choice) {
                case 0: return;
                case 1: updateReturnRecord(); break;
                case 2: deleteBorrowRecord(); break;
                default: System.out.println("Invalid choice!");
            }
            if (!InputHelper.askToContinue("Continue")) break;
        }
    }
    
    public static void updateReturnRecord() {
        List<BorrowRecord> allRecords = transactionManager.readAllBorrowRecords();
        if (allRecords.isEmpty()) {
            System.out.println("No borrow records to update!");
            return;
        }
        
        List<BorrowRecord> returnedRecords = new ArrayList<>();
        for (BorrowRecord record : allRecords) {
            if (record.getStatus().equals("Returned")) {
                returnedRecords.add(record);
            }
        }
        
        if (returnedRecords.isEmpty()) {
            System.out.println("No returned records to mark as damaged/lost!");
            return;
        }
        
        MenuHelper.displayReturnedRecordsWithNumbers(returnedRecords);
        int selected = InputHelper.getIntInput();
        
        if (selected == -1 || selected < 1 || selected > returnedRecords.size()) {
            System.out.println("Invalid selection!");
            return;
        }
        
        BorrowRecord record = returnedRecords.get(selected - 1);
        
        MenuHelper.showMarkAsDamagedLostMenu();
        int condition = InputHelper.getIntInput();
        
        if (condition == -1) {
            System.out.println("Invalid choice!");
            return;
        }
        
        double damageFine = 0;
        String conditionMsg = "";
        String newStatus = "";
        
        switch (condition) {
            case 1:
                conditionMsg = "Damaged";
                damageFine = 25.00;
                newStatus = "Damaged";
                System.out.println("\nDamage fine: RM25.00");
                break;
            case 2:
                conditionMsg = "Lost";
                damageFine = 60.00;
                newStatus = "Lost";
                System.out.println("\nLost item fine: RM60.00");
                break;
            case 0:
                conditionMsg = "Available";
                damageFine = 0;
                newStatus = "Available";
                System.out.println("\nMarking as Available (No fine)");
                break;
            default:
                System.out.println("Invalid choice! Defaulting to Available.");
                conditionMsg = "Available";
                damageFine = 0;
                newStatus = "Available";
                break;
        }
        
        MenuHelper.showMarkConfirmation(conditionMsg);
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            if (damageFine > 0) {
                fineCalculator.createDamageFine(record.getUserId(), record.getRecordId(), damageFine);
            }
            catalogManager.updateItemStatus(record.getIsbn(), newStatus);
            System.out.println("Record marked as " + conditionMsg + " successfully!");
            if (damageFine > 0) {
                System.out.println("Fine added: RM" + String.format("%.2f", damageFine));
            }
        } else {
            System.out.println("Update cancelled.");
        }
    }
    
    public static void deleteBorrowRecord() {
        List<BorrowRecord> records = transactionManager.readAllBorrowRecords();
        if (records.isEmpty()) {
            System.out.println("No borrow records to delete!");
            return;
        }
        
        MenuHelper.displayReturnedRecordsWithNumbers(records);
        int selected = InputHelper.getIntInput();
        if (selected == -1 || selected < 1 || selected > records.size()) {
            System.out.println("Invalid selection!");
            return;
        }
        
        BorrowRecord record = records.get(selected - 1);
        
        MenuHelper.showDeleteConfirmation(record.getRecordId() + " - " + record.getItemTitle());
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            transactionManager.deleteBorrowRecord(record.getRecordId());
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    //  STAFF: FINES 
    
    public static void manageFines() {
        while (true) {
            MenuHelper.displayFines(fineCalculator.readAllFines());
            MenuHelper.showFinesSubMenu();
            int choice = InputHelper.getIntInput();
            if (choice == -1) continue;
            
            switch (choice) {
                case 0: return;
                case 1: updateFine(); break;
                case 2: deleteFine(); break;
                default: System.out.println("Invalid choice!");
            }
            if (!InputHelper.askToContinue("Continue")) break;
        }
    }
    
    public static void updateFine() {
        MenuHelper.displayFines(fineCalculator.readAllFines());
        
        Fine fine = InputHelper.getValidFine(fineCalculator.readAllFines());
        if (fine == null) return;
        
        MenuHelper.showCurrentFineInfo(fine);
        
        double newAmount = InputHelper.getValidAmountWithDefault(fine.getAmount());
        if (newAmount == -1) return;
        String newStatus = InputHelper.getValidPaidStatusWithDefault(fine.getStatus());
        if (newStatus == null) return;
        
        fineCalculator.updateFine(fine.getFineId(), newAmount, newStatus);
    }
    
    public static void deleteFine() {
        MenuHelper.displayFines(fineCalculator.readAllFines());
        
        Fine fine = InputHelper.getValidFine(fineCalculator.readAllFines());
        if (fine == null) return;
        
        MenuHelper.showDeleteFineConfirmation(fine.getFineId());
        String confirm = scanner.nextLine();
        if (confirm.equalsIgnoreCase("y")) {
            fineCalculator.deleteFine(fine.getFineId());
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    // USER: BOOKS 
    
    public static void viewBooks() {
        while (true) {
            MenuHelper.displayItems(catalogManager.readAllItems());
            MenuHelper.showBooksSubMenu();
            int choice = InputHelper.getIntInput();
            if (choice == -1) continue;
            
            switch (choice) {
                case 0: return;
                case 1: borrowItem(); break;
                default: System.out.println("Invalid choice!");
            }
            if (!InputHelper.askToContinue("Continue")) break;
        }
    }
    
    public static void borrowItem() {
        User user = userManager.readCurrentUser();
        if (!user.canBorrow()) {
            System.out.println("You cannot borrow!");
            return;
        }
        if (fineCalculator.hasUnpaidFines(user.getUserId())) {
            System.out.println("You have unpaid fines!");
            return;
        }
        
        MenuHelper.displayItems(catalogManager.readAllItems());
        
        LibraryItem item = InputHelper.getValidItem(catalogManager.readAllItems());
        if (item == null) return;
        transactionManager.createBorrowRecord(user, item.getIsbn());
    }
    
    // USER: MY BORROWS 
    
    public static void myBorrows() {
        while (true) {
            transactionManager.viewUserBorrows(userManager.readCurrentUser().getUserId());
            MenuHelper.showMyBorrowsSubMenu();
            int choice = InputHelper.getIntInput();
            if (choice == -1) continue;
            
            switch (choice) {
                case 0: return;
                case 1: returnItem(); break;
                default: System.out.println("Invalid choice!");
            }
            if (!InputHelper.askToContinue("Continue")) break;
        }
    }
    
    public static void returnItem() {
        List<BorrowRecord> userBorrows = transactionManager.readBorrowRecordsByUser(userManager.readCurrentUser().getUserId());
        if (userBorrows.isEmpty()) {
            System.out.println("No borrow records to return!");
            return;
        }
        
        List<BorrowRecord> activeBorrows = new ArrayList<>();
        for (BorrowRecord record : userBorrows) {
            if (record.getStatus().equals("Borrowed")) {
                activeBorrows.add(record);
            }
        }
        
        if (activeBorrows.isEmpty()) {
            System.out.println("No active borrow records to return!");
            return;
        }
        
        MenuHelper.displayUserBorrowsWithNumbers(activeBorrows);
        System.out.print("Enter number (1-" + activeBorrows.size() + "): ");
        int selected = InputHelper.getIntInput();
        
        if (selected == -1 || selected < 1 || selected > activeBorrows.size()) {
            System.out.println("Invalid selection!");
            return;
        }
        
        BorrowRecord record = activeBorrows.get(selected - 1);
        
        double damageFine = 0;
        transactionManager.createReturnRecord(record.getRecordId(), userManager.readCurrentUser(), damageFine);
        
        if (record.isOverdue()) {
            fineCalculator.createOverdueFine(record, userManager.readCurrentUser());
        }
    }
    
    //USER: MY FINES 
    
    public static void myFines() {
        while (true) {
            fineCalculator.viewUserFines(userManager.readCurrentUser().getUserId());
            MenuHelper.showMyFinesSubMenu();
            int choice = InputHelper.getIntInput();
            if (choice == -1) continue;
            
            switch (choice) {
                case 0: return;
                case 1: payFine(); break;
                default: System.out.println("Invalid choice!");
            }
            if (!InputHelper.askToContinue("Continue")) break;
        }
    }
    
    public static void payFine() {
        List<Fine> userFines = new ArrayList<>();
        for (Fine fine : fineCalculator.readAllFines()) {
            if (fine.getUserId().equals(userManager.readCurrentUser().getUserId()) && fine.getStatus().equals("Unpaid")) {
                userFines.add(fine);
            }
        }
        
        if (userFines.isEmpty()) {
            System.out.println("No unpaid fines to pay!");
            return;
        }
        
        for (int i = 0; i < userFines.size(); i++) {
            Fine fine = userFines.get(i);
            System.out.println((i+1) + ". " + fine.getFineId() + " - RM" + String.format("%.2f", fine.getAmount()));
        }
        MenuHelper.promptSelectFineToPay(userFines.size());
        int selected = InputHelper.getIntInput();
        
        if (selected == -1 || selected < 1 || selected > userFines.size()) {
            System.out.println("Invalid selection!");
            return;
        }
        
        Fine fine = userFines.get(selected - 1);
        fineCalculator.payFine(fine.getFineId());
    }
    
    //UTILITIES 
    
    public static void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdir();
    }
}