#include "Dijkstra.h"

int Dijkstra::shortestPath(Graph &g, int src, int dest, int parent[]) {
    int n = g.getNodeCount();
    
    // Initialize arrays
    int dist[20];      // Distance from source
    bool visited[20];  // Track visited nodes
    
    // Set all distances to infinity (using 999999)
    // Set all nodes as not visited
    for (int i = 0; i < n; i++) {
        dist[i] = 999999;
        visited[i] = false;
        parent[i] = -1;
    }
    
    // Distance to source is 0
    dist[src] = 0;
    
    // Main Dijkstra loop
    for (int count = 0; count < n - 1; count++) {
        
        // Find minimum distance node that is not visited
        int minDist = 999999;
        int u = -1;
        
        for (int i = 0; i < n; i++) {
            if (!visited[i] && dist[i] < minDist) {
                minDist = dist[i];
                u = i;
            }
        }
        
        // If no node found, break
        if (u == -1) break;
        
        // Mark this node as visited
        visited[u] = true;
        
        // Update distances of neighboring nodes
        for (int v = 0; v < n; v++) {
            int weight = g.getWeight(u, v);
            
            // If edge exists and node not visited
            if (weight > 0 && !visited[v]) {
                int newDist = dist[u] + weight;
                
                // If shorter path found
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    parent[v] = u;  // Track the path
                }
            }
        }
    }
    
    // Return distance to destination
    return dist[dest];
}