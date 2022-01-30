#include <iostream>
#include "Tree.h"
#include <stdio.h>
#include <stdlib.h>

using namespace std;

bool isSorted()
{
    bool passed = true;

    MaxRankTree t1(1);
    for (int i = 0; i < 10000; i++)
    {
        t1.addChild(MaxRankTree(rand()));
    }
    vector<Tree *> children = t1.getChildren();
    for (int i = 1; i < (int)children.size() && passed; i++)
    {
        if (children[i]->getNodeInd() < children[i - 1]->getNodeInd())
        {
            passed = false;
        }
    }

    RootTree t2(1);
    for (int i = 0; i < 10000; i++)
    {
        t2.addChild(RootTree(rand()));
    }
    children = t2.getChildren();
    for (int i = 1; i < (int)children.size() && passed; i++)
    {
        if (children[i]->getNodeInd() < children[i - 1]->getNodeInd())
        {
            passed = false;
        }
    }

    CycleTree t3(1, 0);
    for (int i = 0; i < 10000; i++)
    {
        t3.addChild(CycleTree(rand(), 0));
    }
    children = t3.getChildren();
    for (int i = 1; i < (int)children.size() && passed; i++)
    {
        if (children[i]->getNodeInd() < children[i - 1]->getNodeInd())
        {
            passed = false;
        }
    }

    return passed;
}

bool isRankMaintained()
{
    bool passed = true;

    MaxRankTree t(1);
    MaxRankTree t2(2);
    t2.addChild(MaxRankTree(3));
    t2.addChild(MaxRankTree(4));
    t2.addChild(MaxRankTree(5));
    if (t.traceTree() != 1)
    {
        passed = false;
    }

    t.addChild(t2);
    if (t.traceTree() != 2)
    {
        passed = false;
    }

    t.addChild(MaxRankTree(6));
    if (t.traceTree() != 2)
    {
        passed = false;
    }
    MaxRankTree t7(7);
    t7.addChild(MaxRankTree(8));
    t7.addChild(MaxRankTree(9));
    t7.addChild(MaxRankTree(10));
    t7.addChild(MaxRankTree(11));
    t.addChild(t7);
    if (t.traceTree() != 7)
    {
        passed = false;
    }
    
    return passed;
}
int main(int argc, char **argv)
{
    cout << "\\ ~Tests results: ~//" << endl;
    cout << "Tree children sorting: " << isSorted() << endl;
    cout << "Tree rank maintenance: " << isRankMaintained() << endl;

    return 0;
}
