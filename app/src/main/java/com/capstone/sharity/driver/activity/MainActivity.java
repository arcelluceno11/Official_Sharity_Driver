package com.capstone.sharity.driver.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;

import com.capstone.sharity.driver.R;
import com.capstone.sharity.driver.viewmodel.DriverViewModel;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;

import java.util.List;

import dev.shreyaspatil.MaterialDialog.AbstractDialog;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import dev.shreyaspatil.MaterialDialog.model.TextAlignment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.teliver.sdk.core.Teliver;

public class MainActivity extends AppCompatActivity {

    //Variables
    DriverViewModel driverViewModel;
    Disposable internetDisposable;
    MaterialDialog alertDialog;

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.main_content);

        //Request Permissions
        PermissionX.init(this)
                .permissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.POST_NOTIFICATIONS
                )
                .explainReasonBeforeRequest()
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(@NonNull ExplainScope scope, @NonNull List<String> deniedList) {
                        scope.showRequestReasonDialog(deniedList, "To make this app works properly, you need to enable these permissions: ", "OK", "Cancel");
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(@NonNull ForwardScope scope, @NonNull List<String> deniedList) {
                        scope.showForwardToSettingsDialog(deniedList, "You need to allow necessary permissions in Settings manually", "OK", "Cancel");
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {

                    }
                });

        //Check Internet
        alertDialog = new MaterialDialog.Builder(this)
                .setTitle("No Internet Connection", TextAlignment.CENTER)
                .setMessage("Make sure you're connected to Wi-Fi or Mobile Data")
                .setCancelable(false)
                .setAnimation(R.raw.no_internet)
                .setPositiveButton("Retry", new AbstractDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        onResume();;
                    }
                }).build();

        //IDK WHAT'S THIS
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @SuppressLint({"MissingPermission", "CheckResult"})
    @Override
    protected void onResume() {
        super.onResume();

        internetDisposable = ReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    if (connectivity.state().toString().equals("CONNECTED")) {
                        alertDialog.dismiss();
                    }
                    if (connectivity.state().toString().equals("DISCONNECTED")) {
                        alertDialog.show();
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        safelyDispose(internetDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }
}