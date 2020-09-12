package banking;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BankingSystem {
    private BankStatus bankStatus;
    private long[][] accounts = new long[1000][2];
    private int numOfAccounts = 0;
    private int balance = 0;
    private long curCardNumber;
    private long cardNumForTransfer;
    private Database db;

    BankingSystem(Database db) {
        notLoggedMessage();
        bankStatus = BankStatus.NOT_LOGGED;
        this.db = db;
    }

    /**
     * allow to chose an action depends on current Bank Status
     *
     * @param command - command input from user
     * @return - 0 if user input is 0 and want to exit
     */
    public int pushCommand(long command) {
        //if user choose exit, return 0
        if (command == 0) {
            return 0;
        }

        switch (bankStatus) {
            case START:
                System.out.println();
                notLoggedMessage();
                bankStatus = BankStatus.NOT_LOGGED;
                break;
            case NOT_LOGGED:
                chooseActionNotLogged((int) command);
                break;
            case LOGGED:
                chooseActionLogged((int) command);
                break;
            case ADD_INCOME:
                addIncome((int) command);
                System.out.println("Income was added!");
                bankStatus = BankStatus.LOGGED;

                System.out.println("");
                loggedMessage();
                break;
            case TRANSFER_CARD:
                if (command == this.curCardNumber) {
                    System.out.println("You can't transfer money to the same account!");
                    bankStatus = BankStatus.LOGGED;
                    System.out.println("");
                    loggedMessage();
                    break;
                }

                if (isLuhn(command)) {
                    if (isCardExistForTransfer(command)) {
                        bankStatus = BankStatus.TRANSFER_MONEY;
                        this.cardNumForTransfer = command;
                        System.out.println("Enter how much money you want to transfer:");
                    } else {
                        System.out.println("Such a card does not exist.");
                        bankStatus = BankStatus.LOGGED;
                        System.out.println("");
                        loggedMessage();
                    }
                } else {
                    System.out.println("Probably you made mistake in the card number. Please try again!");
                    bankStatus = BankStatus.LOGGED;
                    System.out.println("");
                    loggedMessage();
                }
                break;
            case TRANSFER_MONEY:
                if (command <= getBalance()) {
                    doTransfer((int) command);
                    System.out.println("Success!");
                    bankStatus = BankStatus.LOGGED;
                    System.out.println("");
                    loggedMessage();
                } else {
                    System.out.println("Not enough money!");
                    bankStatus = BankStatus.LOGGED;
                    System.out.println("");
                    loggedMessage();
                }
                break;
            default:
                System.out.println("Unknown status");
                break;
        }

        return 1;
    }

    private void notLoggedMessage() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
    }

    private void loggedMessage() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
    }

    private int getBalance() {
        return this.db.getBalance(this.curCardNumber);
    }

    private void addIncome(int income) {
        this.db.addIncome(this.curCardNumber, income);
    }

    private void closeAccount() {
        this.db.closeAccount(this.curCardNumber);
    }

    private void doTransfer(int sum) {
        this.db.doTransfer(this.curCardNumber, this.cardNumForTransfer, sum);
    }

    /**
     * Check card number for correctness in terms of Luhn algorithm
     * @param cardNum
     * @return true - match Luhn, false - not match Luhn
     */
    private boolean isLuhn(long cardNum) {
        int incomeCheckSum = (int) (cardNum % 10);

        cardNum /= 10;
        long[] cardNumDigits = new long[15];

        //represent generated number in array by rank
        for (int i = 14; i >= 0; i--) {
            cardNumDigits[i] = cardNum % 10;
            cardNum /= 10;
        }

        int sumOfDigits = 0;

        //use Luhn algorithm
        for (int i = 0; i < 15; i++) {
            if ((i + 1) % 2 != 0) {
                cardNumDigits[i] *= 2;

                if (cardNumDigits[i] > 9) {
                    cardNumDigits[i] -= 9;
                }
            }

            sumOfDigits += cardNumDigits[i];
        }
        //count checksum
        int checkSum = 0;

        if (sumOfDigits % 10 != 0) {
            checkSum = 10 - sumOfDigits % 10;
        }

        return incomeCheckSum == checkSum;
    }

    private boolean isCardExistForTransfer(long cardNum) {
        return this.db.isCardExistForTransfer(cardNum);
    }

    /**
     * Choose action if user is not logged
     *
     * @param command - command input from user
     */
    private void chooseActionNotLogged(int command) {
        switch (command) {
            case 1:
                createAccount();
                bankStatus = BankStatus.START;
                pushCommand(1);
                break;
            case 2:
                if (tryToLogIn()) {
                    System.out.println();
                    System.out.println("You have successfully logged in!");
                    bankStatus = BankStatus.LOGGED;

                    System.out.println();
                    loggedMessage();
                    break;
                }

                System.out.println();
                System.out.println("Wrong card number or PIN!");

                bankStatus = BankStatus.START;
                pushCommand(1);
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    /**
     * Choose action if user is logged
     *
     * @param command - command input from user
     */
    private void chooseActionLogged(int command) {
        switch (command) {
            case 1:
                System.out.println("");
                System.out.println("Balance: " + getBalance());
                System.out.println("");
                loggedMessage();
                break;
            case 2:
                System.out.println("");
                System.out.println("Enter income:");
                bankStatus = BankStatus.ADD_INCOME;
                break;
            case 3:
                System.out.println("");
                System.out.println("Transfer:");
                System.out.println("Enter card number:");
                bankStatus = BankStatus.TRANSFER_CARD;
                break;
            case 4:
                closeAccount();
                System.out.println("");
                System.out.println("The account has been closed!");
                bankStatus = BankStatus.START;
                pushCommand(1);
                break;
            case 5:
                bankStatus = BankStatus.START;
                pushCommand(1);
                break;
            default:
                System.out.println("Unknown command");
        }
    }

    private boolean tryToLogIn() {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("Enter your card number:");
        long cardNumber = scanner.nextLong();

        System.out.println("Enter your PIN:");
        int pinCode = scanner.nextInt();

        if (this.db.isCorrectAcc(cardNumber, pinCode)) {
            this.curCardNumber = cardNumber;
            return true;
        }

        return false;
    }

    private void createAccount() {
        Random random = new Random(); //init random object

        long cardNumber = createCardNumberWithLuhn();
        int pinCode = random.nextInt(10000);

        //put card number and pin to array
        //accounts[numOfAccounts][0] = cardNumber;
        //accounts[numOfAccounts][1] = pinCode;

        //put card number and pin code in database
        this.db.insertCard(cardNumber, pinCode);

        numOfAccounts++;

        //message for user about succeed
        System.out.println();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(cardNumber);
        System.out.println("Your card PIN:");

        String pin; //string for pin code representation in 0000-9999

        if (pinCode == 0) {
            pin = "0000";
        } else {
            if (pinCode > 0 && pinCode < 10) {
                pin = "000" + pinCode;
            } else {
                if (pinCode >= 10 && pinCode < 100) {
                    pin = "00" + pinCode;
                } else {
                    if (pinCode >= 100 && pinCode < 1000) {
                        pin = "0" + pinCode;
                    } else {
                        pin = "" + pinCode;
                    }
                }
            }
        }

        System.out.println(pin);
    }

    private long createCardNumberWithLuhn() {
        Random random = new Random(); //init random object

        long accIdentifier = random.nextInt(1000000000);
        long iNN = 4000000000000000L;
        long cardNumNoComplete = iNN + accIdentifier * 10;
        long tempCardNumber = cardNumNoComplete;

        cardNumNoComplete /= 10;
        long[] cardNumDigits = new long[15];

        //represent generated number in array by rank
        for (int i = 14; i >= 0; i--) {
            cardNumDigits[i] = cardNumNoComplete % 10;
            cardNumNoComplete /= 10;
        }

        int sumOfDigits = 0;

        //use Luhn algorithm
        for (int i = 0; i < 15; i++) {
            if ((i + 1) % 2 != 0) {
                cardNumDigits[i] *= 2;

                if (cardNumDigits[i] > 9) {
                    cardNumDigits[i] -= 9;
                }
            }

            sumOfDigits += cardNumDigits[i];
        }
        //count checksum
        int checkSum = 0;

        if (sumOfDigits % 10 != 0) {
            checkSum = 10 - sumOfDigits % 10;
        }

        return tempCardNumber + checkSum;
    }


}

enum BankStatus {
    START,
    NOT_LOGGED,
    LOGGED,
    ADD_INCOME,
    TRANSFER_CARD,
    TRANSFER_MONEY
}
