To build the program, either run the build.bat file OR execute the following commands in the src directory
	javac -d bin com\rjromanach42\Main.java com\rjromanach42\LibraryDB.java
	jar cvfm Project3.jar manifest.mf -C bin .

To run the program, either run the run.bat file OR execute the following command in the src directory
	java -jar Project3.jar