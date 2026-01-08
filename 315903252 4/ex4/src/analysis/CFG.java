package analysis;

import ir.*;
import java.util.*;

public class CFG {
    public List<CFGNode> nodes = new ArrayList<>();
    public CFGNode entry;
    public CFGNode exit;
    
    public CFG() {
        entry = new CFGNode();
        nodes.add(entry);
    }
    
    public static CFG build(Ir ir) {
        CFG cfg = new CFG();
        
        List<IrCommand> commands = new ArrayList<>();
        if (ir.head != null) commands.add(ir.head);
        IrCommandList list = ir.tail;
        while (list != null) {
            commands.add(list.head);
            list = list.tail;
        }
        
        // Split into basic blocks
        // Leaders: First instruction, Target of Jumps, Instruction after Jumps
        Set<IrCommand> leaders = new HashSet<>();
        if (!commands.isEmpty()) leaders.add(commands.get(0));
        
        for (int i = 0; i < commands.size(); i++) {
            IrCommand cmd = commands.get(i);
            if (cmd instanceof IrCommandJumpLabel || cmd instanceof IrCommandJumpIfEqToZero) {
                 if (i + 1 < commands.size()) leaders.add(commands.get(i+1));
                 // Targets of jumps are handled by looking up labels later? 
                 // Or we can pre-scan labels.
            }
            if (cmd instanceof IrCommandLabel) {
                leaders.add(cmd);
            }
        }
        
        // Create nodes
        Map<IrCommand, CFGNode> cmdToNode = new HashMap<>();
        CFGNode currentNode = null;
        
        for (IrCommand cmd : commands) {
            if (leaders.contains(cmd)) {
                currentNode = new CFGNode();
                cfg.nodes.add(currentNode);
            }
            currentNode.addCommand(cmd);
            cmdToNode.put(cmd, currentNode);
        }
        
        // Add edges
        for (int i = 0; i < cfg.nodes.size(); i++) {
            CFGNode node = cfg.nodes.get(i);
            if (node.commands.isEmpty()) continue;
            
            IrCommand last = node.commands.get(node.commands.size() - 1);
            
            // Fallthrough edge
            if (!(last instanceof IrCommandJumpLabel)) { // Unconditional jump doesn't fall through
                if (i + 1 < cfg.nodes.size()) {
                    connect(node, cfg.nodes.get(i+1));
                }
            }
            
            // Jump edges
            if (last instanceof IrCommandJumpLabel) {
                String label = ((IrCommandJumpLabel)last).label;
                connectToLabel(node, label, cfg.nodes);
            } else if (last instanceof IrCommandJumpIfEqToZero) {
                String label = ((IrCommandJumpIfEqToZero)last).label;
                connectToLabel(node, label, cfg.nodes);
            }
        }
        
        // Connect entry to first real node
        if (cfg.nodes.size() > 1) {
            connect(cfg.entry, cfg.nodes.get(1));
        }
        
        return cfg;
    }
    
    private static void connect(CFGNode src, CFGNode dst) {
        src.successors.add(dst);
        dst.predecessors.add(src);
    }
    
    private static void connectToLabel(CFGNode src, String label, List<CFGNode> nodes) {
        for (CFGNode n : nodes) {
            if (!n.commands.isEmpty() && n.commands.get(0) instanceof IrCommandLabel) {
                if (((IrCommandLabel)n.commands.get(0)).label.equals(label)) {
                    connect(src, n);
                    return;
                }
            }
        }
    }
}
