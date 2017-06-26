package com.novoda.noplayer.exoplayer;

import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class SecurityDowngradingCodecSelectorTest {

    private static final boolean ANY_USE_SECURE_DECODER = false;
    private static final String ANY_MIME_TYPE = "mimeType";

    private static final boolean NOT_DOWNGRADING = false;
    private static final boolean DOWNGRADING = true;
    private static final boolean CONTENT_SECURE = true;
    private static final boolean CONTENT_INSECURE = false;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SecurityDowngradingCodecSelector.InternalMediaCodecUtil internalMediaCodecUtil;

    @Test
    public void givenContentIsSecure_whenNotDowngrading_thenRequiresSecureDecoderIsTrue() throws MediaCodecUtil.DecoderQueryException {
        SecurityDowngradingCodecSelector securityDowngradingCodecSelector = new SecurityDowngradingCodecSelector(internalMediaCodecUtil, NOT_DOWNGRADING);

        securityDowngradingCodecSelector.getDecoderInfo(ANY_MIME_TYPE, CONTENT_SECURE);

        ArgumentCaptor<Boolean> argumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(internalMediaCodecUtil).getDecoderInfo(eq(ANY_MIME_TYPE), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isTrue();
    }

    @Test
    public void givenContentIsInsecure_whenDowngrading_thenRequiresSecureDecoderIsFalse() throws MediaCodecUtil.DecoderQueryException {
        SecurityDowngradingCodecSelector securityDowngradingCodecSelector = new SecurityDowngradingCodecSelector(internalMediaCodecUtil, DOWNGRADING);

        securityDowngradingCodecSelector.getDecoderInfo(ANY_MIME_TYPE, CONTENT_INSECURE);

        ArgumentCaptor<Boolean> argumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(internalMediaCodecUtil).getDecoderInfo(eq(ANY_MIME_TYPE), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isFalse();
    }

    @Test
    public void givenContentIsSecure_whenDowngrading_thenRequiresSecureDecoderIsFalse() throws MediaCodecUtil.DecoderQueryException {
        SecurityDowngradingCodecSelector securityDowngradingCodecSelector = new SecurityDowngradingCodecSelector(internalMediaCodecUtil, DOWNGRADING);

        securityDowngradingCodecSelector.getDecoderInfo(ANY_MIME_TYPE, CONTENT_SECURE);

        ArgumentCaptor<Boolean> argumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(internalMediaCodecUtil).getDecoderInfo(eq(ANY_MIME_TYPE), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isFalse();
    }

    @Test
    public void givenContentIsInsecure_whenNotDowngrading_thenRequiresSecureDecoderIsFalse() throws MediaCodecUtil.DecoderQueryException {
        SecurityDowngradingCodecSelector securityDowngradingCodecSelector = new SecurityDowngradingCodecSelector(internalMediaCodecUtil, NOT_DOWNGRADING);

        securityDowngradingCodecSelector.getDecoderInfo(ANY_MIME_TYPE, CONTENT_INSECURE);

        ArgumentCaptor<Boolean> argumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        verify(internalMediaCodecUtil).getDecoderInfo(eq(ANY_MIME_TYPE), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).isFalse();
    }

    @Test
    public void whenGettingPassthroughDecoderInfo_thenDelegates() throws MediaCodecUtil.DecoderQueryException {
        SecurityDowngradingCodecSelector securityDowngradingCodecSelector = new SecurityDowngradingCodecSelector(internalMediaCodecUtil, ANY_USE_SECURE_DECODER);

        securityDowngradingCodecSelector.getPassthroughDecoderInfo();

        verify(internalMediaCodecUtil).getPassthroughDecoderInfo();
    }
}
