package ast;

import java.util.HashSet;
import java.util.Set;
import symboltable.*;
import types.*;

public class AstDecClass extends AstDec {
    public String name;
    public String superName;
    public AstDecList classContent;

    public AstDecClass(String name, String superName, AstDecList classContent, int lineNumber) {
        super(lineNumber);
        serialNumber = AstNodeSerialNumber.getFresh();
        this.name = name;
        this.superName = superName;
        this.classContent = classContent;
    }

    public void printMe() {
        System.out.format("CLASS DEC = %s EXTENDS %s\n", name, superName != null ? superName : "Object");
        if (classContent != null)
            classContent.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("CLASS\n%s", name));
        if (classContent != null)
            AstGraphviz.getInstance().logEdge(serialNumber, classContent.serialNumber);
    }

    public void semantSignature() {
        TypeClass father = null;
        if (superName != null) {
            Type t = SymbolTable.getInstance().find(superName);
            if (t == null || !t.isClass())
                error("Super class " + superName + " not found");
            father = (TypeClass) t;
        }

        TypeClass selfType = new TypeClass(father, name);
        if (SymbolTable.getInstance().find(name) != null)
            error("Class " + name + " already declared");
        SymbolTable.getInstance().enter(name, selfType);
    }

    public void semantBody() {
        TypeClass selfType = (TypeClass) SymbolTable.getInstance().find(name);
        TypeClass father = selfType.father;
        
        SymbolTable.getInstance().beginScope();

        // Collect declared names
        Set<String> declaredNames = new HashSet<>();
        for (AstDecList it = classContent; it != null; it = it.tail) {
            if (it.head instanceof AstDecVar)
                declaredNames.add(((AstDecVar) it.head).name);
            else if (it.head instanceof AstDecFunc)
                declaredNames.add(((AstDecFunc) it.head).name);
        }

        // Populate scope with ancestor members
        TypeClass ancestor = father;
        while (ancestor != null) {
            for (TypeClassVarDec f : ancestor.fields) {
                if (declaredNames.contains(f.name))
                    continue;
                if (SymbolTable.getInstance().containsInCurrentScope(f.name))
                    continue;
                SymbolTable.getInstance().enter(f.name, f.t);
            }
            for (TypeFunction m : ancestor.methods) {
                if (declaredNames.contains(m.name))
                    continue;
                if (SymbolTable.getInstance().containsInCurrentScope(m.name))
                    continue;
                SymbolTable.getInstance().enter(m.name, m);
            }
            ancestor = ancestor.father;
        }

        // Pass 1: Signatures and Fields
        for (AstDecList it = classContent; it != null; it = it.tail) {
            AstDec dec = it.head;
            if (dec instanceof AstDecVar) {
                AstDecVar v = (AstDecVar) dec;
                Type vt = SymbolTable.getInstance().find(v.type);
                if (vt == null || vt.isVoid())
                    v.error("Field " + v.name + " has invalid type");

                // Shadowing check
                if (selfType.getFieldType(v.name) != null || selfType.getMethod(v.name) != null)
                    v.error("Shadowing field " + v.name);

                SymbolTable.getInstance().enter(v.name, vt);
                selfType.fields.add(new TypeClassVarDec(vt, v.name));
            } else if (dec instanceof AstDecFunc) {
                AstDecFunc f = (AstDecFunc) dec;
                f.semantSignature();

                if (selfType.getFieldType(f.name) != null)
                    f.error("Method " + f.name + " shadows a field");

                TypeFunction superMethod = (father != null) ? father.getMethod(f.name) : null;
                TypeFunction fType = (TypeFunction) SymbolTable.getInstance().find(f.name);

                if (fType != null) {
                    if (superMethod != null) {
                        if (fType.returnType != superMethod.returnType)
                            f.error("Override return type mismatch");

                        TypeList t1 = fType.params;
                        TypeList t2 = superMethod.params;
                        while (t1 != null && t2 != null) {
                            if (t1.head != t2.head)
                                f.error("Override param type mismatch");
                            t1 = t1.tail;
                            t2 = t2.tail;
                        }
                        if (t1 != null || t2 != null)
                            f.error("Override param count mismatch");
                    } else {
                        for (TypeFunction defined : selfType.methods) {
                            if (defined.name.equals(f.name))
                                f.error("Method overloading " + f.name);
                        }
                    }
                    selfType.methods.add(fType);
                }
            }
        }

        // Pass 2: Bodies/Init
        for (AstDecList it = classContent; it != null; it = it.tail) {
            AstDec dec = it.head;
            if (dec instanceof AstDecVar) {
                AstDecVar v = (AstDecVar) dec;
                if (v.initialValue != null) {
                    Type vt = SymbolTable.getInstance().find(v.type);
                    Type tExp = v.initialValue.semantMe();
                    if (!tExp.isAssignableTo(vt))
                        v.error("Field init type mismatch");
                }
            } else if (dec instanceof AstDecFunc) {
                AstDecFunc f = (AstDecFunc) dec;
                f.semantBody();
            }
        }

        SymbolTable.getInstance().endScope();
    }

    public Type semantMe() {
        semantSignature();
        semantBody();
        return null;
    }

    public void irMe() {
        // Not relevant for assignment 4
    }
}