#ifndef INPUT_THREAD__
#define INPUT_THREAD__

#include <iostream>
#include <mutex>
#include <thread>
#include <condition_variable>
#include "../include/connectionHandler.h"

using namespace std;

class BGRSInputHandler {

public:
    // constructor
    explicit BGRSInputHandler(ConnectionHandler &_connectionHandler, unique_lock<mutex> &_connectionHandlerLock,
                              condition_variable &_terminationCondition);

    // Starts the input thread and encoding process
    void run();

    // Signals to the input thread to terminate GRACEFULLY
    void terminate();

private:
    ConnectionHandler *connectionHandler;
    unique_lock<mutex> &connectionHandlerLock;
    condition_variable &terminationCondition;
    const int opByteSize = 2;
    bool shouldTerminate;
    short opcode;
    size_t readIndex;

    string prepareArgs(const string &clientInput, const string &argType);

    void inputToBytes(const char *opcodeBytes, const string &arguments, char *decodedBytes) const;

    void send(const char *inputBytes, const size_t &inputSize);

    void encodeAndSend(const string &clientInput, const string &argType);
};

#endif