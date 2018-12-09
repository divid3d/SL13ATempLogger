package com.example.divided.sl13atemplogger;

import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.util.Log;

import java.io.IOException;

public class SL13A {

    public static byte[] getTemperature(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};

        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x20, // Flags
                    (byte) 0xAD, // Command: Get Temperature
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            SL13A.errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static double tempResponseToCelsius(byte[] temperatureResponse) throws IOException {
        if (temperatureResponse.length == 3) {
            int tempCode = ((0x003 & temperatureResponse[2]) << 8) |
                    ((0x0FF & temperatureResponse[1]));
            double tempValue = tempCodeToCelsius(tempCode);
            Log.e("Current temp in celsius", String.valueOf(tempValue));

            return tempValue;
        }
        throw new IOException("Wrong temperature response format");
    }

    private static double tempCodeToCelsius(int tempCode) {
        return 0.169 * tempCode - 92.7 - 0.169 * 32;
    }

    public static byte[] getDataFromReadBlock(byte[] response) throws IOException {
        if (response.length == 5) {
            return new byte[]{response[1], response[2], response[3], response[4]};
        }
        throw new IOException("Wrong response format");
    }

    public static double batteryResponseToVoltage(byte[] batteryRequestResponse, Battery batteryLevel) throws IOException {
        if (batteryRequestResponse.length == 2) {
            switch (batteryLevel) {
                case BATTERY_15V:
                    Log.e("Battery level:", String.valueOf((int) batteryRequestResponse[1] * 3.35 + 860) + " mV");
                    return (int) batteryRequestResponse[1] * 3.35 + 860; // battery voltage in mV

                case BATTERY_3V:
                    Log.e("Battery level:", String.valueOf((int) batteryRequestResponse[1] * 6.32 + 1.62) + " mV");
                    return (int) batteryRequestResponse[1] * 6.32 + 1.62; // battery voltage in mV
            }
        }
        throw new IOException("Battery response wrong format");
    }

