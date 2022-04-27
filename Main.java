
import soot.*;
import soot.JastAddJ.Opt;
import soot.options.Options;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        String sourceDirectory = args[0];
        String outputFmt = args[2];
        configureSoot(sourceDirectory, args[1].split(","), outputFmt);// configure soot
        Scene.v().loadNecessaryClasses(); // load all the library and dependencies for given program
        CacheInsertionTransformer transform = new CacheInsertionTransformer();
        Transform cacheTransform = new Transform("wjtp.cacheTransform", transform);
        PackManager.v().getPack("wjtp").add(cacheTransform);// add in Whole-program
        PackManager.v().runPacks(); // process and build call graph
        PackManager.v().writeOutput();
    }

    public static void configureSoot(String classpath, String[] className, String outputFmt) {
        G.reset();
        Options.v().set_whole_program(true); // process whole program
        Options.v().set_allow_phantom_refs(true); // load phantom references
        Options.v().set_prepend_classpath(true); // prepend class path
        Options.v().set_src_prec(Options.src_prec_class); // process only .class
        if(outputFmt == "-jimple"){
            Options.v().set_output_format(Options.output_format_jimple); // to output in jimple format
        } else{
            Options.v().set_output_format(Options.output_format_class);
        }
        Options.v().set_soot_classpath(classpath);
        for (String cls : className) {
            SootClass sc = Scene.v().loadClassAndSupport(cls);
            sc.setApplicationClass();
        }
    }
}
