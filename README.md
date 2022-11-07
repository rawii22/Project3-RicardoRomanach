# Project3-RicardoRomanach
This program uses Java to interact with a MySQL database. This is for CSC-3300 Fall 2022 with Beata Kubiak.

## Introduction

This program is our third major project for CSC-3300 at Tennessee Technological University during Fall 2022 with Professor Beata Kubiak. It is meant to teach us how to communicate with a MySQL database with Java.

## Tools

1. **IntelliJ** - For Java development and some database navigation
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

## Compilation

1. Navigate to the src folder in ~\Project3-RicardoRomanach\Project3\src
    > If no "bin" folder exists in the src folder, then create a folder called "bin" inside the src folder before continuing. This can also be done by running the makeBinFolder.bat file.  
2. To build the program, either run the build.bat file **OR** execute the following commands in the src directory
    1. **javac -d bin com\rjromanach42\Main.java com\rjromanach42\LibraryDB.java**
    2. **jar cvfm Project3.jar manifest.mf -C bin .**
3. To run the program, either run the run.bat file **OR** execute the following command in the same src directory
    1. **java -jar Project3.jar**


## Limitations

Unfortunately, since this program had to be graded on a separate machine, and therefore a separate database instance, we were not able to use functions or stored procedures since the grader would have been missing those functions and procedures in their database when they tried to run the program.