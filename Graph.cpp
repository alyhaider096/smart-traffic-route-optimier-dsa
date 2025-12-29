#include "Graph.h"

Graph::Graph() {
    nodes = 0;

    for (int i = 0; i < MAX; i++) {
        for (int j = 0; j < MAX; j++) {
            adj[i][j] = -1;
        }
    }
}

void Graph::setNodeCount(int n) {
    nodes = n;
}

void Graph::addEdge(int u, int v, int w) {
    adj[u][v] = w;
    adj[v][u] = w;
}

int Graph::getNodeCount() const {
    return nodes;
}

int Graph::getWeight(int u, int v) const {
    return adj[u][v];
}