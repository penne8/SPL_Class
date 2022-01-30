package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.passiveObjects.JsonIO;
import bgu.spl.mics.application.services.*;

import java.io.IOException;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system. In
 * the end, you should output a JSON.
 */
public class Main {
    public static void main(String[] args) throws IOException {

        // Parse the input file
        Input input = JsonIO.getInputFromJson(args[0]);

        // Create the different components of the application

        //// Construct passive objects
        MessageBusImpl.getInstance();
        Diary.getInstance();
        Ewoks.getInstance().initEwoks(input.getEwoks());

        //// Construct Microservices
        Thread Leia = new Thread(new LeiaMicroservice(input.getAttacks()));
        Thread HanSolo = new Thread(new HanSoloMicroservice());
        Thread C3PO = new Thread(new C3POMicroservice());
        Thread R2D2 = new Thread(new R2D2Microservice(input.getR2D2()));
        Thread Lando = new Thread(new LandoMicroservice(input.getLando()));

        // Run the system
        Leia.start();
        HanSolo.start();
        C3PO.start();
        R2D2.start();
        Lando.start();
        try { // Allowing all threads to end before generating the output file
            Leia.join();
            HanSolo.join();
            C3PO.join();
            R2D2.join();
            Lando.join();
        } catch (InterruptedException ignored) {
        }

        // Output a JSON
        JsonIO.getJsonFromDiary(args[1]);
    }
}