#ifndef LOCATION_MAP_H
#define LOCATION_MAP_H

#include <string>
using std::string;

class LocationMap {
private:
    string names[8] = {
        "Air University (E-9)",
        "Air University (H-11)",
        "F-8",
        "Blue Area",
        "Centaurus Mall",
        "F-9 Park",
        "G-8",
        "Bahria Town"
    };

public:
    int getId(const string& name);
    string getName(int id);
};

#endif