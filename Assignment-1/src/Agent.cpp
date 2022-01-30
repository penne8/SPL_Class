#include "../include/Agent.h"
#include "../include/Session.h"
#include "../include/Graph.h"
#include "../include/Tree.h"

#include <vector>

using namespace std;

// constructors
Agent::Agent() {} // Agent

ContactTracer::ContactTracer() : Agent() {} // Contact Tracer

Virus::Virus(int nodeInd) : Agent(), nodeInd(nodeInd) {} // Virus

// helpers
Agent *ContactTracer::clone() const // Contact Tracer
{
    return new ContactTracer(*this);
}

Agent *Virus::clone() const // Virus
{
    return new Virus(*this);
}

// main functions
void ContactTracer::act(Session &session)
{
    // Deque an infected node from infection queue
    int infectedNode = session.dequeueInfected();

    if (infectedNode != -1)
    {
        // Create the shortest path tree from the dequeued node using BFS
        Tree *bfs = Tree::createTree(session, infectedNode);

        // Obtain an index of a node in the graph according to the different tree type rules
        int toIsolate = bfs->traceTree();

        // We have no use for the tree anymore, so we must delete it
        delete bfs;
        bfs = nullptr;

        // Remove all the edges from the graph which are incident with the obtained node
        Graph g = session.getGraph();
        g.isolate(toIsolate);
        session.setGraph(g);
    }
}

void Virus::act(Session &session)
{
    // Infects the node it occupies, if it is not already infected
    session.enqueueInfected(nodeInd);

    // Spreads itself in each iteration to one of the virus-free neighbors of the host node, in ascending order
    Graph g = session.getGraph();
    vector<int> neighbors = g.getNeighbors(nodeInd);
    if (!neighbors.empty())
    {
        for (int &neighbor : neighbors)
        {
            if (g.isVirusFree(neighbor))
            {
                session.addAgent(Virus(neighbor));
                g.occupyNode(neighbor);
                session.setGraph(g);
                break;
            }
        }
    }
}