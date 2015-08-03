To use the Config file, one needs to give the fully qualified path to the configuration file

For example:
Config.load(/user/bin/example/xyz.txt, "production", "staging");

Config class uses HashMap of HashMap and returns the Config object which can be called by calling get(String key) method
to fetch the values. It will return null if can't find a valid configuration value for the given set of overrides.

Assumptions:
1. Only those configuration values are persisted which are in the override
2. Sequential processing of the configuration file ensures the override will happen on the last seen value.
3. Configuration file size with valid configuration key value pairs for a system are within the memory limit.
4. Integers and Strings at the moment are treated as same.
5. A String is defined if it starts with ' “ ' and ends with  ' ” '
6. An array is defined as the value which is not a string and has a ',' in it.
7. Default values are overridden by the overrides and if there are two overrides present for the same key than the last 
   seen one is picked up.
8. For boolean values it returns what is mentioned in the configuration file which could be yes, no, true or false, 0 or 1
9. For all the queries after pre-processing, access time is O(1);
10. Incorrect entries in the configuration files are ignored at the moment.
11. Returns null for any configuration key which cannot be found.
12. For section details without a valid section, error is thrown.