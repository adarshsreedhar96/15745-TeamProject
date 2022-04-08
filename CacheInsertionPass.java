
//package dev.navids.soottutorial.helloworldanalysis;
import java.io.File;

import soot.*;
import soot.G;
import soot.Scene;
import soot.options.Options;
import soot.jimple.JimpleBody;

public class CacheInsertionPass {
    public static void setupSoot(String sourceDirectory, String className, String methodName) {
        // for some cleanup
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDirectory);
        SootClass sc = Scene.v().loadClassAndSupport(className);
        sc.setApplicationClass();
        Scene.v().loadNecessaryClasses();
    }

    public static void main(String[] args) {
        String sourceDirectory = args[0];
        String className = args[1];
        String methodName = args[2];
        setupSoot(sourceDirectory, className, methodName);

        // Retrieve HelloWorld's body
        SootClass mainClass = Scene.v().getSootClass(className);
        SootMethod sm = mainClass.getMethodByName(methodName);
        JimpleBody body = (JimpleBody) sm.retrieveActiveBody();
        System.out.println(body.toString());
    }

}
