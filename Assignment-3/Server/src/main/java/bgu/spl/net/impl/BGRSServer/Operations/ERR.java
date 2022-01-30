package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

import java.nio.ByteBuffer;

public class ERR extends AbstractOperation {

    private static final short OPCODE = 13;
    private static final fragmentType[] fragmentTypes = {};

    public ERR(Short sentOpcode) {
        super(OPCODE, fragmentTypes);
        dataList.add(Short.toString(sentOpcode)); // The code of the operation that had ERR
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        return null;
    }

    @Override
    public byte[] output() {
        byte[] ERR_OPCODE = shortToBytes(opcode);
        byte[] OPERATION_CODE = shortToBytes(Short.parseShort(dataList.get(0)));
        ByteBuffer output = ByteBuffer.wrap(new byte[ERR_OPCODE.length + OPERATION_CODE.length]);
        output.put(ERR_OPCODE);
        output.put(OPERATION_CODE);
        return output.array();
    }
}
