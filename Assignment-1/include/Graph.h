#ifndef GRAPH_H_
#define GRAPH_H_

#include <vector>

using namespace std;

class Graph
{
public:
    // constructor
    Graph(vector<vector<int>> matrix);

    // main functions
    void infectNode(int nodeInd);
    bool isInfected(int nodeInd);

    // helpers
    vector<vector<int>> getEdges() const;
    vector<int> getNeighbors(int nodeInd);
    void occupyNode(int nodeInd); // Marks a node as NOT-virus-free
    bool isVirusFree(int nodeInd);
    void isolate(int toIsolate); // Isolate a node from its neighbors
    bool shouldTerminate();      // Checks if the termination conditions are met

private:
    vector<vector<int>> edges;
};

#endif