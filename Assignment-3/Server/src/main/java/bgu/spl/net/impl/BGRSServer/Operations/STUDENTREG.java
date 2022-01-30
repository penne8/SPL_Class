package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class STUDENTREG extends AbstractOperation {

    private static final short OPCODE = 2;
    private static final fragmentType[] fragmentTypes = {fragmentType.STRING, fragmentType.STRING};

    public STUDENTREG() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Get username and password
        String username = dataList.get(0);
        String password = dataList.get(1);

        // Try to register and inform the client upon success/failure
        // The client can't register while logged in
        if (protocol.getActiveUser().isEmpty() && database.REG(username, password, false)) {
            return new ACK(opcode, "");
        } else {
            return new ERR(opcode);
        }
    }
}
