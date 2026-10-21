# Banking Management System
Console-based Java project using Core Java, OOP, Collections, custom exceptions, JDBC and MySQL.

## Features
- Login validation
- Create, view, search and delete accounts
- Deposit and withdraw
- Transaction history
- JDBC CRUD operations and commit/rollback
- Custom exceptions: InvalidAccount, InsufficientFunds, InvalidAmt

## Setup
1. Run `database.sql` in MySQL.
2. Add MySQL Connector/J JAR to the project.
3. Set `DB_URL`, `DB_USER`, and `DB_PASSWORD` environment variables if needed.
4. Compile and run:

Windows:
```
javac -cp ".;mysql-connector-j.jar" *.java
java -cp ".;mysql-connector-j.jar" BankingManagementSystem
```
macOS/Linux: replace `;` with `:`.

Demo login: `admin` / `admin123` (created by database.sql).
Educational demo only; plain-text passwords are not suitable for real banking software.
