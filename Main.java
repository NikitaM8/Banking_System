package banking;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        //url to our DB
/*
        String url = "jdbc:sqlite:" +
                "/Users/ann/IdeaProjects/Simple Banking System/" +
                "Simple Banking System/task/src/banking/banking.db";


 */
        String url = "jdbc:sqlite:" + args[1];

        Database db = new Database(url);

        BankingSystem bankingSystem = new BankingSystem(db);
        int act;

        //while user not choose exit, do read inputs from user
        do {
            act = bankingSystem.pushCommand(scanner.nextLong());
        } while (act != 0);
    }
}
