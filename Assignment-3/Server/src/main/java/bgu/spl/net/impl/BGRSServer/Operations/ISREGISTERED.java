package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class ISREGISTERED extends AbstractOperation {

    private static final short OPCODE = 9;
    private static final fragmentType[] fragmentTypes = {fragmentType.SHORT};

    public ISREGISTERED() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Get inquired course number
        int courseNumber = Integer.parseInt(dataList.get(0));

        // Inform the client if the active user is inquired to the given course
        String isRegistered = database.ISREGISTERED(protocol.getActiveUser(), courseNumber);
        if (isRegistered != null) {
            return new ACK(opcode, isRegistered);
        } else {
            return new ERR(opcode);
        }
    }
}
