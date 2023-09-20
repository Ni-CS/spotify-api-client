package com.nics.spotifyapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

@Service
public class SpotifyConfig {

    @Value("http://localhost:8080")
    private String address;

    public SpotifyApi getSpotifyObject() {
        URI redirectedURL =  SpotifyHttpManager.makeUri(address + "/get-user-code");

        return new SpotifyApi
                .Builder()
                .setClientId("clientid")
                .setClientSecret("clientsecret")
                .setRedirectUri(redirectedURL)
                .build();
    }

}
