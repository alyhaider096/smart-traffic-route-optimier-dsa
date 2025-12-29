#include "LocationMap.h"

// Get ID from location name
int LocationMap::getId(const string& name) {
    for (int i = 0; i < 8; i++) {
        if (names[i] == name)
            return i;
    }
    return -1;  // Not found
}

// Get name from location ID
string LocationMap::getName(int id) {
    if (id < 0 || id >= 8)
        return "UNKNOWN";
    return names[id];
}