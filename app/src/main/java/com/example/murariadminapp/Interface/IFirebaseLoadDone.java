package com.example.murariadminapp.Interface;
import com.example.murariadminapp.Model.IDs;

import java.util.List;

public interface IFirebaseLoadDone {
    void onFirebaseLoadSuccess(List<IDs> Locationlist);
    void onFirebaseLoadFailed(String Message);
}


