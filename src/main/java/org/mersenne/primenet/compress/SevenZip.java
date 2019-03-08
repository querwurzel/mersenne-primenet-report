package org.mersenne.primenet.compress;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class SevenZip implements Iterable<byte[]>, Iterator<byte[]> {

    private final SevenZFile archive;

    private byte[] currentEntry = null;

    private SevenZip(byte[] archive) throws IOException {
        this.archive = new SevenZFile(new SeekableInMemoryByteChannel(archive));
    }

    public static Iterable<byte[]> extract(byte[] archive) throws IOException {
        return new SevenZip(archive);
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
