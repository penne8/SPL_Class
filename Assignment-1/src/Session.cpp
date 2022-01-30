#include "../include/Session.h"
#include "../include/Agent.h"
#include "../include/json.hpp"

#include <queue>
#include <iostream>
#include <fstream>

using json = nlohmann::json;
using namespace std;

// helpers
json jsonReader(const string &jFile)
{
    ifstream i(jFile);
    json j;
    i >> j;
    return j;
}

TreeType initTreeType(string initTree)
{
    switch (initTree[0])
    {
    case 'M':
        return MaxRank;
        break;

    case 'C':
        return Cycle;
        break;

    default: // = Root
        return Root;
    }
}

vector<Agent *> initAgents(json &config, Graph &g)
{
    vector<Agent *> agents;
    for (auto &elem : config["agents"])
    {
        Agent *aptr;
        if (elem[0] == "C")
        {
            aptr = new ContactTracer();
        }
        else
        {
            aptr = new Virus(elem[1]);
            g.occupyNode(elem[1]);
        }
        agents.push_back(aptr);
    }
    return agents;
}

void clear(vector<Agent *> &toDelete)
{
    for (Agent *&agent : toDelete)
    {
        if (agent)
        {
            delete agent;
            agent = nullptr;
        }
    }
}

vector<Agent *> copyAgents(const vector<Agent *> &otherAgents)
{
    vector<Agent *> copiedAgents;
    for (Agent *agent : otherAgents)
    {
        copiedAgents.push_back(agent->clone());
    }
    return copiedAgents;
}

Graph Session::getGraph() const
{
    return g;
}

int Session::getCurrCycle() const
{
    return currCycle;
}

void Session::output()
{
    json output;
    vector<int> infected;
    vector<vector<int>> edges = g.getEdges();

    for (int i = 0; i < (int)edges.size(); i++)
    {
        if (edges[i][i] != 0)
        {
            infected.push_back(i);
            edges[i][i] = 0;
        }
    }
    output["graph"] = edges;
    output["infected"] = infected;

    // Output file
    ofstream file("./output.json");
    file << output;
}

// constructor
Session::Session(const string &path) : config(jsonReader(path)),
                                       g(Graph(config["graph"])),
                                       treeType(initTreeType(config["tree"])),
                                       agents(initAgents(config, g)),
                                       infectedNodes(),
                                       currCycle(0) {}

// destructor
Session::~Session()
{
    clear(agents);
}

// copy constructor
//// init new object using existing object's data
Session::Session(const Session &other) : config(other.config),
                                         g(other.g),
                                         treeType(other.treeType),
                                         agents(copyAgents(other.agents)),
                                         infectedNodes(other.infectedNodes),
                                         currCycle(other.currCycle) {}

// assignment operator
//// change existing object data using existing object's data
Session &Session::operator=(const Session &other)
{
    // Prevent self-copy issue
    if (this != &other)
    {
        // Delete own resource before allocating new resource
        clear(agents);

        // Copy the stack head data that does not require resource allocation
        config = other.config;
        g = other.g;
        treeType = other.treeType;
        infectedNodes = other.infectedNodes;
        currCycle = other.currCycle;

        // Allocate new resource using the copy helper function
        agents = copyAgents(other.agents);
    }
    return *this;
}

// move constructor
//// init new object using rvalue's data (&& is a reference to rvalue)
Session::Session(Session &&other) : config(other.config),
                                    g(other.g),
                                    treeType(other.treeType),
                                    agents(other.agents),
                                    infectedNodes(other.infectedNodes),
                                    currCycle(other.currCycle)
{
    // First, "steal" the rValue's resources, avoiding creating a redundant copy of the them on the heap
    // Then, set rValue to not point to its resources anymore, so that destructor won't delete them
    other.agents.clear();
}

// move assignment operator
//// change existing object data using rvalue's data
Session &Session::operator=(Session &&other)
{
    if (this != &other)
    {
        // Delete own resource before allocating new resource
        clear(agents);

        // "Steal" rValue's resources, thus avoids creating a redundant copy of the them on the heap
        config = other.config;
        g = other.g;
        treeType = other.treeType;
        agents = other.agents;
        infectedNodes = other.infectedNodes;
        currCycle = other.currCycle;

        // Set rValue to not point to its resources anymore, so that destructor won't delete them
        other.agents.clear();
    }

    // Return a pointer to own for chained assignments
    return *this;
}

// main functions
void Session::simulate()
{
    bool shouldTerminate = false;
    while (!shouldTerminate)
    {
        vector<Agent *> cycleAgents = agents; // Copies the state of the agents before the cycle begins
        for (Agent *&agent : cycleAgents)     // Act will be activated only by agents that existed before the cycle began
        {
            agent->act(*this);
        }
        currCycle++;
        shouldTerminate = g.shouldTerminate(); // Checking if the termination conditions are met
    }
    output(); // Generating the output JSON file
}

void Session::addAgent(const Agent &agent)
{
    agents.push_back(agent.clone());
}

void Session::setGraph(const Graph &graph)
{
    g = graph;
}

void Session::enqueueInfected(int newInfected)
{
    if (!g.isInfected(newInfected))
    {
        infectedNodes.push(newInfected);
        g.infectNode(newInfected);
    }
}

int Session::dequeueInfected()
{
    int infectedNode = -1;
    if (!infectedNodes.empty())
    {
        infectedNode = infectedNodes.front();
        infectedNodes.pop();
    }
    return infectedNode;
}

TreeType Session::getTreeType() const
{
    return treeType;
};