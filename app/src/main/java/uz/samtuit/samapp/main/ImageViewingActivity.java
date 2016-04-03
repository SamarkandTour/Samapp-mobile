package uz.samtuit.samapp.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.senab.photoview.PhotoViewAttacher;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;

public class ImageViewingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageView;
    private Bundle extras;
    private String ImageTitle, FilePath;
    PhotoViewAttacher mAttacher;
    private Bitmap _bitmap;
    private Boolean isCorrect = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewing);
        toolbar = (Toolbar)findViewById(R.id.actionbar);

        extras = getIntent().getExtras();
        imageView = (ImageView)findViewById(R.id.image_holder);
        ImageTitle = extras.getString("name");
        FilePath = extras.getString("photo");
        toolbar.setTitle(ImageTitle);
        setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(Color.parseColor("#33000000"));
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try{
            isCorrect = true;
            String encodedBytes = FileUtil.fileReadFromExternalDir(this, FilePath);
            _bitmap = BitmapUtil.decodeBase64Image(encodedBytes);
            Glide.with(this).load(Base64.decode(encodedBytes,Base64.DEFAULT)).asBitmap().into(imageView);
            mAttacher = new PhotoViewAttacher(imageView);
        } catch (Exception ex) {
            ex.printStackTrace();
            isCorrect = false;
            Glide.with(this).load(R.drawable.no_image).into(imageView);
        }



        mAttacher.setOnMatrixChangeListener(new PhotoViewAttacher.OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {

            }
        });
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {

            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
        mAttacher.setOnSingleFlingListener(new PhotoViewAttacher.OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_save_image){
            if(isCorrect){
                try {
                    Toast.makeText(this, "Image Saved to SamTour folder in Pictures", Toast.LENGTH_LONG).show();
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    _bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    String folderPath = Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator + "SamTour";
                    File folder = new File(folderPath);
                    if(!folder.exists()){
                        File photosFolder = new File(folderPath);
                        photosFolder.mkdirs();
                    }

                    File f = new File(folderPath, ImageTitle + ".jpg");
                    f.createNewFile();
                    Log.e("TAG", f.getPath());
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bytes.toByteArray());
                    fos.close();
                } catch (IOException ex) {

                }

            } else {
                Toast.makeText(this,"No Image",Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
