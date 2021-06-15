package com.example.lesson12_sqlite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.lesson12_sqlite.adapter.ListItem;
import com.example.lesson12_sqlite.db.MyConstants;
import com.example.lesson12_sqlite.db.MyDbManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditActivity extends AppCompatActivity {
    private ImageView imNewImage;
    private ConstraintLayout imageContainer;
    private ImageButton imEditImage, imDeleteImage;
    private EditText edTitle, edDesc;
    private MyDbManager myDbManager;
    private static final int PICK_IMAGE_CODE = 123;
    private String tempUri = "empty";
    private boolean isEditState = true;
    private ListItem item;
    private DatabaseReference dRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
        getMyIntents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_act_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        startActivityForResult(chooser, PICK_IMAGE_CODE);
        imageContainer.setVisibility(View.VISIBLE);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null) {
            tempUri = data.getData().toString();
            imNewImage.setImageURI(data.getData());
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    private void init() {
        myDbManager = new MyDbManager(this);
        edTitle = findViewById(R.id.edTitle);
        edDesc = findViewById(R.id.edDesc);
        imageContainer = findViewById(R.id.imageContainer);
        imNewImage = findViewById(R.id.imNewImage);
        imEditImage = findViewById(R.id.imEditImage);
        imDeleteImage = findViewById(R.id.imDeleteImage);
    }

    public void getMyIntents() {
        Intent i = getIntent();
        if (i != null) {
            item = (ListItem) i.getSerializableExtra(MyConstants.LIST_ITEM_INTENT);
            isEditState = i.getBooleanExtra(MyConstants.EDIT_STATE, true);

            if (!isEditState) {
                edTitle.setText(item.getTitle());
                edDesc.setText(item.getDesc());

                if (!item.getUri().equals("empty")) {
                    tempUri = item.getUri();
                    imageContainer.setVisibility(View.VISIBLE);
                    imNewImage.setImageURI(Uri.parse(item.getUri()));
                }
            }
        }
    }

    public void onClickSave(View view) {
        saveNote();
        String title = edTitle.getText().toString();
        String desc = edDesc.getText().toString();
        String uri = tempUri;
        if (title.equals("") || desc.equals("")) {
            Toast.makeText(this, R.string.empty_text, Toast.LENGTH_LONG).show();
        } else {
            if (isEditState) {
                myDbManager.insertToDb(title, desc, uri);
            } else {
                myDbManager.updateItem(title, desc, uri, item.getId());
            }
            myDbManager.closeDb();
            finish();
        }
    }

    public void onClickDeleteImage(View view) {
            imageContainer.setVisibility(View.GONE);
            tempUri = item.setUri("");
    }

    public void onClickChooseImage(View view) {
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.setType("image/*");
        startActivityForResult(chooser, PICK_IMAGE_CODE);
    }

    private void saveNote() {
        dRef = FirebaseDatabase.getInstance().getReference(edTitle.getText().toString());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getUid() != null) {
            String key = dRef.push().getKey();
            NewNote note = new NewNote();
            note.setImageId(tempUri);
            note.setTitle(edTitle.getText().toString());
            note.setDescription(edDesc.getText().toString());
            note.setKey(key);
            if (key != null) dRef.child(mAuth.getUid()).child(key).setValue(note);
        }
    }
}