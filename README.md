# 15745 Team Project
### Inserting Caches using Compiler Passes to Optimize RPCs between Microservices
This repository includes a compiler-based cache optimization pass to replace RPC calls in a Microservice Architecture.
Inserting Caches using Compiler Passes to Optimize RPCs between Microservices


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

# Environment Setup
The soot jar is already present in the repo so it is not required to install.
### What's is in the repo?
- ```CacheInsertionTransform.java```: Compiler transform to insert a cache to replace a target method call.
- ```Main.java```: Main class to run the pass
- ```utils```: Some Soot utilities written along the way
- ```test```: Java class to test the passs on
- ```demo, demo2```: Dummy Java microservices to apply and test this compiler pass on
- ```sootOutput```: Soot's output directory

## Running the pass
### Apply on test/Gateway.java
1. Compile all java classes -
```
javac -cp ./test test/Gateway.java test/Cache.java // compile test classes
javac -cp soot-4.3.0-jar-with-dependencies.jar:. Main.java CacheInsertionTransformer.java // compile the pass
```
2. Run the pass -
```
java  -cp soot-4.3.0-jar-with-dependencies.jar:. Main ./test Gateway,Cache  -class
```
3. See output under ```sootOutput/```
#### Arguments -
- ```./test```: Source directory used to set Soot classpath inside the pass
- ```Gateway,Cache```: List of classes to load, their .class file must be present in the source directory .
- ```-class```: Output type. Other supported type is ```-jimple``` to generate IR.

### Apply on dummy microservices
```
java  -cp soot-4.3.0-jar-with-dependencies.jar:. Main ./test Gateway,Cache  -class
```

### Apply on custom microservices/classes
Currently we do not have a generic way to detect RPC calls. If you want to apply this pass on one of your own classes, please replace the relevant target class names and method names defined in ```CacheInsertionTransformer.java```. 

# Useful Links
https://soot-oss.github.io/soot/docs/4.3.0/options/soot_options.html
https://github.com/noidsirius/SootTutorial
https://noidsirius.medium.com/a-beginners-guide-to-static-program-analysis-using-soot-5aee14a878d

# Documentation
https://www.sable.mcgill.ca/soot/tutorial/pldi03/tutorial.pdf
https://www.brics.dk/SootGuide/

# Train ticket 
https://github.com/Willendless/train-ticket
https://github.com/ovkulkarni/train-ticket


java  -cp soot-4.3.0-jar-with-dependencies.jar:. CacheInsertionPass /Users/bhakti/Documents/coursework/15745/train-ticket/ts-admin-basic-info-service/target/classes/adminbasic/controller AdminBasicInfoController getAllContacts