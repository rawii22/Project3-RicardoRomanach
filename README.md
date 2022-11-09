# Project3-RicardoRomanach
This program uses Java to interact with a MySQL database. This is for CSC-3300 Fall 2022 with Beata Kubiak.

GitHub: https://github.com/rawii22/Project3-RicardoRomanach

Author: Ricardo Romanach

## Introduction

This program is our third major project for CSC-3300 at Tennessee Technological University during Fall 2022 with Professor Beata Kubiak. It is meant to teach us how to communicate with a MySQL database with Java.

## Tools

1. **IntelliJ IDEA** - For Java development and some database navigation
2. **MySQL Server\*** - Server used for program to communicate with - try this: https://dev.mysql.com/downloads/mysql/
3. **MySQL Workbench** - For database navigation and testing SQL snippets
4. **GitKraken** - For git repository management and visualization
5. **Visual Studio Code** - For writing the README file and other basic tasks

> **\*** This program requires MySQL to already be installed and running on your computer.

## Included Files

I want to make sure that this repository comes with everything it needs. I want it to be easy to clone and run this program, so I have included the following files:

1. **3300-F2022-project3-readme.pdf** - Instructions for this project.
2. **GuideForConnectingMySQLDatabaseToIntelliJ(Update-V2).pdf** - This is a guide that I personally wrote. It was intended to help my class set up a MySQL connection in IntelliJ and show them how to start talking to the database in Java. (I did this since I REALLY did not want to use Eclipse.) This file also includes a link to install IntelliJ and to the MySQL connector jar file web page.
3. **mysql-connector-java-8.0.30.jar** - This allows Java to communicate with the MySQL database. This is also included so other users don't have to install the library manually.
4. **library_schema_MySQL.sql** - Provided by Professor. This file establishes the table structure of the "library" database.
5. **libraryInsertFile_MySQL.sql** - Provided by Professor. This file adds some testing data into the "library" database.

## Compiling and Running

1. Navigate to the src folder in "~\Project3-RicardoRomanach\Project3\src". If no "bin" folder exists in the src folder, then either run the **makeBinFolder.bat** file **OR** create a folder called "bin" inside the src folder before continuing.
2. To build the program, either run the **build.bat** file **OR** execute the following commands in the src directory
    1. `javac -d bin com\rjromanach42\Main.java com\rjromanach42\LibraryDB.java`
    2. `jar cvfm Project3.jar manifest.mf -C bin .`
3. To run the program, either run the **run.bat** file **OR** execute the following command in the same src directory
    1. `java -jar Project3.jar`

> **Note:** Make sure to include the dot at the end of step 2 command 2 (the jar command)

> **Note:** Files of type .bat can only be used on Windows. For Linux or Mac, you will have to execute the commands manually since these scripts have not been written for those operating systems yet.

## Features

**This program should never end because of an error.** Since the contents of the primary while loop that controls the main menu is within a classic Java try/catch statement, any error should simply redirect the user back to the menu.

Every time the program asks for user input, **the user should be able to type "exit" to return to the main menu.** The only time this does not apply is in the main menu itself, where instead of typing "exit" you should enter in the appropriate option number to "Quit" the program.

When a user wants to search for the authors associated with a book (option 2), they have the choice to search for a book (or books) by title or ISBN. **They are not required to type in the full title or ISBN of a book perfectly** since the program will retrieve books with the closest match. (This is done through the SQL using the % signs around the desired title/ISBN). This is for ease of use.

Whenever the program prints a list of book COPIES, **if there is more than one copy of the same book in the list, the full book data will not be reprinted for each copy.** It will only add the copy ID of each similar book onto the "Copy ID" row.


## Limitations

Unfortunately, since this program had to be graded on a separate machine, and therefore a separate database instance, **we were not able to use sql functions or stored procedures since the grader would have been missing those functions and procedures in their database if they tried to run the program.**

With this program a user is **not allowed to renew a book more than twice.**

The total amount of money owed to the library by a certain member includes books not yet returned. However, **a late fee payment cannot be made on a book that has not been returned.** This is by design. A member can only register a payment on a book that has been returned. The menu will limit your options accordingly.

There are a few functions in this program that are **expecting the queried book data to be sorted by title.** So, if you add a new function that retrieves books, it would be a good idea to order the data by "title".

Indentations look best when this program is run on Command Prompt. Spacing might look slightly off when run inside of IntelliJ.

## Note to the grader

The instructions say that "brown" should be a user who can access the database, however that is something that must be set up on the computer's local database instance. Even if we set up a user called "brown" on our databases, your test database will not have that user. Also, we cannot create a user called "brown" on any program user's database since that would first require the program to be logged in.

The only solution to this problem was to just allow a mechanism through which the program user can sign into whichever database user they desired. So, make sure that you create a user called brown that you can sign into (with sufficient permissions if applicable)