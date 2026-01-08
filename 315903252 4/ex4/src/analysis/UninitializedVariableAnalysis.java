package analysis;

import ir.*;
import temp.*;
import java.util.*;
import java.io.*;

public class UninitializedVariableAnalysis {
    private CFG cfg;
    private Set<String> initializedGlobals = new HashSet<>();
    private Map<Temp, String> tempToName;
    
    // Lattice: Set of initialized Strings (Variables) AND Integers (Temps serial)
    // We use Strings for vars and "T"+num for temps.
    
    public UninitializedVariableAnalysis(CFG cfg, Map<Temp, String> tempToName) {
        this.cfg = cfg;
        this.tempToName = tempToName;
    }
    
    public void setInitializedGlobals(Set<String> globals) {
        this.initializedGlobals = new HashSet<>(globals);
    }
    
    public Set<String> analyze() {
        Set<String> errors = new HashSet<>();
        
        // Initialize OUT sets
        // Entry node has ONLY initializedGlobals
        // All other nodes have ALL possible variables (optimistic for Intersection)
        // Actually, for Intersection (Must Analysis), Top is ALL, Bottom is EMPTY.
        // Entry IN = InitializedGlobals.
        // We want to find Fixed Point of Init Sets.
        
        Map<CFGNode, Set<String>> in = new HashMap<>();
        Map<CFGNode, Set<String>> out = new HashMap<>();
        
        // Universe of all tracked items? 
        // We can just use null to represent Universal Set (Top) if we want to save space,
        // or we just assume non-entry nodes key missing = Top.
        // Since we are propagating "Known Initialized", the set grows smaller (intersection).
        // Wait, Intersection means: If I have {a} from path 1 and {a, b} from path 2, result is {a}.
        // So effectively we start with Universe and shrink.
        
        // Let's identify the Universe of all variables/temps?
        // It's dynamic as we traverse.
        // Easier: Start with "All Universal" flag?
        
        for (CFGNode node : cfg.nodes) {
             out.put(node, null); // null means Universe
        }
        
        // Setup Entry
        Set<String> entryInit = new HashSet<>(initializedGlobals);
        entryInit.add("T" + TempFactory.getInstance().getDummyTEMP().getSerialNumber());
        out.put(cfg.entry, entryInit);
        
        boolean changed = true;
        while (changed) {
            changed = false;
            
            for (CFGNode node : cfg.nodes) {
                if (node == cfg.entry) continue; // Entry out is fixed
                
                // IN[n] = Intersection(OUT[p]) for p in preds
                Set<String> currentIn = null;
                
                if (node.predecessors.isEmpty()) {
                     // Unreachable node? Or just start with empty?
                     // If unreachable, it shouldn't affect.
                     // But for safety, empty.
                     currentIn = new HashSet<>(); 
                } else {
                    boolean first = true;
                    for (CFGNode pred : node.predecessors) {
                        Set<String> predOut = out.get(pred);
                        if (predOut == null) continue; // Universe, intersection doesn't change
                        
                        if (first) {
                            currentIn = new HashSet<>(predOut);
                            first = false;
                        } else {
                            currentIn.retainAll(predOut);
                        }
                    }
                    if (first) { 
                        // All preds were Universe
                        currentIn = null;
                    }
                }
                
                in.put(node, currentIn);
                
                // OUT[n] = Transfer(IN[n])
                Set<String> oldOut = out.get(node);
                Set<String> newOut = transfer(currentIn, node.commands);
                
                if (!setsEqual(oldOut, newOut)) {
                    out.put(node, newOut);
                    changed = true;
                }
            }
        }
        
        // 2. Error Collection Pass
        // Re-run transfer and check for uses of uninitialized vars
        for (CFGNode node : cfg.nodes) {
            Set<String> current = in.get(node);
            
            // Re-simulate transfer and catch errors
            Set<String> state = (current == null) ? null : new HashSet<>(current);
            // If state is null (Universe), everything is initialized, no errors.
            // Unless unreachable code?
            if (state == null) continue; 
            
            for (IrCommand cmd : node.commands) {
                checkCommand(cmd, state, errors);
                updateState(cmd, state);
            }
        }
        
        // System.out.println("DEBUG: Errors: " + errors);
        return errors;
    }
    
    private void checkCommand(IrCommand cmd, Set<String> state, Set<String> errors) {
        if (cmd instanceof IrCommandLoad) {
            IrCommandLoad l = (IrCommandLoad) cmd;
            if (!state.contains(l.varName)) {
                // Reporting rule: "lexicographically sorted list of the names"
                errors.add(l.varName);
            }
        }
        else if (cmd instanceof IrCommandBinopAddIntegers) {
            IrCommandBinopAddIntegers b = (IrCommandBinopAddIntegers) cmd;
            checkTemp(b.t1, state, errors);
            checkTemp(b.t2, state, errors);
        }
        else if (cmd instanceof IrCommandBinopMulIntegers) {
             IrCommandBinopMulIntegers b = (IrCommandBinopMulIntegers) cmd;
             checkTemp(b.t1, state, errors);
             checkTemp(b.t2, state, errors);
        }
        else if (cmd instanceof IrCommandBinopEqIntegers) {
             IrCommandBinopEqIntegers b = (IrCommandBinopEqIntegers) cmd;
             checkTemp(b.t1, state, errors);
             checkTemp(b.t2, state, errors);
        }
        else if (cmd instanceof IrCommandBinopLtIntegers) {
             IrCommandBinopLtIntegers b = (IrCommandBinopLtIntegers) cmd;
             checkTemp(b.t1, state, errors);
             checkTemp(b.t2, state, errors);
        }
        else if (cmd instanceof IrCommandPrintInt) {
             IrCommandPrintInt p = (IrCommandPrintInt) cmd;
             checkTemp(p.t, state, errors);
        }
        else if (cmd instanceof IrCommandMove) {
             IrCommandMove m = (IrCommandMove) cmd;
             checkTemp(m.src, state, errors);
        }
        else if (cmd instanceof IrCommandStore) {
             IrCommandStore s = (IrCommandStore) cmd;
             checkTemp(s.src, state, errors);
        }
        else if (cmd instanceof IrCommandJumpIfEqToZero) {
             IrCommandJumpIfEqToZero j = (IrCommandJumpIfEqToZero) cmd;
             checkTemp(j.t, state, errors);
        }
    }
    
