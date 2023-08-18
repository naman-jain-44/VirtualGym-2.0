package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;

public class PdfActivity extends AppCompatActivity {

    PDFView  pdfview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        pdfview =(PDFView) findViewById(R.id.pdfView);
        pdfview.fromAsset("virtualgym.pdf").load();
    }
}