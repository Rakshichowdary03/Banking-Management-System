import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class BankService {
    public boolean login(String username, String password) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(
                 "SELECT 1 FROM app_users WHERE username=? AND password=?")) {
            p.setString(1, username); p.setString(2, password);
            try (ResultSet r = p.executeQuery()) { return r.next(); }
        }
    }

    public void createAccount(Customer x) throws SQLException {
        String sql = "INSERT INTO customers(acno,cname,email,accounttype,bal) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1,x.getAcno()); p.setString(2,x.getCname()); p.setString(3,x.getEmail());
            p.setString(4,x.getAccounttype()); p.setBigDecimal(5,x.getBal()); p.executeUpdate();
        }
    }

    public List<Customer> viewAccounts() throws SQLException {
        List<Customer> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT * FROM customers ORDER BY acno");
             ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(new Customer(r.getLong("acno"),r.getString("cname"),
                r.getString("email"),r.getString("accounttype"),r.getBigDecimal("bal")));
        }
        return list;
    }

    public Customer searchAccount(long acno) throws SQLException, InvalidAccount {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement("SELECT * FROM customers WHERE acno=?")) {
            p.setLong(1,acno);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) throw new InvalidAccount("Account not found.");
                return new Customer(r.getLong("acno"),r.getString("cname"),r.getString("email"),
                    r.getString("accounttype"),r.getBigDecimal("bal"));
            }
        }
    }

    public void deleteAccount(long acno) throws SQLException, InvalidAccount {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                searchAccountIn(c, acno);
                try (PreparedStatement p = c.prepareStatement("DELETE FROM transactions WHERE acno=?")) {
                    p.setLong(1,acno); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement("DELETE FROM customers WHERE acno=?")) {
                    p.setLong(1,acno); p.executeUpdate();
                }
                c.commit();
            } catch (SQLException | InvalidAccount e) { c.rollback(); throw e; }
            finally { c.setAutoCommit(true); }
        }
    }

    private void searchAccountIn(Connection c, long acno) throws SQLException, InvalidAccount {
        try (PreparedStatement p = c.prepareStatement("SELECT 1 FROM customers WHERE acno=?")) {
            p.setLong(1,acno);
            try (ResultSet r = p.executeQuery()) { if (!r.next()) throw new InvalidAccount("Account not found."); }
        }
    }

    public void transact(long acno, String type, BigDecimal amount)
        throws SQLException, InvalidAccount, InsufficientFunds, InvalidAmt, InvalidTransaction {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new InvalidAmt("Amount must be positive.");
        String t = type == null ? "" : type.toUpperCase(Locale.ROOT);
        if (!t.equals("W") && !t.equals("D")) throw new InvalidTransaction("Use W or D.");
        if (t.equals("W") && amount.compareTo(new BigDecimal("100")) < 0)
            throw new InvalidAmt("Withdrawal must be at least 100.");
        if (t.equals("D") && amount.compareTo(new BigDecimal("50000")) > 0)
            throw new InvalidAmt("Deposit cannot exceed 50000 at once.");

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                BigDecimal balance;
                try (PreparedStatement p = c.prepareStatement("SELECT bal FROM customers WHERE acno=? FOR UPDATE")) {
                    p.setLong(1,acno);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new InvalidAccount("Account not found.");
                        balance = r.getBigDecimal("bal");
                    }
                }
                BigDecimal next;
                if (t.equals("W")) {
                    if (amount.compareTo(balance) > 0) throw new InsufficientFunds("Insufficient balance.");
                    next = balance.subtract(amount);
                } else next = balance.add(amount);

                try (PreparedStatement p = c.prepareStatement("UPDATE customers SET bal=? WHERE acno=?")) {
                    p.setBigDecimal(1,next); p.setLong(2,acno); p.executeUpdate();
                }
                try (PreparedStatement p = c.prepareStatement(
                    "INSERT INTO transactions(transaction_type,acno,amount) VALUES(?,?,?)")) {
                    p.setString(1,t.equals("W") ? "WITHDRAW" : "DEPOSIT");
                    p.setLong(2,acno); p.setBigDecimal(3,amount); p.executeUpdate();
                }
                c.commit();
                System.out.println("Transaction successful. Balance: " + next);
            } catch (SQLException | InvalidAccount | InsufficientFunds | InvalidAmt | InvalidTransaction e) {
                c.rollback(); throw e;
            } finally { c.setAutoCommit(true); }
        }
    }

    public void printTransactions(long acno) throws SQLException, InvalidAccount {
        searchAccount(acno);
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(
                "SELECT tid,transaction_type,amount,transaction_date FROM transactions WHERE acno=? ORDER BY tid DESC")) {
            p.setLong(1,acno);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) System.out.println(r.getLong(1)+" | "+r.getString(2)+" | "+
                    r.getBigDecimal(3)+" | "+r.getTimestamp(4));
            }
        }
    }
}