    public static byte[] getSystemInformation(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};
        try {
            nfcVTag.close();
            nfcVTag.connect();
            byte[] command = new byte[]{
                    (byte) 0x23, // Flags
                    (byte) 0x2B, // Command: Get Temperature
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);


            response = nfcVTag.transceive(command);
            errorHandler(response);


            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static String getUIDFromSystemInfo(byte[] response) throws IOException {
        if (response.length == 15) {
            byte[] UID = new byte[8];
            System.arraycopy(response, 2, UID, 0, 8);
            return Utils.ConvertHexByteArrayToString(UID);
        }
        throw new IOException("Wrong response format");
    }

    public static String getDSFIDFromSystemInfo(byte[] response) throws IOException {
        if (response.length == 15) {
            byte[] DSFID = new byte[1];
            System.arraycopy(response, 10, DSFID, 0, 1);
            return Utils.ConvertHexByteArrayToString(DSFID);
        }
        throw new IOException("Wrong response format");
    }

    public static String getTagMemorySizeFromSystemInfo(byte[] response) throws IOException {
        if (response.length == 15) {
            byte[] tagMemorySize = new byte[2];
            System.arraycopy(response, 12, tagMemorySize, 0, 2);
            return Utils.ConvertHexByteArrayToString(tagMemorySize);
        }
        throw new IOException("Wrong response format");
    }

    public static byte[] readSingleBlock(Tag tag, byte blockAddress) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};
        try {
            nfcVTag.close();
            nfcVTag.connect();
            byte[] command = new byte[]{
                    (byte) 0x23, // Flags
                    (byte) 0x20, // Command: Read Single Block
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) (blockAddress & 0x0FF)

            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            Log.e("Read block " + String.valueOf(blockAddress), "Value:\t" + Utils.ConvertHexByteArrayToString(response) + "\tResponse length:\t" + response.length);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] resetToReady(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};
        try {
            nfcVTag.close();
            nfcVTag.connect();
            byte[] command = new byte[]{
                    (byte) 0x23, // Flags
                    (byte) 0x26, // Command: Reset To Ready
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,

            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] readBlocks(Tag tag, byte blockAddress, byte numberOfBlocks) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};
        try {
            nfcVTag.close();
            nfcVTag.connect();
            byte[] command = new byte[]{
                    (byte) 0x23, // Flags
                    (byte) 0x23, // Command: Read Blocks
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) (blockAddress & 0x0ff),
                    (byte) (numberOfBlocks & 0x0ff)

            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            Log.e("Read blocs " + String.valueOf(blockAddress) + "\tLength:\t" + String.valueOf(numberOfBlocks), "Value:\t" + Utils.ConvertHexByteArrayToString(response) + "\tResponse length:\t" + response.length);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] writeBlock(Tag tag, byte blockAddress, byte[] data) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};
        try {
            nfcVTag.close();
            nfcVTag.connect();
            byte[] command = new byte[]{
                    (byte) 0x23,
                    (byte) 0x21, // Command: Write Block
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                    (byte) (blockAddress & 0x0ff),
                    data[3], data[2], data[1], data[0]

            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);
            response = nfcVTag.transceive(command);
            errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] initialize(Tag tag, byte[] delayTime, byte[] nbOfUserBlocks) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};
        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x02,  // FLAGS
                    (byte) 0xAC,  // Read single block command
                    0, 0, 0, 0, 0, 0, 0, 0,
                    delayTime[1], delayTime[0],
                    nbOfUserBlocks[1], nbOfUserBlocks[0]};

            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] getCalibrationData(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};
        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x02,  // FLAGS
                    (byte) 0xA9,  // Command: Get Calibration Data
                    0, 0, 0, 0, 0, 0, 0, 0,
            };

            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            byte[] internalSensorCalibrationData = new byte[4];
            byte[] externalSensorCalibrationData = new byte[4];

            System.arraycopy(response, 1, internalSensorCalibrationData, 0, 4);
            System.arraycopy(response, 5, externalSensorCalibrationData, 0, 4);

            Log.e("Int calibration data", Utils.ConvertHexByteArrayToString(internalSensorCalibrationData));
            Log.e("Ext calibration data", Utils.ConvertHexByteArrayToString(externalSensorCalibrationData));

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }


    public static byte[] getBatteryLevel(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};

        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x20, // Flags
                    (byte) 0xAA, // Command: Get Battery level
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] setPassive(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};

        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x20, // Flags
                    (byte) 0xA6, // Command: Get Temperature
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] startLog(Tag tag, byte[] startTime) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};

        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x20, // Flags
                    (byte) 0xA7, // Command: Start Log
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
                    startTime[3], startTime[2], startTime[1], startTime[0]
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] getLogState(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};

        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x20, // Flags
                    (byte) 0xA8, // Command: Get Log State
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            byte[] measurementsStatus = new byte[4];
            System.arraycopy(response, 1, measurementsStatus, 0, 4);

            byte[] limitsCounter = new byte[4];
            System.arraycopy(response, 5, limitsCounter, 0, 4);

            Log.e("Log state:", "Measurements status:\t" + Utils.ConvertHexByteArrayToString(measurementsStatus));
            Log.e("Log state:", "Measurements status int:\t" + String.valueOf(Utils.byteArrayToInt(Utils.reverse(measurementsStatus))));
            Log.e("Log state:", "Limits counter:\t" + Utils.ConvertHexByteArrayToString(limitsCounter));

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] getMeasurementSetup(Tag tag) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};

        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x20, // Flags
                    (byte) 0xA3, // Command: Get Measurements Setup
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            byte[] startTime = new byte[4];
            byte[] logLimits = new byte[4];
            byte[] logMode = new byte[4];
            byte[] delayTime = new byte[4];

            System.arraycopy(response, 1, startTime, 0, 4);
            System.arraycopy(response, 5, logLimits, 0, 4);
            System.arraycopy(response, 9, logMode, 0, 4);
            System.arraycopy(response, 13, delayTime, 0, 4);

            Log.e("Measurement setup:", "Start time:\t" + Utils.ConvertHexByteArrayToString(startTime));
            Log.e("Measurement setup:", "Log limits:\t" + Utils.ConvertHexByteArrayToString(logLimits));
            Log.e("Measurement setup:", "Log mode:\t" + Utils.ConvertHexByteArrayToString(logMode));
            Log.e("Measurement setup:", "Delay time:\t" + Utils.ConvertHexByteArrayToString(delayTime));


            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static double[] getTemperatureCodesFromMemoryBlock(Tag tag, byte memoryBlock) throws IOException {
        byte[] response = readSingleBlock(tag, memoryBlock);


        byte[] data = SL13A.getDataFromReadBlock(response);

        int temperatureCode1 = ((0x003 & data[1]) << 8) | ((0x0FF & data[0]));
        double temperatureValue1 = tempCodeToCelsius(temperatureCode1);
        Log.e("Temperature1:", String.valueOf(temperatureValue1));

        int temperatureCode2 = ((data[2] & 0x0f) << 6) | (data[1] & 0xfc) >>> 2;
        double temperatureValue2 = tempCodeToCelsius(temperatureCode2);
        Log.e("Temperature2:", String.valueOf(temperatureValue2));

        int temperatureCode3 = ((data[3] & 0x3f) << 4) | (data[2] & 0xf0) >>> 4;
        double temperatureValue3 = tempCodeToCelsius(temperatureCode3);
        Log.e("Temperature3:", String.valueOf(temperatureValue3));

        return new double[]{temperatureValue1, temperatureValue2, temperatureValue3};
    }

    public static byte[] setLogMode(Tag tag, byte[] logMode) {
        NfcV nfcVTag = NfcV.get(tag);
        byte[] response = new byte[]{(byte) 0xFF};

        try {
            nfcVTag.close();
            nfcVTag.connect();

            byte[] command = new byte[]{
                    (byte) 0x20, // Flags
                    (byte) 0xA7, // Command: Start Log
                    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // placeholder for tag UID
                    logMode[0], logMode[1], logMode[2], logMode[3]
            };
            System.arraycopy(tag.getId(), 0, command, 2, 8);

            response = nfcVTag.transceive(command);
            errorHandler(response);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }
    }

    public static byte[] composeLogModeParameter() {
        byte[] logModeParameter = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0};
        byte byte4 = Utils.binaryStringToByte("00000001");
        byte byte3 = Utils.binaryStringToByte("00000000");
        byte byte2 = Utils.binaryStringToByte("00000000");
        byte byte1 = Utils.binaryStringToByte("00000000");

        logModeParameter[0] = byte1;
        logModeParameter[1] = byte2;
        logModeParameter[2] = byte3;
        logModeParameter[3] = byte4;

        return logModeParameter;
    }

    public static void errorHandler(byte[] response) throws IOException {
        if (response.length == 2) {
            byte errorCode = response[1];

            switch (errorCode) {
                case (byte) 0x01:
                    Log.e("SLA13A", "Command not supported - wrong command code");
                    throw new IOException("Command not supported - wrong command code");

                case (byte) 0x02:
                    Log.e("SLA13A", "Command not recognized - format error");
                    throw new IOException("Command not recognized - format error");

                case (byte) 0x03:
                    Log.e("SLA13A", "Option not supported");
                    throw new IOException("Option not supported");

                case (byte) 0x0F:
                    Log.e("SLA13A", "Unknown error");
                    throw new IOException("Unknown error");

                case (byte) 0x10:
                    Log.e("SLA13A", "The specified block is not available");
                    throw new IOException("The specified block is not available");

                case (byte) 0x11:
                    Log.e("SLA13A", "The specified block is already locked and cannot be locked again");
                    throw new IOException("The specified block is already locked and cannot be locked again");

                case (byte) 0x12:
                    Log.e("SLA13A", "The specified block is already locked and cannot be written");
                    throw new IOException("The specified block is already locked and cannot be written");

                case (byte) 0xA0:
                    Log.e("SLA13A", "Incorrect password");
                    throw new IOException("Incorrect password");

                case (byte) 0xA1:
                    Log.e("SLA13A", "Log parameters missing");
                    throw new IOException("Log parameters missing");

                case (byte) 0xA2:
                    Log.e("SLA13A", "Battery measurement error");
                    throw new IOException("Battery measurement error");

                case (byte) 0xA3:
                    Log.e("SLA13A", "Temperature measurement error");
                    throw new IOException("Temperature measurement error");

                case (byte) 0xA5:
                    Log.e("SLA13A", "User data area error");
                    throw new IOException("User data area error");

                case (byte) 0xA6:
                    Log.e("SLA13A", "EEPROM collision");
                    throw new IOException("EEPROM collision");

                default:
                    break;
            }
        }
    }

    public enum Battery {
        BATTERY_15V, //battery 1.5V
        BATTERY_3V   //battery  3V
    }
}
