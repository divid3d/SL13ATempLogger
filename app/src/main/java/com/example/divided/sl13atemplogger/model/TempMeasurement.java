package com.example.divided.sl13atemplogger.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.divided.sl13atemplogger.Utils;

public class TempMeasurement implements Parcelable {


    public static final Parcelable.Creator<TempMeasurement> CREATOR = new Parcelable.Creator<TempMeasurement>() {
        public TempMeasurement createFromParcel(Parcel in) {
            return new TempMeasurement(in);
        }

        public TempMeasurement[] newArray(int size) {
            return new TempMeasurement[size];
        }

    };
    private float temperatureCelsius;
    private float temperatureFahrenheit;
    private float temperatureKelvin;
    private float timeStamp;
    private int timeUnit;

    public TempMeasurement(String adcValue, String timeStamp) {
        this.timeStamp = Utils.hexStringToInteger(timeStamp);
        this.temperatureCelsius = Utils.hexStringToInteger(adcValue);
        this.temperatureKelvin = Utils.celsiusToKelvin(this.temperatureCelsius);
        this.temperatureFahrenheit = Utils.celsiusToFahrenheit(this.temperatureCelsius);
    }

    //contructor for testing purpose
    public TempMeasurement(Float tempCelsius, float timeStamp, int timeUnit) {
        this.timeStamp = timeStamp;
        this.temperatureCelsius = tempCelsius;
        this.temperatureKelvin = Utils.celsiusToKelvin(this.temperatureCelsius);
        this.temperatureFahrenheit = Utils.celsiusToFahrenheit(this.temperatureCelsius);
        this.timeUnit = timeUnit;
    }

    public TempMeasurement(Parcel in) {
        super();
        readFromParcel(in);
    }

    public int getTimeUnit() {
        return timeUnit;
    }

    public float getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(float temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public float getTemperatureFahrenheit() {
        return temperatureFahrenheit;
    }

    public float getTemperatureKelvin() {
        return temperatureKelvin;
    }

    public float getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(float timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void readFromParcel(Parcel in) {
        this.temperatureCelsius = in.readFloat();
        this.temperatureFahrenheit = in.readFloat();
        this.temperatureKelvin = in.readFloat();
        this.timeStamp = in.readFloat();
        this.timeUnit = in.readInt();

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.temperatureCelsius);
        dest.writeFloat(this.temperatureFahrenheit);
        dest.writeFloat(this.temperatureKelvin);
        dest.writeFloat(this.timeStamp);
        dest.writeInt(this.timeUnit);
    }
}