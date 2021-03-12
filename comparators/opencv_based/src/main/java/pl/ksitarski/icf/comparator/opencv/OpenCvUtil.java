package pl.ksitarski.icf.comparator.opencv;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import pl.ksitarski.icf.core.exc.WrapperException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.opencv.core.CvType.*;
import static pl.ksitarski.icf.core.util.ConversionUtil.*;

public class OpenCvUtil {
    public static BufferedImage toBufferedImage(Mat input) {
        MatOfByte mapOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", input, mapOfByte);
        byte[] mapOfByteAsArray = mapOfByte.toArray();

        BufferedImage output = null;
        try {
            output = ImageIO.read(new ByteArrayInputStream(mapOfByteAsArray));
        } catch (IOException e) {
            throw new WrapperException(e);
        }
        return output;
    }

    public static Mat toMat(BufferedImage bufferedImage) {
        BufferedImage convertedImg = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        convertedImg.getGraphics().drawImage(bufferedImage, 0, 0, null);
        convertedImg.getGraphics().dispose();
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC4);
        byte[] data = ((DataBufferByte) convertedImg.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    public static Mat toGrayscaleMat(BufferedImage bufferedImage) {
        BufferedImage convertedImg = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        convertedImg.getGraphics().drawImage(bufferedImage, 0, 0, null);
        convertedImg.getGraphics().dispose();
        Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC4);
        Mat matGray = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8U);
        byte[] data = ((DataBufferByte) convertedImg.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        Imgproc.cvtColor(mat, matGray, Imgproc.COLOR_BGR2GRAY);
        return matGray;
    }

    public static SerializedMat SerializeFromMat(Mat mat) {
        if (mat.type() == 5) {
            Mat mat2 = new Mat();
            mat.convertTo(mat2, CV_8S);
            mat = mat2;
        }

        if (mat.type() == 0 || mat.type() == 5) {
            SerializedMat serializedMat = new SerializedMat();
            serializedMat.setType(mat.type());
            serializedMat.setRows(mat.rows());
            serializedMat.setCols(mat.cols());
            byte[] bytes = new byte[(int) (mat.total() * mat.elemSize())];
            mat.get(0, 0, bytes);
            serializedMat.setBytes(bytes);

            return serializedMat;

        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static byte[] serializeFromMat(Mat mat) {
        return SerializeFromMat(mat).asData();
    }

    public static Mat deserializeToMat(int type, SerializedMat serializedMat) {
        if (type == 5) {
            Mat mat = new Mat(serializedMat.getRows(), serializedMat.getCols(), CV_8S);

            mat.put(0, 0, serializedMat.getBytes());

            Mat mat2 = new Mat();
            mat.convertTo(mat2, CV_32F);

            return mat2;
        } else if (type == 0) {
            Mat mat = new Mat(serializedMat.getRows(), serializedMat.getCols(), CV_8U);
            mat.put(0 , 0, serializedMat.getBytes());
            return mat;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static Mat deserializeFromData(int type, byte[] data) {
        return deserializeToMat(type, new SerializedMat(data));
    }

    private static class SerializedMat {
        byte[] bytes;

        int type;
        int rows;
        int cols;

        private SerializedMat() {
        }

        public byte[] getBytes() {
            return bytes;
        }

        public SerializedMat setBytes(byte[] bytes) {
            this.bytes = bytes;
            return this;
        }

        private int getType() {
            return type;
        }

        private void setType(int type) {
            this.type = type;
        }

        private int getRows() {
            return rows;
        }

        private void setRows(int rows) {
            this.rows = rows;
        }

        private int getCols() {
            return cols;
        }

        private void setCols(int cols) {
            this.cols = cols;
        }

        private byte[] asData() {
            byte[] out = new byte[INT_TO_ARRAY_LENGTH * 3 + bytes.length];

            ByteBuffer.wrap(out)
                    .putInt(type)
                    .putInt(cols)
                    .putInt(rows)
                    .put(bytes);
            return out;
        }

        public SerializedMat(byte[] data) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            int type = buffer.getInt();
            int cols = buffer.getInt(1 * INT_TO_ARRAY_LENGTH);
            int rows = buffer.getInt(2 * INT_TO_ARRAY_LENGTH);
            byte[] dataOut = new byte[data.length - 3 * INT_TO_ARRAY_LENGTH];
            buffer.get(3 * INT_TO_ARRAY_LENGTH, dataOut);

            this.type = type;
            this.cols = cols;
            this.rows = rows;
            this.bytes = dataOut;
        }

    }
}
