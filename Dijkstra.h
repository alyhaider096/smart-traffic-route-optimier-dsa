#ifndef DIJKSTRA_H
#define DIJKSTRA_H

#include "Graph.h"

class Dijkstra {
public:
    static int shortestPath(Graph &g, int src, int dest, int parent[]);
};

#endif