package com.example.museumapp;

import android.net.Uri;

import com.example.museumapp.Api.RouteBody;
import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Route;
import com.example.museumapp.Models.User;

import java.net.URI;

public class SharedData {

    protected static SharedData instance;
    public User user;
    public Museum museo = null;

    public Route route = null;
    public RouteBody routeBody = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Museum getMuseo() {
        return museo;
    }

    public void setMuseo(Museum museo) {
        this.museo = museo;
    }

    public static void setInstance(SharedData instance) {
        SharedData.instance = instance;
    }

    public RouteBody getRouteBody() {
        return routeBody;
    }

    public void setRouteBody(RouteBody route) {
        this.routeBody = route;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public static synchronized SharedData getInstance() {
        if (instance == null) {
            instance = new SharedData();
        }
        return instance;
    }

    public void destroy() {
        this.user = null;
        this.museo = null;
        instance = null; // También nulifica la instancia única
    }

    @Override
    public String toString() {
        return "SharedData{" +
                "user=" + user +
                ", museo=" + museo +
                '}';
    }
}
