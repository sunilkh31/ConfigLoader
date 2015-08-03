package com.configloader.main;

/**
 * Sample usage of the Config class;
 */
public class MainTest {

    /**
     * Config class requires fully qualified path to the configuration file
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String[] overrides = { "itscript" };
        Config config = Config.load(
                "/Users/Guest/Documents/workspace/ConfigLoader/src/com/twitter/configloader/config.txt", overrides);
        System.out.println(config.get("common.path"));
        System.out.println(config.get("common.paid_users_size_limit"));
        System.out.println(config.get("ftp.name"));
        System.out.println(((String[]) config.get("http.params"))[0]);
        System.out.println(config.get("ftp.lastname"));
        System.out.println(config.get("ftp.enabled"));
        System.out.println(config.get("ftp.path"));
        System.out.println(config.get("http"));
    }
}
