
package com.google.mlkit.vision.demo.java;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.pushupcounter;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.situpscounter;
import static com.google.mlkit.vision.demo.java.LivePreviewActivity.squatcounter;

import static com.google.mlkit.vision.demo.java.LivePreviewActivity.bicepscounter;

import static com.google.mlkit.vision.demo.java.LivePreviewActivity.shouldercounter;

import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.mlkit.vision.demo.BuildConfig;
import com.google.mlkit.vision.demo.DateClass;
import com.google.mlkit.vision.demo.PdfActivity;
import com.google.mlkit.vision.demo.R;

/** Demo app chooser which allows you pick from all available testing Activities. */
public final class ChooserActivity extends AppCompatActivity
    implements AdapterView.OnItemClickListener {
  private static final String TAG = "ChooserActivity";
  public static String  jj ="" ; //exercise that has  been chosen
  public static String  state ="NONE" ; //for each exercising initial state will be declared as none

  @SuppressWarnings("NewApi") // CameraX is only available on API 21+
  private static final Class<?>[] CLASSES =
      VERSION.SDK_INT < VERSION_CODES.LOLLIPOP
          ? new Class<?>[] {
            LivePreviewActivity.class,  LivePreviewActivity.class,
          }:
           new Class<?>[] {
            LivePreviewActivity.class, LivePreviewActivity.class,
              LivePreviewActivity.class, LivePreviewActivity.class, LivePreviewActivity.class
          };

  private static final int[] DESCRIPTION_IDS =
      VERSION.SDK_INT < VERSION_CODES.LOLLIPOP
          ? new int[] {
            R.string.desc_camera_source_activity, R.string.desc_still_image_activity,
          }:
           new int[] {
              R.string.desc_camera_source_activity,
              R.string.desc_still_image_activity,
              R.string.desc_camerax_live_preview_activity,
              R.string.desc_cameraxsource_demo_activity,R.string.desc_cameraxsource_demo,
          };

  private static final String[]  gh ={"Squats","Pushups","ShoulderPress","BicepsCurl","SitUps"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
          new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
      StrictMode.setVmPolicy(
          new StrictMode.VmPolicy.Builder()
              .detectLeakedSqlLiteObjects()
              .detectLeakedClosableObjects()
              .penaltyLog()
              .build());
    }
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_chooser);

    // Set up ListView and Adapter
    ListView listView = findViewById(R.id.test_activity_list_view);
  //classes get or display item  or the classes we have to call , according to the sdk version available
    MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);

    adapter.setDescriptionIds(DESCRIPTION_IDS); //creating text written on it

    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);

    TextView usermanual = (TextView)findViewById(R.id.usermanual);
    usermanual.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ChooserActivity.this, PdfActivity.class);
            startActivity(i);
        }
    });


    TextView show_analy=(TextView)findViewById(R.id.show_analytics);



   show_analy.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent i = new Intent(ChooserActivity.this, DateClass.class);
            startActivity(i);
        }
    });

  }




  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
   Class<?> clicked = CLASSES[position];
    jj = gh[position];
    bicepscounter =0;  //initialize all counters to be zero while choosing the activity
    squatcounter =0;
    pushupcounter =0;
    shouldercounter =0;
    situpscounter =0;
    state = "NONE";
    //declaring again as variable is public static   and after choosing one exercise when we go to next it will be again become none



    startActivity(new Intent(this, clicked));  //open that activity take from the classes
  }

  private static class MyArrayAdapter extends ArrayAdapter<Class<?>> {

    private final Context context;
    private final Class<?>[] classes;
    private int[] descriptionIds;

    MyArrayAdapter(Context context, int resource, Class<?>[] objects) {
      super(context, resource, objects);

      this.context = context;
      classes = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = convertView;

      if (convertView == null) {
        LayoutInflater inflater =
            (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(android.R.layout.simple_list_item_2, null); //when this list use we can add two text in it text1 and text2
      }

      ((TextView) view.findViewById(android.R.id.text1)).setText(descriptionIds[position]);
      ((TextView) view.findViewById(android.R.id.text2)).setText(" ");

      return view;
    }

    void setDescriptionIds(int[] descriptionIds) {
      this.descriptionIds = descriptionIds;
    }
  }
}
