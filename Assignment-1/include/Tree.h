#ifndef TREE_H_
#define TREE_H_

#include <vector>

using namespace std;

class Session;

class Tree
{
public:
    // constructor
    Tree(int rootLabel);

    // Rule of five
    virtual ~Tree();                    // destructor
    Tree(const Tree &other);            // copy constructor
    Tree &operator=(const Tree &other); // assignment operator
    Tree(Tree &&other);                 // move constructor
    Tree &operator=(Tree &&other);      // move assignment operator

    // main functions
    void addChild(const Tree &child);
    static Tree *createTree(const Session &session, int rootLabel);
    virtual int traceTree() = 0;

    // helpers
    virtual Tree *clone() const = 0;
    vector<Tree *> getChildren() const;
    int getNodeInd() const;
    int getMaxRankNodeInd() const;
    int getMaxRankNodeSize() const;
    void setParent(Tree &other);
    void maintainMaxRank();
    void setMaxRank(int otherMaxRankNodeInd, int otherMaxRankNodeSize);

private:
    int node;
    vector<Tree *> children;
    Tree *parent;
    int maxRankNodeInd;
    int maxRankNodeSize;
};

class CycleTree : public Tree
{
public:
    // constructor
    CycleTree(int rootLabel, int currCycle);

    // main functions
    virtual int traceTree();

    // helpers
    virtual Tree *clone() const;

private:
    int currCycle;
};

class MaxRankTree : public Tree
{
public:
    // constructor
    MaxRankTree(int rootLabel);

    // main functions
    virtual int traceTree();

    // helpers
    virtual Tree *clone() const;
};

class RootTree : public Tree
{
public:
    // constructor
    RootTree(int rootLabel);

    // main functions
    virtual int traceTree();

    // helpers
    virtual Tree *clone() const;
};

#endif