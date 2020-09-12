# Banking_System

With this simple banking system you can create accounts, log in into your accounts, check balance, add income,
do transfers between accounts, log out and delete accounts.

Account consist of - card number, pin code and balance. Card number created according to Luhn algorithm. 
Also this algorithm apply when you do transfer.

This program use database with table 'cards', so persistence is here.

To run program correctly, you need to clarify database file, use arguments -filename database.db where database 
is any .db file.  