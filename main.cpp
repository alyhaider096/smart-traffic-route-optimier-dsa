#include <iostream>
#include <string>
#include "Server.h"
#include "LocationMap.h"
#include "Graph.h"
#include "Dijkstra.h"

using namespace std;

// ---------------- INITIALIZE GRAPH ----------------
void initializeGraph(Graph& graph) {
    // Set number of nodes (we have 8 locations)
    graph.setNodeCount(8);
    
    // Location indices:
    // 0 = Air University (E-9)
    // 1 = Air University (H-11)
    // 2 = F-8
    // 3 = Blue Area
    // 4 = Centaurus Mall
    // 5 = F-9 Park
    // 6 = G-8
    // 7 = Bahria Town
    
    // Add edges (bidirectional roads) with distances in KM
    // Format: addEdge(from, to, distance_km)
    
    // E-9 connections
    graph.addEdge(0, 5, 3);   // E-9 to F-9 Park (3 km)
    graph.addEdge(0, 2, 5);   // E-9 to F-8 (5 km)
    
    // H-11 connections
    graph.addEdge(1, 5, 8);   // H-11 to F-9 Park (8 km)
    graph.addEdge(1, 7, 12);  // H-11 to Bahria Town (12 km)
    
    // F-8 connections
    graph.addEdge(2, 3, 2);   // F-8 to Blue Area (2 km)
    graph.addEdge(2, 5, 4);   // F-8 to F-9 Park (4 km)
    
    // Blue Area connections
    graph.addEdge(3, 4, 3);   // Blue Area to Centaurus (3 km)
    graph.addEdge(3, 6, 5);   // Blue Area to G-8 (5 km)
    
    // Centaurus Mall connections
    graph.addEdge(4, 5, 4);   // Centaurus to F-9 Park (4 km)
    graph.addEdge(4, 6, 3);   // Centaurus to G-8 (3 km)
    
    // F-9 Park connections
    graph.addEdge(5, 6, 6);   // F-9 Park to G-8 (6 km)
    
    // G-8 connections
    graph.addEdge(6, 7, 15);  // G-8 to Bahria Town (15 km)
    
    cout << "Graph initialized with 8 locations and connections!\n";
}

// ---------------- PROCESS REQUEST ----------------
void processRequest(
    const string& req,
    Server& server,
    LocationMap& locations,
    Graph& graph
) {
    // ---------- PING ----------
    if (req == "PING") {
        server.sendLine("OK\nEND\n");
        return;
    }

    // ---------- ROUTE ----------
    if (req.rfind("ROUTE:", 0) == 0) {
        // Format: ROUTE:Source:Destination
        string data = req.substr(6);
        int pos = data.find(':');

        if (pos == string::npos) {
            server.sendLine("ERROR:BAD_FORMAT\nEND\n");
            return;
        }

        string src = data.substr(0, pos);
        string dst = data.substr(pos + 1);

        int srcId = locations.getId(src);
        int dstId = locations.getId(dst);

        if (srcId == -1 || dstId == -1) {
            server.sendLine("ERROR:UNKNOWN_LOCATION\nEND\n");
            return;
        }

        // Calculate shortest path using Dijkstra
        int parent[20];
        int dist = Dijkstra::shortestPath(graph, srcId, dstId, parent);

        // Check if path exists
        if (dist >= 999999) {
            server.sendLine("ERROR:NO_PATH_FOUND\nEND\n");
            return;
        }

        // ---------- BUILD PATH ----------
        string path = locations.getName(dstId);
        int cur = parent[dstId];

        while (cur != -1) {
            path = locations.getName(cur) + " -> " + path;
            cur = parent[cur];
        }

        // Calculate estimated time (assuming 40 km/h average speed)
        // Time in minutes = (distance_km / speed_kmh) * 60
        int timeMinutes = (dist * 60) / 40;

        server.sendLine("PATH:" + path + "\n");
        server.sendLine("DISTANCE:" + to_string(dist) + "\n");
        server.sendLine("TIME:" + to_string(timeMinutes) + "\nEND\n");
        return;
    }

    server.sendLine("ERROR:UNKNOWN_COMMAND\nEND\n");
}

// ---------------- MAIN ----------------
int main() {
    Server server;
    LocationMap locations;
    Graph graph;

    // Initialize the graph with all locations and roads
    initializeGraph(graph);

    if (!server.start(8080)) {
        cout << "Server failed to start\n";
        return 1;
    }

    cout << "Smart Traffic Backend Ready...\n";

    while (true) {
        string req = server.recvLine();
        if (req.empty()) break;

        // Trim newline / carriage return
        while (!req.empty() &&
              (req.back() == '\n' || req.back() == '\r')) {
            req.pop_back();
        }

        cout << "JAVA -> " << req << endl;
        processRequest(req, server, locations, graph);
    }

    server.close();
    return 0;
}