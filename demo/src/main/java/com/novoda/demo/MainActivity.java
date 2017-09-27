package com.novoda.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.novoda.noplayer.ContentType;
import com.novoda.noplayer.Listeners;
import com.novoda.noplayer.NoPlayer;
import com.novoda.noplayer.PlayerBuilder;
import com.novoda.noplayer.PlayerState;
import com.novoda.noplayer.PlayerView;
import com.novoda.noplayer.model.AudioTracks;
import com.novoda.noplayer.model.PlayerAudioTrack;
import com.novoda.noplayer.model.PlayerSubtitleTrack;
import com.novoda.utils.NoPlayerLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final String URI_VIDEO_MP4 = "http://yt-dash-mse-test.commondatastorage.googleapis.com/media/car-20120827-85.mp4";
    private static final String URI_VIDEO_MPD = "https://storage.googleapis.com/content-samples/multi-audio/manifest.mpd";

    private static final String URI_VIDEO_WIDEVINE_EXAMPLE_MODULAR_MPD = "https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd";
    private static final String EXAMPLE_MODULAR_LICENSE_SERVER_PROXY = "https://proxy.uat.widevine.com/proxy?provider=widevine_test";

    private NoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NoPlayerLog.setLoggingEnabled(true);
        setContentView(R.layout.activity_main);
        PlayerView playerView = (PlayerView) findViewById(R.id.player_view);
        View audioSelectionButton = findViewById(R.id.button_audio_selection);
        View subtitleSelectionButton = findViewById(R.id.button_subtitle_selection);
        final ControllerView controllerView = (ControllerView) findViewById(R.id.controller_view);

        audioSelectionButton.setOnClickListener(showAudioSelectionDialog);
        subtitleSelectionButton.setOnClickListener(showSubtitleSelectionDialog);

        DataPostingModularDrm drmHandler = new DataPostingModularDrm(EXAMPLE_MODULAR_LICENSE_SERVER_PROXY);

        player = new PlayerBuilder()
                .withWidevineModularStreamingDrm(drmHandler)
                .withDowngradedSecureDecoder()
                .build(this);

        Listeners playerListeners = player.getListeners();
        playerListeners.addPreparedListener(new NoPlayer.PreparedListener() {
            @Override
            public void onPrepared(PlayerState playerState) {
                player.play();
            }
        });
        playerListeners.addStateChangedListener(new NoPlayer.StateChangedListener() {
            @Override
            public void onVideoPlaying() {
                controllerView.setPlaying();
            }

            @Override
            public void onVideoPaused() {
                controllerView.setPaused();
            }

            @Override
            public void onVideoStopped() {

            }
        });

        player.attach(playerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO: Add switch in UI to avoid redeploy.
        Uri uri = Uri.parse(URI_VIDEO_WIDEVINE_EXAMPLE_MODULAR_MPD);
        player.loadVideo(uri, ContentType.DASH);
    }

    private final View.OnClickListener showAudioSelectionDialog = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showAudioSelectionDialog();
        }

        private void showAudioSelectionDialog() {
            final AudioTracks audioTracks = player.getAudioTracks();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item);
            adapter.addAll(mapAudioTrackToLabel(audioTracks));
            AlertDialog audioSelectionDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Select audio track")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            PlayerAudioTrack audioTrack = audioTracks.get(position);
                            player.selectAudioTrack(audioTrack);
                        }
                    }).create();
            audioSelectionDialog.show();
        }

        private List<String> mapAudioTrackToLabel(AudioTracks audioTracks) {
            List<String> labels = new ArrayList<>();
            for (PlayerAudioTrack audioTrack : audioTracks) {
                String label = String.format(
                        Locale.UK,
                        "Group: %s Format: %s Type: %s",
                        audioTrack.groupIndex(),
                        audioTrack.formatIndex(),
                        audioTrack.audioTrackType()
                );
                labels.add(label);
            }
            return labels;
        }
    };

    private final View.OnClickListener showSubtitleSelectionDialog = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!player.getSubtitleTracks().isEmpty()) {
                showSubtitleSelectionDialog();
            } else {
                Toast.makeText(MainActivity.this, "no subtitles available!", Toast.LENGTH_LONG).show();
            }
        }

        private void showSubtitleSelectionDialog() {
            final List<PlayerSubtitleTrack> subtitleTracks = player.getSubtitleTracks();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.list_item);
            adapter.addAll(mapSubtitleTrackToLabel(subtitleTracks));
            AlertDialog subtitlesSelectionDialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Select subtitle track")
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position) {
                                case 0:
                                    player.hideSubtitleTrack();
                                    break;
                                default:
                                    PlayerSubtitleTrack subtitleTrack = subtitleTracks.get(position - 1);
                                    player.showSubtitleTrack(subtitleTrack);
                                    break;
                            }
                        }
                    }).create();
            subtitlesSelectionDialog.show();
        }

        private List<String> mapSubtitleTrackToLabel(List<PlayerSubtitleTrack> subtitleTracks) {
            List<String> labels = new ArrayList<>();
            labels.add("Dismiss subtitles");
            for (PlayerSubtitleTrack subtitleTrack : subtitleTracks) {
                labels.add("Group: " + subtitleTrack.groupIndex() + " Format: " + subtitleTrack.formatIndex());
            }
            return labels;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        player.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}
