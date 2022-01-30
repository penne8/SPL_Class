#include <iostream>
#include <mutex>
#include "../include/connectionHandler.h"
#include "../include/BGRSInputHandler.h"

using namespace std;

BGRSInputHandler::BGRSInputHandler(ConnectionHandler &_connectionHandler, unique_lock<mutex> &_connectionHandlerLock,
                                   condition_variable &_terminationCondition)
        : connectionHandler(&_connectionHandler), connectionHandlerLock(_connectionHandlerLock),
          terminationCondition(_terminationCondition),
          shouldTerminate(false), opcode(0), readIndex(0) {
}

void shortToBytes(const short &num, char *bytesArr) {
    bytesArr[0] = (num >> 8) & 0xFF;
    bytesArr[1] = num & 0xFF;
}

string BGRSInputHandler::prepareArgs(const string &clientInput, const string &argType) {

    // Extract arguments from client input
    string arguments;
    while (readIndex < clientInput.length()) {
        for (readIndex++; readIndex < clientInput.length(); readIndex++) {
            if (clientInput[readIndex] == ' ') {
                break;
            }
            arguments += clientInput[readIndex];
        }

        // Each string argument needs to be separated by '\0'
        if (argType == "string") {
            arguments += '\0';
        }
    }

    // Handle short argument scenario
    if (argType == "short") {
        short number = stoi(arguments);
        string output = "00";
        output[0] = (number >> 8) & 0xFF;
        output[1] = number & 0xFF;
        return output;
    }

    return arguments;
}

void BGRSInputHandler::inputToBytes(const char *opcodeBytes, const string &arguments, char *decodedBytes) const {
    const char *argsBytes = &arguments[0];
    size_t index = 0;
    for (int c = 0; c < opByteSize; c++) {
        decodedBytes[index] = opcodeBytes[c];
        index++;
    }
    for (size_t c = 0; c < arguments.length(); c++) {
        decodedBytes[index] = argsBytes[c];
        index++;
    }
}

void BGRSInputHandler::send(const char *inputBytes, const size_t &inputSize) {
    if (!connectionHandler->sendBytes(inputBytes, inputSize)) {
        cerr << "Disconnected. Exiting...\n"
             << endl;
        shouldTerminate = true;
    }
}

void BGRSInputHandler::encodeAndSend(const string &clientInput, const string &argType) {

    // Prepare opcode
    char opcodeBytes[opByteSize];
    shortToBytes(opcode, opcodeBytes);

    // Prepare arguments
    string arguments = prepareArgs(clientInput, argType);

    // Encode the opcode and the arguments
    size_t decodedBytesSize = opByteSize + arguments.length();
    char decodedBytes[decodedBytesSize];
    inputToBytes(opcodeBytes, arguments, decodedBytes);

    // Send the the client's input to the server
    send(decodedBytes, decodedBytesSize);
}

void BGRSInputHandler::run() {
    while (!shouldTerminate) {

        // Get user input
        const short bufSize = 1024;
        char buf[bufSize];
        cin.getline(buf, bufSize);

        // Parse operation type
        string clientInput(buf);
        string operation;
        for (readIndex = 0; readIndex < clientInput.length(); readIndex++) {
            if (clientInput[readIndex] == ' ') {
                break;
            }
            operation += clientInput[readIndex];
        }

        if (operation == "ADMINREG") {
            opcode = 1;
            encodeAndSend(clientInput, "string");
            continue;
        }

        if (operation == "STUDENTREG") {
            opcode = 2;
            encodeAndSend(clientInput, "string");
            continue;
        }

        if (operation == "LOGIN") {
            opcode = 3;
            encodeAndSend(clientInput, "string");
            continue;
        }

        if (operation == "LOGOUT") {
            opcode = 4;
            encodeAndSend(clientInput, "");
            terminationCondition.wait(connectionHandlerLock);
            continue;
        }

        if (operation == "COURSEREG") {
            opcode = 5;
            encodeAndSend(clientInput, "short");
            continue;
        }

        if (operation == "KDAMCHECK") {
            opcode = 6;
            encodeAndSend(clientInput, "short");
            continue;
        }

        if (operation == "COURSESTAT") {
            opcode = 7;
            encodeAndSend(clientInput, "short");
            continue;
        }

        if (operation == "STUDENTSTAT") {
            opcode = 8;
            encodeAndSend(clientInput, "string");
            continue;
        }

        if (operation == "ISREGISTERED") {
            opcode = 9;
            encodeAndSend(clientInput, "short");
            continue;
        }

        if (operation == "UNREGISTER") {
            opcode = 10;
            encodeAndSend(clientInput, "short");
            continue;
        }

        if (operation == "MYCOURSES") {
            opcode = 11;
            encodeAndSend(clientInput, "");
            continue;
        }
    }
}

void BGRSInputHandler::terminate() {
    shouldTerminate = true;
    terminationCondition.notify_all();
}




