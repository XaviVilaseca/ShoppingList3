package com.example.xavi.shoppinglist3;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShoppingList3Activity extends AppCompatActivity {

    private static final String FILENAME = "shopping_list.txt";
    private static final int MAX_BYTES = 8000;

    private ArrayList<ShoppingItem> itemlist;
    private ShoppingListAdapter adapter;
    private ListView list;
    private Button addButton;
    private EditText editItem;

    private void WriteItemList() {

        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            for (int i = 0; i < itemlist.size(); i++) {
                ShoppingItem it = itemlist.get(i);
                String line = String.format("%s;%b\n", it.getText(), it.isCheck());
                fos.write(line.getBytes());
            }
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("xavi", "writeItemList: FileNotFoundException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("xavi", "writeItemList: IOException");
            Toast.makeText(this, R.string.cannot_write, Toast.LENGTH_LONG).show();
        }
    }

    private void ReadItemList() {
        itemlist = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(FILENAME);
            byte[] buffer = new byte[MAX_BYTES];
            int nread = fis.read(buffer);
            if (nread > 0){
            String content = new String(buffer, 0, nread);
            String[] lines = content.split("\n");
            for (String line : lines) {
                String[] parts = line.split(";");
                itemlist.add(new ShoppingItem(parts[0], parts[1].equals("true")));
            }
            }
            fis.close();
        } catch (FileNotFoundException e) {
            Log.e("xavi", "ReadItemList: FileNotFoundException");
        } catch (IOException e) {
            Log.e("xavi", "ReadItemList: IOException");
            Toast.makeText(this, R.string.cannot_read, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        WriteItemList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list3);

        list = (ListView) findViewById(R.id.list);
        addButton = (Button) findViewById(R.id.button_add);
        editItem = (EditText) findViewById(R.id.editItem);

        itemlist = new ArrayList<>();

        ReadItemList();

        adapter = new ShoppingListAdapter(this, android.R.layout.simple_list_item_1, itemlist);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        editItem.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                addItem();
                return false;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                itemlist.get(pos).toggleCheck();
                adapter.notifyDataSetChanged();
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> list, View item, int pos, long id) {
                maybeRemobeItem(pos);
                return false;
            }
        });

        list.setAdapter(adapter);

    }

    private void maybeRemobeItem(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String fmt = getResources().getString(R.string.confirm_message);
        builder.setMessage(String.format(fmt, itemlist.get(pos).getText()));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemlist.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }

    private void addItem() {


        String item_text = editItem.getText().toString();
        if (!item_text.isEmpty()) {
            itemlist.add(new ShoppingItem(item_text));
            adapter.notifyDataSetChanged();
            editItem.setText("");
        }
        list.smoothScrollToPosition(itemlist.size() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clear_checked:
                clearChecked();
                return true;

            case R.id.clear_all:
                clearAll();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_clear_all);
        builder.setPositiveButton(R.string.clear_all, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemlist.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();
    }
    private void clearChecked() {
        int i =0;
        while (i < itemlist.size()) {
            if (itemlist.get(i).isCheck()) {
                itemlist.remove(i);
            } else {
                i++;
            }
        }
        adapter.notifyDataSetChanged();
    }
}