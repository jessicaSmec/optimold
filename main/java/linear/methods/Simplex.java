package linear.methods;

import linear.table.SimTableThreads;
import linear.table.SimTable;

public class

Simplex implements SimCommand {
    @Override
     public SimTable method(SimTable simTable)  {
        simTable.simplex();
        return simTable;
    }
    @Override
     public SimTableThreads method(SimTableThreads table) throws InterruptedException {
        table.simplex();
        return table;
    }
}


