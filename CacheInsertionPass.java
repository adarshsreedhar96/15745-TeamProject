
//package dev.navids.soottutorial.helloworldanalysis;
import java.io.File;
import java.util.*;

import fj.data.Array;
import soot.*;
import soot.G;
import soot.Scene;
import soot.options.Options;
import soot.jimple.JimpleBody;
import soot.jimple.toolkits.callgraph.*;

public class CacheInsertionPass {
    public static void setupSoot(String sourceDirectory, String[] className, String methodName) {
        // for some cleanup
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDirectory);
        // for (String sd : sourceDirectory) {
        // System.out.println(sd);
        // // Scene.v().extendSootClassPath(sd);
        // Options.v().set_soot_classpath(sd);
        // }
        Options.v().set_whole_program(true);
        // ArrayList<String> jars = new ArrayList<String>();
        // jars.add(sourceDirectory);
        // Options.v().set_process_jar_dir(jars);
        // Scene.v().extendSootClassPath(sourceDirectory);
        for (String cls : className) {
            // System.out.println(cls);
            SootClass sc = Scene.v().loadClassAndSupport(cls);
            sc.setApplicationClass();
        }
        Scene.v().loadNecessaryClasses();

        // System.out.println(Scene.v().getClasses());
    }

    public static void main(String[] args) {
        String sourceDirectory = args[0];
        String[] className = args[1].split(",");
        String methodName = args[2];
        setupSoot(sourceDirectory, className, methodName);

        // Retrieve HelloWorld's body
        SootClass mainClass = Scene.v().getSootClass(className[0]);
        // mainClass.setApplicationClass();
        SootMethod sm = mainClass.getMethodByName("getAllContacts");

        ArrayList<SootMethod> entryPoints = new ArrayList<SootMethod>();
        for (SootClass currentClass : Scene.v().getClasses()) {
            for (SootMethod method : currentClass.getMethods()) {
                // System.out.println(method.getName());
                entryPoints.add(method);
            }
        }
        Scene.v().setEntryPoints(entryPoints);
        CHATransformer.v().transform();
        CallGraph cg = Scene.v().getCallGraph();
        Iterator<Edge> it = cg.edgesInto(sm);
        System.out.println("Number of edges is " + cg.size());
        while (it.hasNext()) {
            Edge e = it.next();
            System.out.println(e.toString());

        }

    }

}
