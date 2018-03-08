package ru.track.io;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.track.io.vendor.Bootstrapper;
import ru.track.io.vendor.FileEncoder;
import ru.track.io.vendor.ReferenceTaskImplementation;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Base64;

public final class TaskImplementation implements FileEncoder {

    /**
     * @param finPath  where to read binary data from
     * @param foutPath where to write encoded data. if null, please create and use temporary file.
     * @return file to read encoded data from
     * @throws IOException is case of input/output errors
     */
    @NotNull
    public File encodeFile(@NotNull String finPath, @Nullable String foutPath) throws IOException {
        /* XXX: https://docs.oracle.com/javase/8/docs/api/java/io/File.html#deleteOnExit-- */
        final File fin = new File(finPath);
        final File fout;

        if (foutPath != null) {
            fout = new File(foutPath);
        } else {
            fout = File.createTempFile("based_file_", ".txt");
            fout.deleteOnExit();
        }

        try (
                final InputStream is = new FileInputStream(fin);
                final OutputStream os = new BufferedOutputStream(new FileOutputStream(fout));
        ) {
            byte[] fileIn = new byte[3];
            byte[] fileOut = new byte[4];
            int i=0;

            int readByte =0;
            while((readByte = is.read(fileIn, 0, 3))!=-1)
            {
                if (readByte==2)
                {
                    fileOut[0] = (byte) toBase64[((fileIn[0] & 0xFC) >> 2)];
                    fileOut[1] = (byte) toBase64[(((fileIn[0] & 0x03) << 4) | ((fileIn[1] & 0xF0) >> 4))];
                    fileOut[2] = (byte) toBase64[(((fileIn[1] & 0x0F) << 2))];
                    fileOut[3] = (byte) '=';
                }
                else if(readByte==1){
                    fileOut[0] = (byte) toBase64[((fileIn[0] & 0xFC) >> 2)];
                    fileOut[1] = (byte) toBase64[(((fileIn[0] & 0x03) << 4))];
                    fileOut[2] = (byte) '=';
                    fileOut[3] = (byte) '=';
                }
                else{
                    fileOut[0] = (byte) toBase64[((fileIn[0] & 0xFC) >> 2)];
                    fileOut[1] = (byte) toBase64[(((fileIn[0] & 0x03) << 4) | ((fileIn[1] & 0xF0) >> 4))];
                    fileOut[2] = (byte) toBase64[(((fileIn[1] & 0x0F) << 2) | ((fileIn[2] & 0xC0) >> 6))];
                    fileOut[3] = (byte) toBase64[((fileIn[2] & 0x3F))];
                }

                os.write(fileOut,0,4);

            }

        }
        return fout;
    }

    private static final char[] toBase64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static void main(String[] args) throws Exception {
        final FileEncoder encoder = new TaskImplementation();
        // NOTE: open http://localhost:9000/ in your web browser
        (new Bootstrapper(args, encoder))
                .bootstrap("", new InetSocketAddress("127.0.0.1", 9000));
    }

}
