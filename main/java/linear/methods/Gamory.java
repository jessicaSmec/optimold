package linear.methods;

import linear.table.SimTable;
import linear.table.SimTableThreads;

public class Gamory implements SimCommand {
    @Override
    public SimTable method(SimTable simTable) {
        simTable.gamory();
        return simTable;
    }

    @Override
    public SimTableThreads method(SimTableThreads table) throws InterruptedException {
        table.gamory();
        return table;
    }
}
