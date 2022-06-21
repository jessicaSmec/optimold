package nonlinear.methods;

import linear.table.SimTableThreads;
import nonlinear.gradtable.GradTable;
import nonlinear.gradtable.GradTableThreads;

public interface GradCommand {
    GradTable method(GradTable gradTable);
    public GradTableThreads method(SimTableThreads table) throws InterruptedException;
}
