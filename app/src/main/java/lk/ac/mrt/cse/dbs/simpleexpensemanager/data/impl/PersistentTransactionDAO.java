package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private static final String DATABASE_NAME = "170454J.db";
    private static final String TABLE_NAME = "transactions";
    private static final String COL_1 = "Tid";
    private static final String COL_2 = "accountno";
    private static final String COL_3 = "date";
    private static final String COL_4 = "type";
    private static final String COL_5 = "amount";

    private List<Transaction> transactions;
    public PersistentTransactionDAO(Context context) {
        super(context,DATABASE_NAME, null,1);
        transactions = new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + TABLE_NAME+
                "(Tid varchar primary key autoincrement, accountno varchar,date String,type text,amount double)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(database);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        String accountno = transaction.getAccountNo();
        Date dates = transaction.getDate();

        String Date = dates.toString();
        ExpenseType types = transaction.getExpenseType();
        String strType = types.toString();
        Double amounts = transaction.getAmount();

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues CV = new ContentValues();
        CV.put("accountno", accountno);
        CV.put("amount", amounts);
        CV.put("type",strType);
        CV.put("date", Date);

        database.insert(TABLE_NAME, null, CV);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        transactions.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.rawQuery( " select * from " + TABLE_NAME, null );

        result.moveToFirst();

        while(!result.isAfterLast()){

            String accountNo = result.getString(result.getColumnIndex(COL_2));
            Double amount = result.getDouble(result.getColumnIndex(COL_5));
            String transType = result.getString(result.getColumnIndex(COL_4));

            ExpenseType type = ExpenseType.valueOf(transType);
            String date = result.getString(result.getColumnIndex(COL_3));
            Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(date);

            transactions.add(new Transaction(date1,accountNo,type,amount));
            result.moveToNext();
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        transactions.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.rawQuery( " select * from " + TABLE_NAME, null );

        result.moveToFirst();

        while(!result.isAfterLast()){

            String accountNo = result.getString(result.getColumnIndex(COL_2));
            Double amount = result.getDouble(result.getColumnIndex(COL_5));
            String transType = result.getString(result.getColumnIndex(COL_4));

            ExpenseType type = ExpenseType.valueOf(transType);
            String date = result.getString(result.getColumnIndex(COL_3));
            Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(date);

            transactions.add(new Transaction(date1,accountNo,type,amount));
            result.moveToNext();
        }
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }
}
