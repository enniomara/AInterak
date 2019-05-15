package com.example.ainterak;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

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
    private long mShakeTimeStamp;
    private int mShakeCount;

    public void setOnShakeListener(OnShakeListener listener){
        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mListener != null){
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY){
                final long now = System.currentTimeMillis();

                // ignore shake events too close to each other
                if (mShakeTimeStamp + SHAKE_SLOP_TIME_MS > now){
                    return;
                }

                // reset the shake count if shake count is bigger than 3 or times up.
                if (mShakeCount >= 3 || mShakeTimeStamp + SHAKE_COUNT_RESET_TIME < now){
                    mShakeCount = 0;
                }

                mShakeTimeStamp = now;
                mShakeCount++;

                mListener.onShake(mShakeCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
