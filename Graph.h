#ifndef GRAPH_H
#define GRAPH_H

class Graph {
private:
    static const int MAX = 20;
    int adj[MAX][MAX];
    int nodes;

public:
    Graph();
    void setNodeCount(int n);
    void addEdge(int u, int v, int w);
    int getNodeCount() const;
    int getWeight(int u, int v) const;
};

#endif
