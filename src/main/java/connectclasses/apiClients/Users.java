package connectclasses.apiClients;

import connectclasses.common.ConfigClass;

public class Users implements ConfigClass {
    private String user;
    private String host;
    private String dbName;
    private String password;
    private Integer count;
    private String companyName;

    public Users(String user, String host, String dbName, String password, Integer count, String companyName) {
        this.user = user;
        this.host = host;
        this.dbName = dbName;
        this.password = password;
        this.count = count;
        this.companyName = companyName;
    }
    public Users(){ }

    @Override
    public String getUser() {

        return this.user;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public String getDatabase() {
        return this.dbName;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Integer setId() {
        return this.count;
    }

    @Override
    public String getCompanyName() {
        return this.companyName;
    }
}
