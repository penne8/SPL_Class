package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.BGRSServer.Operations.Operation;

public class BGRSProtocol implements MessagingProtocol<Operation> {

    private String activeUser = "";

    @Override
    public Operation process(Operation operation) {
        if (operation != null) {
            return operation.process(this);
        }
        return null;
    }

    public String getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(String activeUser) {
        this.activeUser = activeUser;
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
