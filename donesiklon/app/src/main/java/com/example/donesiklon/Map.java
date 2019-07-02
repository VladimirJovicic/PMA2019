package com.example.donesiklon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.model.Direction;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Map extends Fragment implements OnMapReadyCallback, RoutingListener{
    //GoogleMap mGoogleMAp;
    MapView mMapView;
    View mView;
    Double mLatitude = 32.109333;
    Double mLongitude = 34.855499;
  //  MyLocation myLocation;
   // MyLocation.LocationResult locationResult;
    CameraPosition cameraPosition;
    Context context123;
    private List<Polyline> polylines;

    private static final int[] COLORS = new int[]{Color.RED};

    public Map() {
    }

    public void changeCordinate(Double latitude, Double longitude, Context mContext) {
        mLatitude = latitude;
        mLongitude = longitude;
        this.context123 = mContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myLocation = new MyLocation();
        //to start the polyline
        polylines = new ArrayList<>();
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        mView = inflater.inflate(R.layout.fragment_map, container, false);
//        return mView;
//    }


    private Activity mActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            mActivity = (Activity) context;
        }
    }

    double endLat;
    double endLon;

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//
//       // mMapView = mView.findViewById(R.id.map);
//
//
//        Bundle bundle = this.getArguments();
//
//        String endlatString = bundle.getString("endLat");
//        String endLonString = bundle.getString("endLon");
//
//        endLat = Double.valueOf(endlatString);
//         endLon = Double.valueOf(endLonString);
//
//        mapFragment.getMapAsync(new OnMapReadyCallback() {
//
//
//            @Override
//            public void onMapReady(final GoogleMap mMap) {
//                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//                mMap.clear(); //clear old markers
//
//
//                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                    @Override
//                    public void onMyLocationChange(Location location) {
//
//                        Location usersLocation = RestaurantDirections.getUserLocation((MainActivity) mActivity);
//                        double startLat = Double.valueOf(usersLocation.getLatitude());
//                        double startLon = Double.valueOf(usersLocation.getLongitude());
//
//
//                        //when i have the user current location start show his location on the map
//                        MapsInitializer.initialize(getContext());
//                        //map tye(animated , looks like real map etc...)
//                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                        //adds marker in map according to the current user position
//                        //first add marker object
//                        MarkerOptions marker = new MarkerOptions();
//                        //to the above marker object - add information
//                        mMap.addMarker(marker
//                                .position(new LatLng(startLat, startLon))
//                                .snippet("Starting point"))
//                                .setTitle("You");
//
//                        /**add polyline start**/
//                        // Add polyline and polygons to the map. This section shows just
//                        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
//                                .clickable(true)
//                                .add(
//                                        //  new LatLng(31.785964, 34.704885),
//                                        // new LatLng(location.getLatitude(), location.getLongitude()),
//                                        new LatLng(startLat, startLon),
//                                        new LatLng(endLat, endLon)));
//                        getRout((new LatLng(endLat, endLon)), new LatLng(startLat, startLon));
//
//                        /**add polyline end***/
//
//
//                        //move the camera to the current position
//                        cameraPosition = CameraPosition.builder()
//                                .target(new LatLng(startLat, startLon))
//                                .zoom(16)
//                                .bearing(0)
//                                .tilt(45)
//                                .build();
//                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
////                }
////            };
//
//
//                    }
//                });
//            }
//
//
//
//
//        });
//    }

    LatLng userLocation;
  List<LatLng> lista = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle bundle = this.getArguments();

        final String restName = bundle.getString("restName");
        String endlatString = bundle.getString("endLat");
        String endLonString = bundle.getString("endLon");

        endLat = Double.valueOf(endlatString);
        endLon = Double.valueOf(endLonString);


        String distance = bundle.getString("distance");
        String time = bundle.getString("time");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        TextView tv1 = rootView.findViewById(R.id.distanceOnMap);

        tv1.setText(((MainActivity)mActivity).getString(R.string.distance)+" "+distance);

        TextView tv2 = rootView.findViewById(R.id.timeOnMap);
        tv2.setText(((MainActivity)mActivity).getString(R.string.deliveryTime)+" "+time);




        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(final GoogleMap mMap) {
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


//                    RestaurantDirections.getDirections(userLocation.latitude,userLocation.longitude,endLat,endLon);
//                    Direction direction = RestaurantDirections.direction;

 //                    if(direction!=null)
//                        for(com.akexorcist.googledirection.model.Route route : direction.getRouteList()) {
//                         //   Log.i("->" ,route.getLegList().get(0).getDirectionPoint().toString());
//                            lista = route.getOverviewPolyline().getPointList();
//                            break;
//                        }
//                    Log.i("lista:",lista.toString());









                    GoogleDirection.withServerKey("AIzaSyAU8awJCP75PAy9AcocqyJXNtinbEn5CnE")
                            .from(userLocation)
                            .to(new LatLng(endLat, endLon))
                            .avoid(AvoidType.FERRIES)
                            .avoid(AvoidType.HIGHWAYS)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    if (direction.isOK()) {
                                        // Do something
                                        Log.i("onDirectionSucces",direction.getRouteList().toString());




                                        for(com.akexorcist.googledirection.model.Route route : direction.getRouteList()) {
                                            Log.i("->" ,route.getLegList().get(0).getDirectionPoint().toString());
                                            lista = route.getOverviewPolyline().getPointList();
                                            break;
                                        }




                                        //when i have the user current location start show his location on the map
                                        MapsInitializer.initialize(getContext());
                                        //map tye(animated , looks like real map etc...)
                                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        //adds marker in map according to the current user position
                                        //first add marker object
                                        MarkerOptions marker = new MarkerOptions();
                                        //to the above marker object - add information
                                        mMap.addMarker(marker
                                                .position(userLocation)
                                                .snippet("Starting point"))
                                                .setTitle("You");

                                        /**add polyline start**/
                                        // Add polyline and polygons to the map. This section shows just
                                        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                                                .clickable(true)
                                                .addAll(


                                                        lista

                                                )


                                        );
                                        getRout((new LatLng(endLat, endLon)), userLocation);

                                        /**add polyline end***/


                                        //move the camera to the current position
                                        cameraPosition = CameraPosition.builder()
                                                .target(userLocation)
                                                .zoom(16)
                                                .bearing(0)
                                                .tilt(45)
                                                .build();
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));










                                        // mMap.clear();
                                        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(endLat,endLon)).title(restName));




                                    } else {
                                        // Do something
                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something
                                }
                            });









                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

                    LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 200.0f));

                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                   // mMap.clear();

















                    GoogleDirection.withServerKey("AIzaSyAU8awJCP75PAy9AcocqyJXNtinbEn5CnE")
                            .from(userLocation)
                            .to(new LatLng(endLat, endLon))
                            .avoid(AvoidType.FERRIES)
                            .avoid(AvoidType.HIGHWAYS)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    if (direction.isOK()) {
                                        // Do something
                                        Log.i("onDirectionSucces",direction.getRouteList().toString());




                                        for(com.akexorcist.googledirection.model.Route route : direction.getRouteList()) {
                                            Log.i("->" ,route.getLegList().get(0).getDirectionPoint().toString());
                                            lista = route.getOverviewPolyline().getPointList();
                                            break;
                                        }




                                        //when i have the user current location start show his location on the map
                                        MapsInitializer.initialize(getContext());
                                        //map tye(animated , looks like real map etc...)
                                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        //adds marker in map according to the current user position
                                        //first add marker object
                                        MarkerOptions marker = new MarkerOptions();
                                        //to the above marker object - add information
                                        mMap.addMarker(marker
                                                .position(userLocation)
                                                .snippet("Starting point"))
                                                .setTitle("You");

                                        /**add polyline start**/
                                        // Add polyline and polygons to the map. This section shows just
                                        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                                                .clickable(true)
                                                .addAll(


                                                        lista

                                                )


                                        );
                                        getRout((new LatLng(endLat, endLon)), userLocation);

                                        /**add polyline end***/


                                        //move the camera to the current position
                                        cameraPosition = CameraPosition.builder()
                                                .target(userLocation)
                                                .zoom(16)
                                                .bearing(0)
                                                .tilt(45)
                                                .build();
                                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));










                                        // mMap.clear();
                                        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                                        mMap.addMarker(new MarkerOptions().position(new LatLng(endLat,endLon)).title("Destination"));




                                    } else {
                                        // Do something
                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something
                                }
                            });













                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(endLat,endLon)).title(restName));
                }

            }

        });


        return rootView;
    }


    GoogleMap mMap;

    @Override
    public void onMapReady(final GoogleMap mMap) {
        this.mMap = mMap;
        //myLocation.getLocation(getContext(), locationResult);
        mapConfig(this.mMap);
    }

    //a method ot add polyline to google maps
    public void addPolyline(){
        Polyline line = mMap.addPolyline(
                new PolylineOptions().add(
                        new LatLng(mLatitude, mLongitude),
                        new LatLng(32.113618, 34.804972)
                ).width(2).color(Color.BLUE).geodesic(true)
        );
    }

    private void mapConfig(GoogleMap googleMap){
        // TODO: 30/10/2018 make sure that the user uproved location permission
        //  googleMap.setMyLocationEnabled(true); // false to disable
        googleMap.getUiSettings().setZoomControlsEnabled(true); // true to enable
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
    }

    /**************************start listeners for rout*************************************************************/
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
    /*************************end listeners for routend listeners for rout*************************************************************/

//a method to delete the polylines
    private void deletePolyline(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
    //get rout to specific marker
    private void getRout(LatLng start,LatLng end){
        start = new LatLng(18.015365, -77.499382);
        LatLng waypoint= new LatLng(18.01455, -77.499333);
        end = new LatLng(18.012590, -77.500659);

        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(start, waypoint, end)
                .build();
        routing.execute();
    }
}