package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class LOGIN extends AbstractOperation {

    private static final short OPCODE = 3;
    private static final fragmentType[] fragmentTypes = {fragmentType.STRING, fragmentType.STRING};

    public LOGIN() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Get the username and password
        String username = dataList.get(0);
        String password = dataList.get(1);

        // Try to login and inform the client upon success/failure
        if (database.LOGIN(username, password)) {
            // Set active user
            protocol.setActiveUser(username);
            return new ACK(opcode, "");
        } else {
            return new ERR(opcode);
        }
    }
}
