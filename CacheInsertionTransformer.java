import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.SceneTransformer;
import soot.Transform;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.*;
import soot.G;
import soot.Scene;
import soot.options.Options;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.util.*;
import java.io.*;
import java.io.FileOutputStream;
import java.util.*;

public class CacheInsertionTransformer extends SceneTransformer {

    public static void addCacheField(SootClass cls) {
        SootClass cacheClass = Scene.v().getSootClass("Cache");
        Local cacheLocal = Jimple.v().newLocal("cacheLocal", cacheClass.getType());
         // clinit method in java contains static variable initializations
        SootMethod init = cls.getMethodByName("<clinit>");
        JimpleBody body = (JimpleBody) init.retrieveActiveBody();

        // add a local variable "cacheLocal" and instantiate it
        body.getLocals().add(cacheLocal);
        Value initcall = Jimple.v().newNewExpr(cacheClass.getType());
        Unit assg1 = Jimple.v().newAssignStmt(cacheLocal, initcall);
        body.getUnits().addFirst(assg1);
        Value invokeCacheInit = Jimple.v().newSpecialInvokeExpr(cacheLocal,
                cacheClass.getMethodByName("<init>").makeRef());
        Unit assg2 = Jimple.v().newInvokeStmt(invokeCacheInit);
        
        // Assign the cacheLocal variable to the static cache member
        Value staticCache = Jimple.v().newStaticFieldRef(cls.getFieldByName("cache").makeRef());
        Unit assg3 = Jimple.v().newAssignStmt(staticCache, cacheLocal);

        // insert these instruction into the body
        body.getUnits().insertAfter(assg2, assg1);
        body.getUnits().insertBefore(assg3, body.getUnits().getLast());

        // validate body
         try {
            body.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void internalTransform(String phaseName, Map options) {
        // tragte method which should caontain the optimisation
        String methodName = "getInfo";
        // target class containting target method and RPC method
        SootClass mainClass = Scene.v().getSootClass("Gateway");
        mainClass.setApplicationClass();
        // get our Cache class
        SootClass cacheClass = Scene.v().getSootClass("Cache");
        // Add a static cache field to targte class
        SootField f = Scene.v().makeSootField("cache", cacheClass.getType(),
                Modifier.STATIC | Modifier.PUBLIC | Modifier.FINAL);
        mainClass.addField(f);
        // initialize the field
        addCacheField(mainClass);

        // get the target method
        SootMethod sm = mainClass.getMethodByName(methodName);
        JimpleBody body = (JimpleBody) sm.retrieveActiveBody();
        Iterator it = body.getUnits().iterator();

        // identify various insertion points
        Value resultVal = null; // represents the variable storing result of RPC call
        Unit rpcCall = null; // storing the statement containing RPC method call
        Unit wrapRes = null; // storing the statement from where result is further processed

        Value staticCache = Jimple.v().newStaticFieldRef(mainClass.getFieldByName("cache").makeRef());
        while (it.hasNext()) {
            Unit ub = (Unit) it.next();
            // identify call site
            if (ub.toString().contains("getInfoCall(java.lang.String)")) {
                rpcCall = ub;
                JAssignStmt as = (JAssignStmt) ub;
                resultVal = as.getLeftOp();
                // identify replacement point
                wrapRes = (Unit) it.next();
                break;
            }
        }
        JAssignStmt rpcCallStmt = (JAssignStmt) rpcCall;

        // get the methods of cache class
        SootMethod cacheGet = cacheClass.getMethodByName("get");
        SootMethod cachePut = cacheClass.getMethodByName("put");

        // create new local cache variable and assign static cache variable to it
        Local cacheLocal = Jimple.v().newLocal("cacheLocal", cacheClass.getType());
        body.getLocals().add(cacheLocal);
        Unit assg2 = Jimple.v().newAssignStmt(cacheLocal, staticCache);

        // invoke cache.get on the key
        Value newub = Jimple.v().newVirtualInvokeExpr(cacheLocal, cacheGet.makeRef(), rpcCallStmt.getInvokeExpr().getArgs());
        AssignStmt assg = Jimple.v().newAssignStmt(resultVal, newub);

        // cast expression to type cast Cache return type (java.lang.Object) into expected type
        CastExpr cast = Jimple.v().newCastExpr(resultVal, resultVal.getType());
        AssignStmt assgCast = Jimple.v().newAssignStmt(resultVal, cast);

        // insert generated instructions in appropriate place
        body = (JimpleBody) sm.retrieveActiveBody();
        body.getUnits().insertBefore(assg, rpcCall);
        body.getUnits().insertBefore(assg2, assg);
        body.getUnits().insertBefore(assgCast, wrapRes);
        wrapRes = assgCast;

        // generate branch instructions 
        NeExpr qualExpr = Jimple.v().newNeExpr(NullConstant.v(), resultVal);
        EqExpr qualExpr2 = Jimple.v().newEqExpr(NullConstant.v(), resultVal);
        Unit ifstatement = Jimple.v().newIfStmt(qualExpr, wrapRes);
        Unit ifstatement2 = Jimple.v().newIfStmt(qualExpr2, rpcCall);
        List<Value> putArgs = rpcCallStmt.getInvokeExpr().getArgs();
        putArgs.add(resultVal);
        // call Cache.put in case of cache miss
        InvokeExpr putStmt = Jimple.v().newVirtualInvokeExpr(cacheLocal, cachePut.makeRef(),
                putArgs);

        // insert branch instructions in appropriate place
        body.getUnits().insertAfter(ifstatement, assg);
        body.getUnits().insertAfter(ifstatement2, ifstatement);
        body.getUnits().insertAfter(soot.jimple.Jimple.v().newInvokeStmt(putStmt), rpcCall);

        // validate transformed body
        body = (JimpleBody) sm.retrieveActiveBody();
        try {
            body.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}