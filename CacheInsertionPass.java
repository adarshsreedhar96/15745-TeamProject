
import java.util.*;

import soot.*;
import soot.G;
import soot.Scene;
import soot.options.Options;
import soot.jimple.*;
import soot.jimple.internal.*;

public class CacheInsertionPass {
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

    public static void addCacheField(SootClass cls) {

        SootClass cacheClass = Scene.v().getSootClass("Cache");
        Local cacheLocal = Jimple.v().newLocal("cacheLocal", cacheClass.getType());
        Value invokeCacheInit = Jimple.v().newSpecialInvokeExpr(cacheLocal,
                cacheClass.getMethodByName("<init>").makeRef());
        Unit assg1 = Jimple.v().newAssignStmt(cacheLocal, invokeCacheInit);
        Value staticCache = Jimple.v().newStaticFieldRef(cls.getFieldByName("cache").makeRef());
        Unit assg2 = Jimple.v().newAssignStmt(staticCache, cacheLocal);
        SootMethod init = cls.getMethodByName("<clinit>");
        System.out.println(init.retrieveActiveBody());
        // System.out.println(cls.getMethodByName("<init>").retrieveActiveBody());
        JimpleBody body = (JimpleBody) init.retrieveActiveBody();
        Iterator it = body.getUnits().iterator();
        Unit u = null;
        while (it.hasNext()) {
            u = (Unit) it.next();
            if (u instanceof AssignStmt) {
                AssignStmt as = (AssignStmt) u;
                if (as.getLeftOp() instanceof StaticFieldRef) {
                    System.out.println("static field - " + as.getLeftOp());
                }
            }
        }
        body.getLocals().add(cacheLocal);
        body.getUnits().addFirst(assg2);
        body.getUnits().addFirst(assg1);
        System.out.println(init.retrieveActiveBody());
        body.validate();
    }

    public static void main(String[] args) {
        String sourceDirectory = args[0];
        String[] className = args[1].split(",");
        String methodName = args[2];
        setupSoot(sourceDirectory, className, methodName);

        SootClass mainClass = Scene.v().getSootClass(className[0]);
        SootClass cacheClass = Scene.v().getSootClass("Cache");
        SootField f = Scene.v().makeSootField("cache", cacheClass.getType(), Modifier.STATIC);
        mainClass.addField(f);
        mainClass.setApplicationClass();

        addCacheField(mainClass);
        SootMethod sm = mainClass.getMethodByName("getAllContacts");
        JimpleBody body = (JimpleBody) sm.retrieveActiveBody();
        Iterator it = body.getUnits().iterator();
        Value v = null;
        Unit toreplace = null;
        Unit wrapRes = null;
        Value staticCache = Jimple.v().newStaticFieldRef(mainClass.getFieldByName("cache").makeRef());
        while (it.hasNext()) {
            Unit ub = (Unit) it.next();
            // identify call site
            if (ub.toString().contains("getAllContacts(org.springframework.http.HttpHeaders)")) {
                toreplace = ub;
                JAssignStmt as = (JAssignStmt) ub;
                v = as.getLeftOp();
            }
            // identify replacement point
            else if (ub.toString().contains(
                    "staticinvoke <org.springframework.http.ResponseEntity: org.springframework.http.ResponseEntity ok(java.lang.Object)>")) {
                wrapRes = ub;
            }
        }
        JAssignStmt as = (JAssignStmt) toreplace;
        SootMethod cacheGet = cacheClass.getMethodByName("get");
        SootMethod cachePut = cacheClass.getMethodByName("put");
        Local cacheLocal = Jimple.v().newLocal("cacheLocal", cacheClass.getType());
        body.getLocals().add(cacheLocal);
        Value newub = Jimple.v().newVirtualInvokeExpr(cacheLocal, cacheGet.makeRef(),
                as.getInvokeExpr().getArgs());
        AssignStmt assg = Jimple.v().newAssignStmt(v, newub);
        body = (JimpleBody) sm.retrieveActiveBody();
        body.getUnits().insertBefore(assg, toreplace);
        NeExpr qualExpr = Jimple.v().newNeExpr(NullConstant.v(), v);
        EqExpr qualExpr2 = Jimple.v().newEqExpr(NullConstant.v(), v);
        Unit ifstatement = Jimple.v().newIfStmt(qualExpr, wrapRes);
        Unit ifstatement2 = Jimple.v().newIfStmt(qualExpr2, toreplace);
        List<Value> putArgs = as.getInvokeExpr().getArgs();
        putArgs.add(v);
        InvokeExpr putStmt = Jimple.v().newVirtualInvokeExpr(cacheLocal, cachePut.makeRef(),
                putArgs);

        body.getUnits().insertAfter(ifstatement, assg);
        body.getUnits().insertAfter(ifstatement2, ifstatement);
        body.getUnits().insertAfter(soot.jimple.Jimple.v().newInvokeStmt(putStmt), toreplace);
        body = (JimpleBody) sm.retrieveActiveBody();
        System.out.println(body);
        body.validate();
    }
}
