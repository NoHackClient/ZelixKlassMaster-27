package com.zelix.klassmaster.archive;

import com.zelix.klassmaster.util.ZkmAssert;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipEntryWriter {
    public BufferedOutputStream bufferedOutput;
    public ByteArrayOutputStream byteBuffer;
    public int inputLength;
    public InputStream inputStream;
    public ZipOutputStream zipOutputStream;
    public String entryName;
    public boolean compressed;
    public File sourceFile;
    public String sourceKind;

    public CRC32 writeEntry() throws IOException {
        Object object = null;
        return this.writeEntry((Long) null, (Long) object);
    }

    public CRC32 writeEntry(final Long n, final Long n2, final String comment) throws IOException {
        CRC32 crc32 = null;
        final ZipEntry e = new ZipEntry(this.entryName);
        if (n != null) {
            e.setTime(n);
        }
        if (n2 != null) {
            final Object b = n2.toString();
            e.setExtra(((String) b).getBytes("UTF-8"));
            if (comment != null && comment.length() > 0) {
                e.setComment(comment);
            }
        }
        e.setMethod(this.compressed ? 8 : 0);
        if (!this.compressed || this.byteBuffer != null) {
            if (this.sourceFile != null) {
                BufferedInputStream bufferedInputStream = null;
                try {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(this.sourceFile));
                    final byte[] array = new byte[1024];
                    long size = 0L;
                    crc32 = new CRC32();
                    int n4;
                    int n3 = n4 = bufferedInputStream.read(array);
                    while (true) {
                        final int len = n4;
                        if (n3 < 0) {
                            break;
                        }
                        crc32.update(array, 0, len);
                        size += len;
                        n3 = (n4 = bufferedInputStream.read(array));
                    }
                    bufferedInputStream.close();
                    e.setCrc(crc32.getValue());
                    e.setSize(size);
                    this.zipOutputStream.putNextEntry(e);
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(this.sourceFile));
                    int n6;
                    int n5 = n6 = bufferedInputStream.read(array);
                    while (true) {
                        final int len2 = n6;
                        if (n5 < 0) {
                            break;
                        }
                        this.zipOutputStream.write(array, 0, len2);
                        n5 = (n6 = bufferedInputStream.read(array));
                    }
                    try {
                        bufferedInputStream.close();
                    } catch (final IOException ex) {
                    }
                } finally {
                    if (bufferedInputStream != null) {
                        try {
                            bufferedInputStream.close();
                        } catch (final IOException ex2) {
                        }
                    }
                }
            } else {
                Object b;
                if (this.byteBuffer != null) {
                    this.bufferedOutput.flush();
                    b = this.byteBuffer.toByteArray();
                    this.byteBuffer.close();
                } else {
                    try {
                        b = new byte[this.inputLength];
                        int off = 0;
                        int n7 = 0;
                        int inputLength = this.inputLength;
                        int read;
                        while (n7 < inputLength && (read = this.inputStream.read((byte[]) b, off, Math.min(8192, this.inputLength - off))) != -1) {
                            off = (n7 = off + read);
                            inputLength = this.inputLength;
                        }
                    } finally {
                        if (this.inputStream != null) {
                            try {
                                this.inputStream.close();
                            } catch (final IOException ex3) {
                            }
                        }
                    }
                }
                ZipOutputStream zipOutputStream;
                if (!this.compressed) {
                    crc32 = new CRC32();
                    crc32.update((byte[]) b);
                    e.setCrc(crc32.getValue());
                    e.setSize(((byte[]) b).length);
                    zipOutputStream = this.zipOutputStream;
                } else {
                    zipOutputStream = this.zipOutputStream;
                }
                zipOutputStream.putNextEntry(e);
                this.zipOutputStream.write((byte[]) b, 0, ((byte[]) b).length);
            }
        } else {
            this.zipOutputStream.putNextEntry(e);
            try {
                if (this.inputStream == null) {
                    final File sourceFile = this.sourceFile;
                    final StringBuilder append = new StringBuilder().append("(").append(this.inputStream == null).append(",");
                    ZkmAssert.assertNotNull(sourceFile, ((this.byteBuffer == null) ? append.append(true) : append.append(false)).append(",'").append(this.sourceKind).append("',").append(this.compressed).append(")").toString());
                    this.inputStream = new BufferedInputStream(new FileInputStream(this.sourceFile));
                }
                crc32 = new CRC32();
                final byte[] b2 = new byte[1024];
                InputStream inputStream = this.inputStream;
                int read2;
                while ((read2 = inputStream.read(b2)) >= 0) {
                    this.zipOutputStream.write(b2, 0, read2);
                    crc32.update(b2, 0, read2);
                    inputStream = this.inputStream;
                }
            } finally {
                if (this.inputStream != null) {
                    try {
                        this.inputStream.close();
                    } catch (final IOException ex4) {
                    }
                }
            }
        }
        this.zipOutputStream.flush();
        this.zipOutputStream.closeEntry();
        return crc32;
    }

    public ZipEntryWriter(ZipOutputStream zipOutputStream1, String string, boolean compressed, File file1) {
        this.zipOutputStream = zipOutputStream1;
        this.entryName = string;
        this.compressed = compressed;
        this.sourceFile = file1;
        this.sourceKind = "FILE";
    }

    public CRC32 writeEntry(Long long1) throws IOException {
        return this.writeEntry(long1, (Long) null);
    }

    public ZipEntryWriter(ZipOutputStream zipOutputStream1, String string, boolean compressed, InputStream inputStream1, int inputLength) {
        this.zipOutputStream = zipOutputStream1;
        this.entryName = string;
        this.compressed = compressed;
        this.inputStream = inputStream1;
        this.inputLength = inputLength;
        this.sourceKind = "IS";
    }

    public ZipEntryWriter(ZipOutputStream zipOutputStream1, String string, boolean compressed) {
        this.zipOutputStream = zipOutputStream1;
        this.entryName = string;
        this.compressed = compressed;
        this.sourceKind = "BAOS";
    }

    public OutputStream getOutputStream() {
        if (this.bufferedOutput == null) {
            this.byteBuffer = new ByteArrayOutputStream();
            CheckedOutputStream checkedOutputStream = new CheckedOutputStream(this.byteBuffer, new CRC32());
            this.bufferedOutput = new BufferedOutputStream(checkedOutputStream);
        }

        this.sourceKind = this.sourceKind + "<";
        return this.bufferedOutput;
    }

    public CRC32 writeEntry(Long long1, Long long2) throws IOException {
        return this.writeEntry(long1, long2, (String) null);
    }
}
