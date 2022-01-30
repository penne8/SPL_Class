#include "../include/connectionHandler.h"
#include "../include/BGRSInputHandler.h"
#include "../include/BGRSResponseHandler.h"
#include <cstdlib>
#include <iostream>
#include <mutex>
#include <thread>
#include <condition_variable>

using namespace std;

int main(int argc, char *argv[]) {
    // Connect to the server
    if (argc < 3) {
        cerr << "Usage: " << argv[0] << " ip port" << endl;
        return -1;
    }
    string ip = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(ip, port);
    if (!connectionHandler.connect()) {
        cerr << "Cannot connect to " << ip << ":" << port << endl;
        return 1;
    }

    // Create a lock for the connection handler
    // Will be used for the BGRSInputHandler graceful termination
    mutex lock;
    unique_lock<mutex> connectionHandlerLock(lock);
    condition_variable terminationCondition;

    // Launch the client input thread
    BGRSInputHandler clientInput(connectionHandler, connectionHandlerLock, terminationCondition);
    thread clientInputThread(&BGRSInputHandler::run, &clientInput);

    // Start the server response listener (on the main thread)
    BGRSResponseHandler responseHandler(connectionHandler, terminationCondition);
    responseHandler.listen();

    // When the listen process is done, we need to gracefully terminate the client input thread
    clientInput.terminate();
    clientInputThread.join();

    // Finish
    return 0;
}