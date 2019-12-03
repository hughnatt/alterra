package ca.uqac.alterra.database;

public class AlterraCloud {

    private static AlterraDatabase mDatabase;
    private static AlterraAuth mAuth;
    private static AlterraStorage mStorage;

    public static AlterraDatabase getDatabaseInstance(){
        if (mDatabase == null){
            mDatabase = AlterraFirebase.getInstance();
        }
        return mDatabase;
    }

    public static AlterraAuth getAuthInstance(){
        if (mAuth == null){
            mAuth = AlterraFirebase.getInstance();
        }
        return mAuth;
    }

    public static AlterraStorage getStorageInstance(){
        if (mStorage == null){
            mStorage = AlterraFirebase.getInstance();
        }
        return mStorage;
    }

    private AlterraCloud(){}

}
