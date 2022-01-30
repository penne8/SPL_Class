#ifndef RESPONSE_THREAD_
#define RESPONSE_THREAD_

#include <iostream>
#include <mutex>
#include <thread>
#include <condition_variable>
#include "../include/connectionHandler.h"

using namespace std;

class BGRSResponseHandler {

public:
    BGRSResponseHandler(ConnectionHandler &_connectionHandler, condition_variable &_terminationCondition);

    // Listens to the server and processing incoming messages
    void listen();

private:
    ConnectionHandler *connectionHandler;
    condition_variable &terminationCondition;
    bool shouldTerminate;
    const int opcodesSize = 4;

    void process(short opcode);
};

#endif