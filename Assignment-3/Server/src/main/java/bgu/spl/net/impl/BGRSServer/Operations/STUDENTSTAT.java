package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class STUDENTSTAT extends AbstractOperation {

    private static final short OPCODE = 8;
    private static final fragmentType[] fragmentTypes = {fragmentType.STRING};

    public STUDENTSTAT() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Get the inquired username
        String username = dataList.get(0);

        // Get the relevant stats, if the inquired user exist
        String studentStat = database.STUDENTSTAT(protocol.getActiveUser(), username);

        if (studentStat != null) {
            // Return the relevant stats
            return new ACK(opcode, studentStat);
        }
        // Return an error, in case user doesn't exist or activeUser's not logged in/not an admin or the inquired user is an admin
        return new ERR(opcode);
    }
}
