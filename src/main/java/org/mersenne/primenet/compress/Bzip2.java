package org.mersenne.primenet.compress;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Bzip2 {

    public static InputStream stream(byte[] archive) throws IOException {
        return new BZip2CompressorInputStream(new ByteArrayInputStream(archive));
    }

    public static byte[] extract(byte[] archive) throws IOException {
        try (InputStream input = new BZip2CompressorInputStream(new ByteArrayInputStream(archive))) {
            return StreamUtils.copyToByteArray(input);
        }
    }

    private Bzip2() {}

}
