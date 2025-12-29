🚦 Smart Traffic Route Optimizer

A desktop-based route optimization system using core Data Structures & Algorithms



📌 Overview

Smart Traffic Route Optimizer is a Java Swing desktop application connected to a C++ backend that calculates the optimal route between locations using Dijkstra’s shortest path algorithm.

The project demonstrates practical usage of graphs, hashing, and priority queues with a clean frontend–backend architecture.

✨ Features

Interactive Java Swing UI

Source & destination route planning

Shortest path calculation using Dijkstra

Graph-based city map representation

Location mapping using hashing

C++ backend with socket-based communication

Traffic visualization & simulation panel

🧠 Data Structures & Algorithms Used

Graph (Adjacency Matrix)

Dijkstra’s Shortest Path Algorithm

HashMap (Location → Node mapping)

Priority Queue (Min-Heap)

Arrays for distance & path tracking

Socket programming (Java ↔ C++)

🏗️ Architecture
Java Swing UI
   │
   │  Socket Communication
   ▼
C++ Backend
   ├── Graph
   ├── Dijkstra
   ├── LocationMap
   └── Server

📁 Project Structure
SmartTrafficOptimizer/
│
├── java/
│   ├── ui/
│   │   ├── MainFrame.java
│   │   ├── RouteSelectionPanel.java
│   │   ├── RouteResultPanel.java
│   │   ├── SimulationPanel.java
│   │   └── TrafficMonitorPanel.java
│   └── utils/
│       └── NetworkClient.java
│
├── c++/
│   ├── main.cpp
│   ├── Server.h / Server.cpp
│   ├── Graph.h / Graph.cpp
│   ├── Dijkstra.h / Dijkstra.cpp
│   ├── LocationMap.h / LocationMap.cpp
│   └── backend.exe
│
└── README.md

🚀 How to Run
Run C++ Backend
cd c++
g++ main.cpp Server.cpp Graph.cpp Dijkstra.cpp LocationMap.cpp -o backend -lws2_32
backend

Run Java Frontend

Open Java project in NetBeans / IntelliJ

Run MainFrame.java

Select source & destination

Click Compute Optimal Route

🖥️ Screens Included

Dashboard (animated)

Route planning panel

Route analysis results

Traffic monitor

Traffic simulation view

👨‍💻 Authors

Hadia Abbas
Ali Haider
BSCS – Pakistan 🇵🇰

make a submission version

Just say the word.
