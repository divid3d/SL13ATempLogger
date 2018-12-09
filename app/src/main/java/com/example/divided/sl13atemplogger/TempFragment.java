package com.example.divided.sl13atemplogger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.divided.sl13atemplogger.model.Temp;
import com.example.divided.sl13atemplogger.model.TempMeasurement;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TempFragment extends android.app.Fragment implements ResultActivity.ListenerFromResultActivity {

    ArrayList<TempMeasurement> tempMeasurements = new ArrayList<>();
    LineChart lineChart;
    LineDataSet dataSet;
    List<Entry> chartEntries = new ArrayList<>();
    TextView minTemp;
    TextView maxTemp;
    TextView avgTemp;
    Temp averageTempValue;
    Temp minTempValue;
    Temp maxTempValue;
    YAxis yAxis;

    @Override
    public void refreshFragmentUI(int unit) {
        dataSet.clear();
        chartEntries.clear();
        yAxis.removeAllLimitLines();

        if (unit == Utils.UNIT_KELVIN) {

            minTemp.setText(String.format("%.1f", minTempValue.getKelvin()) + "K");
            maxTemp.setText(String.format("%.1f", maxTempValue.getKelvin()) + "K");
            avgTemp.setText(String.format("%.1f", averageTempValue.getKelvin()) + "K");

            for (TempMeasurement measurement : tempMeasurements) {
                chartEntries.add(new Entry(measurement.getTimeStamp(), measurement.getTemperatureKelvin()));
            }
            dataSet.notifyDataSetChanged();
            LimitLine averageLine = new LimitLine(averageTempValue.getKelvin(), "Avg " + String.format("%.1f", averageTempValue.getKelvin()) + Utils.tempUnitToString(Utils.UNIT_KELVIN));
            LimitLine maxLine = new LimitLine(maxTempValue.getKelvin(), "Max " + String.format("%.1f", maxTempValue.getKelvin()) + Utils.tempUnitToString(Utils.UNIT_KELVIN));
            LimitLine minLine = new LimitLine(minTempValue.getKelvin(), "Min " + String.format("%.1f", minTempValue.getKelvin()) + Utils.tempUnitToString(Utils.UNIT_KELVIN));
            averageLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            maxLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            minLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            yAxis.addLimitLine(averageLine);
            yAxis.addLimitLine(maxLine);
            yAxis.addLimitLine(minLine);
            lineChart.setData(new LineData(dataSet));
            dataSet.notifyDataSetChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate(); // refresh
        } else if (unit == Utils.UNIT_FAHRENHEIT) {
            minTemp.setText(String.format("%.1f", minTempValue.getFahrenheit()) + "F");
            maxTemp.setText(String.format("%.1f", maxTempValue.getFahrenheit()) + "F");
            avgTemp.setText(String.format("%.1f", averageTempValue.getFahrenheit()) + "F");

            for (TempMeasurement measurement : tempMeasurements) {
                chartEntries.add(new Entry(measurement.getTimeStamp(), measurement.getTemperatureFahrenheit()));
            }
            dataSet.notifyDataSetChanged();
            LimitLine averageLine = new LimitLine(averageTempValue.getFahrenheit(), "Avg " + String.format("%.1f", averageTempValue.getFahrenheit()) + Utils.tempUnitToString(Utils.UNIT_FAHRENHEIT));
            LimitLine maxLine = new LimitLine(maxTempValue.getFahrenheit(), "Max " + String.format("%.1f", maxTempValue.getFahrenheit()) + Utils.tempUnitToString(Utils.UNIT_FAHRENHEIT));
            LimitLine minLine = new LimitLine(minTempValue.getFahrenheit(), "Min " + String.format("%.1f", minTempValue.getFahrenheit()) + Utils.tempUnitToString(Utils.UNIT_FAHRENHEIT));
            averageLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            maxLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            minLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            yAxis.addLimitLine(averageLine);
            yAxis.addLimitLine(maxLine);
            yAxis.addLimitLine(minLine);
            lineChart.setData(new LineData(dataSet));
            dataSet.notifyDataSetChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate(); // refresh
        } else {
            minTemp.setText(String.format("%.1f", minTempValue.getCelsius()) + "°C");
            maxTemp.setText(String.format("%.1f", maxTempValue.getCelsius()) + "°C");
            avgTemp.setText(String.format("%.1f", averageTempValue.getCelsius()) + "°C");

            for (TempMeasurement measurement : tempMeasurements) {
                chartEntries.add(new Entry(measurement.getTimeStamp(), measurement.getTemperatureCelsius()));
            }
            dataSet.notifyDataSetChanged();
            LimitLine averageLine = new LimitLine(averageTempValue.getCelsius(), "Avg " + String.format("%.1f", averageTempValue.getCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
            LimitLine maxLine = new LimitLine(maxTempValue.getCelsius(), "Max " + String.format("%.1f", maxTempValue.getCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
            LimitLine minLine = new LimitLine(minTempValue.getCelsius(), "Min " + String.format("%.1f", minTempValue.getCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
            averageLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            maxLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            minLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
            yAxis.addLimitLine(averageLine);
            yAxis.addLimitLine(maxLine);
            yAxis.addLimitLine(minLine);
            lineChart.setData(new LineData(dataSet));
            dataSet.notifyDataSetChanged();
            lineChart.notifyDataSetChanged();
            lineChart.invalidate(); // refresh
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temp_multi_measurment_layout, container, false);

        tempMeasurements = getArguments().getParcelableArrayList("fragment_data");
        Collections.sort(tempMeasurements, new Comparator<TempMeasurement>() {
            @Override
            public int compare(TempMeasurement o1, TempMeasurement o2) {
                return Float.compare(o1.getTimeStamp(), o2.getTimeStamp());
            }
        });

        ((ResultActivity) getActivity()).setResultActivityListener(this);

        minTempValue = Utils.getMinTemp(tempMeasurements);
        maxTempValue = Utils.getMaxTemp(tempMeasurements);
        averageTempValue = Utils.getAverageTemp(tempMeasurements);


        minTemp = view.findViewById(R.id.text_view_temp_min);
        maxTemp = view.findViewById(R.id.text_view_temp_max);
        avgTemp = view.findViewById(R.id.text_view_temp_avg);
        lineChart = view.findViewById(R.id.temp_chart);

        minTemp.setText(String.format("%.1f", minTempValue.getCelsius()) + "°C");
        maxTemp.setText(String.format("%.1f", maxTempValue.getCelsius()) + "°C");
        avgTemp.setText(String.format("%.1f", averageTempValue.getCelsius()) + "°C");


        for (TempMeasurement measurement : tempMeasurements) {
            chartEntries.add(new Entry(measurement.getTimeStamp(), measurement.getTemperatureCelsius()));
        }

        dataSet = new LineDataSet(chartEntries, null);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setCircleColor(getResources().getColor(R.color.colorAccent));
        dataSet.setCircleRadius(3f);
        dataSet.setCircleHoleRadius(100);
        dataSet.setFillColor(getResources().getColor(R.color.colorPrimary));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(20);
        dataSet.setValueTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
        dataSet.setValueTextSize(7f);
        dataSet.setMode(LineDataSet.Mode.LINEAR);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData data = new LineData(dataSets);
        yAxis = lineChart.getAxisLeft();
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_bold.ttf"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.enableGridDashedLine(8f, 8f, 0);

        yAxis.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_bold.ttf"));
        yAxis.setAxisLineWidth(1.5f);
        yAxis.setDrawZeroLine(true);
        yAxis.setZeroLineWidth(1f);
        yAxis.enableGridDashedLine(8f, 8f, 0);
        LimitLine averageLine = new LimitLine(averageTempValue.getCelsius(), "Avg " + String.format("%.1f", averageTempValue.getCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
        LimitLine maxLine = new LimitLine(maxTempValue.getCelsius(), "Max " + String.format("%.1f", maxTempValue.getCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
        LimitLine minLine = new LimitLine(minTempValue.getCelsius(), "Min " + String.format("%.1f", minTempValue.getCelsius()) + Utils.tempUnitToString(Utils.UNIT_CELSIUS));
        averageLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
        maxLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
        minLine.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "product_sans_regular.ttf"));
        yAxis.addLimitLine(averageLine);
        yAxis.addLimitLine(maxLine);
        yAxis.addLimitLine(minLine);
        yAxis.setDrawLimitLinesBehindData(true);

        lineChart.setDescription(null);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setData(data);
        lineChart.invalidate(); // refresh


        return view;
    }


}
