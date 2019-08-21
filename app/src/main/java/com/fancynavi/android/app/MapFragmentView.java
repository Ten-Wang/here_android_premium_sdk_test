package com.fancynavi.android.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.fancynavi.app.R;
import com.here.android.mpa.common.CopyrightLogoPosition;
import com.here.android.mpa.common.DataNotReadyException;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.MapSettings;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.common.PositioningManager.OnPositionChangedListener;
import com.here.android.mpa.common.RoadElement;
import com.here.android.mpa.common.TrafficSign;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.customlocation2.CLE2DataManager;
import com.here.android.mpa.customlocation2.CLE2Geometry;
import com.here.android.mpa.customlocation2.CLE2ProximityRequest;
import com.here.android.mpa.customlocation2.CLE2Request;
import com.here.android.mpa.customlocation2.CLE2Result;
import com.here.android.mpa.guidance.LaneInformation;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.SafetySpotNotification;
import com.here.android.mpa.guidance.SafetySpotNotificationInfo;
import com.here.android.mpa.guidance.TrafficNotification;
import com.here.android.mpa.guidance.TrafficNotificationInfo;
import com.here.android.mpa.guidance.TrafficWarner;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoiceGuidanceOptions;
import com.here.android.mpa.mapping.LocalMesh;
import com.here.android.mpa.mapping.Location;
import com.here.android.mpa.mapping.LocationInfo;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapCartoMarker;
import com.here.android.mpa.mapping.MapCircle;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapLocalModel;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapOverlay;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.MapState;
import com.here.android.mpa.mapping.OnMapRenderListener;
import com.here.android.mpa.mapping.SafetySpotObject;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.mapping.TrafficEvent;
import com.here.android.mpa.mapping.TrafficEventObject;
import com.here.android.mpa.mapping.customization.CustomizableScheme;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.DynamicPenalty;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.ReverseGeocodeMode;
import com.here.android.mpa.search.ReverseGeocodeRequest;
import com.here.android.mpa.search.SearchRequest;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalView;
import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewPresenter;
import com.here.msdkui.guidance.GuidanceManeuverData;
import com.here.msdkui.guidance.GuidanceManeuverListener;
import com.here.msdkui.guidance.GuidanceManeuverPresenter;
import com.here.msdkui.guidance.GuidanceManeuverView;
import com.here.msdkui.guidance.GuidanceNextManeuverData;
import com.here.msdkui.guidance.GuidanceNextManeuverListener;
import com.here.msdkui.guidance.GuidanceNextManeuverPresenter;
import com.here.msdkui.guidance.GuidanceNextManeuverView;
import com.here.msdkui.guidance.GuidanceSpeedLimitView;
import com.here.msdkui.guidance.GuidanceSpeedPresenter;
import com.here.msdkui.guidance.GuidanceSpeedView;
import com.here.msdkui.guidance.GuidanceStreetLabelData;
import com.here.msdkui.guidance.GuidanceStreetLabelListener;
import com.here.msdkui.guidance.GuidanceStreetLabelPresenter;
import com.here.msdkui.guidance.GuidanceStreetLabelView;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.fancynavi.android.app.MainActivity.isMapRotating;
import static com.fancynavi.android.app.MainActivity.textToSpeech;
import static java.util.Locale.TRADITIONAL_CHINESE;

//import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewData;
//import com.here.msdkui.guidance.GuidanceEstimatedArrivalViewListener;
//import com.here.msdkui.guidance.GuidanceSpeedData;
//import com.here.msdkui.guidance.GuidanceSpeedListener;

class MapFragmentView {
    static boolean isDragged;
    static Map m_map;
    static GeoPosition currentGeoPosition;
    static NavigationManager m_navigationManager;
    static Button m_naviControlButton;
    static Button clearButton;
    static PositioningManager m_positioningManager;
    static GeoBoundingBox mapRouteBBox;
    static MapOverlay laneMapOverlay;
    static ImageView junctionViewImageView;
    static ImageView signpostImageView;
    static boolean isRoadView = false;
    static boolean isSatMap = false;
    static SupportMapFragment supportMapFragment;
    static boolean isRouteOverView;
    static boolean isNavigating;
    static boolean isSignShowing;
    static MapLocalModel currentPositionMapLocalModel;
    static Button northUpButton;
    private static OnTouchListener emptyMapOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    };
    static OnTouchListener mapOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            m_navigationManager.pause();
            isRoadView = false;
            isRouteOverView = true;
            m_map.removeMapOverlay(laneMapOverlay);
            m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
            new ShiftMapCenter(m_map, 0.5f, 0.6f);
            m_map.setTilt(0);
            m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, 0f);
            m_naviControlButton.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
            junctionViewImageView.setAlpha(0f);
            signpostImageView.setAlpha(0f);
            supportMapFragment.setOnTouchListener(emptyMapOnTouchListener);
            return false;
        }
    };
    private CLE2ProximityRequest cle2ProximityRequest;
    private LinearLayout distanceMarkerLinearLayout;
    private ImageView distanceMarkerFreeIdImageView;
    private TextView distanceMarkerDistanceValue;
    private TrafficWarner trafficWarner;
    private boolean isPositionLogging = false;
    private MapSchemeChanger mapSchemeChanger;
    private LinearLayout laneDcmLinearLayout;
    private LinearLayout safetyCamLinearLayout;
    private LinearLayout laneInfoLinearLayoutOverlay;
    private double distanceToSafetyCamera;
    private HereRouter hereRouter;
    private List<TrafficSign> lastTrafficSignList = new ArrayList<>();
    private RoadElement lastRoadElement;
    private String signName;
    private ElectronicHorizonActivation electronicHorizonActivation;
    private Snackbar searchResultSnackbar;
    private String searchResultString;
    private MapMarker selectedFeatureMapMarker;
    private MapCircle positionAccuracyMapCircle;
    private ImageView gpsStatusImageView;
    private ImageView signImageView1;
    private ImageView signImageView2;
    private ImageView signImageView3;
    private Switch gpsSwitch;
    private boolean safetyCameraAhead;
    private GeoCoordinate safetyCameraLocation;
    private double safetyCameraSpeedLimit;
    private int safetyCameraSpeedLimitKM;
    private ImageView safetyCamImageView;
    private TextView safetyCamTextView;
    private TextView safetyCamSpeedTextView;
    private MapMarker safetyCameraMapMarker;
    private int speedLimitLinearLayoutHeight;
    private View speedLimitLinearLayout;
    private AppCompatActivity m_activity;
    private VoiceActivation voiceActivation;
    private Button zoomInButton;
    private Button zoomOutButton;
    private Button carRouteButton;
    private Button truckRouteButton;
    private Button scooterRouteButton;
    private Button bikeRouteButton;
    private Button pedsRouteButton;
    private Button trafficButton;
    private Button satMapButton;
    private Button searchButton;
    private EditText searchTextBar;
    private LinearLayout searchBarLinearLayout;
    private boolean trafficEnabled;
    private ProgressBar progressBar;
    private TextView calculatingTextView;
    private TextView trafficWarningTextView;
    private Route m_route;
    private MapRoute mapRoute;
    private boolean m_foregroundServiceStarted;
    private CoreRouter coreRouter;
    //HERE SDK UI KIT components
    private GuidanceManeuverView guidanceManeuverView;
    private GuidanceManeuverPresenter guidanceManeuverPresenter;
    private GuidanceEstimatedArrivalViewPresenter guidanceEstimatedArrivalViewPresenter;
    private GuidanceStreetLabelPresenter guidanceStreetLabelPresenter;
    private GuidanceEstimatedArrivalView guidanceEstimatedArrivalView;
    private GuidanceSpeedLimitView guidanceSpeedLimitView;
    private GuidanceSpeedPresenter guidanceSpeedPresenter;
    private GuidanceStreetLabelView guidanceStreetLabelView;
    private GuidanceNextManeuverView guidanceNextManeuverView;
    private GuidanceNextManeuverPresenter guidanceNextManeuverPresenter;
    private GuidanceSpeedView guidanceSpeedView;
    private ArrayList<GeoCoordinate> waypointList = new ArrayList<>();
    private ArrayList<MapMarker> userInputWaypoints = new ArrayList<>();
    private ArrayList<MapMarker> wayPointIcons = new ArrayList<>();
    private ArrayList<MapMarker> placeSearchResultIcons = new ArrayList<>();
    private String diskCacheRoot = Environment.getExternalStorageDirectory().getPath() + File.separator + ".isolated-here-maps";
    //HERE UI Kit
    private long simulationSpeedMs = 20; //defines the speed of navigation simulation
    private GeoCoordinate lastKnownLocation;
    private double proceedingDistance = 0;
    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {
//            if (electronicHorizonActivation != null) {
//                electronicHorizonActivation.startElectronicHorizonUpdate();
//            }
            if (lastKnownLocation != null) {
                proceedingDistance = lastKnownLocation.distanceTo(geoPosition.getCoordinate());
                if (lastKnownLocation.distanceTo(geoPosition.getCoordinate()) > 0) {
                    lastKnownLocation = geoPosition.getCoordinate();
                }
            } else {
                lastKnownLocation = geoPosition.getCoordinate();
                CLE2ProximityRequest cle2ProximityRequest = new CLE2ProximityRequest("TWN_HWAY_DIST_MARKER", geoPosition.getCoordinate(), 30000);
                cle2ProximityRequest.setCachingEnabled(true);
                cle2ProximityRequest.execute(new CLE2Request.CLE2ResultListener() {
                    @Override
                    public void onCompleted(CLE2Result cle2Result, String s) {
                        Log.d("Test", "cle2Result.getGeometries().size(): " + cle2Result.getGeometries().size());
                        int numberOfStoredGeometries = CLE2DataManager.getInstance().getNumberOfStoredGeometries("TWN_HWAY_DIST_MARKER");
                        Log.d("Test", "CLE2ProximityRequest numberOfStoredGeometries: " + numberOfStoredGeometries);
                    }
                });
            }
            GeoCoordinate geoPositionGeoCoordinate = geoPosition.getCoordinate();
            geoPositionGeoCoordinate.setAltitude(1);
            currentPositionMapLocalModel.setAnchor(geoPositionGeoCoordinate);
            currentPositionMapLocalModel.setYaw((float) geoPosition.getHeading());
        }

    };

    //    private void initGuidanceEstimatedArrivalView(NavigationManager navigationManager) {
