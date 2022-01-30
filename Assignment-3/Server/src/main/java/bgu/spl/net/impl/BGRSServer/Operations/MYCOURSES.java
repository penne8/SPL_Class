package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class MYCOURSES extends AbstractOperation {

    private static final short OPCODE = 11;
    private static final fragmentType[] fragmentTypes = {};

    public MYCOURSES() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        String myCourses = database.MYCOURSES(protocol.getActiveUser());

        // Prepare a response message for the client containing the inquired information
        if (myCourses != null) {
            return new ACK(opcode, myCourses);
        }
        return new ERR(opcode);
    }
}
