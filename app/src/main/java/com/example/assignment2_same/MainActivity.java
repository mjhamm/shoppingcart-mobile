package com.example.assignment2_same;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements Dialog.NoticeDialogListener {

    private static final String TAG = "DATABASE ";

    DatabaseHelper mDatabaseHelper;

    protected Button createBudgetButton;
    protected Button goShoppingButton;
    protected Button addItemButton;
    protected static EditText budgetTextBox;
    protected static EditText itemPriceTextBox;
    protected static EditText productTextBox;
    protected static TextView totalBudgetTextView;
    protected static TextView quantityLabel;
    protected static TextView priorityLabel;
    protected static Spinner quantityComboBox;
    protected static Spinner priorityComboBox;
    protected static SwipeMenuListView mainListView;
    protected static ArrayList<Item> item = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabaseHelper = new DatabaseHelper(this);
        //Log.i(TAG, "ITEMS: " + mDatabaseHelper.loadItems());

        populateItemsInDatabase();

        //-----------------------------------------------------------------------------------------------------------------------------------------
        createBudgetButton = findViewById(R.id.createBudgetButton);
        goShoppingButton = findViewById(R.id.goShoppingButton);
        addItemButton = findViewById(R.id.addItemButton);
        budgetTextBox = findViewById(R.id.budgetTextBox);
        totalBudgetTextView = findViewById(R.id.totalBudgetTextView);
        itemPriceTextBox = findViewById(R.id.itemPriceTextBox);
        productTextBox = findViewById(R.id.productTextBox);
        quantityLabel = findViewById(R.id.quantityLabel);
        priorityLabel = findViewById(R.id.priorityLabel);
        quantityComboBox = findViewById(R.id.quantityComboBox);
        priorityComboBox = findViewById(R.id.priorityComboBox);

        //-----------------------------------------------------------------------------------------------------------------------------------------

        //ADAPTER FOR MAIN LIST VIEW
        ArrayAdapter<Item> itemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, item);
        mainListView = findViewById(R.id.mainListView);
        mainListView.setAdapter(itemAdapter);

        //CREATES PRIORITY AND QUANTITY SPINNERS
        //-----------------------------------------------------------------------------------------------------------------------------------------
        Integer[] quantitySpinner = new Integer[] {1,2,3,4,5,6,7,8,9,10};
        Integer[] prioritySpinner = new Integer[] {1,2,3};
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, quantitySpinner);
        ArrayAdapter<Integer> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, prioritySpinner);
        quantityComboBox.setAdapter(quantityAdapter);
        priorityComboBox.setAdapter(priorityAdapter);
        quantityComboBox.setSelection(0);
        priorityComboBox.setSelection(2);
        //-----------------------------------------------------------------------------------------------------------------------------------------

        //TOAST
        //-----------------------------------------------------------------------------------------------------------------------------------------
        Toast budgetErrorEmpty = Toast.makeText(getApplicationContext(),"Please Enter A Budget.", Toast.LENGTH_SHORT);
        Toast budgetErrorZero = Toast.makeText(getApplicationContext(),"Your Budget Needs To Be Greater Than Zero.", Toast.LENGTH_SHORT);
        Toast productErrorToast = Toast.makeText(getApplicationContext(), "Please Enter Product/Price.", Toast.LENGTH_SHORT);
        Toast shoppingBudgetToast = Toast.makeText(getApplicationContext(), "You Must Enter A Budget To Go Shopping.", Toast.LENGTH_SHORT);
        Toast shoppingListToast = Toast.makeText(getApplicationContext(), "You Must Have Items In Your Cart To Go Shopping.", Toast.LENGTH_SHORT);
        //-----------------------------------------------------------------------------------------------------------------------------------------


        //CREATE BUDGET BUTTON LISTENER
        createBudgetButton.setOnClickListener((createBudget) -> {
            if (budgetTextBox.getText().toString().isEmpty()) {
                budgetErrorEmpty.show();
            }
            else if(budgetToDouble() <= 0) {
                budgetErrorZero.show();
            }
            else {
                totalBudgetTextView.setText(String.format(Locale.US, "%2s", "$" + budgetTextBox.getText().toString()));
                budgetTextBox.setText("");
            }
        });

        //ADD ITEM BUTTON LISTENER
        addItemButton.setOnClickListener((addItem) -> {
            if (productTextBox.getText().toString().isEmpty()) {
                productErrorToast.show();
            }
            else if (itemPriceTextBox.getText().toString().isEmpty()) {
                productErrorToast.show();
            }
            else if (item.size() == 0) {
                addItemToList();
                itemAdapter.notifyDataSetChanged();
                productTextBox.setText("");
                itemPriceTextBox.setText("");
                quantityComboBox.setSelection(0);
                priorityComboBox.setSelection(2);
            }
            else {
                checkForDuplicates();
                itemAdapter.notifyDataSetChanged();
                productTextBox.setText("");
                itemPriceTextBox.setText("");
                quantityComboBox.setSelection(0);
                priorityComboBox.setSelection(2);
            }
        });

        //GO SHOPPING BUTTON LISTENER
        goShoppingButton.setOnClickListener((goShopping) -> {
            if (totalBudgetTextView.getText().toString().isEmpty()) {
                shoppingBudgetToast.show();
            }
            else if(item.size() == 0) {
                shoppingListToast.show();
            }
            else {
                confirmGoShopping();
            }
        });

        //CREATES SWIPE MENU FOR LIST
        //-----------------------------------------------------------------------------------------------------------------------------------------
        SwipeMenuCreator creator = menu -> {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(200);
            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete_black_24dp);
            // add to menu
            menu.addMenuItem(deleteItem);
        };

        mainListView.setMenuCreator(creator);

        mainListView.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    Toast removeItemToast = Toast.makeText(getApplicationContext(), "Removed Item: " + item.get(position).getProduct(), Toast.LENGTH_SHORT);
                    item.remove(position);
                    itemAdapter.notifyDataSetChanged();
                    removeItemToast.show();
                    break;
            }
            // false : close the menu; true : not close the menu
            return false;
        });

        mainListView.setCloseInterpolator(new BounceInterpolator());
        //-----------------------------------------------------------------------------------------------------------------------------------------

    }

    //DIALOG FOR GO SHOPPING
    //-----------------------------------------------------------------------------------------------------------------------------------------
    public void showNoticeDialog() {
        DialogFragment dialog = new Dialog();
        dialog.show(getSupportFragmentManager(), "Dialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        deleteItemsInDatabase();
        getSortedItemByPriority();
        for (int x = 0; x < item.size(); x++) {
            double budgetDoubleShopping = (totalBudgetDouble() - Double.parseDouble(item.get(x).getPrice()));
            String budgetSetter = ("$" + String.format(Locale.US, "%.2f", budgetDoubleShopping));
            if (item.get(x).getPriority().equals(1)) {
                if (budgetDoubleShopping < 0) {
                    mainListView.getChildAt(x).setBackgroundColor(Color.parseColor("#ff0f0f"));
                }
                else {
                    totalBudgetTextView.setText(budgetSetter);
                    mainListView.getChildAt(x).setBackgroundColor(Color.parseColor("#06d117"));
                    addItemsToDatabase(item.get(x));
                }
            }
            else if (item.get(x).getPriority().equals(2)) {
                if (budgetDoubleShopping < 0) {
                    mainListView.getChildAt(x).setBackgroundColor(Color.parseColor("#ff0f0f"));
                }
                else {
                    totalBudgetTextView.setText(budgetSetter);
                    mainListView.getChildAt(x).setBackgroundColor(Color.parseColor("#06d117"));
                    addItemsToDatabase(item.get(x));
                }
            }
            else if (item.get(x).getPriority().equals(3)) {
                if (budgetDoubleShopping < 0) {
                    mainListView.getChildAt(x).setBackgroundColor(Color.parseColor("#ff0f0f"));
                }
                else {
                    totalBudgetTextView.setText(budgetSetter);
                    mainListView.getChildAt(x).setBackgroundColor(Color.parseColor("#06d117"));
                    addItemsToDatabase(item.get(x));
                }
            }
            else {
                mainListView.getChildAt(x).setBackgroundColor(Color.parseColor("#ff0f0f"));
            }
        }
    }
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    //CREATES NEW DIALOG FROM DIALOG CLASS
    public void confirmGoShopping() {
        DialogFragment newFragment = new Dialog();
        newFragment.show(getSupportFragmentManager(), "goShopping");
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------

    //GETS ITEM OBJECT AND ADDS IT TO MAIN LIST
    public void addItemToList() {
        Item newItem = new Item(getProductName(),priorityToInt(),quantityToInt(),getProductPrice(), getProductName(),getProductName());
        item.add(newItem);
    }

    //CHECK FOR DUPLICATES
    public void checkForDuplicates() {
        Toast itemInListToast = Toast.makeText(getApplicationContext(), "Item Is Already In Your Cart.", Toast.LENGTH_SHORT);
        boolean foundIt = false;
        int i;
        int j;

        firstLabel:
        for (i = 0; i < item.size(); i++) {
            for (j = 0; j < item.size(); j++) {
                if (item.get(i).getProduct().equalsIgnoreCase(productTextBox.getText().toString())) {
                    foundIt = true;
                    break firstLabel;
                }
            }
        }
        if (foundIt) {
            itemInListToast.show();
        }
        else {
            addItemToList();
        }
    }

    //PRODUCT NAME FROM PRODUCT TEXT BOX AND CAPITALIZE FIRST LETTER
    public static String getProductName() {
        return productTextBox.getText().toString().substring(0,1).toUpperCase() + productTextBox.getText().toString().substring(1);
    }

    //PRODUCT PRICE FROM PRICE TEXT BOX
    public static Double priceToInt() {
        double priceInt = Double.parseDouble(itemPriceTextBox.getText().toString());
        return priceInt;
    }

    //QUANTITY COMBOBOX TO INTEGER
    public static Integer quantityToInt() {
        Integer quantityInt = Integer.parseInt(quantityComboBox.getSelectedItem().toString());
        return quantityInt;
    }

    //PRIORITY COMBOBOX TO INTEGER
    public static Integer priorityToInt() {
        Integer priorityInt = Integer.parseInt(priorityComboBox.getSelectedItem().toString());
        return priorityInt;
    }

    //MULTIPLY ITEM PRICE AND ITEM QUANTITY AND CONVERT TO STRING
    public static String getProductPrice() {
        return String.format(Locale.US,"%.2f", (quantityToInt() * priceToInt()));
    }

    //CONVERT BUDGET FROM STRING TO A DOUBLE
    public Double budgetToDouble() {
        Double budgetDouble = Double.parseDouble(budgetTextBox.getText().toString());
        return budgetDouble;
    }

    public StringBuilder budgetToString() {
        StringBuilder budgetString = new StringBuilder(totalBudgetTextView.getText().toString());
        budgetString.deleteCharAt(0);
        return budgetString;
    }

    public Double totalBudgetDouble() {
        Double totalBudget = Double.parseDouble(budgetToString().toString());
        return totalBudget;
    }

    //SORTS ITEM ARRAY BY PRIORITY
    public ArrayList<Item> getSortedItemByPriority() {
        Collections.sort(item);
        return item;
    }

    public void addItemsToDatabase(Item newEntry) {
        boolean insertData = mDatabaseHelper.addItem(newEntry);

        if (insertData) {
            toastMessage("Data Successfully Added");
        } else {
            toastMessage("Something Went Wrong");
        }
    }

    public void populateItemsInDatabase() {
        Cursor data = mDatabaseHelper.loadItems();
        ArrayList<String> databaseItems = new ArrayList<>();
        while(data.moveToNext()) {
            databaseItems.add(data.getString(0));
        }
        toastMessage("Items Bought Last Time:\n" + databaseItems);
    }

    public void deleteItemsInDatabase() {
        mDatabaseHelper.deleteItems();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
