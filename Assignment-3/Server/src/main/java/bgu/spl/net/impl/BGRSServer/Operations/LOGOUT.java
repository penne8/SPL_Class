package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class LOGOUT extends AbstractOperation {

    private static final short OPCODE = 4;
    private static final fragmentType[] fragmentTypes = {};

    public LOGOUT() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Try to logout and inform the client upon success/failure
        if (database.LOGOUT(protocol.getActiveUser())) {
            // Clear active user
            protocol.setActiveUser("");
            return new ACK(opcode, "");
        } else {
            return new ERR(opcode);
        }
    }
}
