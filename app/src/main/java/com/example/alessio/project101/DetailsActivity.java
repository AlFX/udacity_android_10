package com.example.alessio.project101;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import static com.example.alessio.project101.R.string.email;
import static com.example.alessio.project101.R.string.phone;

public class DetailsActivity extends AppCompatActivity {

    private static final int PERMISSIONS_EXTERNAL_STORAGE = 1;
    private InventoryDbHelper dbHelper;
    EditText nameEdit;
    EditText priceEdit;
    EditText quantEdit;
    EditText vendorNameEdit;
    EditText vendorPhoneEdit;
    EditText vendorEmailEdit;
    long currItemId;
    ImageButton lessQuant;
    ImageButton moreQuant;
    Button imageBtn;
    ImageView imageView;
    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean itemChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEdit = (EditText) findViewById(R.id.name_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        quantEdit = (EditText) findViewById(R.id.quant_edit);
        vendorNameEdit = (EditText) findViewById(R.id.vendor_name_edit);
        vendorPhoneEdit = (EditText) findViewById(R.id.vendor_phone_edit);
        vendorEmailEdit = (EditText) findViewById(R.id.vendor_email_edit);
        lessQuant = (ImageButton) findViewById(R.id.less_quant);
        moreQuant = (ImageButton) findViewById(R.id.more_quant);
        imageBtn = (Button) findViewById(R.id.select_img);
        imageView = (ImageView) findViewById(R.id.img_view);

        dbHelper = new InventoryDbHelper(this);
        currItemId = getIntent().getLongExtra("itemId", 0);

        if (currItemId == 0) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            addValues(currItemId);
        }

        lessQuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minusOneQuant();
                itemChanged = true;
            }
        });

        moreQuant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plusOneQuant();
                itemChanged = true;
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryImageSelector();
                itemChanged = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!itemChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* close the current activity */
                        finish();
                    }
                };
        /* unsaved change */
        unsavedChangesNotif(discardButtonClickListener);
    }

    /* notify user he did not save changes */
    private void unsavedChangesNotif(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void minusOneQuant() {
        String beforeStr = quantEdit.getText().toString();
        int before;
        if (beforeStr.isEmpty()) {
            return;
        } else if (beforeStr.equals("0")) {
            return;
        } else {
            before = Integer.parseInt(beforeStr);
            quantEdit.setText(String.valueOf(before - 1));
        }
    }

    private void plusOneQuant() {
        String beforeStr = quantEdit.getText().toString();
        int before;
        if (beforeStr.isEmpty()) {
            before = 0;
        } else {
            before = Integer.parseInt(beforeStr);
        }
        quantEdit.setText(String.valueOf(before + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currItemId == 0) {
            MenuItem deleteOneItemMenuItem = menu.findItem(R.id.delete_item);
            MenuItem deleteAllMenuItem = menu.findItem(R.id.delete_all);
            MenuItem orderMenuItem = menu.findItem(R.id.order);
            deleteOneItemMenuItem.setVisible(false);
            deleteAllMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                /* save item in database */
                if (!addItemToDb()) {
                    /*user clicked button*/
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if (!itemChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /*clicked Discard button = go to parent activity*/
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                /* unsaved changes*/
                unsavedChangesNotif(discardButtonClickListener);
                return true;
            case R.id.order:
                showConfirm();
                return true;
            case R.id.delete_item:
                confirmDel(currItemId);
                return true;
            case R.id.delete_all:
                confirmDel(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addItemToDb() {
        boolean isAllOk = true;
        if (!checkValue(nameEdit, "name")) {
            isAllOk = false;
        }
        if (!checkValue(priceEdit, "price")) {
            isAllOk = false;
        }
        if (!checkValue(quantEdit, "quantity")) {
            isAllOk = false;
        }
        if (!checkValue(vendorNameEdit, "vendor name")) {
            isAllOk = false;
        }
        if (!checkValue(vendorPhoneEdit, "vendor phone")) {
            isAllOk = false;
        }
        if (!checkValue(vendorEmailEdit, "vendor email")) {
            isAllOk = false;
        }
        if (actualUri == null && currItemId == 0) {
            isAllOk = false;
            imageBtn.setError("Missing image");
        }
        if (!isAllOk) {
            return false;
        }

        if (currItemId == 0) {
            Item item = new Item(
                    nameEdit.getText().toString().trim(),
                    priceEdit.getText().toString().trim(),
                    Integer.parseInt(quantEdit.getText().toString().trim()),
                    vendorNameEdit.getText().toString().trim(),
                    vendorPhoneEdit.getText().toString().trim(),
                    vendorEmailEdit.getText().toString().trim(),
                    actualUri.toString());
            dbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(quantEdit.getText().toString().trim());
            dbHelper.updateItem(currItemId, quantity);
        }
        return true;
    }

    private boolean checkValue(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void addValues(long itemId) {
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();
        nameEdit.setText(cursor.getString(cursor.getColumnIndex(StuffContract.StockEntry.COL_NAME)));
        priceEdit.setText(cursor.getString(cursor.getColumnIndex(StuffContract.StockEntry.COL_PRICE)));
        quantEdit.setText(cursor.getString(cursor.getColumnIndex(StuffContract.StockEntry.COL_QUANT)));
        vendorNameEdit.setText(cursor.getString(cursor.getColumnIndex(StuffContract.StockEntry.COL_VENDOR_NAME)));
        vendorPhoneEdit.setText(cursor.getString(cursor.getColumnIndex(StuffContract.StockEntry.COL_VENDOR_PHONE)));
        vendorEmailEdit.setText(cursor.getString(cursor.getColumnIndex(StuffContract.StockEntry.COL_VENDOR_EMAIL)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(StuffContract.StockEntry.COL_IMAGE))));
        nameEdit.setEnabled(false);
        priceEdit.setEnabled(false);
        vendorNameEdit.setEnabled(false);
        vendorPhoneEdit.setEnabled(false);
        vendorEmailEdit.setEnabled(false);
        imageBtn.setEnabled(false);
    }

    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /* intent to phone*/
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + vendorPhoneEdit.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /* intent to email*/
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + vendorEmailEdit.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order");
                String bodyMessage = "We need more " +
                        nameEdit.getText().toString().trim() +
                        "!!!";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int deleteAllRows() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(StuffContract.StockEntry.TAB_NAME, null, null);
    }

    private int deleteRow(long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = StuffContract.StockEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(itemId) };
        int rowsDeleted = database.delete(
                StuffContract.StockEntry.TAB_NAME, selection, selectionArgs);
        return rowsDeleted;
    }

    private void confirmDel(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (itemId == 0) {
                    deleteAllRows();
                } else {
                    deleteRow(itemId);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_EXTERNAL_STORAGE: {
                 /* return empty array when request is cancelled*/
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}
