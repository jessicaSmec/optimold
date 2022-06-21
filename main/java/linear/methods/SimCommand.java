package linear.methods;

import linear.table.SimTable;
import linear.table.SimTableThreads;

public interface SimCommand {

    SimTable method(SimTable simTable);
    public SimTableThreads method(SimTableThreads table) throws InterruptedException;


}
