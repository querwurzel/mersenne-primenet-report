package org.mersenne.primenet.compression;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class SevenZip implements Iterable<byte[]>, Iterator<byte[]> {

    private static final Logger log = LoggerFactory.getLogger(SevenZip.class);

    private final SevenZFile archive;

    private byte[] currentEntry = null;

    private SevenZip(byte[] archive) throws IOException {
        this.archive = new SevenZFile(new SeekableInMemoryByteChannel(archive));
        log.debug("Reading 7zip archive of {} bytes", archive.length);
    }

    public static Stream<byte[]> stream(byte[] archive) throws IOException {
        return StreamSupport.stream(new SevenZip(archive).spliterator(), false);
    }

    @Override
    public Iterator<byte[]> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        try {
            return Objects.nonNull(currentEntry) || Objects.nonNull(currentEntry = this.nextEntry());
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public byte[] next() {
        if (Objects.isNull(currentEntry)) {
            return this.nextEntry();
        } else {
            final byte[] entry = currentEntry;
            currentEntry = null;
            return entry;
        }
    }

    private byte[] nextEntry() throws NoSuchElementException {
        try {
            for (SevenZArchiveEntry entry = archive.getNextEntry(); Objects.nonNull(entry); entry = archive.getNextEntry()) {
                if (!entry.isDirectory()) {
                    final byte[] content = new byte[(int)entry.getSize()];
                    archive.read(content, 0, content.length);
                    return content;
                }
            }

            throw new NoSuchElementException();
        } catch (IOException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }
}
