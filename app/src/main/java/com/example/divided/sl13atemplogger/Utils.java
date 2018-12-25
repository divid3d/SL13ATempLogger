package com.example.divided.sl13atemplogger;

import com.example.divided.sl13atemplogger.model.Temp;
import com.example.divided.sl13atemplogger.model.TempMeasurement;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public final static int UNIT_CELSIUS = 0;
    public final static int UNIT_KELVIN = 1;
    public final static int UNIT_FAHRENHEIT = 2;
    public final static int UNIT_SECOND = 3;
    public final static int UNIT_MINUTE = 4;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String ConvertHexByteArrayToString(byte[] byteArrayToConvert,boolean withSplit) {
        String ConvertedByte = "";
        String splitSign ="";
        if(withSplit){
            splitSign = " ";
        }

        for (int i = byteArrayToConvert.length - 1; i >= 0; i--) {
            if (byteArrayToConvert[i] < 0) {
                ConvertedByte += Integer.toString(byteArrayToConvert[i] + 256, 16)+splitSign;
            } else if (byteArrayToConvert[i] <= 15) {
                ConvertedByte += "0" + Integer.toString(byteArrayToConvert[i], 16)+splitSign;
            } else {
                ConvertedByte += Integer.toString(byteArrayToConvert[i], 16)+splitSign;
            }
        }

        return ConvertedByte;
    }

    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static byte[] reverse(byte[] array) {
        byte[] temp = array;
        int i = 0;
        int j = temp.length - 1;
        byte tmp;
        while (j > i) {
            tmp = temp[j];
            temp[j] = temp[i];
            temp[i] = tmp;
            j--;
            i++;
        }
        return temp;
    }

    public static byte binaryStringToByte(String binaryString) {
        int val = Integer.parseInt(binaryString, 2);
        return (byte) val;
    }


    public static int hexStringToInteger(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public static float celsiusToKelvin(float celsius) {
        return celsius + 273.15f;
    }

    public static float celsiusToFahrenheit(float celsius) {
        return 32 + (celsius * 9 / 5);
    }

    public static Temp getMinTemp(ArrayList<TempMeasurement> measurements) {
        float minTemp = measurements.get(0).getTemperatureCelsius();

        for (int i = 1; i < measurements.size(); i++) {
            if (measurements.get(i).getTemperatureCelsius() < minTemp) {
                minTemp = measurements.get(i).getTemperatureCelsius();
            }
        }
        return new Temp(minTemp);
    }

    public static Temp getMaxTemp(ArrayList<TempMeasurement> measurements) {
        float maxTemp = measurements.get(0).getTemperatureCelsius();

        for (int i = 1; i < measurements.size(); i++) {
            if (measurements.get(i).getTemperatureCelsius() > maxTemp) {
                maxTemp = measurements.get(i).getTemperatureCelsius();
            }
        }
        return new Temp(maxTemp);
    }

    public static Temp getAverageTemp(ArrayList<TempMeasurement> measurements) {
        float averageCelsiusTemp = 0;

        for (TempMeasurement measurement : measurements) {
            averageCelsiusTemp += measurement.getTemperatureCelsius();
        }

        averageCelsiusTemp = averageCelsiusTemp / measurements.size();
        return new Temp(averageCelsiusTemp);
    }

    public static String tempUnitToString(int tempUnit) {
        if (tempUnit == UNIT_CELSIUS) {
            return "°C";
        } else if (tempUnit == UNIT_KELVIN) {
            return "K";
        } else {
            return "F";
        }
    }


    public static String timeUnitToString(int timeUnit) {
        if (timeUnit == UNIT_SECOND) {
            return "s";
        } else if (timeUnit == UNIT_MINUTE) {
            return "m";
        } else {
            return "";
        }
    }

    public static String prepareStringData(List<TempMeasurement> measurements, String header) {
        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n\n");

        for (int i = 0; i < measurements.size(); i++) {
            TempMeasurement currentMeasurement = measurements.get(i);
            sb.append(String.valueOf(i + 1)).append(".\t")
                    .append(currentMeasurement.getTimeStamp()).append(timeUnitToString(currentMeasurement.getTimeUnit())).append("\t")
                    .append(String.format("%.1f", currentMeasurement.getTemperatureCelsius())).append(" °C").append("\t")
                    .append(String.format("%.1f", currentMeasurement.getTemperatureKelvin())).append(" K").append("\t")
                    .append(String.format("%.1f", currentMeasurement.getTemperatureFahrenheit())).append(" F")
                    .append("\r\n");
        }
        return sb.toString();
    }

    public static boolean isBitSet(byte[] arr, int bit) {
        int index = bit / 8;
        int bitPosition = bit % 8;
        return (arr[index] >> bitPosition & 1) == 1;
    }
}