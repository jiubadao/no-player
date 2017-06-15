package com.novoda.noplayer.exoplayer;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;

import java.util.EnumMap;

class RendererTrackIndexExtractor {

    EnumMap<TrackType, Integer> extractFrom(MappingTrackSelector.MappedTrackInfo mappedTrackInfo,
                                            SimpleExoPlayer simpleExoPlayer) {
        EnumMap<TrackType, Integer> trackIndex = new EnumMap<>(TrackType.class);

        int numberOfTracks = mappedTrackInfo.length;
        for (int i = 0; i < numberOfTracks; i++) {
            switch (simpleExoPlayer.getRendererType(i)) {
                case C.TRACK_TYPE_AUDIO:
                    trackIndex.put(TrackType.AUDIO, i);
                    break;
                case C.TRACK_TYPE_VIDEO:
                    trackIndex.put(TrackType.VIDEO, i);
                    break;
                case C.TRACK_TYPE_TEXT:
                    trackIndex.put(TrackType.TEXT, i);
                    break;
            }
        }

        return trackIndex;
    }
}
