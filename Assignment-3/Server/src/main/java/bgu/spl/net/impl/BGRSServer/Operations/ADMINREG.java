package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class ADMINREG extends AbstractOperation {

    private static final short OPCODE = 1;
    private static final fragmentType[] fragmentTypes = {fragmentType.STRING, fragmentType.STRING};

    public ADMINREG() {
        super(OPCODE, fragmentTypes);
    }

    public Operation process(BGRSProtocol protocol) {
        // Get username and password
        String username = dataList.get(0);
        String password = dataList.get(1);

        // Try to register and inform the client upon success/failure
        // The client can't register while logged in
        if (protocol.getActiveUser().isEmpty() && database.REG(username, password, true)) {
            return new ACK(opcode, "");
        } else {
            return new ERR(opcode);
        }
    }
}