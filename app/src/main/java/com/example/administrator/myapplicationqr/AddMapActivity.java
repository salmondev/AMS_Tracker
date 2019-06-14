package com.example.administrator.myapplicationqr;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class AddMapActivity extends AppCompatActivity {

    EditText editText;
    ImageButton img;
    Bitmap bitmap;

    private final int SELECT_IMAGE = 1;
    private final int STORAGE_PERMISSION_CODE = 2;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_map);

        editText = (EditText)findViewById(R.id.et_mapname);
    }

    public void onAddPhoto(View v){
        if (ContextCompat.checkSelfPermission(AddMapActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent itn = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(itn,SELECT_IMAGE);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent itn = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(itn,SELECT_IMAGE);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            String [] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String pathfile = cursor.getString(columnIndex);
            cursor.close();

            img = (ImageButton)findViewById(R.id.btn_addPhoto);
            bitmap = BitmapFactory.decodeFile(pathfile);

            img.setImageBitmap(bitmap);
            i++;
        }
    }

    public void onSubmit(View v){
        if( TextUtils.isEmpty( editText.getText().toString() ) ) {
            editText.setError("Please fill the map name.");
        }
        if(i > 0 && TextUtils.isEmpty( editText.getText().toString() ) == false ){
            bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] bytesArray = bos.toByteArray();

            //MainActivity.mydb.onInsert_MAP_TABLE(bytesArray,editText.getText().toString());

            Intent itn = new Intent(this,MainActivity.class);
            startActivity(itn);
        }
        else{
            Toast.makeText(this, "Please add map photo and map name.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }
}
