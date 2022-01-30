package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class KDAMCHECK extends AbstractOperation {

    private static final short OPCODE = 6;
    private static final fragmentType[] fragmentTypes = {fragmentType.SHORT};

    public KDAMCHECK() {
        super(OPCODE, fragmentTypes);
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        // Get inquired course number
        int courseNumber = Integer.parseInt(dataList.get(0));

        // Get the relevant kdam courses, if the inquired course exist
        String kdamCourses = database.KDAMCHECK(protocol.getActiveUser(), courseNumber);

        if (kdamCourses != null) {
            // Return the relevant kdam courses
            return new ACK(opcode, kdamCourses);
        }
        // Return an error, in case course doesn't exist or activeUser's not logged in/is an admin
        return new ERR(opcode);
    }
}
