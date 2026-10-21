import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BankingManagementSystem {
    private static final Scanner sc = new Scanner(System.in);
    private static final BankService bank = new BankService();

    public static void main(String[] args) {
        System.out.println("=== BANKING MANAGEMENT SYSTEM ===");
        try {
            System.out.print("Username: "); String u = sc.nextLine().trim();
            System.out.print("Password: "); String p = sc.nextLine();
            if (!bank.login(u,p)) throw new InvalidUser("Invalid username or password.");
            menu();
        } catch (InvalidUser e) {
            System.out.println("Login failed: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("Check MySQL, credentials, schema and Connector/J.");
        } finally { sc.close(); }
    }

    private static void menu() throws SQLException {
        while (true) {
            System.out.println("\n1.Create Account  2.View Accounts  3.Search Account");
            System.out.println("4.Deposit  5.Withdraw  6.Delete Account  7.Transactions  8.Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": create(); break;
                    case "2": for (Customer c : bank.viewAccounts()) System.out.println(c); break;
                    case "3": System.out.println(bank.searchAccount(readLong("Account number: "))); break;
                    case "4": transact("D"); break;
                    case "5": transact("W"); break;
                    case "6":
                        long id = readLong("Account number to delete: ");
                        System.out.print("Confirm deletion? (yes/no): ");
                        if (sc.nextLine().trim().equalsIgnoreCase("yes")) {
                            bank.deleteAccount(id); System.out.println("Account deleted.");
                        }
                        break;
                    case "7": bank.printTransactions(readLong("Account number: ")); break;
                    case "8": System.out.println("Goodbye."); return;
                    default: System.out.println("Invalid menu option.");
                }
            } catch (InvalidAccount | InsufficientFunds | InvalidAmt | InvalidTransaction e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private static void create() throws SQLException {
        long id = readLong("Account number: ");
        System.out.print("Customer name: "); String name = sc.nextLine().trim();
        System.out.print("Email: "); String email = sc.nextLine().trim();
        System.out.print("Account type (Savings/Current): "); String type = sc.nextLine().trim();
        BigDecimal bal = readAmount("Opening balance: ");
        if (name.isEmpty() || type.isEmpty() || bal.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Name/type required; balance cannot be negative."); return;
        }
        bank.createAccount(new Customer(id,name,email,type,bal));
        System.out.println("Account created.");
    }

    private static void transact(String type)
        throws SQLException, InvalidAccount, InsufficientFunds, InvalidAmt, InvalidTransaction {
        bank.transact(readLong("Account number: "),type,readAmount("Amount: "));
    }
    private static long readLong(String prompt) {
        System.out.print(prompt); return Long.parseLong(sc.nextLine().trim());
    }
    private static BigDecimal readAmount(String prompt) {
        System.out.print(prompt); return new BigDecimal(sc.nextLine().trim());
    }
}
