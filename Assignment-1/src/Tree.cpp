#include "../include/Tree.h"
#include "../include/Graph.h"
#include "../include/Session.h"
#include <vector>
#include <queue>

using namespace std;

// helpers
Tree *CycleTree::clone() const
{
    return new CycleTree(*this);
}

Tree *MaxRankTree::clone() const
{
    return new MaxRankTree(*this);
}

Tree *RootTree::clone() const
{
    return new RootTree(*this);
}

void clear(vector<Tree *> &toDelete)
{
    for (Tree *&tree : toDelete)
    {
        if (tree)
        {
            delete tree;
            tree = nullptr;
        }
    }
}

vector<Tree *> copyChildren(const vector<Tree *> &otherChildren)
{
    vector<Tree *> copiedChildren;
    for (Tree *tree : otherChildren)
    {
        copiedChildren.push_back(tree->clone());
    }
    return copiedChildren;
}

Tree *initTree(TreeType treeType, int rootLabel, int currCycle)
{
    switch (treeType)
    {
    case MaxRank:
        return new MaxRankTree(rootLabel);
        break;

    case Cycle:
        return new CycleTree(rootLabel, currCycle);
        break;

    default: // = Root
        return new RootTree(rootLabel);
    }
}

vector<Tree *> Tree::getChildren() const
{
    return children;
}

int Tree::getNodeInd() const
{
    return node;
}

int Tree::getMaxRankNodeInd() const
{
    return maxRankNodeInd;
}

int Tree::getMaxRankNodeSize() const
{
    return maxRankNodeSize;
}

void Tree::setParent(Tree &other)
{
    parent = &other;
}

void Tree::maintainMaxRank()
{
    if (parent)
    {
        // init params
        int parentRank = (int)parent->getChildren().size();
        int desiredNode = maxRankNodeInd;     // child max rank node index
        int desiredMaxRank = maxRankNodeSize; // child max rank

        // Compare the parent's own rank after the child addition with the the child's max rank
        // In case of a tie, the node with the smallest depth in the tree would be picked
        if (parentRank >= desiredMaxRank)
        {
            desiredNode = parent->node;
            desiredMaxRank = parentRank;
        }

        // desiredMaxRank holds the greater between the parent's own rank and the child max rank
        // Compare the desired max rank with the parent's max rank
        int parentMaxRank = parent->getMaxRankNodeSize();
        if (desiredMaxRank > parentMaxRank) // If true, changes the parent's max rank accordingly
        {
            parent->setMaxRank(desiredNode, desiredMaxRank);
        }
        // If the parent's max rank is the biggest of them all, no change is needed
    }
}

void Tree::setMaxRank(int desiredNode, int desiredMaxRank)
{
    // Set the tree max rank node index and size according to the maintainMaxRank if checks
    maxRankNodeInd = desiredNode;
    maxRankNodeSize = desiredMaxRank;

    // Diffuse the max rank changes to the parent
    if (parent)
    {
        int parentMaxRank = parent->getMaxRankNodeSize();
        if (maxRankNodeSize > parentMaxRank)
        {
            parent->setMaxRank(desiredNode, desiredMaxRank);
        }
    }
}

// Tree // constructor
Tree::Tree(int rootLabel) : node(rootLabel), children(), parent(nullptr), maxRankNodeInd(rootLabel), maxRankNodeSize(0) {}

// Tree // copy constructor
Tree::Tree(const Tree &other) : node(other.node), children(copyChildren(other.children)), parent(other.parent), maxRankNodeInd(other.maxRankNodeInd), maxRankNodeSize(other.maxRankNodeSize) {}

// Tree // destructor
Tree::~Tree()
{
    clear(children);
}

// Tree // assignment operator
Tree &Tree::operator=(const Tree &other)
{
    if (this != &other)
    {
        clear(children);

        node = other.node;
        children = copyChildren(other.children);
        parent = other.parent;
        maxRankNodeInd = other.maxRankNodeInd;
        maxRankNodeSize = other.maxRankNodeSize;
    }
    return *this;
}

