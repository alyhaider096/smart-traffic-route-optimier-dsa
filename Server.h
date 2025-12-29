#ifndef SERVER_H
#define SERVER_H

#include <string>
#include <winsock2.h>

using std::string;

class Server {
private:
    SOCKET serverSocket;
    SOCKET clientSocket;

public:
    Server();
    bool start(int port);
    string recvLine();
    void sendLine(const string& msg);
    void close();
};

#endif