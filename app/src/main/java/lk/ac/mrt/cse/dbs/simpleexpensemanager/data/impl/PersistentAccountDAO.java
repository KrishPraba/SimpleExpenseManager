package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    public static final String DATABASE_NAME = "170454J.db";
    public static final String TABLE_NAME = "account";
    public static final String COLUMN_1 = "accountno";
    public static final String COLUMN_2 = "bankname";
    public static final String COLUMN_3 = "accountholdername";
    public static final String COLUMN_4 = "balance";

    public PersistentAccountDAO(Context context) {
        super(context, DATABASE_NAME , null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + TABLE_NAME+
                "(accountno varchar primary key , bankname text,accountholdername text,balance double)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(database);
    }
    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> accountNumber = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result =  database.rawQuery( "select * from "+TABLE_NAME, null );
        result.moveToFirst();

        while(result.isAfterLast() == false){
            accountNumber.add(result.getString(result.getColumnIndex(COLUMN_1)));
            result.moveToNext();
        }
        return accountNumber;


    }

    @Override
    public List<Account> getAccountsList()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result =  database.rawQuery( "select * from "+TABLE_NAME, null );
        ArrayList<Account> Accounts = new ArrayList<>();

        result.moveToFirst();

        while(result.isAfterLast() == false){
            String accountNo = result.getString(result.getColumnIndex(COLUMN_1));
            String bankName = result.getString(result.getColumnIndex(COLUMN_2));
            String accountHolderName = result.getString(result.getColumnIndex(COLUMN_3));
            Double balance = result.getDouble(result.getColumnIndex(COLUMN_4));

            Accounts.add(new Account(accountNo,bankName,accountHolderName,balance));
            result.moveToNext();
        }
        return Accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result =  database.rawQuery( "select * from "+TABLE_NAME+" where accountno="+accountNo+"", null );

        String accountno = result.getString(result.getColumnIndex(COLUMN_1));
        String bankName = result.getString(result.getColumnIndex(COLUMN_2));
        String accountHolderName = result.getString(result.getColumnIndex(COLUMN_3));
        Double balance = result.getDouble(result.getColumnIndex(COLUMN_4));

        return  new Account(accountno,bankName,accountHolderName,balance);


    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cV = new ContentValues();

        String accountno = account.getAccountNo();
        String bankname = account.getBankName();
        String accountholdername = account.getAccountHolderName();
        Double balance = account.getBalance();

        cV.put("accountno", accountno);
        cV.put("bankname", bankname);
        cV.put("accountholdername", accountholdername);
        cV.put("balance", balance);

        database.insert(TABLE_NAME, null, cV);

    }

    @Override
    public void removeAccount(String accountNumber) throws InvalidAccountException {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME,
                "accountno = ? ",
                new String[] { accountNumber});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
//        if (!accounts.containsKey(accountNo)) {
//            String msg = "Account " + accountNo + " is invalid.";
//            throw new InvalidAccountException(msg);
//        }
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor result =  database.rawQuery( "select balance from "+TABLE_NAME+" where accountno="+accountNo+"", null );

        Double balance = result.getDouble(result.getColumnIndex(COLUMN_4));
        Double balanceNew;

        ContentValues cV = new ContentValues();


//        Account account = accounts.get(accountNo);
        // specific implementation based on the transaction type
        switch (expenseType) {
            case EXPENSE:
                balanceNew = balance - amount;
                cV.put("balance", balance);
//                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                balanceNew = balance + amount;
                cV.put("balance", balance);
//                account.setBalance(account.getBalance() + amount);
                break;
        }
//        accounts.put(accountNo, account);
    }
}
