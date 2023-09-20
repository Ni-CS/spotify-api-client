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
                .setClientId("80db7f2b032a44a4a8a56991588a2ae2")
                .setClientSecret("9438fcf9634a444cb065722e8f79cdc3")
                .setRedirectUri(redirectedURL)
                .build();
    }

}