//        guidanceEstimatedArrivalView = m_activity.findViewById(R.id.guidance_estimated_arrival_view);
//        guidanceEstimatedArrivalViewPresenter = new GuidanceEstimatedArrivalViewPresenter(navigationManager);
//        guidanceEstimatedArrivalViewPresenter.addListener(new GuidanceEstimatedArrivalViewListener() {
//            @Override
//            public void onDataChanged(GuidanceEstimatedArrivalViewData guidanceEstimatedArrivalViewData) {
//                Log.d("Test", "onDataChanged");
//                guidanceEstimatedArrivalView.setEstimatedArrivalData(guidanceEstimatedArrivalViewData);
//                if (guidanceEstimatedArrivalViewData != null) {
//                    Log.d("Test", "guidanceEstimatedArrivalViewData " + guidanceEstimatedArrivalViewData.getEta());
//                }
//            }
//        });
//    }
    private NavigationManager.LaneInformationListener m_LaneInformationListener = new NavigationManager.LaneInformationListener() {

        @Override
        public void onLaneInformation(List<LaneInformation> list, RoadElement roadElement) {
            super.onLaneInformation(list, roadElement);
            boolean isLaneDisplayed = false;
            if (laneMapOverlay != null) {
                m_map.removeMapOverlay(laneMapOverlay);
            }
            laneDcmLinearLayout = new LinearLayout(m_activity);
            laneDcmLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
            laneDcmLinearLayout.setVisibility(View.VISIBLE);
            laneInfoLinearLayoutOverlay = new LinearLayout(m_activity);
            laneInfoLinearLayoutOverlay.setOrientation(LinearLayout.VERTICAL);
            laneInfoLinearLayoutOverlay.setVisibility(View.VISIBLE);
            if (list.size() > 0) {
                for (LaneInformation laneInformation : list) {
                    LaneInformation.RecommendationState recommendationState = laneInformation.getRecommendationState();
                    EnumSet<LaneInformation.Direction> directions = laneInformation.getDirections();
                    int laneDirectionCategory = 0;
                    ImageView laneDcmImageView = new ImageView(m_activity);
                    for (LaneInformation.Direction direction : directions) {
                        laneDirectionCategory += direction.value();
                    }
                    switch (laneDirectionCategory) {
                        case 1:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_1);
                            isLaneDisplayed = true;
                            break;
                        case 2:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2);
                            isLaneDisplayed = true;
                            break;
                        case 3:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_3);
                            isLaneDisplayed = true;
                            break;
                        case 4:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_4);
                            isLaneDisplayed = true;
                            break;
                        case 5:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_5);
                            isLaneDisplayed = true;
                            break;
                        case 6:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_6);
                            isLaneDisplayed = true;
                            break;
                        case 9:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_9);
                            isLaneDisplayed = true;
                            break;
                        case 16:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_16);
                            isLaneDisplayed = true;
                            break;
                        case 17:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_17);
                            isLaneDisplayed = true;
                            break;
                        case 64:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_64);
                            isLaneDisplayed = true;
                            break;
                        case 65:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_65);
                            isLaneDisplayed = true;
                            break;
                        case 67:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_67);
                            isLaneDisplayed = true;
                            break;
                        case 68:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_68);
                            isLaneDisplayed = true;
                            break;
                        case 69:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_69);
                            isLaneDisplayed = true;
                            break;
                        case 80:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_80);
                            isLaneDisplayed = true;
                            break;
                        case 128:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_128);
                            isLaneDisplayed = true;
                            break;
                        case 129:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_129);
                            isLaneDisplayed = true;
                            break;
                        case 130:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_130);
                            isLaneDisplayed = true;
                            break;
                        case 131:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_131);
                            isLaneDisplayed = true;
                            break;
                        case 192:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_192);
                            isLaneDisplayed = true;
                            break;
                        case 256:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_256);
                            isLaneDisplayed = true;
                            break;
                        case 257:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_257);
                            isLaneDisplayed = true;
                            break;
                        case 512:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_512);
                            isLaneDisplayed = true;
                            break;
                        case 513:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_513);
                            isLaneDisplayed = true;
                            break;
                        case 2048:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2048);
                            isLaneDisplayed = true;
                            break;
                        case 2049:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2049);
                            isLaneDisplayed = true;
                            break;
                        case 2052:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2052);
                            isLaneDisplayed = true;
                            break;
                        case 2053:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2053);
                            isLaneDisplayed = true;
                            break;
                        case 2056:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2056);
                            isLaneDisplayed = true;
                            break;
                        case 2112:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2112);
                            isLaneDisplayed = true;
                            break;
                        case 2113:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_2113);
                            isLaneDisplayed = true;
                            break;
                        case 4096:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_4096);
                            isLaneDisplayed = true;
                            break;
                        case 8192:
                            laneDcmImageView.setImageResource(R.drawable.ic_lane_dcm_8192);
                            isLaneDisplayed = true;
                            break;
                        default:
                            laneDcmImageView.setImageResource(R.drawable.transparent_24px);
                            laneDcmImageView.setAlpha(0.3f);
                    }
                    laneDcmImageView.setCropToPadding(false);
                    if (recommendationState == LaneInformation.RecommendationState.HIGHLY_RECOMMENDED) {
                        laneDcmImageView.setBackgroundColor(Color.argb(255, 0, 160, 0));
                    } else if (recommendationState == LaneInformation.RecommendationState.RECOMMENDED) {
                        laneDcmImageView.setBackgroundColor(Color.argb(64, 0, 128, 0));
                    } else {
                        laneDcmImageView.setBackgroundColor(Color.argb(32, 64, 64, 64));
                    }
                    int laneDcmImageViewPadding = (int) DpConverter.convertDpToPixel(4, m_activity);
                    laneDcmLinearLayout.addView(laneDcmImageView);
                    laneDcmImageView.setPadding(laneDcmImageViewPadding, laneDcmImageViewPadding, laneDcmImageViewPadding, laneDcmImageViewPadding);
                }
                laneInfoLinearLayoutOverlay.addView(laneDcmLinearLayout);
                ImageView downArrowImageView = new ImageView(m_activity);
                downArrowImageView.setImageResource(R.drawable.ic_arrow_point_to_down);
                laneInfoLinearLayoutOverlay.addView(downArrowImageView);

                laneInfoLinearLayoutOverlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        laneInfoLinearLayoutOverlay.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        laneMapOverlay.setAnchorPoint(getMapOverlayAnchorPoint(laneInfoLinearLayoutOverlay.getWidth(), laneInfoLinearLayoutOverlay.getHeight()));
                    }
                });
                laneMapOverlay = new MapOverlay(laneInfoLinearLayoutOverlay, roadElement.getGeometry().get(roadElement.getGeometry().size() - 1));
                if (!isRouteOverView && isLaneDisplayed) {
                    m_map.addMapOverlay(laneMapOverlay);
                }
            }
        }
    };

    //    private void initGuidanceSpeedView(NavigationManager navigationManager, PositioningManager positioningManager) {
//        guidanceSpeedLimitView = m_activity.findViewById(R.id.guidance_speed_limit_view);
//        guidanceSpeedView = m_activity.findViewById(R.id.guidance_speed_view);
//        guidanceSpeedPresenter = new GuidanceSpeedPresenter(navigationManager, positioningManager);
//        guidanceSpeedPresenter.addListener(new GuidanceSpeedListener() {
//            @Override
//            public void onDataChanged(@Nullable GuidanceSpeedData guidanceSpeedData) {
//                guidanceSpeedLimitView.setCurrentSpeedData(guidanceSpeedData);
//                guidanceSpeedView.setCurrentSpeedData(guidanceSpeedData);
//            }
//        });
//    }
    private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
        }

        @Override
        public void onNavigationModeChanged() {
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
            m_navigationManager.removeLaneInformationListener(m_LaneInformationListener);
            distanceMarkerLinearLayout.setVisibility(View.GONE);
            mapSchemeChanger.navigationMapOff();
            isNavigating = false;
            isRoadView = false;
            isRouteOverView = true;
            junctionViewImageView.setVisibility(View.GONE);
            signpostImageView.setVisibility(View.GONE);
            m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.NONE);
            new ShiftMapCenter(m_map, 0.5f, 0.6f);
            m_map.setTilt(0);
            m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, 0f);
            m_naviControlButton.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
            stopForegroundService();
        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
