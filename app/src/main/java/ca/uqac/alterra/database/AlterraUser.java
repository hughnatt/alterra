package ca.uqac.alterra.database;

public class AlterraUser {

    private String mUID;
    private String mEmail;
    private AuthMethod mAuthMethod;


    AlterraUser(String UID, String email, AuthMethod authMethod){
        mUID = UID;
        mEmail = email;
        mAuthMethod = authMethod;
    }

    public String getUID(){
        return mUID;
    }

    public String getEmail(){
        return mEmail;
    }

    public AuthMethod getAuthMethod(){
        return mAuthMethod;
    }
}
