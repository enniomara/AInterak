package com.example.ainterak;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.FragmentActivity;

/**
 * @author chrillebile, Christian Bilevits
 * @since 2019-05-14
 */
public class ShakeDetector implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7f;
    // Buffer on 0,3 sec
    private static final int SHAKE_SLOP_TIME_MS = 300;
    // Reset after 1,5 sec of no shake
    private static final int SHAKE_COUNT_RESET_TIME = 1500;

    private OnShakeListener mListener;
    private long lastShakeTimeStamp;
    private int mShakeCount;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    public ShakeDetector(FragmentActivity activity) {
        this.mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.mAccelerometer = this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mListener != null) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();

                // ignore shake events too close to each other
                if (lastShakeTimeStamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // reset the shake count if shake count is bigger than 2 or times up.
                if (mShakeCount >= 2 || lastShakeTimeStamp + SHAKE_COUNT_RESET_TIME < now) {
                    mShakeCount = 0;
                }

                lastShakeTimeStamp = now;
                mShakeCount++;

                mListener.onShake(mShakeCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onResume() {
        this.mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void onPause() {
        this.mSensorManager.unregisterListener(this);
    }
}