//            Log.d("Test", "mapUpdateMode is: " + mapUpdateMode);
        }

        @Override
        public void onRouteUpdated(Route route) {
            resetMapRoute(route);
        }

        @Override
        public void onCountryInfo(String s, String s1) {
        }
    };
    /* Navigation Listeners */
    private TrafficWarner.Listener trafficWarnerListener = new TrafficWarner.Listener() {
        @Override
        public void onTraffic(TrafficNotification trafficNotification) {
            String warningText = "";
            List<TrafficNotificationInfo> trafficNotificationInfoList = trafficNotification.getInfoList();
            for (TrafficNotificationInfo trafficNotificationInfo : trafficNotificationInfoList) {
                TrafficNotificationInfo.Type trafficNotificationInfoType = trafficNotificationInfo.getType();
                TrafficEvent.Severity trafficEventSeverity = trafficNotificationInfo.getSeverity();
                if (trafficEventSeverity == TrafficEvent.Severity.VERY_HIGH || trafficEventSeverity == TrafficEvent.Severity.BLOCKING) {
                    long trafficNotificationInfoDistance = trafficNotificationInfo.getDistanceInMeters();
                    if (trafficNotificationInfoDistance < 1000 && trafficNotificationInfoDistance > 0) {
                        switch (trafficNotificationInfoType) {
                            case ON_ROUTE:
                                if ((trafficNotificationInfoDistance / 100) * 100 > 0) {
                                    warningText = (trafficNotificationInfoDistance / 100) * 100 + "m 後\n壅塞路段";
                                    textToSpeech.speak((trafficNotificationInfoDistance / 100) * 100 + "公尺後經過壅塞路段，請小心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
                                    warningText = "經過\n壅塞路段";
                                    textToSpeech.speak("經過壅塞路段，請耐心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                }
                                break;
                            case ON_HIGHWAY:
                                if ((trafficNotificationInfoDistance / 100) * 100 > 0) {
                                    warningText = (trafficNotificationInfoDistance / 100) * 100 + "m 後\n壅塞路段";
                                    textToSpeech.speak((trafficNotificationInfoDistance / 100) * 100 + "公尺後經過壅塞路段，請耐心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
                                    warningText = "經過\n壅塞路段";
                                    textToSpeech.speak("經過壅塞路段，請耐心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                }
                                break;
                            case NEAR_DESTINATION:
                                warningText = "目的地\n附近壅塞";
                                textToSpeech.speak("目的地附近為壅塞路段，請小心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
                                break;
                        }
                        showTrafficWarningTextView(trafficWarningTextView, warningText);
                    }
                }


            }
        }
    };
    private NavigationManager.TrafficRerouteListener m_trafficRerouteListener = new NavigationManager.TrafficRerouteListener() {
        @Override
        public void onTrafficRerouted(RouteResult routeResult) {
            super.onTrafficRerouted(routeResult);
            textToSpeech.speak("路徑已避開壅塞路段，請小心駕駛。", TextToSpeech.QUEUE_FLUSH, null);
            resetMapRoute(routeResult.getRoute());
            Log.d("test", "traffic rerouted.");
        }

        @Override
        public void onTrafficRerouteFailed(TrafficNotification trafficNotification) {
            super.onTrafficRerouteFailed(trafficNotification);
        }

        @Override
        public void onTrafficRerouteBegin(TrafficNotification trafficNotification) {
            super.onTrafficRerouteBegin(trafficNotification);
        }

        @Override
        public void onTrafficRerouteState(TrafficEnabledRoutingState trafficEnabledRoutingState) {
            super.onTrafficRerouteState(trafficEnabledRoutingState);

        }
    };
    private NavigationManager.RerouteListener m_rerouteListener = new NavigationManager.RerouteListener() {
        @Override
        public void onRerouteBegin() {
            super.onRerouteBegin();
            safetyCameraAhead = false;
            safetyCameraMapMarker.setTransparency(0);
            safetyCamLinearLayout.setVisibility(View.GONE);
//            gpsStatusImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRerouteEnd(RouteResult routeResult, RoutingError routingError) {
            super.onRerouteEnd(routeResult, routingError);
            if (routingError == RoutingError.NONE) {
                resetMapRoute(routeResult.getRoute());
            }
        }
    };
    private NavigationManager.RealisticViewListener m_realisticViewListener = new NavigationManager.RealisticViewListener() {
        @Override
        public void onRealisticViewNextManeuver(NavigationManager.AspectRatio aspectRatio, Image junction, Image signpost) {
        }

        @Override
        public void onRealisticViewShow(NavigationManager.AspectRatio aspectRatio, Image junction, Image signpost) {
            View mainLinearLayout = m_activity.findViewById(R.id.main_linear_layout);
            junctionViewImageView.requestLayout();
            signpostImageView.requestLayout();
            int jvViewWidth = (int) (mainLinearLayout.getWidth() / 2.5);
            int jvViewHeight;
            switch (aspectRatio) {
                case AR_16x9:
                    jvViewHeight = jvViewWidth / 16 * 9;
                    break;
                case AR_5x3:
                    jvViewHeight = jvViewWidth / 5 * 3;
                    break;
                case AR_4x3:
                    jvViewHeight = jvViewWidth / 4 * 3;
                    break;
                default:
                    jvViewHeight = jvViewWidth * 2;
            }
            junctionViewImageView.getLayoutParams().height = jvViewHeight;
            junctionViewImageView.getLayoutParams().width = jvViewWidth;
            signpostImageView.getLayoutParams().height = jvViewHeight;
            signpostImageView.getLayoutParams().width = jvViewWidth;
            Bitmap junctionBitmap = junction.getBitmap((int) junction.getWidth(), (int) junction.getHeight());
            Bitmap signpostBitMap = signpost.getBitmap((int) signpost.getWidth(), (int) signpost.getHeight());
            junctionViewImageView.setImageBitmap(junctionBitmap);
            signpostImageView.setImageBitmap(signpostBitMap);
            junctionViewImageView.setVisibility(View.VISIBLE);
            signpostImageView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRealisticViewHide() {
            junctionViewImageView.setVisibility(View.GONE);
            signpostImageView.setVisibility(View.GONE);
        }
    };
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
        }
    };
    private NavigationManager.SafetySpotListener safetySpotListener = new NavigationManager.SafetySpotListener() {
        @Override
        public void onSafetySpot(SafetySpotNotification safetySpotNotification) {
            super.onSafetySpot(safetySpotNotification);
            List<SafetySpotNotificationInfo> safetySpotNotificationInfoList = safetySpotNotification.getSafetySpotNotificationInfos();
            for (int i = 0; i < safetySpotNotificationInfoList.size(); i++) {
                SafetySpotNotificationInfo safetySpotInfo = safetySpotNotificationInfoList.get(i);
                safetyCameraLocation = safetySpotInfo.getSafetySpot().getCoordinate();
                /* Adding MapMarker to indicate selected safety camera */
                safetyCameraMapMarker.setCoordinate(safetyCameraLocation);
                Image icon = new Image();
                icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(m_activity, R.drawable.ic_pin, 128, 128));
                safetyCameraMapMarker.setIcon(icon);
                safetyCameraMapMarker.setAnchorPoint(getMapMarkerAnchorPoint(safetyCameraMapMarker));
                safetyCameraMapMarker.setTransparency(1);
                distanceToSafetyCamera = safetySpotInfo.getDistance();
                safetyCameraSpeedLimit = safetySpotInfo.getSafetySpot().getSpeedLimit1();
                if (safetyCameraSpeedLimit * 3.6 % 10 >= 8 || safetyCameraSpeedLimit * 3.6 % 10 <= 2) {
                    safetyCameraSpeedLimitKM = (int) ((Math.round((safetyCameraSpeedLimit * 3.6) / 10)) * 10);
                } else {
                    safetyCameraSpeedLimitKM = (int) (Math.round((safetyCameraSpeedLimit * 3.6)));
                }
                textToSpeech.speak("前方有測速照相，速限：" + safetyCameraSpeedLimitKM + "公里", TextToSpeech.QUEUE_FLUSH, null);
                safetyCameraAhead = true;
            }
        }
    };
    private OnPositionChangedListener positionChangedListener = new OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {

            GeoCoordinate geoPositionGeoCoordinate = geoPosition.getCoordinate();
            geoPositionGeoCoordinate.setAltitude(1);
            GeoCoordinate geoPositionGeoCoordinateOnGround = geoPosition.getCoordinate();
            geoPositionGeoCoordinateOnGround.setAltitude(0);

            if (!isNavigating && m_map.getZoomLevel() >= 17) {
                positionAccuracyMapCircle.setCenter(geoPositionGeoCoordinateOnGround);
                float radius = (geoPosition.getLatitudeAccuracy() + geoPosition.getLongitudeAccuracy()) / 2;
                if (radius > 0) {
                    positionAccuracyMapCircle.setRadius(radius);
                }
            }

            if (m_positioningManager.getRoadElement() != null) {
                RoadElement roadElement = m_positioningManager.getRoadElement();
                List<GeoCoordinate> geoCoordinateList = roadElement.getGeometry();
                RoadElement.FormOfWay formOfWay = roadElement.getFormOfWay();
                String routeName = roadElement.getRouteName();
                String roadName = roadElement.getRoadName();
                Log.d("test", formOfWay + " \\ " + routeName + " \\ " + roadName);
                if (isNavigating) {
                    if (formOfWay == RoadElement.FormOfWay.MOTORWAY && routeName != null) {
                        String layerId = "TWN_HWAY_DIST_MARKER";
                        int radius = 150;
                        cle2ProximityRequest = new CLE2ProximityRequest(layerId, geoPositionGeoCoordinateOnGround, radius);
                        cle2ProximityRequest.setConnectivityMode(CLE2Request.CLE2ConnectivityMode.AUTO);
                        cle2ProximityRequest.setCachingEnabled(true);
                        cle2ProximityRequest.execute(new CLE2Request.CLE2ResultListener() {
                            @Override
                            public void onCompleted(CLE2Result result, String error) {
                                Log.d("test", "cle2ProximityRequest completed: " + result.getConnectivityModeUsed());
                                /*Display mileage and route number on the upper right corner*/
                                if (error.equals(CLE2Request.CLE2Error.NONE)) {
                                    List<CLE2Geometry> geometries = result.getGeometries();
                                    Log.d("test", "geometries " + geometries.toString());

                                    if (geometries.size() > 0) {
                                        CLE2Geometry geometry = geometries.get(0);
                                        java.util.Map<String, String> geometryAttributeMap = geometry.getAttributes();
                                        String distanceValue = geometryAttributeMap.get("DISTANCE_VALUE");
                                        String freeWayId = geometryAttributeMap.get("FREE_WAY_ID");
                                        Log.d("test", routeName + " \\ " + freeWayId + " \\ " + distanceValue);
                                        Log.d("test", "routeName.equals(freeWayId) " + (routeName.equals(freeWayId)));
                                        if (routeName.equals(freeWayId) && !isRouteOverView) {
                                            distanceMarkerLinearLayout.setVisibility(View.VISIBLE);
                                            switch (freeWayId) {
                                                case "1":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_1);
                                                    break;
                                                case "10":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_10);
                                                    break;
                                                case "2":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_2);
                                                    break;
                                                case "2a":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_2a);
                                                    break;
                                                case "2甲":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_2a);
                                                    break;
                                                case "3":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_3);
                                                    break;
                                                case "3a":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_3a);
                                                    break;
                                                case "3甲":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_3a);
                                                    break;
                                                case "4":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_4);
                                                    break;
                                                case "5":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_5);
                                                    break;
                                                case "6":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_6);
                                                    break;
                                                case "7":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_7);
                                                    break;
                                                case "8":
                                                    distanceMarkerFreeIdImageView.setBackgroundResource(R.drawable.twhw_8);
                                                    break;
                                                default:
                                                    distanceMarkerLinearLayout.setVisibility(View.GONE);
                                            }
                                            distanceMarkerDistanceValue.setText(distanceValue);
                                        } else {
                                            distanceMarkerLinearLayout.setVisibility(View.GONE);
                                        }
                                    } else {
                                        distanceMarkerLinearLayout.setVisibility(View.GONE);
                                    }
                                } else {
                                    distanceMarkerLinearLayout.setVisibility(View.GONE);
                                    Log.d("test", "CLE2ResultError: " + error);
                                }
                            }
                        });
                    } else {
                        distanceMarkerLinearLayout.setVisibility(View.GONE);
                    }
                }

                /* Traffic Sign display*/
                if (!roadElement.equals(lastRoadElement)) {
                    List<TrafficSign> targetTrafficSignList = new ArrayList<>();
                    GeoCoordinate trafficSignGeoCoordinate = null;
                    try {
                        List<TrafficSign> trafficSignList = roadElement.getTrafficSigns();
                        for (TrafficSign trafficSign : trafficSignList) {
                            double distanceToSign = geoCoordinateList.get(0).distanceTo(trafficSign.coordinate);
                            if (distanceToSign > 0 && !lastTrafficSignList.equals(trafficSignList)) {
                                targetTrafficSignList.add(trafficSign);
                                trafficSignGeoCoordinate = trafficSign.coordinate;
                            }
                        }
                        lastTrafficSignList = targetTrafficSignList;
                    } catch (DataNotReadyException e) {
                        e.printStackTrace();
                    }
                    if (trafficSignGeoCoordinate != null && lastKnownLocation != null) {
                        if (lastKnownLocation.distanceTo(trafficSignGeoCoordinate) < geoPosition.getCoordinate().distanceTo(trafficSignGeoCoordinate)) {
                            isSignShowing = false;
                            TrafficSignPresenter trafficSignPresenter = new TrafficSignPresenter();
                            trafficSignPresenter.setSignImageViews(signImageView1, signImageView2, signImageView3);
                            trafficSignPresenter.showTrafficSigns(targetTrafficSignList, m_activity);
                        }
                    }

                }
                lastRoadElement = roadElement;
            }

            currentPositionMapLocalModel.setAnchor(geoPositionGeoCoordinate);
            if (locationMethod.equals(PositioningManager.LocationMethod.GPS)) {
                if (!isNavigating && m_map.getZoomLevel() >= 17) {
                    positionAccuracyMapCircle.setLineWidth(16);
                    positionAccuracyMapCircle.setLineColor(Color.argb(64, 0, 255, 0));
                    positionAccuracyMapCircle.setFillColor(Color.argb(32, 0, 255, 0));
                }
                gpsStatusImageView.setImageResource(R.drawable.ic_gps_fixed_white_24dp);
                gpsStatusImageView.setImageTintList(m_activity.getResources().getColorStateList(R.color.green));
            } else if (locationMethod.equals(PositioningManager.LocationMethod.NETWORK)) {
                if (!isNavigating && m_map.getZoomLevel() >= 17) {
                    positionAccuracyMapCircle.setLineWidth(16);
                    positionAccuracyMapCircle.setLineColor(Color.argb(64, 255, 255, 0));
                    positionAccuracyMapCircle.setFillColor(Color.argb(32, 255, 255, 0));
                }
                gpsStatusImageView.setImageResource(R.drawable.ic_gps_not_fixed_white_24dp);
                gpsStatusImageView.setImageTintList(m_activity.getResources().getColorStateList(R.color.yellow));
            }

            currentGeoPosition = geoPosition;

            if (!isRouteOverView) {
                if (!isDragged) {
                    m_map.setCenter(geoPosition.getCoordinate(), Map.Animation.NONE);
                }
            }
            if (safetyCameraAhead) {
                distanceToSafetyCamera -= proceedingDistance;
                if (distanceToSafetyCamera < 0) {
                    safetyCameraAhead = false;
                    safetyCameraMapMarker.setTransparency(0);
                    safetyCamLinearLayout.setVisibility(View.GONE);
//                    gpsStatusImageView.setVisibility(View.VISIBLE);
                } else {
                    safetyCamLinearLayout.setVisibility(View.VISIBLE);
                    safetyCamTextView.setText((int) distanceToSafetyCamera + "m");
                    safetyCamSpeedTextView.setText(safetyCameraSpeedLimitKM + "km/h");
//                    gpsStatusImageView.setVisibility(View.INVISIBLE);
                }
            }

        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {
            Log.d("test", "locationMethod: " + locationMethod.toString());
            Log.d("test", "locationStatus: " + locationStatus.toString());
            if (locationStatus.equals(PositioningManager.LocationStatus.OUT_OF_SERVICE) || locationStatus.equals(PositioningManager.LocationStatus.TEMPORARILY_UNAVAILABLE)) {
                gpsStatusImageView.setImageResource(R.drawable.ic_gps_off_white_24dp);
                gpsStatusImageView.setImageTintList(m_activity.getResources().getColorStateList(R.color.red));
                gpsSwitch.setEnabled(false);
            } else {
                gpsSwitch.setEnabled(true);
            }
        }
    };
    private MapGesture.OnGestureListener customOnGestureListener = new MapGesture.OnGestureListener() {

        @Override
        public void onPanStart() {
            isDragged = true;
        }

        @Override
        public void onPanEnd() {

        }

        @Override
        public void onMultiFingerManipulationStart() {

        }

        @Override
        public void onMultiFingerManipulationEnd() {

        }

        @Override
        public boolean onMapObjectsSelected(List<ViewObject> list) {
//            Log.d("Test", "onMapObjectsSelected: " + list.size());
            for (ViewObject viewObject : list) {
//                Log.d("Test", "viewObject: " + viewObject.getClass().getName());
                if (viewObject.getBaseType() == ViewObject.Type.USER_OBJECT) {
                    if (viewObject.equals(currentPositionMapLocalModel)) {
                        isMapRotating = !isMapRotating;
                    } else if (viewObject.equals(positionAccuracyMapCircle)) {
                        isMapRotating = !isMapRotating;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean onTapEvent(PointF pointF) {
            searchBarLinearLayout.setVisibility(View.GONE);
            if (searchResultSnackbar != null) {
                searchResultSnackbar.dismiss();
            }
            if (selectedFeatureMapMarker != null) {
                m_map.removeMapObject(selectedFeatureMapMarker);
            }
            InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
            new SearchResultHandler(m_activity.findViewById(R.id.mapFragmentView), pointF, m_map);
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF pointF) {
            searchBarLinearLayout.setVisibility(View.GONE);
            touchToAddWaypoint(pointF);
            switchUiControls(View.VISIBLE);
            InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
            m_map.setCenter(pointF, Map.Animation.LINEAR, m_map.getZoomLevel(), m_map.getOrientation(), m_map.getTilt());
            return true;
        }

        @Override
        public void onPinchLocked() {

        }

        @Override
        public boolean onPinchZoomEvent(float v, PointF pointF) {
            isDragged = true;
            return false;
        }

        @Override
        public void onRotateLocked() {

        }

        @Override
        public boolean onRotateEvent(float v) {
            isDragged = true;
            return false;
        }

        @Override
        public boolean onTiltEvent(float v) {
            isDragged = true;
            return false;
        }

        @Override
        public boolean onLongPressEvent(PointF pointF) {
            GeoCoordinate touchPointGeoCoordinate = m_map.pixelToGeo(pointF);
            GeoCoordinate coordinate = new GeoCoordinate(touchPointGeoCoordinate);
            InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
            ReverseGeocodeRequest reverseGeocodeRequest = new ReverseGeocodeRequest(coordinate, ReverseGeocodeMode.RETRIEVE_ADDRESSES, 0);
            reverseGeocodeRequest.execute(new ResultListener<com.here.android.mpa.search.Location>() {
                @Override
                public void onCompleted(com.here.android.mpa.search.Location location, ErrorCode errorCode) {
                    if (errorCode == ErrorCode.NONE) {
                        if (location != null) {
                            new SearchResultHandler(m_activity.findViewById(R.id.mapFragmentView), location, m_map);
                        } else {
                            Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Unable to find an address at " + touchPointGeoCoordinate.toString(), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), errorCode.name(), Snackbar.LENGTH_INDEFINITE).show();
                    }
                }
            });
            return false;
        }

        @Override
        public void onLongPressRelease() {
        }

        @Override
        public boolean onTwoFingerTapEvent(PointF pointF) {
            return false;
        }
    };
    MapMarker.OnDragListener mapMarkerOnDragListenerForRoute = new MapMarker.OnDragListener() {
        @Override
        public void onMarkerDrag(MapMarker mapMarker) {

        }

        @Override
        public void onMarkerDragEnd(MapMarker mapMarker) {
            int mapMarkerIndex = Integer.valueOf(mapMarker.getTitle());
            wayPointIcons.get(mapMarkerIndex).setCoordinate(mapMarker.getCoordinate());
            waypointList.clear();
            RoutePlan routePlan = m_route.getRoutePlan();
            RouteOptions.TransportMode transportMode = routePlan.getRouteOptions().getTransportMode();
            routePlan.removeAllWaypoints();
            for (MapMarker waypointMapMarker : wayPointIcons) {
//                Log.d("test", "mapMarkerIndex: " + waypointMapMarker.getTitle() + " getCoordinate: " + mapMarker.getCoordinate());
                routePlan.addWaypoint(new RouteWaypoint(waypointMapMarker.getCoordinate()));
                waypointList.add(waypointMapMarker.getCoordinate());
            }
            hereRouter.setRoutePlan(routePlan);
            hereRouter.setWaypoints(waypointList);
            calculateRoute(prepareRouteOptions(transportMode));
        }

        @Override
        public void onMarkerDragStart(MapMarker mapMarker) {

        }
    };
    MapFragmentView(AppCompatActivity activity) {
        m_activity = activity;
        initSupportMapFragment();
    }

    private static PointF getMapMarkerAnchorPoint(MapMarker mapMarker) {
        int iconHeight = (int) mapMarker.getIcon().getHeight();
        int iconWidth = (int) mapMarker.getIcon().getWidth();
        return new PointF((float) (iconWidth / 2), (float) iconHeight);
    }

    private static PointF getMapOverlayAnchorPoint(int width, int height) {
        return new PointF((float) (width / 2), (float) height);
    }

    private void showTrafficWarningTextView(TextView textView, String warningString) {
        textView.setText(warningString);
        textView.setVisibility(View.VISIBLE);
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                textView.setVisibility(View.GONE);
                textView.setText("");
            }
        }.start();
    }

    private void hideTrafficSigns() {
        signName = "";
        signImageView1.setVisibility(View.GONE);
        signImageView2.setVisibility(View.GONE);
        signImageView3.setVisibility(View.GONE);
    }

    private void initGuidanceStreetLabelView(Context context, NavigationManager navigationManager, Route route) {
        guidanceStreetLabelView = m_activity.findViewById(R.id.guidance_street_label_view);
        guidanceStreetLabelPresenter = new GuidanceStreetLabelPresenter(context, navigationManager, route);
        guidanceStreetLabelPresenter.addListener(new GuidanceStreetLabelListener() {
            @Override
            public void onDataChanged(GuidanceStreetLabelData guidanceStreetLabelData) {
//                Log.d("Test", guidanceStreetLabelData.getCurrentStreetName());
                guidanceStreetLabelView.setCurrentStreetData(guidanceStreetLabelData);
            }
        });
    }

    private void initGuidanceManeuverView(Context context, NavigationManager navigationManager, Route route) {
        guidanceManeuverView = m_activity.findViewById(R.id.guidanceManeuverView);
        guidanceManeuverPresenter = new GuidanceManeuverPresenter(context, navigationManager, route);
        guidanceManeuverPresenter.addListener(new GuidanceManeuverListener() {
            @Override
            public void onDataChanged(@Nullable GuidanceManeuverData guidanceManeuverData) {
                guidanceManeuverView.setManeuverData(guidanceManeuverData);
                if (guidanceManeuverData != null) {
                }
            }

            @Override
            public void onDestinationReached() {
            }
        });
    }

    private void initGuidanceNextManeuverView(Context context, NavigationManager navigationManager, Route route) {
        guidanceNextManeuverView = m_activity.findViewById(R.id.guidance_next_maneuver_view);
        guidanceNextManeuverPresenter = new GuidanceNextManeuverPresenter(context, navigationManager, route);
        guidanceNextManeuverPresenter.addListener(new GuidanceNextManeuverListener() {
            @Override
            public void onDataChanged(GuidanceNextManeuverData guidanceNextManeuverData) {
                guidanceNextManeuverView.setNextManeuverData(guidanceNextManeuverData);
            }
        });
    }

    private void switchUiControls(int visibility) {
        northUpButton.setVisibility(visibility);
        carRouteButton.setVisibility(visibility);
        truckRouteButton.setVisibility(visibility);
        scooterRouteButton.setVisibility(visibility);
        bikeRouteButton.setVisibility(visibility);
        pedsRouteButton.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            carRouteButton.setAlpha(1.0f);
            truckRouteButton.setAlpha(1.0f);
            scooterRouteButton.setAlpha(1.0f);
            bikeRouteButton.setAlpha(1.0f);
            pedsRouteButton.setAlpha(1.0f);
        }

    }

    private void switchGuidanceUiViews(int visibility) {
        m_activity.findViewById(R.id.guidanceManeuverView).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_next_maneuver_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_info_linear_layout).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_speed_limit_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_speed_view).setVisibility(visibility);
        m_activity.findViewById(R.id.guidance_street_label_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_estimated_arrival_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.speed_limit_linear_layout).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_speed_limit_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.speed_limit_text_view).setVisibility(visibility);
