package ca.uqac.alterra.database;

public class AlterraCloud {

    private static AlterraDatabase mDatabase;
    private static AlterraAuth mAuth;
    private static AlterraStorage mStorage;

    public static AlterraDatabase getDatabaseInstance(){
        if (mDatabase == null){
            mDatabase = new AlterraFirebase();
        }
        return mDatabase;
    }

    public static AlterraAuth getAuthInstance(){
        if (mAuth == null){
            mAuth = new AlterraFirebase();
        }
        return mAuth;
    }

    public static AlterraStorage getStorageInstance(){
        if (mStorage == null){
            mStorage = new AlterraFirebase();
        }
        return mStorage;
    }

    private AlterraCloud(){}

}
