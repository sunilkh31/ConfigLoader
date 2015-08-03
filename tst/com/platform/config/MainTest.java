package com.platform.config;

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
        AppConfig appConfig = AppConfig.load("/Users/StarLord/Documents/workspace/ConfigLoader/tst/config.txt");
        System.out.println(appConfig.get("common.path"));
        System.out.println(appConfig.get("common.paid_users_size_limit"));
        System.out.println(appConfig.get("ftp.name"));
        System.out.println(((String[]) appConfig.get("http.params"))[0]);
        System.out.println(appConfig.get("ftp.lastname"));
        System.out.println(appConfig.get("ftp.enabled"));
        System.out.println(appConfig.get("ftp.path"));
        System.out.println(appConfig.get("http"));
    }
}