//        m_activity.findViewById(R.id.guidance_info).setVisibility(visibility);

    }

    private void switchGuidanceUiPresenters(Boolean switchOn) {
        if (switchOn) {
            guidanceManeuverPresenter.resume();
//            guidanceEstimatedArrivalViewPresenter.resume();
//            guidanceSpeedPresenter.resume();
            guidanceStreetLabelPresenter.resume();
            guidanceNextManeuverPresenter.resume();
        } else {
            if (guidanceManeuverPresenter != null) {
                guidanceManeuverPresenter.pause();
            }
            if (guidanceEstimatedArrivalViewPresenter != null) {
                guidanceEstimatedArrivalViewPresenter.pause();
            }
            if (guidanceSpeedPresenter != null) {
                guidanceSpeedPresenter.pause();
            }
            if (guidanceStreetLabelPresenter != null) {
                guidanceStreetLabelPresenter.pause();
            }
            if (guidanceNextManeuverPresenter != null) {
                guidanceNextManeuverPresenter.pause();
            }
        }
    }

    private void resetMapRoute(Route route) {
        safetyCameraAhead = false;
        safetyCameraMapMarker.setTransparency(0);
        if (mapRoute != null) {
            m_map.removeMapObject(mapRoute);
        }
        mapRoute = new MapRoute(route);
        mapRoute.setColor(Color.argb(255, 243, 174, 255)); //F3AEFF
        mapRoute.setOutlineColor(Color.argb(255, 78, 0, 143)); //4E008F
        mapRoute.setTraveledColor(Color.DKGRAY);
        mapRoute.setUpcomingColor(Color.LTGRAY);
        mapRoute.setTrafficEnabled(true);
        m_map.addMapObject(mapRoute);
    }

    private void touchToAddWaypoint(PointF p) {
        isDragged = true;
        if (selectedFeatureMapMarker != null) {
            m_map.removeMapObject(selectedFeatureMapMarker);
        }
        GeoCoordinate touchPointGeoCoordinate = m_map.pixelToGeo(p);
        MapMarker mapMarker = new MapMarker(touchPointGeoCoordinate);
        mapMarker.setDraggable(true);
        userInputWaypoints.add(mapMarker);
        mapMarker.setAnchorPoint(getMapMarkerAnchorPoint(mapMarker));
        m_map.addMapObject(mapMarker);
        carRouteButton.setVisibility(View.VISIBLE);
        truckRouteButton.setVisibility(View.VISIBLE);
        scooterRouteButton.setVisibility(View.VISIBLE);
        bikeRouteButton.setVisibility(View.VISIBLE);
        pedsRouteButton.setVisibility(View.VISIBLE);
        m_naviControlButton.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
    }

    private SupportMapFragment getMapFragment() {
        return (SupportMapFragment) m_activity.getSupportFragmentManager().findFragmentById(R.id.mapFragmentView);
    }

    /*
     * Android 8.0 (API level 26) limits how frequently background apps can retrieve the user's
     * current location. Apps can receive location updates only a few times each hour.
     * See href="https://developer.android.com/about/versions/oreo/background-location-limits.html
     * In order to retrieve location updates more frequently start a foreground service.
     * See https://developer.android.com/guide/components/services.html#Foreground
     */
    private void startForegroundService() {
        if (!m_foregroundServiceStarted) {
            m_foregroundServiceStarted = true;
            Intent startIntent = new Intent(m_activity, ForegroundService.class);
            startIntent.setAction(ForegroundService.START_ACTION);
            m_activity.getApplicationContext().startService(startIntent);
        }
    }

    private void stopForegroundService() {
        if (m_foregroundServiceStarted) {
            m_foregroundServiceStarted = false;
            Intent stopIntent = new Intent(m_activity, ForegroundService.class);
            stopIntent.setAction(ForegroundService.STOP_ACTION);
            m_activity.getApplicationContext().startService(stopIntent);
        }
    }

    private void hudMapScheme(Map map) {
        /*Map Customization - Start*/
        CustomizableScheme m_colorScheme;
        String m_colorSchemeName = "colorScheme";
        if (map != null && map.getCustomizableScheme(m_colorSchemeName) == null) {
            map.createCustomizableScheme(m_colorSchemeName, Map.Scheme.CARNAV_NIGHT_GREY);
            map.setMapScheme(Map.Scheme.CARNAV_NIGHT_GREY);
            /*
            m_colorScheme = map.getCustomizableScheme(m_colorSchemeName);
            ZoomRange range = new ZoomRange(0.0, 20.0);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_WIDTH, 20, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_TUNNELCOLOR, Color.GRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY0_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY1_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY2_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY3_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);
            m_colorScheme.setVariableValue(CustomizableVariables.Street.CATEGORY4_STREETPOLYLINEATTRIBUTE_TOLL_COLOR, Color.LTGRAY, range);

            map.setMapScheme(m_colorScheme);
            map.setLandmarksVisible(false);
            map.setExtrudedBuildingsVisible(false);
            map.setCartoMarkersVisible(false);
            */


        } else {
            if (map != null) {
                map.setMapScheme(Map.Scheme.CARNAV_NIGHT);
            }
        }
        /*Map Customization - End*/
    }

    private void createPosition3dObj() {
        currentPositionMapLocalModel = new MapLocalModel();
        LocalModelLoader localModelLoader = new LocalModelLoader(m_activity);
        LocalMesh localMesh = new LocalMesh();
        localMesh.setVertices(localModelLoader.getObjVertices());
        localMesh.setVertexIndices(localModelLoader.getObjIndices());
        localMesh.setTextureCoordinates(localModelLoader.getObjTexCoords());

        currentPositionMapLocalModel.setMesh(localMesh);
        Image image = null;
        try {
            image = new Image();
            image.setImageResource(R.drawable.grad);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentPositionMapLocalModel.setTexture(image); //an Image object
        currentPositionMapLocalModel.setScale(6.0f);
        currentPositionMapLocalModel.setDynamicScalingEnabled(true);
        m_map.addMapObject(currentPositionMapLocalModel);
    }

    private void initJunctionView() {
        junctionViewImageView = m_activity.findViewById(R.id.junctionImageView);
        junctionViewImageView.setVisibility(View.GONE);
        signpostImageView = m_activity.findViewById(R.id.signpostImageView);
        signpostImageView.setVisibility(View.GONE);

    }

    private void intoNavigationMode() {
        initJunctionView();
        safetyCameraAhead = false;
        safetyCamLinearLayout.setVisibility(View.GONE);
        zoomInButton.setVisibility(View.GONE);
        zoomOutButton.setVisibility(View.GONE);
//        gpsStatusImageView.setVisibility(View.GONE);
        gpsSwitch.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        switchUiControls(View.GONE);
        initGuidanceManeuverView(m_activity, m_navigationManager, m_route);
        initGuidanceNextManeuverView(m_activity, m_navigationManager, m_route);
//        initGuidanceEstimatedArrivalView(m_navigationManager);
        initGuidanceStreetLabelView(m_activity, m_navigationManager, m_route);
//        initGuidanceSpeedView(m_navigationManager, m_positioningManager);
        initGuidanceManeuverView(m_activity, m_navigationManager, m_route);
        initGuidanceNextManeuverView(m_activity, m_navigationManager, m_route);
        switchGuidanceUiViews(View.VISIBLE);
        switchGuidanceUiPresenters(true);
        m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);
        isRoadView = true;
        EnumSet<NavigationManager.NaturalGuidanceMode> naturalGuidanceModes = EnumSet.of(
                NavigationManager.NaturalGuidanceMode.JUNCTION,
                NavigationManager.NaturalGuidanceMode.STOP_SIGN,
                NavigationManager.NaturalGuidanceMode.TRAFFIC_LIGHT
        );
        m_navigationManager.setTrafficAvoidanceMode(NavigationManager.TrafficAvoidanceMode.DYNAMIC);
        m_navigationManager.setRouteRequestInterval(180);

        m_navigationManager.setNaturalGuidanceMode(naturalGuidanceModes);
        new ShiftMapCenter(m_map, 0.5f, 0.8f);
        //hudMapScheme(m_map);
        m_map.setTilt(60);
        m_navigationManager.startNavigation(m_route);
        m_positioningManager.setMapMatchingEnabled(true);

        /* Voice Guidance init */
        VoiceCatalog voiceCatalog = voiceActivation.getVoiceCatalog();
        VoiceGuidanceOptions voiceGuidanceOptions = m_navigationManager.getVoiceGuidanceOptions();
        voiceGuidanceOptions.setVoiceSkin(voiceCatalog.getLocalVoiceSkin(voiceActivation.getDesiredVoiceId()));
        EnumSet<NavigationManager.AudioEvent> audioEventEnumSet = EnumSet.of(
                NavigationManager.AudioEvent.MANEUVER,
                NavigationManager.AudioEvent.ROUTE,
                NavigationManager.AudioEvent.SPEED_LIMIT,
                NavigationManager.AudioEvent.GPS
        );
        m_navigationManager.setEnabledAudioEvents(audioEventEnumSet);

        supportMapFragment.setOnTouchListener(mapOnTouchListener);
        supportMapFragment.getMapGesture().removeOnGestureListener(customOnGestureListener);
    }

    private RouteOptions prepareRouteOptions(RouteOptions.TransportMode transportMode) {
        EnumSet<Map.PedestrianFeature> pedestrianFeatureEnumSet = EnumSet.of(
                Map.PedestrianFeature.BRIDGE,
                Map.PedestrianFeature.CROSSWALK,
                Map.PedestrianFeature.ELEVATOR,
                Map.PedestrianFeature.ESCALATOR,
                Map.PedestrianFeature.STAIRS,
                Map.PedestrianFeature.TUNNEL
        );
        RouteOptions routeOptions = new RouteOptions();
        switch (transportMode) {
            case CAR:
                routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
                routeOptions.setHighwaysAllowed(true);
                break;
            case TRUCK:
                routeOptions.setTransportMode(RouteOptions.TransportMode.TRUCK);
                routeOptions.setHighwaysAllowed(true);
                break;
            case SCOOTER:
                routeOptions.setTransportMode(RouteOptions.TransportMode.SCOOTER);
                routeOptions.setHighwaysAllowed(false);
                break;
            case BICYCLE:
                routeOptions.setTransportMode(RouteOptions.TransportMode.BICYCLE);
                routeOptions.setHighwaysAllowed(false);
                break;
            case PEDESTRIAN:
                routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
                m_map.setPedestrianFeaturesVisible(pedestrianFeatureEnumSet);
                routeOptions.setHighwaysAllowed(false);
                break;
        }
        routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        routeOptions.setRouteCount(1);
        return routeOptions;
    }

    private void retryRouting(Context context, RoutingError routingError, RouteOptions m_routeOptions) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Route Calculation Failed:\n" + routingError.name());
        alertDialogBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                calculateRoute(m_routeOptions);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(m_activity.getResources().getColor(R.color.green));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(m_activity.getResources().getColor(R.color.red));
        alertDialog.show();
    }

    private void calculateRoute(RouteOptions routeOptions) {
        searchBarLinearLayout.setVisibility(View.GONE);
        searchButton.setVisibility(View.GONE);
        if (selectedFeatureMapMarker != null) {
            m_map.removeMapObject(selectedFeatureMapMarker);
        }
        hereRouter = new HereRouter(m_activity, routeOptions);
        if (wayPointIcons.size() == 0) {
            for (int i = 0; i < userInputWaypoints.size(); i++) {
                MapMarker mapMarker = userInputWaypoints.get(i);
                waypointList.add(mapMarker.getCoordinate());
                m_map.removeMapObject(mapMarker);
            }
            if (mapRoute != null) {
                m_map.removeMapObject(mapRoute);
            }
            wayPointIcons = hereRouter.getOutputWaypointIcons();
        }
        hereRouter.setWaypoints(waypointList);
        hereRouter.setContext(m_activity);
        hereRouter.createRouteForNavi();
        for (MapMarker m : wayPointIcons) {
            m.setAnchorPoint(getMapMarkerAnchorPoint(m));
            m_map.addMapObject(m);
        }
//        Log.d("Test", "wayPointIcons: " + wayPointIcons.size());


        if (m_map.isTrafficInfoVisible()) {
            DynamicPenalty dynamicPenalty = new DynamicPenalty();
            dynamicPenalty.setTrafficPenaltyMode(Route.TrafficPenaltyMode.OPTIMAL);
            coreRouter.setDynamicPenalty(dynamicPenalty);
        }

        progressBar = m_activity.findViewById(R.id.progressBar);
        calculatingTextView = m_activity.findViewById(R.id.calculatingTextView);
//        Log.d("Test", "Route Calculation Started.");
        coreRouter.calculateRoute(hereRouter.getRoutePlan(), new Router.Listener<List<RouteResult>, RoutingError>() {
            @Override
            public void onProgress(int i) {
                if (i < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                    calculatingTextView.setVisibility(View.VISIBLE);
                    progressBar.setProgress(i);
                } else {
                    calculatingTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCalculateRouteFinished(List<RouteResult> routeResults, RoutingError routingError) {
//                Log.d("Test", "Route Calculation Ended.");
//                Log.d("Test", routingError.toString());
                if (routingError == RoutingError.NONE) {
                    if (routeResults.get(0).getRoute() != null) {
                        isRouteOverView = true;
                        m_route = routeResults.get(0).getRoute();
                        supportMapFragment.setMapMarkerDragListener(mapMarkerOnDragListenerForRoute);
                        resetMapRoute(m_route);
                        mapRouteBBox = m_route.getBoundingBox();
                        GeoBoundingBoxDimensionCalculator geoBoundingBoxDimensionCalculator = new GeoBoundingBoxDimensionCalculator(mapRouteBBox);

                        mapRouteBBox.expand((float) (geoBoundingBoxDimensionCalculator.getBBoxHeight() * 0.8), (float) (geoBoundingBoxDimensionCalculator.getBBoxWidth() * 0.6));
                        m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
                        m_naviControlButton.setText("Start Navi");
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Route of " + m_route.getRoutePlan().getRouteOptions().getTransportMode() + " / " + m_route.getLength() + "m", Snackbar.LENGTH_LONG).show();
                        supportMapFragment.getMapGesture().removeOnGestureListener(customOnGestureListener);

                    } else {
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Can't find a route.", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Error: " + routingError.name(), Snackbar.LENGTH_LONG).show();
                    calculatingTextView.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    if (mapRoute != null) {
                        m_map.removeMapObject(mapRoute);
                    }
                }
//                retryRouting(m_activity, routingError, routeOptions);
            }
        });
    }

    void onDestroy() {
        /* Stop the navigation when app is destroyed */
        if (m_navigationManager != null) {
            stopForegroundService();
            m_navigationManager.stop();
        }
    }

    private void initSupportMapFragment() {
        supportMapFragment = getMapFragment();
        supportMapFragment.setCopyrightLogoPosition(CopyrightLogoPosition.BOTTOM_CENTER);
        // Set path of isolated disk cache
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = m_activity.getPackageManager().getApplicationInfo(m_activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(this.getClass().toString(), "Failed to find intent name, NameNotFound: " + e.getMessage());
        }


        boolean success = MapSettings.setIsolatedDiskCacheRootPath(diskCacheRoot, intentName);
        if (!success) {
            // Setting the isolated disk cache was not successful, please check if the path is valid and
            // ensure that it does not match the default location
            // (getExternalStorageDirectory()/.here-maps).
            // Also, ensure the provided intent name does not match the default intent name.
        } else {
            if (supportMapFragment != null) supportMapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(Error error) {
                    if (error == Error.NONE) {
                        coreRouter = new CoreRouter();
                        m_map = supportMapFragment.getMap();

                        MapScaleView mapScaleView = m_activity.findViewById(R.id.map_scale_view);
                        mapScaleView.setMap(m_map);
                        mapScaleView.setColor(R.color.black);

                        mapSchemeChanger = new MapSchemeChanger(m_map);

                        isNavigating = false;
                        GeoCoordinate defaultMapCenter = new GeoCoordinate(25.038137, 121.513936);
                        m_map.setCenter(defaultMapCenter, Map.Animation.NONE);
                        isDragged = false;

                        /* Rotate compass icon*/
                        m_map.addTransformListener(new Map.OnTransformListener() {
                            @Override
                            public void onMapTransformStart() {

                            }

                            @Override
                            public void onMapTransformEnd(MapState mapState) {
                                if (isNavigating || m_map.getZoomLevel() < 17) {
                                    positionAccuracyMapCircle.setLineWidth(0);
                                    positionAccuracyMapCircle.setFillColor(Color.argb(0, 0, 0, 0));
                                }
                                northUpButton.setRotation(mapState.getOrientation() * -1);
                            }
                        });

                        m_map.addSchemeChangedListener(s -> Log.d("test", "onMapSchemeChanged: " + s));

                        supportMapFragment.addOnMapRenderListener(new OnMapRenderListener() {
                            @Override
                            public void onPreDraw() {
                            }

                            @Override
                            public void onPostDraw(boolean b, long l) {
                            }

                            @Override
                            public void onSizeChanged(int i, int i1) {
                                if (!isNavigating) {
                                    new ShiftMapCenter(m_map, 0.5f, 0.6f);
                                } else {
                                    new ShiftMapCenter(m_map, 0.5f, 0.8f);
                                }
//                                Log.d("test", "isRouteOverView " + isRouteOverView);

                                if (isRouteOverView) {
                                    m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
                                }
                            }

                            @Override
                            public void onGraphicsDetached() {

                            }

                            @Override
                            public void onRenderBufferCreated() {

                            }
                        });

                        m_positioningManager = new PositionActivation(PositioningManager.LocationMethod.GPS_NETWORK).getPositioningManager();
                        m_positioningManager.addListener(new WeakReference<>(positionChangedListener));

                        trafficWarningTextView = m_activity.findViewById(R.id.traffic_warning_text_view);

                        gpsSwitch = m_activity.findViewById(R.id.gps_switch);
                        gpsSwitch.setChecked(true);
                        gpsSwitch.setEnabled(false);
                        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    m_positioningManager.stop();
                                    m_positioningManager = null;
                                    m_positioningManager = new PositionActivation(PositioningManager.LocationMethod.GPS_NETWORK).getPositioningManager();
                                } else {
                                    m_positioningManager.stop();
                                    m_positioningManager = null;
                                    m_positioningManager = new PositionActivation(PositioningManager.LocationMethod.NETWORK).getPositioningManager();
                                }
                            }
                        });

                        signImageView1 = m_activity.findViewById(R.id.sign_imageView_1);
                        signImageView2 = m_activity.findViewById(R.id.sign_imageView_2);
                        signImageView3 = m_activity.findViewById(R.id.sign_imageView_3);

                        new ShiftMapCenter(m_map, 0.5f, 0.6f);
                        m_map.setMapScheme(Map.Scheme.NORMAL_DAY);
                        m_map.setMapDisplayLanguage(TRADITIONAL_CHINESE);
                        m_map.setSafetySpotsVisible(true);
                        m_map.setExtrudedBuildingsVisible(false);
                        m_map.setLandmarksVisible(true);
                        m_map.setExtendedZoomLevelsEnabled(true);

//                            speedLimitLinearLayout = m_activity.findViewById(R.id.speed_limit_linear_layout);
//                            speedLimitLinearLayoutHeight = speedLimitLinearLayout.getLayoutParams().height;
                        switchGuidanceUiViews(View.GONE);
                        gpsStatusImageView = m_activity.findViewById(R.id.gps_status_image_view);
                        /* Listeners of map buttons */
                        northUpButton = m_activity.findViewById(R.id.north_up);
                        northUpButton.setOnClickListener(v -> {
                            if (searchResultSnackbar != null) {
                                searchResultSnackbar.dismiss();
                            }
                            isMapRotating = false;
                            m_map.setOrientation(0);
                            northUpButton.setRotation(0);
                            m_map.setTilt(0);
                            m_map.setZoomLevel(16);
                            new ShiftMapCenter(m_map, 0.5f, 0.6f);
                            if (!isRouteOverView) {
                                m_map.setCenter(m_positioningManager.getPosition().getCoordinate(), Map.Animation.LINEAR);
                            } else {
                                m_map.zoomTo(mapRouteBBox, Map.Animation.LINEAR, Map.MOVE_PRESERVE_ORIENTATION);
                            }
                        });
                        zoomInButton = m_activity.findViewById(R.id.zoom_in);
                        zoomInButton.setOnClickListener(v -> {
                            double zoomLevel = m_map.getZoomLevel();
                            m_map.setZoomLevel(zoomLevel + 1);
                        });
                        zoomOutButton = m_activity.findViewById(R.id.zoom_out);
                        zoomOutButton.setOnClickListener(v -> {
                            double zoomLevel = m_map.getZoomLevel();
                            m_map.setZoomLevel(zoomLevel - 1);
                        });
                        carRouteButton = m_activity.findViewById(R.id.car_route);
                        truckRouteButton = m_activity.findViewById(R.id.truck_route);
                        scooterRouteButton = m_activity.findViewById(R.id.scooter_route);
                        bikeRouteButton = m_activity.findViewById(R.id.bike_route);
                        pedsRouteButton = m_activity.findViewById(R.id.peds_route);
                        carRouteButton.setOnClickListener(vCarRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.CAR));
                            carRouteButton.setAlpha(1.0f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(0.3f);

                        });
                        truckRouteButton.setOnClickListener(vTruckRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.TRUCK));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(1.0f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(0.3f);
                        });
                        scooterRouteButton.setOnClickListener(vScooterRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.SCOOTER));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(1.0f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(0.3f);
                        });
                        bikeRouteButton.setOnClickListener(vBikeRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.BICYCLE));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(1.0f);
                            pedsRouteButton.setAlpha(0.3f);
                        });
                        pedsRouteButton.setOnClickListener(vPedsRouteButton -> {
                            calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.PEDESTRIAN));
                            carRouteButton.setAlpha(0.3f);
                            truckRouteButton.setAlpha(0.3f);
                            scooterRouteButton.setAlpha(0.3f);
                            bikeRouteButton.setAlpha(0.3f);
                            pedsRouteButton.setAlpha(1.0f);
                        });
                        trafficButton = m_activity.findViewById(R.id.traffic_button);
                        trafficButton.setBackgroundResource(R.drawable.round_button_off);
                        trafficButton.setOnClickListener(v -> {
                            if (!m_map.isTrafficInfoVisible()) {
                                trafficEnabled = true;
                                m_map.setTrafficInfoVisible(true);
                                mapSchemeChanger.trafficMapOn();
                                trafficButton.setBackgroundResource(R.drawable.round_button_on);
                            } else {
                                trafficEnabled = false;
                                m_map.setTrafficInfoVisible(false);
                                mapSchemeChanger.trafficMapOff();
                                trafficButton.setBackgroundResource(R.drawable.round_button_off);
                            }
                        });
                        satMapButton = m_activity.findViewById(R.id.sat_map_button);
                        satMapButton.setBackgroundResource(R.drawable.round_button_off);
                        satMapButton.setOnClickListener(v -> {
                            if (!isSatMap) {
                                isSatMap = true;
                                satMapButton.setBackgroundResource(R.drawable.round_button_on);
                                mapSchemeChanger.satelliteMapOn();

                            } else {
                                isSatMap = false;
                                satMapButton.setBackgroundResource(R.drawable.round_button_off);
                                mapSchemeChanger.satelliteMapOff();
                            }
                        });
                        searchButton = m_activity.findViewById(R.id.search_button);
                        searchTextBar = m_activity.findViewById(R.id.search_input_text);
                        searchBarLinearLayout = m_activity.findViewById(R.id.search_bar_linear_layout);
                        searchButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (searchResultSnackbar != null) {
                                    searchResultSnackbar.dismiss();
                                }
                                searchTextBar.setText("");
                                searchBarLinearLayout.setVisibility(View.VISIBLE);
                                searchTextBar.requestFocus();
                                InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(searchTextBar, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                        searchTextBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                    new SearchResultHandler(m_activity.findViewById(R.id.mapFragmentView), m_map.getCenter(), searchTextBar.getText().toString(), m_map);
                                    searchTextBar.clearFocus();
                                    InputMethodManager inputMethodManager = (InputMethodManager) m_activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(searchTextBar.getWindowToken(), 0);
                                    return true;
                                }
                                return false;
                            }
                        });

                        m_activity.findViewById(R.id.log_button).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!isPositionLogging) {
                                    isPositionLogging = true;
                                    m_activity.findViewById(R.id.log_button).setBackgroundTintList(m_activity.getResources().getColorStateList(R.color.red));
                                    m_positioningManager.setLogType(EnumSet.of(
                                            PositioningManager.LogType.RAW,
                                            PositioningManager.LogType.MATCHED,
                                            PositioningManager.LogType.DATA_SOURCE
                                    ));
                                    Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "GPX Logging: " + m_activity.getFilesDir().getAbsolutePath() + File.separator + "gpx/", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

                        m_naviControlButton = m_activity.findViewById(R.id.startGuidance);
                        m_naviControlButton.setText("Create Route");
                        m_naviControlButton.setOnClickListener(v -> {
                            if (m_route != null) {
                                if (laneMapOverlay != null) {
                                    m_map.removeMapOverlay(laneMapOverlay);
                                }
                                laneMapOverlay = null;
                                if (m_navigationManager != null) {
                                    m_navigationManager.stop();
                                }
                                new ShiftMapCenter(m_map, 0.5f, 0.6f);
                                m_map.setTilt(0);
                                switchGuidanceUiPresenters(false);
                                startNavigation(m_route, true);
                            } else {
                                calculateRoute(prepareRouteOptions(RouteOptions.TransportMode.CAR));
                                carRouteButton.setAlpha(1.0f);
                                truckRouteButton.setAlpha(0.5f);
                                scooterRouteButton.setAlpha(0.5f);
                                bikeRouteButton.setAlpha(0.5f);
                                pedsRouteButton.setAlpha(0.5f);
                            }
                        });
                        clearButton = m_activity.findViewById(R.id.clear);
                        clearButton.setOnClickListener(v -> resetMap());
                        safetyCamLinearLayout = m_activity.findViewById(R.id.safety_cam_linear_layout);
                        safetyCamImageView = m_activity.findViewById(R.id.safety_cam_image_view);
                        safetyCamTextView = m_activity.findViewById(R.id.safety_cam_text_view);
                        safetyCamSpeedTextView = m_activity.findViewById(R.id.safety_cam_speed_text_view);
                        distanceMarkerLinearLayout = m_activity.findViewById(R.id.distance_marker_linear_layout);
                        distanceMarkerFreeIdImageView = m_activity.findViewById(R.id.distance_marker_freeway_id);
                        distanceMarkerDistanceValue = m_activity.findViewById(R.id.distance_marker_distance_value);

                        supportMapFragment.getMapGesture().addOnGestureListener(customOnGestureListener, 0, false);

                        /* Download voice */
                        voiceActivation = new VoiceActivation(m_activity);
                        voiceActivation.setContext(m_activity);
                        String desiredVoiceLanguageCode = "CHT";
                        voiceActivation.setDesiredLangCode(desiredVoiceLanguageCode);
                        voiceActivation.downloadCatalogAndSkin();

                        /* adding rotatable position indicator to the map */
                        createPosition3dObj();
                        positionAccuracyMapCircle = new MapCircle();
                        positionAccuracyMapCircle.setRadius(1f);
                        positionAccuracyMapCircle.setLineWidth(0);
                        positionAccuracyMapCircle.setFillColor(Color.argb(0, 0, 0, 0));
                        positionAccuracyMapCircle.setLineColor(Color.argb(0, 0, 0, 0));
                        positionAccuracyMapCircle.setCenter(defaultMapCenter);
                        m_map.addMapObject(positionAccuracyMapCircle);

                        /* init safetyCameraMapMarker */
                        safetyCameraMapMarker = new MapMarker();
                        m_map.addMapObject(safetyCameraMapMarker);

                    } else {
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "ERROR: Cannot initialize Map with error " + error, Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void startNavigation(Route route, boolean zoomToRoute) {

        resetMapRoute(route);
        for (MapMarker m : placeSearchResultIcons) {
            m_map.removeMapObject(m);
        }

        new ShiftMapCenter(m_map, 0.5f, 0.6f);
        m_map.setTilt(0);
        if (zoomToRoute) {
            m_map.zoomTo(mapRouteBBox, Map.Animation.NONE, 0f);
        }
        isDragged = false;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(m_activity);
        alertDialogBuilder.setTitle("Navigation");
        alertDialogBuilder.setMessage("Choose Mode");
        alertDialogBuilder.setNegativeButton("Navigation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                isNavigating = true;
                hideTrafficSigns();
                isSignShowing = false;
                m_naviControlButton.setText("Stop Navi");
                intoNavigationMode();
                isRouteOverView = false;
                NavigationManager.Error error = m_navigationManager.startNavigation(m_route);
                trafficWarner = m_navigationManager.getTrafficWarner();
                trafficWarner.init();
                trafficWarner.addListener(new WeakReference<>(trafficWarnerListener));
                mapSchemeChanger.navigationMapOn();
                m_naviControlButton.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
                Log.e("Error: ", error.toString());
                startForegroundService();
            }
        });
        alertDialogBuilder.setPositiveButton("Simulation", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                isNavigating = true;
                hideTrafficSigns();
                isSignShowing = false;
                m_naviControlButton.setText("Stop Navi");
                intoNavigationMode();
                isRouteOverView = false;
                NavigationManager.Error error = m_navigationManager.simulate(m_route, simulationSpeedMs);
                trafficWarner = m_navigationManager.getTrafficWarner();
                trafficWarner.init();
                trafficWarner.addListener(new WeakReference<>(trafficWarnerListener));
                mapSchemeChanger.navigationMapOn();
//                ElectronicHorizonActivation electronicHorizonActivation = new ElectronicHorizonActivation();
//                electronicHorizonActivation.setRoute(m_route);
                m_naviControlButton.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
                startForegroundService();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(m_activity.getResources().getColor(R.color.green));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(m_activity.getResources().getColor(R.color.red));
        m_navigationManager = NavigationManager.getInstance();
        m_navigationManager.setMap(m_map);
        mapSchemeChanger = new MapSchemeChanger(m_map, m_navigationManager);
        addNavigationListeners();
    }

    private void resetMap() {
        safetyCameraMapMarker.setTransparency(0);
        distanceMarkerLinearLayout.setVisibility(View.GONE);
        mapSchemeChanger = new MapSchemeChanger(m_map);
        if (trafficWarner != null) {
            trafficWarner.stop();
        }
        mapSchemeChanger.navigationMapOff();
        if (searchResultSnackbar != null) {
            searchResultSnackbar.dismiss();
        }
        isMapRotating = false;
        isNavigating = false;
        m_navigationManager = null;
        switchGuidanceUiViews(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
//        gpsStatusImageView.setVisibility(View.VISIBLE);
        gpsSwitch.setVisibility(View.VISIBLE);
        m_activity.findViewById(R.id.junctionImageView).setVisibility(View.INVISIBLE);
        m_activity.findViewById(R.id.signpostImageView).setVisibility(View.INVISIBLE);
        m_naviControlButton.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        if (!placeSearchResultIcons.isEmpty()) {
            for (MapMarker m : placeSearchResultIcons) {
                m_map.removeMapObject(m);
            }
        }
        placeSearchResultIcons.clear();
        isRouteOverView = false;
        if (coreRouter != null) {
            if (coreRouter.isBusy()) {
                coreRouter.cancel();
            }
        }
        supportMapFragment.setOnTouchListener(null);
        if (m_navigationManager != null) {
            if (m_navigationManager.getRunningState() == NavigationManager.NavigationState.RUNNING) {
                m_navigationManager.stop();
            }
        }
        m_naviControlButton.setText("Create Route");
        m_route = null;
        switchGuidanceUiPresenters(false);

        new ShiftMapCenter(m_map, 0.5f, 0.6f);

        m_map.setTilt(0);

        if (mapRoute != null) {
            m_map.removeMapObject(mapRoute);
        }
        if (!userInputWaypoints.isEmpty()) {
            for (MapMarker mkr : userInputWaypoints) {
                m_map.removeMapObject(mkr);
            }
        }
        if (!wayPointIcons.isEmpty()) {
            for (MapMarker mkr : wayPointIcons) {
                m_map.removeMapObject(mkr);
            }
        }
        wayPointIcons.clear();
        userInputWaypoints.clear();
        waypointList.clear();
        isDragged = false;

        northUpButton.callOnClick();
        supportMapFragment.getMapGesture().addOnGestureListener(customOnGestureListener, 0, false);
        switchUiControls(View.GONE);
        zoomInButton.setVisibility(View.VISIBLE);
        zoomOutButton.setVisibility(View.VISIBLE);
        northUpButton.setVisibility(View.VISIBLE);

        EnumSet<Map.LayerCategory> poiLayers = EnumSet.of(
                Map.LayerCategory.POI_ICON,
                Map.LayerCategory.POI_LABEL,
                Map.LayerCategory.POINT_ADDRESS
        );
        m_map.setVisibleLayers(poiLayers, true);
        if (laneMapOverlay != null) {
            m_map.removeMapOverlay(laneMapOverlay);
        }
        if (m_navigationManager != null) {
            removeNavigationListeners();
        }
    }

    private void removeNavigationListeners() {
        m_navigationManager.removeNavigationManagerEventListener(m_navigationManagerEventListener);
        m_navigationManager.removeSafetySpotListener(safetySpotListener);
        m_navigationManager.removeRealisticViewListener(m_realisticViewListener);
        m_navigationManager.removePositionListener(m_positionListener);
        m_navigationManager.removeLaneInformationListener(m_LaneInformationListener);
        m_navigationManager.removeRerouteListener(m_rerouteListener);
        m_navigationManager.removeTrafficRerouteListener(m_trafficRerouteListener);
    }

    private void addNavigationListeners() {
        m_activity.findViewById(R.id.mapFragmentView).getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        m_navigationManager.addNavigationManagerEventListener(new WeakReference<>(m_navigationManagerEventListener));
        m_navigationManager.addSafetySpotListener(new WeakReference<>(safetySpotListener));
        m_navigationManager.setRealisticViewMode(NavigationManager.RealisticViewMode.DAY);
        m_navigationManager.addRealisticViewAspectRatio(NavigationManager.AspectRatio.AR_16x9);
        m_navigationManager.addRealisticViewListener(new WeakReference<>(m_realisticViewListener));
        m_navigationManager.addPositionListener(new WeakReference<>(m_positionListener));
        if (m_route.getFirstManeuver().getTransportMode() == RouteOptions.TransportMode.CAR || m_route.getFirstManeuver().getTransportMode() == RouteOptions.TransportMode.TRUCK) {
            m_navigationManager.addLaneInformationListener(new WeakReference<>(m_LaneInformationListener));
        }
        m_navigationManager.addRerouteListener(new WeakReference<>(m_rerouteListener));
        m_navigationManager.addTrafficRerouteListener(new WeakReference<>(m_trafficRerouteListener));
    }

    class SearchResultHandler {
        String placesSearchResultTitle;
        String placesSearchCategoryName;
        private Map map = m_map;

        public SearchResultHandler(View view, String placesSearchResultTitle) {
            this.placesSearchResultTitle = placesSearchResultTitle;
        }

        SearchResultHandler(View view, GeoCoordinate geoCoordinate, String inputString, Map map) {
            if (selectedFeatureMapMarker != null) {
                map.removeMapObject(selectedFeatureMapMarker);
            }
            SearchRequest request = new SearchRequest(inputString);
            request.setSearchCenter(geoCoordinate);
            request.setCollectionSize(1);
            ErrorCode error = request.execute(new ResultListener<DiscoveryResultPage>() {
                @Override
                public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
                    if (discoveryResultPage.getPlaceLinks().size() > 0) {
                        List<PlaceLink> discoveryResultPlaceLink = discoveryResultPage.getPlaceLinks();
                        for (PlaceLink placeLink : discoveryResultPlaceLink) {
//                            Log.d("Test", placeLink.getTitle());
                            placesSearchResultTitle = placeLink.getTitle();
                            placesSearchCategoryName = placeLink.getCategory().getName();
                            GeoCoordinate placesSearchresultGeoCoordinate = placeLink.getPosition();
                            searchResultString = placesSearchResultTitle + " / " + placesSearchCategoryName;
                            showSelectionFocus(placesSearchresultGeoCoordinate, searchResultString);
                            showResultSnackbar(placesSearchresultGeoCoordinate, searchResultString, view, Snackbar.LENGTH_INDEFINITE);
                            searchBarLinearLayout.setVisibility(View.GONE);
                            isDragged = true;
                            map.setCenter(placesSearchresultGeoCoordinate, Map.Animation.LINEAR);
                        }
                    } else {
                        Snackbar.make(view, "No result returned.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        SearchResultHandler(View view, com.here.android.mpa.search.Location location, Map map) {
            if (selectedFeatureMapMarker != null) {
                map.removeMapObject(selectedFeatureMapMarker);
            }
            searchResultString = location.getAddress().getText();
            this.showSelectionFocus(location.getCoordinate(), searchResultString);
            showResultSnackbar(location.getCoordinate(), location.getAddress().getText(), view, Snackbar.LENGTH_INDEFINITE);
        }

        SearchResultHandler(View view, PointF pointF, Map map) {
            if (selectedFeatureMapMarker != null) {
                map.removeMapObject(selectedFeatureMapMarker);
            }
            List<ViewObject> selectedMapObjects = m_map.getSelectedObjectsNearby(pointF);
            if (selectedMapObjects.size() > 0) {
//                Log.d("test", selectedMapObjects.get(0).getClass().getName());
                switch (selectedMapObjects.get(0).getClass().getName()) {
                    case "com.here.android.mpa.mapping.MapCartoMarker":
                        MapCartoMarker selectedMapCartoMarker = (MapCartoMarker) selectedMapObjects.get(0);
                        Location location = selectedMapCartoMarker.getLocation();
                        String placeName = location.getInfo().getField(LocationInfo.Field.PLACE_NAME);
                        String category = location.getInfo().getField(LocationInfo.Field.PLACE_CATEGORY);
                        searchResultString = placeName + " / " + category;
                        showSelectionFocus(location.getCoordinate(), searchResultString);
                        showResultSnackbar(location.getCoordinate(), searchResultString, view, Snackbar.LENGTH_INDEFINITE);
                        break;
                    case "com.here.android.mpa.mapping.TrafficEventObject":
                        TrafficEventObject trafficEventObject = (TrafficEventObject) selectedMapObjects.get(0);
                        GeoCoordinate trafficEventObjectGeoCoordinate = trafficEventObject.getCoordinate();
                        String trafficEventShortText = trafficEventObject.getTrafficEvent().getEventText();
                        String trafficEventAffectedStreet = trafficEventObject.getTrafficEvent().getFirstAffectedStreet();
                        searchResultString = trafficEventShortText + " / " + trafficEventAffectedStreet;
                        showResultSnackbar(trafficEventObjectGeoCoordinate, searchResultString, view, Snackbar.LENGTH_SHORT);
                        break;
                    case "com.here.android.mpa.mapping.SafetySpotObject":
                        SafetySpotObject safetySpotObject = (SafetySpotObject) selectedMapObjects.get(0);
                        safetyCameraSpeedLimit = safetySpotObject.getSafetySpotInfo().getSpeedLimit1();
                        int safetyCameraSpeedLimitKM;
                        if (safetyCameraSpeedLimit * 3.6 % 10 >= 8 || safetyCameraSpeedLimit * 3.6 % 10 <= 2) {
                            safetyCameraSpeedLimitKM = (int) ((Math.round((safetyCameraSpeedLimit * 3.6) / 10)) * 10);
                        } else {
                            safetyCameraSpeedLimitKM = (int) (Math.round((safetyCameraSpeedLimit * 3.6)));
                        }
                        searchResultString = "Safety Camera / " + safetyCameraSpeedLimitKM + " km/h";
                        showResultSnackbar(safetySpotObject.getSafetySpotInfo().getCoordinate(), searchResultString, view, Snackbar.LENGTH_SHORT);
                        break;
                }
            }
        }

        private void showSelectionFocus(GeoCoordinate geoCoordinate, String string) {
            selectedFeatureMapMarker = new MapMarker();
            selectedFeatureMapMarker.setCoordinate(geoCoordinate);
            Image icon = new Image();
            icon.setBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(m_activity, R.drawable.ic_search_result));
            selectedFeatureMapMarker.setIcon(icon);
            selectedFeatureMapMarker.setTitle(string);
            selectedFeatureMapMarker.setAnchorPoint(getMapMarkerAnchorPoint(selectedFeatureMapMarker));
            m_map.addMapObject(selectedFeatureMapMarker);
        }

        private void showResultSnackbar(GeoCoordinate waypointMapMakerGeoCoordinate, String stringToShow, View view, int duration) {

            searchResultSnackbar = Snackbar.make(view, stringToShow, duration);
            searchResultSnackbar.setAction("Add Waypoint", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedFeatureMapMarker != null) {
                        map.removeMapObject(selectedFeatureMapMarker);
                    }
                    addingWaypointMapMarker(waypointMapMakerGeoCoordinate);
                    map.setCenter(waypointMapMakerGeoCoordinate, Map.Animation.LINEAR);
                    isDragged = true;
                }
            });
            searchResultSnackbar.show();
        }

        private void addingWaypointMapMarker(GeoCoordinate geoCoordinate) {
            MapMarker mapMarker = new MapMarker(geoCoordinate);
            mapMarker.setDraggable(true);
            userInputWaypoints.add(mapMarker);
            mapMarker.setAnchorPoint(getMapMarkerAnchorPoint(mapMarker));
            m_map.addMapObject(mapMarker);
            carRouteButton.setVisibility(View.VISIBLE);
            truckRouteButton.setVisibility(View.VISIBLE);
            scooterRouteButton.setVisibility(View.VISIBLE);
            bikeRouteButton.setVisibility(View.VISIBLE);
            pedsRouteButton.setVisibility(View.VISIBLE);
            m_naviControlButton.setVisibility(View.VISIBLE);
            clearButton.setVisibility(View.VISIBLE);
        }
    }

}