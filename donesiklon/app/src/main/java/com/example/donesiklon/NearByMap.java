package com.example.donesiklon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.RoutingListener;
import com.example.donesiklon.gps.Info;
import com.example.donesiklon.gps.Utils;
import com.example.donesiklon.model.Restaurant;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NearByMap extends Fragment implements OnMapReadyCallback, RoutingListener{
    //GoogleMap mGoogleMAp;
    MapView mMapView;
    View mView;
    Double mLatitude = 32.109333;
    Double mLongitude = 34.855499;
  //  MyLocation myLocation;
   // MyLocation.LocationResult locationResult;
    CameraPosition cameraPosition;
    Context context123;
    private GoogleMap mMap;
    private MapView mapView;
    LocationManager locationManager;
    LocationListener locationListener;
    private MarkerOptions options = new MarkerOptions();
    public List<Restaurant> restsWithLoc = new ArrayList<>();

    private List<Polyline> polylines;

    private static final int[] COLORS = new int[]{Color.RED};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restsWithLoc = new ArrayList<>();












        Address addressInfo;

        Log.i("size: ", String.valueOf(RestaurantListFragment.restaurants.size()));

        for(Restaurant r : RestaurantListFragment.restaurants){
            Log.i("restIs:",r.getAddress());
            addressInfo = getLocationFromAddress(r.getAddress());
            if(addressInfo!=null) {
                Log.i("addressInfo", addressInfo.toString());
                r.setLat(addressInfo.getLatitude());
                r.setLon(addressInfo.getLongitude());
                restsWithLoc.add(r);
            }
            // info = calculateDistance(usersLocation, r);
        }

    }






    LatLng userLocation;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        if (!(ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) ||
                !(ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            LocationManager locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }





        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_near_by_map, container, false);






        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.near_by_map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap mapa) {
                mMap = mapa;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {

                        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                        CameraUpdate zoom=CameraUpdateFactory.zoomTo(11);
                        mMap.moveCamera(center);
                        mMap.animateCamera(zoom);


                    }
                });


                Context c = getActivity().getApplicationContext();




                if (ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                                PackageManager.PERMISSION_GRANTED) {
                    // Permission already Granted


                    LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 1000, null);

                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());


                    centreMapOnLocation(location,getResources().getString(R.string.yourLocation));


//                    GoogleDirection.withServerKey("AIzaSyAU8awJCP75PAy9AcocqyJXNtinbEn5CnE")
//                            .from(userLocation)
//                            .to(new LatLng(endLat, endLon))
//                            .avoid(AvoidType.FERRIES)
//                            .avoid(AvoidType.HIGHWAYS)
//                            .execute(new DirectionCallback() {
//                                @Override
//                                public void onDirectionSuccess(Direction direction, String rawBody) {
//                                    if (direction.isOK()) {
//                                        // Do something
//                                        Log.i("onDirectionSucces",direction.getRouteList().toString());
//
//
//
//
//                                        for(com.akexorcist.googledirection.model.Route route : direction.getRouteList()) {
//                                            Log.i("->" ,route.getLegList().get(0).getDirectionPoint().toString());
//                                            lista = route.getOverviewPolyline().getPointList();
//                                            break;
//                                        }
//
//
//
//
//                                        //when i have the user current location start show his location on the map
//                                        MapsInitializer.initialize(getContext());
//                                        //map tye(animated , looks like real map etc...)
//                                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                                        //adds marker in map according to the current user position
//                                        //first add marker object
//                                        MarkerOptions marker = new MarkerOptions();
//                                        //to the above marker object - add information
//                                        mMap.addMarker(marker
//                                                .position(userLocation)
//                                                .snippet("Starting point"))
//                                                .setTitle("You");
//
//                                        /**add polyline start**/
//                                        // Add polyline and polygons to the map. This section shows just
//                                        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
//                                                .clickable(true)
//                                                .addAll(
//
//
//                                                        lista
//
//                                                )
//
//
//                                        );
//                                        getRout((new LatLng(endLat, endLon)), userLocation);
//
//                                        /**add polyline end***/
//
//
//                                        //move the camera to the current position
//                                        cameraPosition = CameraPosition.builder()
//                                                .target(userLocation)
//                                                .zoom(16)
//                                                .bearing(0)
//                                                .tilt(45)
//                                                .build();
//                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//
//
//
//
//
//
//
//
//                                        // mMap.clear();
//                                        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
//                                        mMap.addMarker(new MarkerOptions().position(new LatLng(endLat,endLon)).title(restName));
//
//
//
//
//                                    } else {
//                                        // Do something
//                                    }
//                                }
//
//                                @Override
//                                public void onDirectionFailure(Throwable t) {
//                                    // Do something
//                                }
//                            });









                } else {

                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                    LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);










                }

            }

        });


        return rootView;
    }














    private Activity mActivity;

    public Info calculateDistance(Location usersLocation, Restaurant restaurant){

        Info info = new Info();


        if(usersLocation !=null) {
            info = Utils.getRealDistance(
                    usersLocation.getLatitude(), usersLocation.getLongitude(),
                    restaurant.getLat(), restaurant.getLon());
            return info;
        }

        info.setDistance("Not Determined");
        info.setDuration("Not Known");

        return info;
    }

    public Address getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this.mActivity.getApplicationContext());
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            if(address.size()>0) {
                Address location = address.get(0);
                Log.i("address",location.toString());
                location.getLatitude();
                location.getLongitude();

                return location;
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = mActivity.getIntent();
        if (intent.getIntExtra("Place Number",0) == 0 ){

            locationManager = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // centreMapOnLocation(location,getResources().getString(R.string.yourLocation));
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centreMapOnLocation(lastKnownLocation,getResources().getString(R.string.yourLocation));
            } else {

                ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (Activity) context;
        }
    }
    Info info = null;
    public void centreMapOnLocation(Location location, String title){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();

            LatLng latLng;
            for (Restaurant r : restsWithLoc) {
                latLng = new LatLng(r.getLat(),r.getLon());
                info =calculateDistance(location, r);
                options.position(latLng);
                options.title(r.getName() );
                options.snippet(r.getAddress()+"\n"+this.getString(R.string.distance)+" "+info.getDistance()
                        +"\n"+this.getString(R.string.deliveryTime)+" "+info.getDuration()+"\n"+r.getDescription());
                mMap.addMarker(options);






            }

            mMap.addMarker(new MarkerOptions().position(userLocation).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(mActivity.getApplicationContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(mActivity.getApplicationContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(mActivity.getApplicationContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });


            mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    LatLng latLon = marker.getPosition();


                    for(Restaurant r : RestaurantListFragment.restaurants){
                        if (latLon.latitude == r.getLat() && latLon.longitude == r.getLon()){


                            Fragment fragment = new RestaurantReview();
                            Bundle args = new Bundle();
                            args.putString("id", r.getId());
                            args.putString("restName", r.getName());
                            args.putString("restAddress", r.getAddress());
                            fragment.setArguments(args);
                            FragmentManager fragmentManager = ((MainActivity)mActivity).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, fragment, "REVIEW_FRAG");
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            return;

                        }

                    }
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastKnownLocation!=null)
                    centreMapOnLocation(lastKnownLocation,getResources().getString(R.string.yourLocation));
            }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            System.out.println("you got rout error " + e.getMessage());
        }
    }
    @Override
    public void onRoutingStart() {
    }
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRoutIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {
            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(context123,"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();

        }
    }
    @Override
    public void onRoutingCancelled() {
    }

}