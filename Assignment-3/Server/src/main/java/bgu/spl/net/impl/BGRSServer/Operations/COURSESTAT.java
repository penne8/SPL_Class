package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class COURSESTAT extends AbstractOperation {

    private static final short OPCODE = 7;
    private static final fragmentType[] fragmentTypes = {fragmentType.SHORT};

    public COURSESTAT() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Get inquired course number
        int courseNumber = Integer.parseInt(dataList.get(0));

        // Get the relevant stats, if the inquired course exist
        String courseStat = database.COURSESTAT(protocol.getActiveUser(), courseNumber);

        // Return the relevant stats
        if (courseStat != null) {
            return new ACK(opcode, courseStat);
        }
        // Return an error, in case course doesn't exist or activeUser's not logged in/not an admin
        return new ERR(opcode);
    }
}
