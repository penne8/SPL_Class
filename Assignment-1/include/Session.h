#ifndef SESSION_H_
#define SESSION_H_

#include <vector>
#include <string>
#include "../include/Graph.h"
#include <queue>
#include "../include/json.hpp"

using namespace std;
using json = nlohmann::json;

class Agent;

enum TreeType
{
  Cycle,
  MaxRank,
  Root
};

class Session
{
public:
  // constructor
  Session(const string &path);

  // Rule of five
  virtual ~Session();                       // destructor
  Session(const Session &other);            // copy constructor
  Session &operator=(const Session &other); // assignment operator
  Session(Session &&other);                 // move constructor
  Session &operator=(Session &&other);      // move assignment operator

  // main functions
  void simulate();
  void addAgent(const Agent &agent);
  void setGraph(const Graph &graph);
  void enqueueInfected(int);
  int dequeueInfected();
  TreeType getTreeType() const;

  // helpers
  Graph getGraph() const;
  int getCurrCycle() const;
  void output(); // Generate the output JSON file

private:
  json config;
  Graph g;
  TreeType treeType;
  vector<Agent *> agents;
  queue<int> infectedNodes;
  int currCycle;
};

#endif