    private void checkTemp(Temp t, Set<String> state, Set<String> errors) {
        String key = "T" + t.getSerialNumber();
        if (!state.contains(key)) {
             // Accessing uninitialized temp.
             // If this temp maps to a variable name, report it.
             String varName = tempToName.get(t);
             if (varName != null) {
                 errors.add(varName);
             }
        }
    }
    
    private Set<String> transfer(Set<String> in, List<IrCommand> commands) {
        if (in == null) return null; // Transformed universe is universe
        
        Set<String> current = new HashSet<>(in);
        for (IrCommand cmd : commands) {
             updateState(cmd, current);
        }
        return current;
    }
    
    private void updateState(IrCommand cmd, Set<String> state) {
        if (cmd instanceof IRcommandConstInt) {
             IRcommandConstInt c = (IRcommandConstInt) cmd;
             state.add("T" + c.t.getSerialNumber());
        }
        else if (cmd instanceof IrCommandLoad) {
             IrCommandLoad l = (IrCommandLoad) cmd;
             // If global var is initialized, temp becomes initialized
             if (state.contains(l.varName)) {
                 state.add("T" + l.dst.getSerialNumber());
             } else {
                 state.remove("T" + l.dst.getSerialNumber());
             }
        }
        else if (cmd instanceof IrCommandStore) {
             IrCommandStore s = (IrCommandStore) cmd;
             // If src temp is initialized, var becomes initialized
             if (state.contains("T" + s.src.getSerialNumber())) {
                 state.add(s.varName);
             } else {
                 // Assignment with uninitialized value does NOT initialize target
                 // So we assume it REMAINS uninitialized if it was?
                 // Spec: "Assigning ... uninitialized value does not initialize"
                 // Implies if it was uninit, it stays uninit.
                 // If it was init, does it become uninit? "Sets x to init value ONLY IF exp ... already assigned init"
                 // So yes, if exp is uninit, x becomes (or stays) uninit?
                 // "Assigning an expression ... uninitialized ... does not initialize"
                 // It doesn't explicitly say "it uninitializes it if it was initialized".
                 // But logically, `x := uninit` makes `x` hold garbage. So it should be uninit.
                 state.remove(s.varName);
             }
        }
        else if (cmd instanceof IrCommandMove) {
             IrCommandMove m = (IrCommandMove) cmd;
             if (state.contains("T" + m.src.getSerialNumber())) {
                 state.add("T" + m.dst.getSerialNumber());
             } else {
                 state.remove("T" + m.dst.getSerialNumber());
             }
        }
        else if (cmd instanceof IrCommandBinopAddIntegers) {
             IrCommandBinopAddIntegers b = (IrCommandBinopAddIntegers) cmd;
             String t1 = "T" + b.t1.getSerialNumber();
             String t2 = "T" + b.t2.getSerialNumber();
             if (state.contains(t1) && state.contains(t2)) {
                 state.add("T" + b.dst.getSerialNumber());
             } else {
                 state.remove("T" + b.dst.getSerialNumber());
             }
        }
        // Similar for other binops
        else if (cmd instanceof IrCommandBinopMulIntegers) {
             IrCommandBinopMulIntegers b = (IrCommandBinopMulIntegers) cmd;
             String t1 = "T" + b.t1.getSerialNumber();
             String t2 = "T" + b.t2.getSerialNumber();
             if (state.contains(t1) && state.contains(t2)) {
                 state.add("T" + b.dst.getSerialNumber());
             } else {
                 state.remove("T" + b.dst.getSerialNumber());
             }
        }
        else if (cmd instanceof IrCommandBinopEqIntegers) {
             IrCommandBinopEqIntegers b = (IrCommandBinopEqIntegers) cmd;
             String t1 = "T" + b.t1.getSerialNumber();
             String t2 = "T" + b.t2.getSerialNumber();
             if (state.contains(t1) && state.contains(t2)) {
                 state.add("T" + b.dst.getSerialNumber());
             } else {
                 state.remove("T" + b.dst.getSerialNumber());
             }
        }
        else if (cmd instanceof IrCommandBinopLtIntegers) {
             IrCommandBinopLtIntegers b = (IrCommandBinopLtIntegers) cmd;
             String t1 = "T" + b.t1.getSerialNumber();
             String t2 = "T" + b.t2.getSerialNumber();
             if (state.contains(t1) && state.contains(t2)) {
                 state.add("T" + b.dst.getSerialNumber());
             } else {
                 state.remove("T" + b.dst.getSerialNumber());
             }
        }
    }

    private boolean setsEqual(Set<String> s1, Set<String> s2) {
        if (s1 == null && s2 == null) return true;
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }
}
