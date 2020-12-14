package com.example.audio_tests;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;
import android.media.audiofx.DynamicsProcessing;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


//import android.platform.test.annotations.AppModeFull;
//import android.test.AndroidTestCase;

import android.util.Pair;
import android.util.Range;
import android.util.Size;
import android.view.View;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // dynamics processing stuff:
    // Recorder used for uncompressed recording
    private static final String TAG = "DynamicsProcessingTest";
    private DynamicsProcessing mDP;
    private static final int MIN_CHANNEL_COUNT = 2;
    private static final float EPSILON = 0.00001f;
    private static final int DEFAULT_VARIANT =
            DynamicsProcessing.VARIANT_FAVOR_FREQUENCY_RESOLUTION;
    private static final boolean DEFAULT_PREEQ_IN_USE = true;
    private static final int DEFAULT_PREEQ_BAND_COUNT = 2;
    private static final boolean DEFAULT_MBC_IN_USE = true;
    private static final int DEFAULT_MBC_BAND_COUNT = 2;
    private static final boolean DEFAULT_POSTEQ_IN_USE = true;
    private static final int DEFAULT_POSTEQ_BAND_COUNT = 2;
    private static final boolean DEFAULT_LIMITER_IN_USE = true;
    private static final float DEFAULT_FRAME_DURATION = 9.5f;
    private static final float DEFAULT_INPUT_GAIN = -12.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        /** Called when the user taps the Send button */
        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
