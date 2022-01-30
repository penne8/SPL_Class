package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.Operations.Operation;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main(String[] args) {
        Database db = Database.getInstance();
        try (Server<Operation> server = Server.threadPerClient(Integer.parseInt(args[0]), () -> new BGRSProtocol(), () -> new BGRSEncoderDecoder())) {
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}