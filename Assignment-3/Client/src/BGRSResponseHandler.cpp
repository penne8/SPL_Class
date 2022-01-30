#include <iostream>
#include <mutex>
#include "../include/connectionHandler.h"
#include "../include/BGRSResponseHandler.h"

using namespace std;

BGRSResponseHandler::BGRSResponseHandler(ConnectionHandler &_connectionHandler,
                                         condition_variable &_terminationCondition)
        : connectionHandler(&_connectionHandler),
          terminationCondition(_terminationCondition),
          shouldTerminate(false) {
}

short bytesToShort(const char *bytesArr, int startIndex) {
    auto result = (short) ((bytesArr[startIndex] & 0xff) << 8);
    result += (short) (bytesArr[startIndex + 1] & 0xff);
    return result;
}

void BGRSResponseHandler::process(short opcode) {

    // Get the server response
    string serverResponse;
    if (!connectionHandler->getFrameAscii(serverResponse, '\0')) {
        cout << "Disconnected. Exiting...\n"
             << endl;
    }

    // Ack the message
    if (opcode >= 1 && opcode <= 11) {
        cout << "ACK " << opcode << endl;
    }

    // Handle the message accordingly
    switch (opcode) {
        case 6:                             // KDAMCHECK
        case 7:                             // COURSESTAT
        case 8:                             // STUDENTSTAT
        case 9:                             // ISREGISTERED
        case 11:                            // MYCOURSES
            cout << serverResponse << endl;
            break;
        case 4:                             // LOGOUT
            shouldTerminate = true;
            break;
        default:                            // ADMINREG | STUDENTREG | LOGIN | COURSEREG | UNREGISTER
            break;
    }
}

void BGRSResponseHandler::listen() {
    while (!shouldTerminate) {
        char bytes[opcodesSize];
        if (!connectionHandler->getBytes(bytes, opcodesSize)) {
            cout << "Disconnected. Exiting...\n"
                 << endl;
            break;
        }
        short serverOpcode = bytesToShort(bytes, 0);
        short clientOpcode = bytesToShort(bytes, 2);
        switch (serverOpcode) {
            case 12:                        // ACK
                process(clientOpcode);
                continue;
            case 13:                        // ERROR
                // If logout failed, need to wake the input thread
                if (clientOpcode == 4) {
                    terminationCondition.notify_all();
                }
                cout << "ERROR " << clientOpcode << endl;
                continue;
            default:
                continue;
        }
    }
}


