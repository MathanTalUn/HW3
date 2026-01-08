package analysis;

import ir.*;
import java.util.ArrayList;
import java.util.List;

public class CFGNode {
    public List<IrCommand> commands = new ArrayList<>();
    public List<CFGNode> successors = new ArrayList<>();
    public List<CFGNode> predecessors = new ArrayList<>();
    
    // For analysis
    public Object in;
    public Object out;
    
    public void addCommand(IrCommand cmd) {
        commands.add(cmd);
    }
}
