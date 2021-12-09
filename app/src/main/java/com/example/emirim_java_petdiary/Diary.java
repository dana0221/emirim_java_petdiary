package com.example.emirim_java_petdiary;

import android.Manifest;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Diary extends AppCompatActivity {
    EditText title, note;
    ImageView imgv;
    Button btnSave, btnPhoto, btnGallrey;
    CheckBox checkWalk, checkPlay, checkFeed;
    Data dbHelper;
    Date nowDate;
    int feed = 0, play = 0, walk = 0;
    SimpleDateFormat ft = new SimpleDateFormat("yyyy년 MM월 dd일");


    final private static String TAG = "태그명";

    final static int TAKE_PICTURE = 1;

    String mCurrentPhotoPaht;
    final static int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        btnSave = findViewById(R.id.btn_save);
        btnPhoto = findViewById(R.id.btn_photo);
        btnGallrey = findViewById(R.id.btn_gallery);
        title = findViewById(R.id.diary_title);
        note = findViewById(R.id.diary_note);
        checkWalk = findViewById(R.id.check_walk);
        checkPlay = findViewById(R.id.check_play);
        checkFeed = findViewById(R.id.check_feed);
        imgv = findViewById(R.id.imgv);

        btnGallrey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "권한 설정 완료");
            }else {
                Log.d(TAG, "권한 설정 요청");
            }

            ActivityCompat.requestPermissions(Diary.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btn_photo:
                        Intent cameralIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameralIntent, TAKE_PICTURE);
                        break;
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dbHelper.insert(title.getText().toString(), note.getText().toString(), feed, walk, play, nowDate.toString());
                Toast.makeText(getApplicationContext(), "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DiaryList.class);
                intent.putExtra("제목", title.getText().toString());
                startActivity(intent);
            }
        });

        checkWalk.setOnClickListener(checkListener);
        checkPlay.setOnClickListener(checkListener);
        checkFeed.setOnClickListener(checkListener);
    }

    View.OnClickListener checkListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.check_walk:
                    feed = 1;
                    Toast.makeText(getApplicationContext(), "산책하기를 완료했습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.check_play:
                    play = 1;
                    Toast.makeText(getApplicationContext(), "놀아주기 완료했습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.check_feed:
                    feed = 1;
                    Toast.makeText(getApplicationContext(), "밥주기를 완료했습니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");

        if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permission : " + permissions[0] + "was" + grantResults[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    imgv.setImageBitmap(img);
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == 0 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgv.setImageBitmap(imageBitmap);
        }


        try { switch (requestCode) {
            case REQUEST_TAKE_PHOTO: {
                if (resultCode == RESULT_OK) {
                    File file = new File(mCurrentPhotoPaht);
                    Bitmap bitmap;
                    if (Build.VERSION.SDK_INT >= 29) {
                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                        try {
                            bitmap = ImageDecoder.decodeBitmap(source);
                            if (bitmap != null) {
                                imgv.setImageBitmap(bitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                            if (bitmap != null) {
                                imgv.setImageBitmap(bitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } break;
            }
        }

        } catch (Exception error) {
            error.printStackTrace();
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );

        mCurrentPhotoPaht = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent(){
        Intent tackPictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(tackPictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            }catch (IOException ex){}

            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.emirim_java_petdiary", photoFile);
                tackPictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(tackPictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    public void clickGetBt(View view) {     // Get버튼 클릭 시   SharedPreferences에 값 불러오기.
        SharedPreferences sharedPreferences= getSharedPreferences("test", MODE_PRIVATE);    // test 이름의 기본모드 설정, 만약 test key값이 있다면 해당 값을 불러옴.
        String titleText = sharedPreferences.getString("title","");
        String noteText = sharedPreferences.getString("note","");
        title.setText(titleText);    // TextView에 SharedPreferences에 저장되어있던 값 찍기.
        note.setText(noteText);    // TextView에 SharedPreferences에 저장되어있던 값 찍기.
        Toast.makeText(this, "불러오기 하였습니다..", Toast.LENGTH_SHORT).show();
    }
}