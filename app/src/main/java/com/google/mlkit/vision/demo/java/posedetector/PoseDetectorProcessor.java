

package com.google.mlkit.vision.demo.java.posedetector;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.nnn;
import static com.google.mlkit.vision.demo.java.posedetector.PoseGraphic.landmarks;

import static com.google.mlkit.vision.demo.java.LivePreviewActivity.pushupcounter;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.squatcounter;

import static com.google.mlkit.vision.demo.java.LivePreviewActivity.bicepscounter;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.save_in_database;

import static com.google.mlkit.vision.demo.java.LivePreviewActivity.shouldercounter;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.situpscounter;

import static com.google.mlkit.vision.demo.java.ChooserActivity.jj;
import static com.google.mlkit.vision.demo.java.ChooserActivity.state;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.spinner;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.showangle;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.angles;




import android.content.Context;
import android.util.Log;
import android.view.View;
//import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.demo.java.posedetector.classification.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** A processor to run pose detector. */
public class PoseDetectorProcessor
    extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification> {
  private static final String TAG = "PoseDetectorProcessor";

  private final PoseDetector detector;

  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private final boolean runClassification;
  private final boolean isStreamMode;
  private final Context context;
  private final Executor classificationExecutor;

  private PoseClassifierProcessor poseClassifierProcessor;
  /** Internal class to hold Pose and classification results. */
  protected static class PoseWithClassification {
    private final Pose pose;
    private final List<String> classificationResult;

    public PoseWithClassification(Pose pose, List<String> classificationResult) {
      this.pose = pose;
      this.classificationResult = classificationResult;
    }

    public Pose getPose() {
      return pose;
    }

    public List<String> getClassificationResult() {
      return classificationResult;
    }
  }

  public PoseDetectorProcessor(
      Context context,
      PoseDetectorOptionsBase options,
      boolean showInFrameLikelihood,
      boolean visualizeZ,
      boolean rescaleZForVisualization,
      boolean runClassification,
      boolean isStreamMode) {
    super(context);
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;
    detector = PoseDetection.getClient(options);
    this.runClassification = runClassification;
    this.isStreamMode = isStreamMode;
    this.context = context;
    classificationExecutor = Executors.newSingleThreadExecutor();
  }

  @Override
  public void stop() {
    super.stop();
    detector.close();
  }

  @Override
  protected Task<PoseWithClassification> detectInImage(InputImage image) {
    return detector
        .process(image)
        .continueWith(
            classificationExecutor,
            task -> {
              Pose pose = task.getResult();
              List<String> classificationResult = new ArrayList<>();
              if (runClassification) {
                if (poseClassifierProcessor == null) {
                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                }
                classificationResult = poseClassifierProcessor.getPoseResult(pose);
              }
              return new PoseWithClassification(pose, classificationResult);
            });
  }

  @Override
  protected Task<PoseWithClassification> detectInImage(MlImage image) {
    return detector
        .process(image)
        .continueWith(
            classificationExecutor,
            task -> {
              Pose pose = task.getResult();
              List<String> classificationResult = new ArrayList<>();
              if (runClassification) {
                if (poseClassifierProcessor == null) {
                  poseClassifierProcessor = new PoseClassifierProcessor(context, isStreamMode);
                }
                classificationResult = poseClassifierProcessor.getPoseResult(pose);
              }
              return new PoseWithClassification(pose, classificationResult);
            });
  }

  static double getAngle(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint) {
    double result =
            Math.toDegrees(
                    Math.atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                            lastPoint.getPosition().x - midPoint.getPosition().x)
                            - Math.atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                            firstPoint.getPosition().x - midPoint.getPosition().x));
    result = Math.abs(result); // Angle should never be negative
    if (result > 180) {
      result = (360.0 - result); // Always get the acute representation of the angle
    }
    return result;
  }
  //will run on success when an image input frame goes to the detector via posedetectorprocessor
  // and comes up with some result  in it which contains the coordinates human body
  @Override
  protected void onSuccess(
      @NotNull PoseWithClassification poseWithClassification,
      @NotNull GraphicOverlay graphicOverlay) {

    showangle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(angles==true){
          angles =false;
        }else{
          angles = true;
        }
      }
    });


    switch(jj){
      case "Squats":

        if(landmarks!=null){
          if(landmarks.size()>0){

            if( getAngle(landmarks.get(PoseLandmark.RIGHT_ANKLE),landmarks.get(PoseLandmark.RIGHT_KNEE),landmarks.get(PoseLandmark.RIGHT_HIP))>90 && getAngle(landmarks.get(PoseLandmark.LEFT_ANKLE),landmarks.get(PoseLandmark.LEFT_KNEE),landmarks.get(PoseLandmark.LEFT_HIP))>90 && getAngle(landmarks.get(PoseLandmark.RIGHT_ANKLE),landmarks.get(PoseLandmark.RIGHT_KNEE),landmarks.get(PoseLandmark.RIGHT_HIP))<160 && getAngle(landmarks.get(PoseLandmark.LEFT_ANKLE),landmarks.get(PoseLandmark.LEFT_KNEE),landmarks.get(PoseLandmark.LEFT_HIP))<160 ){
              state ="up";
            }
            if( getAngle(landmarks.get(PoseLandmark.RIGHT_ANKLE),landmarks.get(PoseLandmark.RIGHT_KNEE),landmarks.get(PoseLandmark.RIGHT_HIP))<70 && state.equals("up") &&  getAngle(landmarks.get(PoseLandmark.LEFT_ANKLE),landmarks.get(PoseLandmark.LEFT_KNEE),landmarks.get(PoseLandmark.LEFT_HIP))<70){
              state ="down";
              squatcounter++;
            }

          }
        }




        nnn.setText(""+squatcounter+"");
        spinner.setText("SQUATS");

        break;
      case "Pushups" :

        if(landmarks!=null){
          if(landmarks.size()>0) {


            if(getAngle(landmarks.get(PoseLandmark.RIGHT_ANKLE),landmarks.get(PoseLandmark.RIGHT_KNEE),landmarks.get(PoseLandmark.RIGHT_HIP))>140 && getAngle(landmarks.get(PoseLandmark.LEFT_ANKLE),landmarks.get(PoseLandmark.LEFT_KNEE),landmarks.get(PoseLandmark.LEFT_HIP))>140 &&getAngle(landmarks.get(PoseLandmark.RIGHT_ANKLE),landmarks.get(PoseLandmark.RIGHT_KNEE),landmarks.get(PoseLandmark.RIGHT_HIP))<180 && getAngle(landmarks.get(PoseLandmark.LEFT_ANKLE),landmarks.get(PoseLandmark.LEFT_KNEE),landmarks.get(PoseLandmark.LEFT_HIP))<180){


              if (getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER), landmarks.get(PoseLandmark.RIGHT_ELBOW), landmarks.get(PoseLandmark.RIGHT_WRIST)) > 160 && getAngle(landmarks.get(PoseLandmark.LEFT_SHOULDER), landmarks.get(PoseLandmark.LEFT_ELBOW), landmarks.get(PoseLandmark.LEFT_WRIST)) > 160) {
                state = "up";
              }
              if (getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER), landmarks.get(PoseLandmark.RIGHT_ELBOW), landmarks.get(PoseLandmark.RIGHT_WRIST)) <130  && getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER), landmarks.get(PoseLandmark.RIGHT_ELBOW), landmarks.get(PoseLandmark.RIGHT_WRIST)) < 130 && state.equals("up") ) {
                state = "down";
                pushupcounter++;
              }

            }
          }

        }

        nnn.setText(""+pushupcounter+"");
        spinner.setText("PUSH UPS");

        break;

      case "ShoulderPress":


        if(landmarks!=null){
          if(landmarks.size()>0){

            if(getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER),landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_WRIST))>160 && getAngle(landmarks.get(PoseLandmark.LEFT_SHOULDER),landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_WRIST))>160 ){
              state ="up";
            }
            if(getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER),landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_WRIST))>70  && getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER),landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_WRIST))<90 && state.equals("up") && getAngle(landmarks.get(PoseLandmark.LEFT_SHOULDER),landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_WRIST))>70 && getAngle(landmarks.get(PoseLandmark.LEFT_SHOULDER),landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_WRIST))<90){
              state ="down";
              shouldercounter++;
}

          }

        }



        nnn.setText(""+shouldercounter+"");
        spinner.setText("SHOULDER PRESS");

        break;
      case "BicepsCurl" :

        if(landmarks!=null){
          if(landmarks.size()>0){

            if(getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER),landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_WRIST))>160 && getAngle(landmarks.get(PoseLandmark.LEFT_SHOULDER),landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_WRIST))>160 ){
              state ="down";
            }
            if(getAngle(landmarks.get(PoseLandmark.RIGHT_SHOULDER),landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_WRIST))<30 && state.equals("down") && getAngle(landmarks.get(PoseLandmark.LEFT_SHOULDER),landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_WRIST))<30){
              state ="up";
              bicepscounter++;

            }

          }

        }


        nnn.setText(""+bicepscounter+"");
        spinner.setText("BICEPS CURL");

        break;
      case "SitUps" :


        if(landmarks!=null){

          if(landmarks.size()>0){

            if(getAngle(landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_HIP),landmarks.get(PoseLandmark.RIGHT_KNEE))>60 && getAngle(landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_HIP),landmarks.get(PoseLandmark.LEFT_KNEE))>60 &&getAngle(landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_HIP),landmarks.get(PoseLandmark.RIGHT_KNEE))<105 && getAngle(landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_HIP),landmarks.get(PoseLandmark.LEFT_KNEE))<105){
              state ="down";
            }
            if(getAngle(landmarks.get(PoseLandmark.RIGHT_ELBOW),landmarks.get(PoseLandmark.RIGHT_HIP),landmarks.get(PoseLandmark.RIGHT_KNEE))<55 && state.equals("down") && getAngle(landmarks.get(PoseLandmark.LEFT_ELBOW),landmarks.get(PoseLandmark.LEFT_HIP),landmarks.get(PoseLandmark.LEFT_KNEE))<55){

              state ="up";
              situpscounter++;

            }

          }
        }

        nnn.setText(""+situpscounter+"");
        spinner.setText("SITUPS");

        break;
      default:
        Log.d(TAG,"NO EXERCISES PRESENT");
    }

    save_in_database.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        switch (jj){
            case "SitUps":
              //retrieve the data

              //sum it up with new of that date and than set that data into that location only

            break;

            case "BicepsCurl":


            break;

            case "Squats":


            break;

            case "Pushups" :


            break;

            case "ShoulderPress":


            break;
        }


      }
    });

  //  Log.d(TAG, String.valueOf(kk));
    if(landmarks!=null){
      if(landmarks.size()>0){
        Log.d(TAG, "hii"+String.valueOf(landmarks.get(0).getPosition().y));
      }

    }


    graphicOverlay.add(
        new PoseGraphic(
            graphicOverlay,
            poseWithClassification.pose,
            showInFrameLikelihood,
            visualizeZ,
            rescaleZForVisualization,
            poseWithClassification.classificationResult));
  }

  @Override
  protected void onFailure(@NotNull Exception e) {
    Log.e(TAG, "Pose detection failed!", e);
  }

  @Override
  protected boolean isMlImageEnabled(Context context) {
    // Use MlImage in Pose Detection by default, change it to OFF to switch to InputImage.
    return true;
  }
}
