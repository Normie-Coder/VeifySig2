package com.dankprojects.veifysig2;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        /*
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);

         */
        int i = 4;
        int j =0;

        try{
            int c = i/j;
        }catch (Exception e){
            toPdf(e);
        }
    }

    public void toPdf(Exception e){
        Log.d(TAG, "toPdf: start >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        String extstoragedir = getApplicationContext().getFilesDir().getAbsolutePath();
        Log.d(TAG, extstoragedir+ " ...extstoragedir: >>>>>>>>>>>>>>>>>>>>>>");
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        Log.d(TAG, stringFormatter(errors.toString())+" errors.toString(): >>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        File fol = new File(extstoragedir+  "/pdf");
        //File folder = new File(fol, "pdf");
        if (!fol.exists()){
            boolean bool = fol.mkdir();
            Log.d(TAG, bool +" bool >>>>>>>>>>>>>>>>>>>>>>>");
        }

        try{
            final File file = new File(fol, "exceptions.txt");
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(errors.toString().getBytes());
            fOut.close();
            /*
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(500,500,1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            canvas.drawText(stringFormatter(errors.toString()), 0,10, paint);
            canvas.

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();
            */


            Log.d("PDF", "Done: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        }
        catch (Exception ex){
            Log.i("Error", e.getLocalizedMessage());
        }
    }

    private String stringFormatter (String errors){
        StringBuilder sb = new StringBuilder(errors);

        for (int i =0; i < sb.length(); ++i)
        {
            if (i% 85 == 0){
                sb.insert(i, "\n");
            }
        }

        return sb.toString();
    }
}