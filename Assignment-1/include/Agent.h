#ifndef AGENT_H_
#define AGENT_H_

#include "../include/Session.h"
#include <vector>

class Agent
{
public:
    // agent constructor
    Agent();

    // agent virtual destructor
    virtual ~Agent() = default;

    // main functions
    virtual void act(Session &session) = 0;

    // helpers
    virtual Agent *clone() const = 0;
};

class ContactTracer : public Agent
{
public:
    // ContactTracer constructor
    ContactTracer();

    // main functions
    virtual void act(Session &session);

    // helpers
    virtual Agent *clone() const;
};

class Virus : public Agent
{
public:
    // Virus constructor
    Virus(int nodeInd);

    // main functions
    virtual void act(Session &session);

    // helpers
    virtual Agent *clone() const;

private:
    const int nodeInd;
};

#endif