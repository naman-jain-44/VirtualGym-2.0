

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
import android.widget.Toast;
//import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.odml.image.MlImage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.MainActivity;
import com.google.mlkit.vision.demo.java.VisionProcessorBase;
import com.google.mlkit.vision.demo.java.posedetector.classification.PoseClassifierProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/** A processor to run pose detector. */
public class PoseDetectorProcessor
    extends VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification> {
  private static final String TAG = "PoseDetectorProcessor";
//  FirebaseFirestore db = FirebaseFirestore.getInstance();
//  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//  CollectionReference user_date_reference = db.collection("users").document(user.getUid()).collection("dates");
 private final PoseDetector detector;

  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private final boolean runClassification;
  private final boolean isStreamMode;
  private final Context context;
  private final Executor classificationExecutor;
  FirebaseFirestore db = FirebaseFirestore.getInstance();
  FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
  //  CollectionReference user_date_reference = db.collection("users").document(user.getUid()).collection("dates");
  // Get the current date
  Date currentDate = new Date();

  // Format the date as a string
  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
  // Log.d(TAG,"NO EXERCISES PRESENT");
  int dateInt = Integer.parseInt(dateFormat.format(currentDate));
      //  Log.d(TAG,""+dateInt+"");
  DocumentReference d= db.collection("users").document(user.getUid()).collection("dates").document(String.valueOf(dateInt));
     //   Log.d(TAG,""+user.getUid()+"");

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
              //retrieve first if present on that day
              save_data_database_required_exercise(jj,situpscounter);
              situpscounter=0;
              nnn.setText(""+situpscounter+"");
              //sum it up with new of that date and than set that data into that location only


            break;

            case "BicepsCurl":
              save_data_database_required_exercise(jj,bicepscounter);
              bicepscounter=0;
              nnn.setText(""+bicepscounter+"");


            break;

            case "Squats":
              save_data_database_required_exercise(jj,squatcounter);
              squatcounter=0;
              nnn.setText(""+squatcounter+"");


            break;

            case "Pushups" :
              save_data_database_required_exercise(jj,pushupcounter);
              pushupcounter=0;
              nnn.setText(""+pushupcounter+"");


            break;

            case "ShoulderPress":
              save_data_database_required_exercise(jj,shouldercounter);
              shouldercounter=0;
              nnn.setText(""+shouldercounter+"");


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

  protected  void save_data_database_required_exercise(String h ,int new_number){

    d.get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
              @Override
              public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                  int currentPushups = 0;
                  if (documentSnapshot.contains(h)){
                    currentPushups = documentSnapshot.getLong(h).intValue();
                  }

                  int newPushups = currentPushups + new_number;
                  Map<String, Object> updates = new HashMap<>();
                  updates.put(h, newPushups);
                  d.set(updates, SetOptions.merge());



                } else {
                  Map<String, Object> updates = new HashMap<>();
                  updates.put(h, 0);
                  d.set(updates, SetOptions.merge());

                }
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(Exception e) {

              }
            });

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
