package org.mersenne.primenet.compression;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Bzip2 {

    public static InputStream stream(byte[] archive) throws IOException {
        return new BZip2CompressorInputStream(new ByteArrayInputStream(archive));
    }

    private Bzip2() {}

}
