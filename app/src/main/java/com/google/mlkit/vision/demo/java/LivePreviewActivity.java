

package com.google.mlkit.vision.demo.java;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.demo.CameraSource;
import com.google.mlkit.vision.demo.CameraSourcePreview;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.java.posedetector.PoseDetectorProcessor;   //use it also
import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;  ///use it importantly
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

  private static final String POSE_DETECTION = "Pose Detection";



  private static final String TAG = "LivePreviewActivity";

  private CameraSource cameraSource = null;  //marked to null
  private CameraSourcePreview preview;  //This receives preview frames from the camera at a specified rate, sending those frames to the detector as fast as it is able to process those frames.
  private GraphicOverlay graphicOverlay;
  private String selectedModel = POSE_DETECTION;   //initally selected  it is when

  public static int squatcounter =0;
  public static int pushupcounter =0;
  public static int bicepscounter =0;
  public static int shouldercounter =0;
  public static int situpscounter =0;
  public static TextView nnn ; //counter  display
  public static TextView spinner;
  public static boolean angles = true;
  public static TextView showangle ;
  public static Button save_in_database;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_vision_live_preview);




    preview = findViewById(R.id.preview_view);   //get id from xml file
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);  //get id from xml file
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

     spinner = (TextView) findViewById(R.id.spinner);  //get id from xml file


    nnn =(TextView)findViewById(R.id.smartCounter);
    showangle=(TextView)findViewById(R.id.showangles);
    save_in_database=(Button)findViewById(R.id.save_reps_in_database);


    ToggleButton facingSwitch = findViewById(R.id.facing_switch);  //get id from xml file
    facingSwitch.setOnCheckedChangeListener(this);
    createCameraSource(selectedModel);   //selected model for now is object detection
  }

  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    createCameraSource(selectedModel);
    startCameraSource();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      switch (model) {

        case POSE_DETECTION:

          //need this one only

          PoseDetectorOptionsBase poseDetectorOptions = PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
          Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);
          boolean shouldShowInFrameLikelihood = false;
             // PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
          boolean visualizeZ =true; // PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
          boolean rescaleZ = true;  //PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
          boolean runClassification = false;//PreferenceUtils.shouldPoseDetectionRunClassification(this);

          cameraSource.setMachineLearningFrameProcessor(
              new PoseDetectorProcessor(
                  this,
                  poseDetectorOptions,
                  shouldShowInFrameLikelihood,
                  visualizeZ,
                  rescaleZ,
                  runClassification,
                  /* isStreamMode = */ true));   //is stream about is in documentation in mlkit website
          break;
          default:
          Log.e(TAG, "Unknown model: " + model);
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "Can not create image processor: " + model, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource(selectedModel);
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }
}
