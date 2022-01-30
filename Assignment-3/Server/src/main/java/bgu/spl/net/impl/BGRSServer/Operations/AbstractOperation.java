package bgu.spl.net.impl.BGRSServer.Operations;

import bgu.spl.net.impl.BGRSServer.Database;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public abstract class AbstractOperation implements Operation {
    protected static Database database = Database.getInstance();
    protected short opcode;
    protected Stack<fragmentType> nextFragmentType;
    protected List<String> dataList;

    protected AbstractOperation(short opcode, fragmentType[] fragmentTypes) {
        nextFragmentType = new Stack<fragmentType>();
        dataList = new LinkedList<>();
        this.opcode = opcode;
        for (Operation.fragmentType fragmentType : fragmentTypes) {
            nextFragmentType.push(fragmentType);
        }
    }

    public short getOpcode() {
        return opcode;
    }

    public fragmentType getNextFragmentType() {
        if (nextFragmentType.isEmpty()) {
            return fragmentType.DONE;
        } else {
            return nextFragmentType.pop();
        }
    }

    public void addDataFragment(String fragment) {
        dataList.add(fragment);
    }

    public List<String> getDataList() {
        return dataList;
    }

    protected byte[] shortToBytes(short num) {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte) ((num >> 8) & 0xFF);
        bytesArr[1] = (byte) (num & 0xFF);
        return bytesArr;
    }

    public byte[] output() {
        return null;
    }
}
