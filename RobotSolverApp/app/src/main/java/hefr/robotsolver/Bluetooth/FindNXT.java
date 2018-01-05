/*
 * Copyright (c) 2010 Jacek Fedorynski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file is derived from:
 * 
 * http://developer.android.com/resources/samples/BluetoothChat/src/com/example/android/BluetoothChat/DeviceListActivity.html
 * 
 * Copyright (c) 2009 The Android Open Source Project
 */

package hefr.robotsolver.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import hefr.robotsolver.R;

public class FindNXT extends Activity {

    public static String EXTRA_DEVICE_ADDRESS = "device_address"; //used in Intent
    public static boolean running = false; //used to determined if this activity is already runnin

    private ArrayAdapter<String> devicesArrayAdapter;
    private BluetoothAdapter bluetoothAdapter;

    private TextView heading;
    private ListView devicesList;
    private Button searchButton;

    private final String HEADING_PAIR = "Paired Devices:";
    private final String HEADING_EMPTY = "No Devices Found...";
    private final String HEADING_SEARCHING = "Searching...";
    private final String HEADING_FOUND = "Devices Found:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        running = true;

        setContentView(R.layout.search_devices); //load content
        setResult(Activity.RESULT_CANCELED); //if nothing selected, return cancelled

        heading = (TextView) findViewById(R.id.searchDevicesHeading);
        heading.setText(HEADING_EMPTY);

        searchButton = (Button) findViewById(R.id.searchDevices); //Add action to the search button
        searchButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                search();
                v.setEnabled(false); //make the button inactive during the discovery
            }
        });


        devicesList = (ListView) findViewById(R.id.devicesList);
        devicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name); //to add content to list

        devicesList.setAdapter(devicesArrayAdapter);
        devicesList.setOnItemClickListener(deviceClickListener);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND); //add bluetooth listeners
        this.registerReceiver(broadcastReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(broadcastReceiver, filter);

        addBluetoothPairs(); //show pairs

    }

    /**
     * Adds bluetooth pairs into arrayAdapter
     */
    private void addBluetoothPairs() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : pairedDevices) {
            if ((device.getBluetoothClass() != null) && (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.TOY_ROBOT)) {
                devicesArrayAdapter.add(device.getName() + "\n(" + device.getAddress() + ")" + " (Paired)");
                heading.setText(HEADING_PAIR);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(broadcastReceiver);
        running = false;
    }


    /**
     * Searches other devices
     */
    private void search() {

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
        devicesArrayAdapter.clear();
        heading.setText(HEADING_SEARCHING);
        addBluetoothPairs(); //add also pairs


    }

    private OnItemClickListener deviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            bluetoothAdapter.cancelDiscovery();

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.indexOf("(") + 1, info.indexOf(")"));

            Intent intent = new Intent(); //adds data that will be returned into main activity
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if ((device.getBondState() != BluetoothDevice.BOND_BONDED) && (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.TOY_ROBOT)) {
                    devicesArrayAdapter.add(device.getName() + "\n(" + device.getAddress() + ")");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                searchButton.setEnabled(true); //after search, actives the search button again
                if (devicesArrayAdapter.getCount() > 0) {
                    heading.setText(HEADING_FOUND);
                } else {
                    heading.setText(HEADING_EMPTY);
                }
            }
        }
    };
}
