#include "../include/Graph.h"
#include <vector>

using namespace std;

// helpers
vector<vector<int>> Graph::getEdges() const
{
    return edges;
}

vector<int> Graph::getNeighbors(int nodeInd)
{
    vector<int> neighbors;
    for (int i = 0; i < (int)edges.size(); i++)
    {
        if (edges[nodeInd][i] == 1)
        {
            neighbors.push_back(i);
        }
    }
    return neighbors;
}

void Graph::occupyNode(int nodeInd)
{
    edges[nodeInd][nodeInd] = -1;
}

bool Graph::isVirusFree(int nodeInd)
{
    return edges[nodeInd][nodeInd] == 0;
}

void Graph::isolate(int toIsolate)
{
    for (int i = 0; i < (int)edges.size(); i++)
    {
        // We won't want to change the main-diagonal
        if (i != toIsolate)
        {
            edges[i][toIsolate] = 0;
            edges[toIsolate][i] = 0;
        }
    }
}

bool Graph::shouldTerminate()
{
    bool shouldTerminate = true;
    for (int i = 0; i < (int)edges.size() && shouldTerminate; i++)
    {
        if (edges[i][i] == -1)
        {
            shouldTerminate = false;
        }
    }
    return shouldTerminate;
}

// constructor
Graph::Graph(vector<vector<int>> matrix) : edges(matrix){};

// main functions

// The graph doesn't have any loops or self-edges.
// Therefore, the main diagonal of the graph consists of 0's only.
// We're using this fact to indicate which node is infected:
// 0 = not carrying the virus, not infected
// -1 = carrying the virus, not infected
// -2 = carrying the virus, and infected
void Graph::infectNode(int nodeInd)
{
    edges[nodeInd][nodeInd] = -2;
}

bool Graph::isInfected(int nodeInd)
{
    return edges[nodeInd][nodeInd] == -2;
}