package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class UNREGISTER extends AbstractOperation {

    private static final short OPCODE = 10;
    private static final fragmentType[] fragmentTypes = {fragmentType.SHORT};

    public UNREGISTER() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Get inquired course number
        int courseNumber = Integer.parseInt(dataList.get(0));

        // Try to unregister and inform the client upon success/failure
        if (database.UNREGISTER(protocol.getActiveUser(), courseNumber)) {
            return new ACK(opcode, "");
        } else {
            return new ERR(opcode);
        }
    }
}
