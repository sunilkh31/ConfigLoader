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

******************
Configuration Loader
Every large software project has its share of configuration files to control settings,
execution, etc. Let’s contemplate a config file format that looks a lot like standard PHP .ini
files, but with a few tweaks. A config file will appear as follows:
[common]
basic_size_limit = 26214400
student_size_limit = 52428800
paid_users_size_limit = 2147483648
path = /srv/var/tmp/
path<itscript> = /srv/tmp/
[ftp]
name = “hello there, ftp uploading”
path = /tmp/
path<production> = /srv/var/tmp/
path<staging> = /srv/uploads/
path<ubuntu> = /etc/var/uploads
enabled = no
; This is a comment
[http]
name = “http uploading”
path = /tmp/
path<production> = /srv/var/tmp/
path<staging> = /srv/uploads/; This is another comment
params = array,of,values
Where
● “[group]” denotes the start of a group of related config options
● “setting = value“ denotes a standard setting name and associated default value
● “setting<override> = value2” denotes the value for the setting if the given override is
enabled
● If multiple enabled overrides are defined on a setting, the one defined last will have
priority.

Objective:
To write a function that parses this format and returns an object that can be
queried for “group.variable” or “group”.
* Note that overrides can be passed as String array
A sample in java would be:
Config conf = Config.load(“/path/to/config_file”, new
String(){“ubuntu”, “production” })
> Config.get(“common.paid_users_size_limit”);
# returns 2147483648
> Config.get("ftp.name”);
# returns “hello there, ftp uploading”
> Config.get("http.params”)
# returns [“array”, “of”, “values”]
> Config.get("ftp.lastname”);
# returns null
> Config.get("ftp.enabled”);
# returns false (permitted bool values are “yes”, “no”, “true”,
“false”, 1, 0)
> Config.get("ftp.path”);
# returns “/etc/var/uploads”
> Config.get("ftp
# returns a symbolized hashmap:
{
“name” == “http uploading”,
“path” == “/etc/var/uploads”,
“enabled” == false
}

Design Considerations
1. Config.load() will be called at boot time, and thus should be as fast as possible. Conf
files can get quite lengthy there can be an arbitrary number of groups and number of
settings within each group.
2. Config.get() will be queried throughout the program’s execution, so each query should
be very fast as well.
3. Certain queries will be made very often (thousands of times), others pretty rarely.
4. If the conf file is not well formed, it is acceptable to print an error and exit from within
Config.load(). Once the object is returned, however, it is not permissible to exit or
crash no matter what the query is. Returning NULL/NIL is acceptable.




