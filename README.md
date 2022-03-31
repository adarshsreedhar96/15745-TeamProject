# 15745-TeamProject
This repository includes a compiler-based cache optimization to replace RPC calls in a Microservice Architecture

# Instructions to Install Soot
wget https://repo1.maven.org/maven2/org/soot-oss/soot/4.3.0/soot-4.3.0-jar-with-dependencies.jar
- Run the following command
java -cp ../soot-4.3.0-jar-with-dependencies.jar soot.Main
- For more instructions on how to use the command line options, follow this link:
https://github.com/soot-oss/soot/wiki/Introduction:-Soot-as-a-command-line-tool
https://github.com/soot-oss/soot/wiki/Running-Soot
- TL;DR : Command to run
java -cp ../soot-4.3.0-jar-with-dependencies.jar soot.Main -cp . -pp HelloWorld

- Options (Add as arguments to the command line):
    - To generate Jimple IR: -f J
    - To generate Shimple IR: -f shimple

- Steps to write a pass in soot-tutorial:
1. Create a folder in demo, say "X"
2. Inside it, have the actual java code of which you want to extract    information (Make sure to have the .class file as well)
3. In src/main/java/dev/navids/soottutorial, create another folder, say x_analysis
4. Create a java file inside it, say CheckXAnalysis. We write the pass code here 

5. In the candidate CheckXAnalysis code:
    a. create a package called package dev.navids.soottutorial.x_analysis; (basically the folder that you create in src/main/java/dev/navids/soottutorial)
    b. Make note of the class name, (here its called CheckXAnalysis), and have a main method
    c. In Main.java (src/main/java/dev/navids/soottutorial/Main.java) import the class CheckXAnalysis, and add an if block matching the ClassName, and if so
    d. load the CheckXAnalysis.main method
Now issue ./gradlew run --args="CheckXAnalysis"



# Useful Links
https://soot-oss.github.io/soot/docs/4.3.0/options/soot_options.html
https://github.com/noidsirius/SootTutorial
https://noidsirius.medium.com/a-beginners-guide-to-static-program-analysis-using-soot-5aee14a878d

# Documentation
https://www.sable.mcgill.ca/soot/tutorial/pldi03/tutorial.pdf
https://www.brics.dk/SootGuide/