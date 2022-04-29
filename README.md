# 15745 Team Project
### Inserting Caches using Compiler Passes to Optimize RPCs between Microservices
This repository includes a compiler-based cache optimization pass to replace RPC calls in a Microservice Architecture.
Inserting Caches using Compiler Passes to Optimize RPCs between Microservices.

We have included three types of Caching Logic present, and either of them can be used by Soot to insert the Cache for the
RPC calls, Least Recently Used, Least Frequently Used and Expiry-based Eviction Logic. Source is available in /test folder.
Currently, their sizes are fixed at 100, change this as required. If a new cache/eviction policy is to be added, please add
them in the /test folder.


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
javac -cp ./test test/Gateway.java test/Cache_LRU.java // compile test classes
javac -cp soot-4.3.0-jar-with-dependencies.jar:. Main.java CacheInsertionTransformer.java // compile the pass
```
Note that there are two places in the CacheInsertionTransformer source that also require the name of the Cache class, so
if you want to add a new cache/eviction policy, please replace with the name of the class where getSootClass() method is called.

2. Run the pass -
```
java  -cp soot-4.3.0-jar-with-dependencies.jar:. Main ./test Gateway,Cache_LRU,CacheEntry_LRU  -class
```
Note the use of two classes here, Cache_LRU and CacheEntry_LRU; These are both used for the Cache functionality (please see source code),
and so if you are using multiple classes for a new caching implementation, all those classes need to be passed as arguments here.

3. See output under ```sootOutput/```
#### Arguments -
- ```./test```: Source directory used to set Soot classpath inside the pass
- ```Gateway,Cache_LRU,CacheEntry_LRU```: List of classes to load, their .class file must be present in the source directory .
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
