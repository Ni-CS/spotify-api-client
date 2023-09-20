package com.nics.spotifyapp.controller;

import com.nics.spotifyapp.config.SpotifyConfig;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.personalization.GetUsersTopArtistsAndTracksRequest;
import se.michaelthelin.spotify.requests.data.personalization.interfaces.IArtistTrackModelObject;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@RestController
public class SpotifyController {

    @Autowired
    SpotifyConfig setup;

    User user = null;

    private String accessToken;
    private String refreshToken;

    private static final ModelObjectType type = ModelObjectType.ARTIST;

    @GetMapping("/login")
    public String loginSpotify() throws IOException {

        SpotifyApi spotifyCont = setup.getSpotifyObject();

        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyCont.authorizationCodeUri()
                .scope("user-library-read user-top-read")
                .show_dialog(true)
                .build();

        final URI uri = authorizationCodeUriRequest.execute();

        return uri.toString();
    }

    @GetMapping("/get-user-code")
    public void getSpotifyUserCode(@RequestParam("code") String userCode, HttpServletResponse response)	throws IOException {
        SpotifyApi object = setup.getSpotifyObject();

        AuthorizationCodeRequest authorizationCodeRequest = object.authorizationCode(userCode).build();


        try {
            final AuthorizationCodeCredentials authorizationCode = authorizationCodeRequest.execute();

            object.setAccessToken(authorizationCode.getAccessToken());
            object.setRefreshToken(authorizationCode.getRefreshToken());
            accessToken = authorizationCode.getAccessToken();
            refreshToken = authorizationCode.getRefreshToken();

            final GetCurrentUsersProfileRequest getCurrentUsersProfile = object.getCurrentUsersProfile().build();
            user = getCurrentUsersProfile.execute();
            System.out.println(user);

        } catch (Exception e) {
            System.out.println("Exception occured while getting user code: " + e);
        }

        response.sendRedirect("/home");
    }

    @GetMapping("/get-user-top-artists-and-tracks")
    public void getUserTopArtistsAndTracks() throws IOException, ParseException, SpotifyWebApiException {
        SpotifyApi conection = setup.getSpotifyObject();

        conection.setAccessToken(accessToken);
        conection.setRefreshToken(refreshToken);

        final GetUsersTopArtistsAndTracksRequest<? extends IArtistTrackModelObject> getUsersTopArtistsAndTracksRequest = conection
                .getUsersTopArtistsAndTracks(type)
          .limit(10)
          .offset(0)
          .time_range("medium_term")
                .build();

            final Paging<? extends IArtistTrackModelObject> artistPaging = getUsersTopArtistsAndTracksRequest.execute();

            System.out.println("Total: " + artistPaging.getTotal());

    }

    @GetMapping(value = "home")
    public String showName(){
        return user.getDisplayName();
    }

    @GetMapping("/user-top-songs")
    public Track[] getUserTopTracks() {
        SpotifyApi object = setup.getSpotifyObject();
        object.setAccessToken(accessToken);
        object.setRefreshToken(refreshToken);

        final GetUsersTopTracksRequest getUsersTopTracksRequest = object.getUsersTopTracks()
                .time_range("medium_term")
                .limit(10)
                .offset(0)
                .build();

        try {
            final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();

            return trackPaging.getItems();
        } catch (Exception e) {
            System.out.println("Exception occured while fetching top songs: " + e);
        }

        return new Track[0];
    }

}
