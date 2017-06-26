package com.novoda.noplayer.drm;

import android.os.Handler;

import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;

class DownloadDrmSessionCreator implements DrmSessionCreator {

    private final DownloadedModularDrm downloadedModularDrm;
    private final FrameworkMediaDrmCreator mediaDrmCreator;
    private final Handler handler;

    DownloadDrmSessionCreator(DownloadedModularDrm downloadedModularDrm, FrameworkMediaDrmCreator mediaDrmCreator, Handler handler) {
        this.downloadedModularDrm = downloadedModularDrm;
        this.mediaDrmCreator = mediaDrmCreator;
        this.handler = handler;
    }

    @Override
    public DrmSessionManager<FrameworkMediaCrypto> create(DefaultDrmSessionManager.EventListener eventListener) {
        return new LocalDrmSessionManager(
                downloadedModularDrm.getKeySetId(),
                mediaDrmCreator.create(WIDEVINE_MODULAR_UUID),
                WIDEVINE_MODULAR_UUID,
                handler,
                eventListener
        );
    }
}
