package linear.methods;

import linear.table.SimTableThreads;
import linear.table.SimTable;

public class DualSimplex implements SimCommand {
    @Override
    public SimTable method(SimTable simTable) {
        simTable.dualSimplex();
        return simTable;
    }
    @Override
    public SimTableThreads method(SimTableThreads table) throws InterruptedException {
        table.dualSimplex();
        return table;
    }
}
