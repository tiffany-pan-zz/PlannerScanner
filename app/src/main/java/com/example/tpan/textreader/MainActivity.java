package com.example.tpan.textreader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.SparseArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import com.google.android.gms.vision.Frame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    //SurfaceView surfaceView;
    TextView textView;
    CameraSource cameraSource;
    ImageView imageview;
    Button btnSchedule;
    Button btnToDo;
    int buttonChosen; // 0 is schedule, 1 is todo
    private static final String IMAGE_DIRECTORY = "/drawable";
    private int GALLERY = 1, CAMERA = 2;
    ArrayList<EventEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        btnSchedule = (Button) findViewById(R.id.takePicBtn);
        btnToDo = (Button) findViewById(R.id.toDoBtn);
        textView = (TextView) findViewById(R.id.textView);
        imageview = (ImageView) findViewById(R.id.imageView);

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonChosen = 0;
                showPictureDialog();

            }
        });
        btnToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonChosen = 1;
                showPictureDialog();

            }
        });
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageview.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    /*public void writeData() throws FileNotFoundException{

        // if file does not exist
        StringBuilder sb = new StringBuilder();
        File f = new File("data.csv");
        PrintWriter pw = new PrintWriter(f);
        if(!f.exists()) {

            sb.append("Date");
            sb.append(',');
            sb.append("StartTime");
            sb.append(',');
            sb.append("EndTime");
            sb.append(',');
            sb.append("EventName");
            sb.append('\n');
        }

        // else file exists
        for (int i = 0; i< entries.size(); i++) {
            sb.append(entries.get(i).date);
            sb.append(',');
            sb.append(entries.get(i).getStartTime());
            sb.append(',');
            sb.append(entries.get(i).getEndTime());
            sb.append(',');
            sb.append(entries.get(i).getEvents());
            sb.append('\n');
        }

        pw.write(sb.toString());
        pw.close();
    }*/

    public void getTextFromImage(View v) {

        imageview.buildDrawingCache();
        Bitmap bitmap = imageview.getDrawingCache();
        //Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.text);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();


        if (!textRecognizer.isOperational()) {
            Toast.makeText(getApplicationContext(), "Could not get the text", Toast.LENGTH_SHORT).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);

            if (buttonChosen == 0) {
                for (int i = 0; i < items.size(); i++) {
                    TextBlock myItem = items.valueAt(i);
                    String[] parsedEntry = myItem.getValue().split("\n");
                    String[] times = parsedEntry[1].split(" ");
                    EventEntry currEntry = new EventEntry(parsedEntry[0], times[0], times[2], parsedEntry[2]);
                    entries.add(currEntry);
                }

                // WRITE DATA

                StringBuilder sb = new StringBuilder();
                FileOutputStream outputStream;
                File path = this.getFilesDir();
                File f = new File(path, "test.csv");
                // if file does not exist
                if (!f.exists()) {

                    sb.append("Date");
                    sb.append(',');
                    sb.append("StartTime");
                    sb.append(',');
                    sb.append("EndTime");
                    sb.append(',');
                    sb.append("EventName");
                    sb.append('\n');
                }

                // else file exists
                for (int i = 0; i < entries.size(); i++) {
                    sb.append(entries.get(i).date);
                    sb.append(',');
                    sb.append(entries.get(i).getStartTime());
                    sb.append(',');
                    sb.append(entries.get(i).getEndTime());
                    sb.append(',');
                    sb.append(entries.get(i).getEvents());
                    sb.append('\n');
                }

                try {
                    outputStream = new FileOutputStream(f);
                    outputStream.write(sb.toString().getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                StringBuilder confirmation = new StringBuilder();
                for (int i = 0; i < entries.size(); i++) {
                    confirmation.append("Added the event: " + entries.get(i).getDate() + " " + entries.get(i).toString() + "\n");
                }
                textView.setText(confirmation);
            } else { // to do button
                StringBuilder str = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    TextBlock myItem = items.valueAt(i);
                    //String[] parsedEntry = myItem.getValue().split("\n");
                    //String[] times = parsedEntry[1].split(" ");
                    str.append(myItem.getValue());
                }

                ToDoList list = new ToDoList(str.toString());

                //Log.i(this.getClass().getName(), list.getToDoItem());

                // WRITE DATA
                //StringBuilder sb = new StringBuilder();
                FileOutputStream outputStream;
                File path = this.getFilesDir();
                File f = new File(path, "ToDo.txt");

                try {
                    outputStream = new FileOutputStream(f);
                    outputStream.write(list.getToDoItem().getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //StringBuilder confirmation = new StringBuilder();

                textView.setText(list.getToDoItem());
            }
        }

    }



}


/* not here
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(this, "Not available!", Toast.LENGTH_SHORT).show();
        } else {
            // Start Camera
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setAutoFocusEnabled(true)
                    .build();
*/
            /*surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        cameraSource.start(surfaceView.getHolder());
                        System.out.println("REACHES HERE");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
        }*/
/*
        cameraSource.takePicture(null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Log.d("BITMAP", bmp.getWidth() + "x" + bmp.getHeight());
            }
        });


        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); i++) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            textView.setText(stringBuilder.toString());
                        }
                    });
                }
            }
        });

    }
}*/
