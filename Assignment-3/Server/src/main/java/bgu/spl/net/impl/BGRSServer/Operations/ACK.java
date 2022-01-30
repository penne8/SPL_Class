package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

import java.nio.ByteBuffer;

public class ACK extends AbstractOperation {

    private static final short OPCODE = 12;
    private static final fragmentType[] fragmentTypes = {};

    public ACK(short sentOpcode, String optional) {
        super(OPCODE, fragmentTypes);
        dataList.add(Short.toString(sentOpcode)); // The code of the operation that is ACK
        dataList.add(optional); // String to be printed at the client side
    }

    @Override
    public Operation process(BGRSProtocol protocol) {
        return null;
    }

    @Override
    public byte[] output() {
        byte[] ACK_OPCODE = shortToBytes(opcode);
        byte[] OPERATION_CODE = shortToBytes(Short.parseShort(dataList.get(0)));
        byte[] RESPONSE_STRING = (dataList.get(1) + "\0").getBytes();
        ByteBuffer output = ByteBuffer.wrap(new byte[ACK_OPCODE.length + OPERATION_CODE.length + RESPONSE_STRING.length]);
        output.put(ACK_OPCODE);
        output.put(OPERATION_CODE);
        output.put(RESPONSE_STRING);
        return output.array();
    }
}
