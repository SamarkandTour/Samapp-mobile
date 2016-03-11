package uz.samtuit.samapp.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.FloatMath;
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

import uz.samtuit.samapp.main.R;
import uz.samtuit.samapp.util.BitmapUtil;
import uz.samtuit.samapp.util.FileUtil;
import uz.samtuit.samapp.util.GlobalsClass;

public class ImageViewingActivity extends AppCompatActivity implements View.OnTouchListener {

    private Toolbar toolbar;
    private ImageView imageView;
    private Bundle extras;
    private String ImageTitle, FilePath;
    private Bitmap _bitmap;
    private Boolean isCorrect = false;
    private static final String TAG = "Touch";

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    double oldDist = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewing);
        toolbar = (Toolbar)findViewById(R.id.actionbar);

        extras = getIntent().getExtras();
        imageView = (ImageView)findViewById(R.id.image_holder);
        ImageTitle = extras.getString("name");
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setOnTouchListener(this);
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
        } catch (Exception ex) {
            ex.printStackTrace();
            isCorrect = false;
            Glide.with(this).load(R.drawable.no_image).into(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // make the image scalable as a matrix
        view.setScaleType(ImageView.ScaleType.MATRIX);
        double scale;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG" );
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = NONE;
                Log.d(TAG, "mode=NONE" );
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //second finger down
                oldDist = spacing(event); // calculates the distance between two points where user touched.
                Log.d(TAG, "oldDist=" + oldDist);
                // minimal distance between both the fingers
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); // sets the mid-point of the straight line between two points where user touched.
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM" );
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG)
                { //movement of first finger
                    matrix.set(savedMatrix);
                    if (view.getLeft() >= -392)
                    {
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }
                }
                else if (mode == ZOOM) { //pinch zooming
                    double newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist/oldDist; //thinking I need to play around with this value to limit it**
                        matrix.postScale((float)scale, (float)scale, mid.x, mid.y);
                    }
                }
                break;
        }

        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private double spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
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
