package com.example.divided.sl13atemplogger;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.divided.sl13atemplogger.model.TempMeasurement;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ResultActivity extends AppCompatActivity {

    public ListenerFromResultActivity activityListener;
    Toolbar mToolbar;
    Spinner toolbarSpinner;
    PopupMenu popupMenu;
    ArrayList<TempMeasurement> tempMeasurements;
    String nfcId;

    public void setResultActivityListener(ListenerFromResultActivity activityListener) {
        this.activityListener = activityListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mToolbar = findViewById(R.id.my_toolbar);
        mToolbar.setTitle("Results");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24px);
        }

        if (this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras();
            tempMeasurements = bundle.getParcelableArrayList("data");
            if (tempMeasurements != null) {
                Toast.makeText(this, Integer.toString(tempMeasurements.size()), Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                TempFragment tempFragment = new TempFragment();
                Bundle fragmentBundle = new Bundle();
                fragmentBundle.putParcelableArrayList("fragment_data", tempMeasurements);
                tempFragment.setArguments(fragmentBundle);
                fragmentTransaction.replace(R.id.fragment_container, tempFragment);
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        toolbarSpinner = (Spinner) item.getActionView();
        toolbarSpinner.setGravity(Gravity.END);
        toolbarSpinner.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.toolbar_spinner_array, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toolbarSpinner.setAdapter(adapter);

        toolbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (null != activityListener) {
                    activityListener.refreshFragmentUI(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        popupMenu = new PopupMenu(ResultActivity.this, menu.getItem(0).getActionView());
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_save, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                View view = getLayoutInflater().inflate(R.layout.dialog_save_layout, null);
                AlertDialog alertDialog = new AlertDialog.Builder(ResultActivity.this).create();
                alertDialog.setTitle("Enter filename");
                alertDialog.setIcon(getResources().getDrawable(R.drawable.ic_baseline_save_dialog));
                alertDialog.setCancelable(true);

                final EditText filenameDialogEditText = view.findViewById(R.id.etComments);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String filename = filenameDialogEditText.getText().toString();
                        switch (item.getItemId()) {
                            case R.id.save_txt:
                                if (saveAsTxt(tempMeasurements, filename)) {
                                    Toast.makeText(getApplicationContext(), "Successfull saved " + filename, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error while saving " + filename, Toast.LENGTH_SHORT).show();
                                }
                                break;

                            case R.id.save_csv:
                                if (saveAsCsv(tempMeasurements, filename)) {
                                    Toast.makeText(getApplicationContext(), "Successfull saved " + filename, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error while saving " + filename, Toast.LENGTH_SHORT).show();
                                }
                                break;

                        }
                    }
                });

                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();
                Toast.makeText(ResultActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            popupMenu.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean saveAsTxt(List<TempMeasurement> measurements, String filename) {
        final String header = nfcId + "_" + new SimpleDateFormat("dd_MM_yyy_HH_mm").format(new Date());
        if (filename.trim().isEmpty()) {
            filename = header;
        }
        String data = Utils.prepareStringData(measurements, header);

        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Temp readings");

        if (!directory.exists()) {
            directory.mkdir();
        }

        File newFile = new File(directory, filename + ".txt");

        if (!newFile.exists()) {
            try {
                newFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            FileOutputStream fOut = new FileOutputStream(newFile);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fOut);
            outputWriter.write(data);
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveAsCsv(List<TempMeasurement> measurements, String filename) {
        if (filename.trim().isEmpty()) {
            filename = nfcId + "_" + new SimpleDateFormat("dd_MM_yyy_HH_mm").format(new Date());
        }

        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "Temp readings");

        if (!directory.exists()) {
            directory.mkdir();
        }

        File newFile = new File(directory, filename + ".csv");
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(newFile));
            List<String[]> data = new ArrayList<>();
            for (TempMeasurement tempMeasurement : measurements) {
                data.add(new String[]{String.format("%f", tempMeasurement.getTimeStamp())
                        , String.format("%.1f", tempMeasurement.getTemperatureCelsius())
                        , String.format("%.1f", tempMeasurement.getTemperatureKelvin())
                        , String.format("%.1f", tempMeasurement.getTemperatureFahrenheit())});
            }
            writer.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public interface ListenerFromResultActivity {
        void refreshFragmentUI(int unit);
    }
}
