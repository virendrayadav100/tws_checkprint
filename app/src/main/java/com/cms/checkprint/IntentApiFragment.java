package com.cms.checkprint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dynamixsoftware.intentapi.IPrintCallback;
import com.dynamixsoftware.intentapi.IPrinterInfo;
import com.dynamixsoftware.intentapi.IServiceCallback;
import com.dynamixsoftware.intentapi.IntentAPI;
import com.dynamixsoftware.intentapi.Result;

public class IntentApiFragment extends Fragment implements View.OnClickListener {

    private IntentAPI intentApi;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

        Log.e("onattach","On Attached call");

        if(getActivity()!=null)
        {
            Log.e("onattach","activity found");
        }else{
            Log.e("onattach","activity not found");
        }

        intentApi = new IntentAPI(getActivity() != null ? getActivity() : context); // some features not worked if initialized without activity
        final Context appContext = context.getApplicationContext();
        try {
            boolean res=intentApi.runService(new IServiceCallback.Stub() {
                @Override
                public void onServiceDisconnected() {
                    toastInMainThread(appContext, "Service disconnected");
                }

                @Override
                public void onServiceConnected() {
                    toastInMainThread(appContext, "Service connected");

                    try {
                        intentApi.setPrintCallback(new IPrintCallback.Stub() {
                            @Override
                            public void startingPrintJob() {
                                toastInMainThread(appContext, "startingPrintJob");
                            }

                            @Override
                            public void start() {
                                toastInMainThread(appContext, "start");
                            }

                            @Override
                            public void sendingPage(int pageNum, int progress) {
                                toastInMainThread(appContext, "sendingPage number " + pageNum + ", progress " + progress);
                            }

                            @Override
                            public void preparePage(int pageNum) {
                                toastInMainThread(appContext, "preparePage number " + pageNum);
                            }

                            @Override
                            public boolean needCancel() {
                                toastInMainThread(appContext, "needCancel");
                                // If you need to cancel printing send true
                                return false;
                            }

                            @Override
                            public void finishingPrintJob() {
                                /*if (intentApi != null) {
                                    intentApi.stopService(null);
                                    try {
                                        intentApi.setServiceCallback(null);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        intentApi.setPrintCallback(null);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                    intentApi = null;
                                }*/

                                startActivity(new Intent(getContext(), CameraActivity.class));
                                getActivity().finish();
                                toastInMainThread(appContext, "finishingPrintJob");
                            }

                            @Override
                            public void finish(Result result, int pagesPrinted) {
                                toastInMainThread(appContext, "finish, Result " + result + "; Result type " + result.getType() + "; Result message " + result.getType().getMessage() + "; pages printed " + pagesPrinted);
                            }
                        });
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    try{
                        IPrinterInfo printer = intentApi.getCurrentPrinter();
                        TextView textView = (TextView) getView().findViewById(R.id.txtPrinterName);
                        textView.setText((printer != null ? printer.getName() : "printer not available 2"));
                    }catch(Exception ex){
                        TextView textView = (TextView) getView().findViewById(R.id.txtPrinterName);
                        textView.setText(("printer not available1"));
                    }
                }

                @Override
                public void onFileOpen(int progress, int finished) {
                    toastInMainThread(appContext, "onFileOpen progress " + progress + "; finished " + (finished == 1));
                }

                @Override
                public void onLibraryDownload(int progress) {
                    toastInMainThread(appContext, "onLibraryDownload progress " + progress);
                }

                @Override
                public boolean onRenderLibraryCheck(boolean renderLibrary, boolean fontLibrary) {
                    toastInMainThread(appContext, "onRenderLibraryCheck render library " + renderLibrary + "; fonts library " + fontLibrary);
                    return true;
                }

                @Override
                public String onPasswordRequired() {
                    toastInMainThread(appContext, "onPasswordRequired");
                    return "password";
                }

                @Override
                public void onError(Result result) {
                    toastInMainThread(appContext, "error, Result " + result + "; Result type " + result.getType());
                }
            });

            Log.e("onattach","result : "+res);
        } catch (RemoteException e) {

            Log.e("onattach","intent api service not started");

            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (intentApi != null) {
            intentApi.stopService(null);
            try {
                intentApi.setServiceCallback(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                intentApi.setPrintCallback(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            intentApi = null;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_intent_api, container, false);
        //root.findViewById(R.id.check_premium).setOnClickListener(this);
        //root.findViewById(R.id.activate_online).setOnClickListener(this);
        //root.findViewById(R.id.setup_printer).setOnClickListener(this);
        //root.findViewById(R.id.change_options).setOnClickListener(this);
        root.findViewById(R.id.button).setOnClickListener(this);
        //root.findViewById(R.id.print_image).setOnClickListener(this);
        root.findViewById(R.id.print_file).setOnClickListener(this);
        //root.findViewById(R.id.show_file_preview).setOnClickListener(this);
        //root.findViewById(R.id.print_with_your_rendering).setOnClickListener(this);
        //root.findViewById(R.id.print_with_your_rendering_without_ui).setOnClickListener(this);
        root.findViewById(R.id.print_image_with_print_hand_rendering_without_ui).setOnClickListener(this);
        //root.findViewById(R.id.change_image_options).setOnClickListener(this);
        //root.findViewById(R.id.print_file_with_print_hand_rendering_without_ui).setOnClickListener(this);
        //root.findViewById(R.id.print_protected_file_with_print_hand_rendering_without_ui).setOnClickListener(this);
        //root.findViewById(R.id.change_files_options).setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                try {
                    //if(intentApi.isServiceRunning()) {
                        IPrinterInfo printer = intentApi.getCurrentPrinter();

                        TextView textView = (TextView) getView().findViewById(R.id.txtPrinterName);
                        textView.setText((printer != null ? printer.getName() : "printer not available"));

                        //Toast.makeText(requireContext().getApplicationContext(), "current printer " + (printer != null ? printer.getName() : "null"), Toast.LENGTH_SHORT).show();
                    //}else{
                        //RunService(requireContext().getApplicationContext());
                    //}
                } catch (RemoteException e) {
                    //e.printStackTrace();
                    toastInMainThread(requireContext().getApplicationContext(),"Printer Service not connected");
                }
                break;
            case R.id.print_file:
                if (intentApi != null) {
                    intentApi.stopService(null);
                    try {
                        intentApi.setServiceCallback(null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    try {
                        intentApi.setPrintCallback(null);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    intentApi = null;
                }

                startActivity(new Intent(getContext(), CameraActivity.class));
                getActivity().finish();
                //intentApi.print(FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_DOC), "application/msword", "from printing sample");
                break;
            case R.id.print_image_with_print_hand_rendering_without_ui:
                try {
                    //if(intentApi.isServiceRunning()) {
                    intentApi.print("PrintingSample", "application/pdf", FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_PDF));
                    //intentApi.print("PrintingSample", "image/png", FilesUtils.getFileUriWithPermission(requireContext(), FilesUtils.FILE_PNG));
                    //}else{
                      //  toastInMainThread(requireContext().getApplicationContext(),"Printer Service not connected");
                    //}
                } catch (RemoteException e) {
                    toastInMainThread(requireContext().getApplicationContext(),"Printer Service not connected");
                    //e.printStackTrace();
                }
                break;
        }
    }

    private void toastInMainThread(final Context appContext, final String message) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
