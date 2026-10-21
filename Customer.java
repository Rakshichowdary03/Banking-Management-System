import java.math.BigDecimal;

public class Customer {
    private long acno;
    private String cname, email, accounttype;
    private BigDecimal bal;

    public Customer(long acno, String cname, String email, String accounttype, BigDecimal bal) {
        this.acno = acno; this.cname = cname; this.email = email;
        this.accounttype = accounttype; this.bal = bal;
    }
    public long getAcno() { return acno; }
    public void setAcno(long acno) { this.acno = acno; }
    public String getCname() { return cname; }
    public void setCname(String cname) { this.cname = cname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAccounttype() { return accounttype; }
    public void setAccounttype(String accounttype) { this.accounttype = accounttype; }
    public BigDecimal getBal() { return bal; }
    public void setBal(BigDecimal bal) { this.bal = bal; }

    public String toString() {
        return "Account: " + acno + " | Name: " + cname + " | Email: " + email
            + " | Type: " + accounttype + " | Balance: " + bal;
    }
}
