#include <iostream>
#include <string>
#include <vector>  
#include "Server.h"
#include "LocationMap.h"
#include "Graph.h"
#include "Dijkstra.h"

using namespace std;


struct TrafficEvent {
    string location;
    string type;
    int priority;
};

vector<TrafficEvent> trafficEvents;
int simulationVehicles = 15;


void initializeGraph(Graph& graph) { 
    graph.setNodeCount(8);
    

    graph.addEdge(0, 2, 15);
    graph.addEdge(0, 6, 20);  
    graph.addEdge(1, 7, 25);  
    graph.addEdge(2, 3, 10);  
    graph.addEdge(2, 4, 12);  
    graph.addEdge(3, 4, 8);   
    graph.addEdge(4, 5, 7);   
    graph.addEdge(5, 6, 15);
    graph.addEdge(6, 7, 30);  
    
    cout << "Graph initialized with 8 locations!\n";
}


void initializeTrafficData() {
    TrafficEvent e1 = {"F-8 Intersection", "Accident", 1};
    TrafficEvent e2 = {"Blue Area", "Construction", 3};
    TrafficEvent e3 = {"Centaurus Mall", "Congestion", 2};
    
    trafficEvents.push_back(e1);
    trafficEvents.push_back(e2);
    trafficEvents.push_back(e3);
    
    cout << "Traffic data loaded (" << trafficEvents.size() << " events)\n";
}


void processRequest(
    const string& req,
    Server& server,
    LocationMap& locations,
    Graph& graph
) {
   
    if (req == "PING") {
        server.sendLine("OK\nEND\n");
        return;
    }

 
    if (req.rfind("ROUTE:", 0) == 0) {
        string data = req.substr(6);
        size_t pos = data.find(':');

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

        int parent[20];
        int dist = Dijkstra::shortestPath(graph, srcId, dstId, parent);

        if (dist >= 999999) {
            server.sendLine("DISTANCE:-1\n");
            server.sendLine("PATH:No route\nEND\n");
            return;
        }

        string path = locations.getName(dstId);
        int cur = parent[dstId];

        while (cur != -1) {
            path = locations.getName(cur) + " → " + path;
            cur = parent[cur];
        }

        server.sendLine("DISTANCE:" + to_string(dist) + "\n");
        server.sendLine("PATH:" + path + "\nEND\n");
        
        cout << "ROUTE: " << src << " to " << dst << endl;
        cout << "  Time: " << dist << " min | Path: " << path << endl;
        
        return;
    }

  
    
    
    if (req == "MONITOR") {
        string response = "EVENTS:\n";
        for (size_t i = 0; i < trafficEvents.size(); i++) {
            response += trafficEvents[i].location + "|" + 
                        trafficEvents[i].type + "|" + 
                        to_string(trafficEvents[i].priority) + "\n";
        }
        response += "END\n";
        server.sendLine(response);
        cout << "  → Sent traffic events\n";
        return;
    }

    if (req == "SIMULATION") {
        string response = "VEHICLES:" + to_string(simulationVehicles) + "\n";
        response += "STATUS:RUNNING\nEND\n";
        server.sendLine(response);
        cout << "  → Simulation data sent\n";
        return;
    }
    
    if (req == "DASHBOARD") {
        int criticalEvents = 0;
        for (size_t i = 0; i < trafficEvents.size(); i++) {
            if (trafficEvents[i].priority == 1) criticalEvents++;
        }
        
        string response = "VEHICLES:" + to_string(simulationVehicles) + "\n";
        response += "EVENTS:" + to_string(trafficEvents.size()) + "\n";
        response += "CRITICAL:" + to_string(criticalEvents) + "\n";
        response += "CACHE:Active\nEND\n";
        
        server.sendLine(response);
        cout << "  → Dashboard stats sent\n";
        return;
    }

    server.sendLine("ERROR:UNKNOWN_COMMAND\nEND\n");
}


int main() {
    Server server;
    LocationMap locations;
    Graph graph;

    initializeGraph(graph);
    initializeTrafficData();  // ⭐ ADD THIS LINE

    if (!server.start(8080)) {
        cout << "Server failed to start on port 8080\n";
        return 1;
    }

    cout << "═══════════════════════════════════════════════\n";
    cout << "   Smart Traffic Route Optimizer - Backend\n";
    cout << "═══════════════════════════════════════════════\n";
    cout << "✓ Server running on port 8080\n";
    cout << "✓ Waiting for Java connection...\n\n";

    while (true) {
        string req = server.recvLine();
        if (req.empty()) break;

        while (!req.empty() && (req.back() == '\n' || req.back() == '\r')) {
            req.pop_back();
        }

        if (!req.empty()) {
            cout << "\n[REQUEST] " << req << endl;
            processRequest(req, server, locations, graph);
        }
    }

    server.close();
    cout << "\nServer shutdown.\n";
    return 0;
}