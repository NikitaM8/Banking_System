package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    SQLiteDataSource dataSource = new SQLiteDataSource();

    Database(String url) {
        dataSource.setUrl(url);

        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "id INTEGER PRIMARY KEY," +
                        "number TEXT NOT NULL," +
                        "pin TEXT NOT NULL," +
                        "balance INTEGER DEFAULT 0);");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertCard(long cardNum, int pin) {
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                int i = statement.executeUpdate("INSERT INTO card ('number', 'pin') VALUES (" +
                        cardNum + ", " + pin + ")");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCorrectAcc(long cardNum, int pin) {
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                try (ResultSet cards = statement.executeQuery("SELECT * FROM card")) {
                    while (cards.next()) {
                        // Retrieve column values
                        int id = cards.getInt("id");
                        String number = cards.getString("number");
                        String cPin = cards.getString("pin");

                        long numAsLong = Long.parseLong(number);
                        int pinAsInt = Integer.parseInt(cPin);

                        if (numAsLong == cardNum) {
                            if (pinAsInt == pin) {
                                return true;
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public int getBalance(long cardNum) {
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                try (ResultSet cards = statement.executeQuery("SELECT * FROM card WHERE number = " + cardNum)) {
                    // Retrieve balance
                    return cards.getInt("balance");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void addIncome(long cardNum, int income) {
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                int i = statement.executeUpdate("UPDATE card SET balance = balance + " +
                        income + " WHERE number = " + cardNum);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void chargeMoney(long cardNum, int money) {
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                int i = statement.executeUpdate("UPDATE card SET balance = balance - " +
                        money + " WHERE number = " + cardNum);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeAccount(long cardNum) {
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                int i = statement.executeUpdate("DELETE FROM card WHERE number =  " + cardNum);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isCardExistForTransfer(long cardNum){
        try (Connection con = dataSource.getConnection()) {
            // Statement creation
            try (Statement statement = con.createStatement()) {
                // Statement execution
                try (ResultSet cards = statement.executeQuery("SELECT * FROM card WHERE number = " + cardNum)) {
                    // Retrieve balance
                    return cards.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void doTransfer(long cardNum, long toCardNum, int sum) {
        this.chargeMoney(cardNum, sum);
        this.addIncome(toCardNum, sum);
    }
}
