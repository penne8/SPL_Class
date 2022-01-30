package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGRSServer.Operations.Operation;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSEncoderDecoder implements MessageEncoderDecoder<Operation> {

    private byte[] bytes = new byte[1 << 10];
    private int len = 0;
    private Operation operation = null;
    private Operation.fragmentType fragmentType;

    @Override
    public Operation decodeNextByte(byte nextByte) {
        if (operation == null) {
            pushByte(nextByte);
            if (len == 2) {
                operation = Operation.getOperation(bytesToShort(bytes));
                fragmentType = operation.getNextFragmentType();
                // If we expect more data from the client, the decoder must receive the nextByte
                if (fragmentType != Operation.fragmentType.DONE) {
                    return null;
                }
            } else {
                // Must read 2 bytes to define operation type
                return null;
            }
        }

        // Once operation type is defined, the rest of the message can be decoded
        switch (fragmentType) {
            case STRING:
                return decodingString(nextByte);
            case SHORT:
                return decodingShort(nextByte);
            case DONE:
                return decodedOperation();
            default:
                return null;
        }
    }

    private Operation decodingString(byte nextByte) {
        if (nextByte == '\0') {
            // Fragment completed
            operation.addDataFragment(popString());
            fragmentType = operation.getNextFragmentType();
            if (fragmentType == Operation.fragmentType.DONE) {
                return decodedOperation();
            } else {
                // Data isn't completed
                return null;
            }
        } else {
            // Fragment isn't completed
            pushByte(nextByte);
            return null;
        }
    }

    private Operation decodingShort(byte nextByte) {
        pushByte(nextByte);
        if (len == 2) {
            operation.addDataFragment(Short.toString(bytesToShort(bytes)));
            fragmentType = operation.getNextFragmentType();
            if (fragmentType == Operation.fragmentType.DONE) {
                return decodedOperation();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private Operation decodedOperation() {
        Operation decodedOperation = operation;
        operation = null;
        fragmentType = null;
        return decodedOperation;
    }

    public byte[] encode(Operation operation) {
        return operation.output();
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        len = 0;
        return result;
    }


    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
}
