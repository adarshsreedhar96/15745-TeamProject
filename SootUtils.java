import soot.*;
import soot.G;
import soot.Scene;
import soot.options.Options;
import soot.jimple.*;
import soot.jimple.internal.*;

public class SootUtils {

    static String sourceDirectory;
    static String[] className;
    static String methodName;

    public static void setupSoot() {
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

    public static void printMethodBody() {
        SootClass mainClass = Scene.v().getSootClass(className[0]);
        SootMethod sm = mainClass.getMethodByName(methodName);
        JimpleBody body = (JimpleBody) sm.retrieveActiveBody();
        System.out.println(body);
    }

    public static void main(String[] args) {
        sourceDirectory = args[0];
        className = args[1].split(",");
        methodName = args[2];
        setupSoot();
        printMethodBody();
    }
}
