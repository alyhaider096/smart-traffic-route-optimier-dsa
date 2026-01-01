#include "Server.h"
#include <iostream>

#pragma comment(lib, "ws2_32.lib")

Server::Server() {
    serverSocket = INVALID_SOCKET;
    clientSocket = INVALID_SOCKET;
}

bool Server::start(int port) {
    WSADATA wsa;
    WSAStartup(MAKEWORD(2,2), &wsa);

    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == INVALID_SOCKET) return false;

    sockaddr_in addr{};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(port);
    addr.sin_addr.s_addr = INADDR_ANY;

    bind(serverSocket, (sockaddr*)&addr, sizeof(addr));
    listen(serverSocket, 1);

    std::cout << "Backend waiting for Java...\n";
    clientSocket = accept(serverSocket, NULL, NULL);

    return clientSocket != INVALID_SOCKET;
}

string Server::recvLine() {
    char buffer[1024];
    int len = recv(clientSocket, buffer, 1023, 0);
    if (len <= 0) return "";

    buffer[len] = '\0';
    return string(buffer);
}

void Server::sendLine(const string& msg) {
    send(clientSocket, msg.c_str(), msg.length(), 0);
}

void Server::close() {
    closesocket(clientSocket);
    closesocket(serverSocket);
    WSACleanup();
}