//                TextView tv = (TextView) findViewById(R.id.button_output);

                try {
                    buildAudioJSON();
//                    tv.setText(buildCameraJSON().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        //FloatingActionButton fab = findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
         //   @Override
         //   public void onClick(View view) {
                //getValidSampleRates();
                //getMinSupportedSampleRate();
                //Log.d("Min sample rate", ""+SR);
                //Snackbar.make(view, "Test Reuven", Snackbar.LENGTH_LONG)
                 //       .setAction("Action", null).show();
         //   }
        //});
    }


    private JSONObject buildAudioJSON() throws JSONException {
        JSONObject retVal = new JSONObject();

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        try {
            String temp = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
            Log.d("PROPERTY_OUTPUT_FRAMES_PER_BUFFER", "" + temp);
            String temp1 = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            Log.d("PROPERTY_OUTPUT_SAMPLE_RATE", "" + temp1);
            String temp2 = audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED);
            Log.d("PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED", "" + temp2);
            String temp3 = audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_MIC_NEAR_ULTRASOUND);
            Log.d("PROPERTY_SUPPORT_MIC_NEAR_ULTRASOUND", "" + temp3);
            String temp4 = audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_SPEAKER_NEAR_ULTRASOUND);
            Log.d("PROPERTY_SUPPORT_SPEAKER_NEAR_ULTRASOUND", "" + temp4);

            Integer mode = audioManager.getMode();
            Log.d("Audio Mode", "" + mode); // 0 is MODE_NORMAL

            List<MicrophoneInfo> micList = audioManager.getMicrophones();
            ListIterator<MicrophoneInfo> micListIterator = micList.listIterator();
            while (micListIterator.hasNext()) {
                MicrophoneInfo micSpec = micListIterator.next();
                Log.d("New Microphone", "------------------------------");
                Log.d("Microphone description", "" + micSpec.getDescription()); //
                Log.d("Microphone Type", "" + micSpec.getType()); // 15 is Builtin MIC
                Log.d("Microphone address", "" + micSpec.getAddress());
                Log.d("Microphone Id", "" + micSpec.getId());
                Log.d("Microphone Group", "" + micSpec.getGroup()); // main body group is 0
                Log.d("Microphone Index in Group", "" + micSpec.getIndexInTheGroup());
                Log.d("Microphone Channel Mapping", "" + micSpec.getChannelMapping()); // only when active microphones returns somethings
                List<Pair<Float,Float>> freqResponse = micSpec.getFrequencyResponse();
                ArrayList<Float> responseArray = new ArrayList();
                for (Pair<Float,Float>  tempPair : freqResponse) {
                    responseArray.add(tempPair.second);
                }
                Log.d("Microphone FrequencyResponse", "" + responseArray);
                Log.d("Microphone directionality", "" + micSpec.getDirectionality()); // 1 is omni-directional
                Log.d("Microphone Location", "" + micSpec.getLocation()); // 1 is main-body
                Log.d("Microphone Position x", "" + micSpec.getPosition().x);
                Log.d("Microphone Position y", "" + micSpec.getPosition().y);
                Log.d("Microphone Position z", "" + micSpec.getPosition().z);
                Log.d("Microphone Orientation x ", "" + micSpec.getOrientation().x);
                Log.d("Microphone Orientation y ", "" + micSpec.getOrientation().y);
                Log.d("Microphone Orientation z ", "" + micSpec.getOrientation().z);
                Log.d("Microphone Sensitivity", "" + micSpec.getSensitivity());
                Log.d("Microphone MinSPL", "" + micSpec.getMinSpl());
                Log.d("Microphone MaxSPL", "" + micSpec.getMaxSpl());
            }


            AudioDeviceInfo[] devices = audioManager.getDevices(AudioManager.GET_DEVICES_ALL);
            for (AudioDeviceInfo device : devices) {
                Log.d("New Device","-------------------------------------------");
                Log.d("Device address", "" + device.getAddress());
                Log.d("Device Id", "" + device.getId());
                //Log.d("Device Product Name", "" + device.getProductName());
                Log.d("Device Type", "" + device.getType()); // 24 is TYPE_BUILTIN_SPEAKER_SAFE, 2 is TYPE_BUILTIN_SPEAKER, 18 is TELEPHONY, 1 is earpiece
                int[] channelCounts = device.getChannelCounts();
                for (int chanC : channelCounts) {
                    Log.d("Device Channel Count", "" + chanC);
                }
                int[] channelIndexMasks = device.getChannelIndexMasks();
                for (int chanIdxM : channelIndexMasks) {
                    Log.d("Device Channel Index Mask", "" + chanIdxM);
                }
                int[] channelMasks = device.getChannelMasks();
                for (int chnmsk : channelMasks) {
                    Log.d("Device Channel Mask", "" + chnmsk);
                }
                int[] encodings = device.getEncodings();
                for (int encd : encodings) {
                    Log.d("Device Encoding", "" + encd);
                }

                int[] sample_rates = device.getSampleRates();
                for (int sampR : sample_rates) {
                    Log.d("Device Sample Rate", "" + sampR);
                }
                Log.d("Device IsSource?", "" + device.isSource());
                Log.d("Device IsSink?", "" + device.isSink());
            }
                //retVal.put(cameraId, buildAudioCharacteristicsJSON(characteristics));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // check dynamic processing defaults:
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // test 0:
        /*try {
            Log.d(TAG, "Create am:" + am);
            Log.d(TAG, "create dynamics processing");
            createDynamicsProcessing(AudioManager.AUDIO_SESSION_ID_GENERATE);
            Log.d(TAG, "After create out");
            releaseDynamicsProcessing();
            Log.d(TAG, "After release out");

            Log.d(TAG, "before generate sessionId");
            final int session = am.generateAudioSessionId();
            Log.d(TAG, "session:"+session);
            createDynamicsProcessing(session);
        } finally {
            releaseDynamicsProcessing();
        } */

        // test 1:
        try {
            createDefaultEffect(am);
            // Check Parameters:
            final int channelCount = mDP.getChannelCount();
            Log.d(TAG, "Channel Count = "+channelCount);
            DynamicsProcessing.Channel channel0 = mDP.getChannelByChannelIndex(0);
            DynamicsProcessing.Channel channel1 = mDP.getChannelByChannelIndex(1);
            final float inputGain0 = channel0.getInputGain();
            Log.d(TAG, "channel 0 input gain = "+inputGain0);
            final float inputGain1 = channel1.getInputGain();
            Log.d(TAG, "channel 1 input gain = "+inputGain1);
        } finally {
            releaseDynamicsProcessing();
        }

        //Logger.d(retVal);
        //buildArrayListFromJson(retVal);
        return retVal;
    }


    private void createDefaultEffect(AudioManager am) {
        DynamicsProcessing.Config config = getBuilderWithValues().build();
        Log.d(TAG, "config="+config);
        Log.d(TAG,"After config");
        int session = am.generateAudioSessionId();
        Log.d(TAG,"session:" + session);
        createDynamicsProcessingWithConfig(session, config);
    }

    private void createDynamicsProcessing(int session) {
        Log.d(TAG, "entered createDynamicsProcessing");
        createDynamicsProcessingWithConfig(session, null);
    }
    private void createDynamicsProcessingWithConfig(int session, DynamicsProcessing.Config config) {
        Log.d(TAG, "entered createDynamicsProcessingWithConfig");
        releaseDynamicsProcessing();
        Log.d(TAG, "after release in");
        Log.d(TAG, "session:" +session);
        try {
            Log.d(TAG, "before new DP");
            mDP = (config == null ? new DynamicsProcessing(session)
                    : new DynamicsProcessing(0 /* priority */, session, config));
            Log.d(TAG, "created new mDP");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "createDynamicsProcessingWithConfig() DynamicsProcessing not found"
                    + "exception: ", e);
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, "createDynamicsProcessingWithConfig() Effect library not loaded exception: ",
                    e);
        }
    }

    private void releaseDynamicsProcessing() {
        if (mDP != null) {
            mDP.release();
            mDP = null;
        }
    }

    private DynamicsProcessing.Config.Builder getBuilder(int channelCount) {
        // simple config
        DynamicsProcessing.Config.Builder builder = new DynamicsProcessing.Config.Builder(
                DEFAULT_VARIANT /* variant */,
                channelCount/* channels */,
                DEFAULT_PREEQ_IN_USE /* enable preEQ */,
                DEFAULT_PREEQ_BAND_COUNT /* preEq bands */,
                DEFAULT_MBC_IN_USE /* enable mbc */,
                DEFAULT_MBC_BAND_COUNT /* mbc bands */,
                DEFAULT_POSTEQ_IN_USE /* enable postEq */,
                DEFAULT_POSTEQ_BAND_COUNT /* postEq bands */,
                DEFAULT_LIMITER_IN_USE /* enable limiter */);
        return builder;
    }
    private DynamicsProcessing.Config.Builder getBuilderWithValues1(int channelCount) {
        // simple config
        DynamicsProcessing.Config.Builder builder = getBuilder(channelCount);
        // Set Defaults
        builder.setPreferredFrameDuration(DEFAULT_FRAME_DURATION);
        builder.setInputGainAllChannelsTo(DEFAULT_INPUT_GAIN);
        return builder;
    }
    private DynamicsProcessing.Config.Builder getBuilderWithValues() {
        return getBuilderWithValues1(MIN_CHANNEL_COUNT);
    }



    private void buildArrayListFromJson(JSONObject jsonObject) throws JSONException {


    }


    private JSONObject buildAudioCharacteristicsJSON(CameraCharacteristics cameraCharacteristics) {
        JSONObject retVal = new JSONObject();
        try {
            Iterator<CameraCharacteristics.Key<?>> iter = cameraCharacteristics.getKeys().iterator();
            while (iter.hasNext()) {
                CameraCharacteristics.Key<?> keyObj = iter.next();
                String key = keyObj.getName();
                /** Differentiate between the different types of characteristic outputs: **/
                if (cameraCharacteristics.get(keyObj) instanceof int[]) {
                    int[] values = (int[]) cameraCharacteristics.get(keyObj);
                    retVal.put(key, Arrays.toString(values));
                } else if (cameraCharacteristics.get(keyObj) instanceof float[]) {
                    float[] values = (float[]) cameraCharacteristics.get(keyObj);
                    retVal.put(key, Arrays.toString(values));
                } else if (cameraCharacteristics.get(keyObj) instanceof Size[]) {
                    Size[] values = (Size[]) cameraCharacteristics.get(keyObj);
                    retVal.put(key, Arrays.toString(values));
                } else if (cameraCharacteristics.get(keyObj) instanceof boolean[]) {
                    boolean[] values = (boolean[]) cameraCharacteristics.get(keyObj);
                    retVal.put(key, Arrays.toString(values));
                } else if (cameraCharacteristics.get(keyObj) instanceof Range[]) {
                    Range[] values = (Range[]) cameraCharacteristics.get(keyObj);
                    retVal.put(key, Arrays.toString(values));
                } else {
                    String value = cameraCharacteristics.get(keyObj).toString();
                    retVal.put(key, value);
                }
            }

            Integer sensorDepth = ImageFormat.getBitsPerPixel(32);
            retVal.put("Sensor color Depth", sensorDepth);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getValidSampleRates() {
        // To get preferred buffer size and sampling rate.
        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("Buffer Size and sample rate", "Size :" + size + " & Rate: " + rate);
    }

    private void getMinSupportedSampleRate() {
        /*
         * Valid Audio Sample rates
         *
         * @see <a
         * href="http://en.wikipedia.org/wiki/Sampling_%28signal_processing%29"
         * >Wikipedia</a>
         */

        final int validSampleRates[] = new int[] { 8000, 11025, 16000, 22050,
                32000, 37800, 44056, 44100, 47250, 48000, 50000, 50400, 88200,
                96000, 176400, 192000, 352800, 2822400, 5644800 }; //
        /*
         * Selecting default audio input source for recording since
         * AudioFormat.CHANNEL_CONFIGURATION_DEFAULT is deprecated and selecting
         * default encoding format.
         */
        Log.d("# Rates", ""+validSampleRates.length);
        for (int i = 0; i < validSampleRates.length; i++) {
            AudioRecord recorder = null;
            try {
                int new_i = validSampleRates.length - i - 1;
                Log.d("Loop number", "" + new_i);
                int result = AudioRecord.getMinBufferSize(validSampleRates[new_i],
                        AudioFormat.CHANNEL_IN_STEREO,
                        AudioFormat.ENCODING_PCM_16BIT);
                Log.d("Buff size result", "" + result);
                if (result != AudioRecord.ERROR
                        && result != AudioRecord.ERROR_BAD_VALUE && result > 0) {
                    recorder = new AudioRecord(MediaRecorder.AudioSource.UNPROCESSED, validSampleRates[new_i], AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, result);
                    Log.d("Good SR", "" + validSampleRates[new_i]);
                   }
            } catch (IllegalArgumentException e) {
                Log.d("Bad SR", "");;
            } finally {
                if (recorder != null) {
                    recorder.release();
                }
            }
        }
    }

}
