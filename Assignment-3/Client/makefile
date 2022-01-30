CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -pthread

all: BGRSclient
	g++ -o bin/BGRSclient bin/BGRSclient.o bin/connectionHandler.o bin/BGRSInputHandler.o bin/BGRSResponseHandler.o $(LDFLAGS)

BGRSclient: bin/BGRSclient.o bin/connectionHandler.o bin/BGRSInputHandler.o bin/BGRSResponseHandler.o

bin/BGRSclient.o: src/BGRSclient.cpp
	g++ $(CFLAGS) -o bin/BGRSclient.o src/BGRSclient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGRSInputHandler.o: src/BGRSInputHandler.cpp
	g++ $(CFLAGS) -o bin/BGRSInputHandler.o src/BGRSInputHandler.cpp

bin/BGRSResponseHandler.o: src/BGRSResponseHandler.cpp
	g++ $(CFLAGS) -o bin/BGRSResponseHandler.o src/BGRSResponseHandler.cpp
	
.PHONY: clean
clean:
	rm -f bin/*
