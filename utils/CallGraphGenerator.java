import java.io.File;
import java.util.*;

import fj.data.Array;
import soot.*;
import soot.G;
import soot.Scene;
import soot.options.Options;
import soot.jimple.JimpleBody;
import soot.jimple.toolkits.callgraph.*;

public class CallGraphGenerator {
    public static void setupSoot(String sourceDirectory, String[] className, String methodName) {
        // for some cleanup
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDirectory);
        Options.v().set_whole_program(true);
        for (String cls : className) {
            SootClass sc = Scene.v().loadClassAndSupport(cls);
            sc.setApplicationClass();
        }
        Scene.v().loadNecessaryClasses();
}

    public static void main(String[] args) {
        String sourceDirectory = args[0];
        String[] className = args[1].split(",");
        String methodName = args[2];
        setupSoot(sourceDirectory, className, methodName);

        SootClass mainClass = Scene.v().getSootClass(className[0]);
        SootMethod sm = mainClass.getMethodByName(methodName);

        // set all methods as possible entry points, soot only considers "main" by default
        ArrayList<SootMethod> entryPoints = new ArrayList<SootMethod>();
        for (SootClass currentClass : Scene.v().getClasses()) {
            for (SootMethod method : currentClass.getMethods()) {
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