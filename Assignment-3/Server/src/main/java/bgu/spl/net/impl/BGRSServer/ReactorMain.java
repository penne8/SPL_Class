package bgu.spl.net.impl.BGRSServer;


import bgu.spl.net.impl.BGRSServer.Operations.Operation;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        Database db = Database.getInstance();
        Server<Operation> reactor = Server.reactor(Integer.parseInt(args[1]), Integer.parseInt(args[0]), () -> new BGRSProtocol(), () -> new BGRSEncoderDecoder());
        reactor.serve();
    }
}
