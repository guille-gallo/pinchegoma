package guillermogallo.com.pinchegoma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static guillermogallo.com.pinchegoma.R.id.map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        OnMapReadyCallback {

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap googleMap;

    private BroadcastReceiver yourReceiver;
    private static final String ACTION_GPS = "android.location.PROVIDERS_CHANGED";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_map);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void buildAlertMessageNoGps() {
        Context context = getApplicationContext();
        CharSequence text = "Por favor habilita el GPS para poder ubicarte";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        googleMap = map;

        createMarkers();
        getLastKnownLocation(googleMap);
    }

    @Override
    public void onConnected(Bundle bundle) { }

    @Override
    public void onLocationChanged(Location location) {
        /*
        * -Ver ventana para cuadras a elegir, ver cálculo distancia.
        * -Sacar login activity,
        *
        * */

        // Personal location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_position)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
    }

    public void createMarkers() {
        //"Gomería Los Gringos"
        LatLng latLng2 = new LatLng(-32.969110, -60.642597);
        googleMap.addMarker(new MarkerOptions().position(latLng2).title("Gomería Los Gringos"));

        //"Neumáticos y Servicios Amante"
        LatLng latLng3 = new LatLng(-32.968575, -60.629303);
        googleMap.addMarker(new MarkerOptions().position(latLng3).title("Neumáticos y Servicios Amante"));

        //"Gomeria Santa Fe 2378"
        LatLng latLng4 = new LatLng(-32.941864, -60.655157);
        googleMap.addMarker(new MarkerOptions().position(latLng4).title("Gomeria Santa Fe 2378"));

        //"Gomería Rubén Cabral S.R.L."
        LatLng latLng5 = new LatLng(-32.951314, -60.672555);
        googleMap.addMarker(new MarkerOptions().position(latLng5).title("Gomería Rubén Cabral S.R.L."));

        //"Gomería Occidente"
        LatLng latLng6 = new LatLng(-32.937949, -60.653278);
        googleMap.addMarker(new MarkerOptions().position(latLng6).title("Gomería Occidente"));
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i(TAG, "Connection Suspended");
        System.out.println("Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        System.out.println("Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        // unregister the receiver
        if (yourReceiver != null) {
            unregisterReceiver(yourReceiver);
            yourReceiver = null;
        }
    }

    @Override protected void onResume() {
        super.onResume();

        checkGPS();
        registerReceiverGPS();
    }

    LocationManager mLocationManager;
    //Location myLocation = getLastKnownLocation();
    private Location getLastKnownLocation(GoogleMap googleMap) {
        mLocationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location l = mLocationManager.getLastKnownLocation(provider);

                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                    // Personal location
                    LatLng latLng = new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_position)));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                }
            } catch (SecurityException ex) {
                showMessage(ex.toString());
            }
        }
        return bestLocation;
    }


    private void checkGPS() {
        /*
        * check if GPS location enabled
        * */
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
    }

    private void registerReceiverGPS() {
        if (yourReceiver == null) {
            // INTENT FILTER FOR GPS MONITORING
            final IntentFilter theFilter = new IntentFilter();
            theFilter.addAction(ACTION_GPS);
            yourReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null) {
                        String s = intent.getAction();
                        if (s != null) {
                            if (s.equals(ACTION_GPS)) {
                                checkGPS();
                            }
                        }
                    }
                }
            };
            registerReceiver(yourReceiver, theFilter);
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}