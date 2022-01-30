package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

import java.util.List;

public interface Operation {

    static Operation getOperation(short opcode) {
        switch (opcode) {
            case 1:
                return new ADMINREG();
            case 2:
                return new STUDENTREG();
            case 3:
                return new LOGIN();
            case 4:
                return new LOGOUT();
            case 5:
                return new COURSEREG();
            case 6:
                return new KDAMCHECK();
            case 7:
                return new COURSESTAT();
            case 8:
                return new STUDENTSTAT();
            case 9:
                return new ISREGISTERED();
            case 10:
                return new UNREGISTER();
            case 11:
                return new MYCOURSES();
            default:
                return new ERR(opcode);
        }
    }

    short getOpcode();

    Operation process(BGRSProtocol protocol);

    fragmentType getNextFragmentType();

    void addDataFragment(String fragment);

    List<String> getDataList();

    byte[] output();

    enum fragmentType {STRING, SHORT, DONE}
}