// Tree // move constructor
Tree::Tree(Tree &&other) : node(other.node), children(other.children), parent(other.parent), maxRankNodeInd(other.maxRankNodeInd), maxRankNodeSize(other.maxRankNodeSize)
{
    other.children.clear();
}

// Tree // move assignment operator
Tree &Tree::operator=(Tree &&other)
{
    if (this != &other)
    {
        clear(children);

        node = other.node;
        children = other.children;
        parent = other.parent;
        maxRankNodeInd = other.maxRankNodeInd;
        maxRankNodeSize = other.maxRankNodeSize;

        other.children.clear();
    }
    return *this;
}

// Cycle Tree // constructor
CycleTree::CycleTree(int rootLabel, int currCycle) : Tree(rootLabel), currCycle(currCycle) {}

// MaxRankTree // constructor
MaxRankTree::MaxRankTree(int rootLabel) : Tree(rootLabel) {}

// RootTree // constructor
RootTree::RootTree(int rootLabel) : Tree(rootLabel) {}

// main functions

void Tree::addChild(const Tree &child)
{
    Tree *newChild = child.clone();
    newChild->setParent(*this);

    // Add the new child at the right index in the vector
    bool inserted = false;
    for (int i = 0; i < (int)children.size() && !inserted; i++)
    {
        if (children[i]->node > newChild->node)
        {
            children.insert(children.begin() + i, newChild);
            inserted = true;
        }
    }

    // children vector might be empty or have only smaller indices
    if (!inserted)
    {
        children.push_back(newChild);
    }

    newChild->maintainMaxRank();
}

Tree *Tree::createTree(const Session &session, int rootLabel)
{
    // base params
    TreeType treeType = session.getTreeType();
    int currCycle = session.getCurrCycle();
    vector<vector<int>> edges = session.getGraph().getEdges();
    int numOfVertices = edges.size();

    // BFS params
    vector<bool> visited(numOfVertices, false); // Make sure that a node is visited no more than once
    queue<Tree *> discoveredNodes;              // Keep track of newly discovered nodes

    // Create source node and set it as visited
    Tree *sourceNode = initTree(treeType, rootLabel, currCycle);
    discoveredNodes.push(sourceNode);
    visited[rootLabel] = true;

    // As long as the queue isn't empty, there are still nodes to expand upon
    while (!discoveredNodes.empty())
    {
        // Extract oldest discovered node from the queue
        Tree *curr = discoveredNodes.front();
        discoveredNodes.pop();
        int currIndex = curr->node;

        // Go over the neighbors of the extracted node
        for (int i = 0; i < numOfVertices; i++)
        {
            // Scan for unvisited neighbors
            if (edges[currIndex][i] == 1 && (!visited[i]))
            {
                // Create neighbor node and set it as visited
                Tree *newChild = initTree(treeType, i, currCycle);
                discoveredNodes.push(newChild);
                visited[i] = true;

                // Add the neighbor node as a child of the node that discovered it
                newChild->setParent(*curr);
                curr->children.push_back(newChild);

                // If this is a MaxRankTree we need ot maintain the rank
                if (treeType == MaxRank)
                {
                    newChild->maintainMaxRank();
                }
            }
        }
    }
    return sourceNode; // This is the root of the BFS tree
}

int CycleTree::traceTree()
{
    Tree *curr = this;
    for (int i = 1; i <= currCycle; i++)
    {
        // if the trip is less than currCycle nodes long, returns the last node in it
        if (curr->getChildren().empty())
        {
            return curr->getNodeInd();
        }
        else
        {
            // Traversing the tree while picking always the left-mostchild
            curr = curr->getChildren().front();
        }
    }
    return curr->getNodeInd();
}

int MaxRankTree::traceTree()
{
    return getMaxRankNodeInd();
}

int RootTree::traceTree()
{
    return getNodeInd();
}