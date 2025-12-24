package com.nguyenthimynguyen.example10.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;

public class VietQRUtils {

    // Tạo TLV
    public static String tlv(String tag, String value) {
        String length = String.format("%02d", value.length());
        return tag + length + value;
    }

    // CRC16 chuẩn EMVCo
    public static String crc16(String data) {
        int crc = 0xFFFF;

        for (char c : data.toCharArray()) {
            crc ^= ((int) c) & 0xFF;

            for (int i = 0; i < 8; i++) {
                if ((crc & 1) != 0) {
                    crc = (crc >>> 1) ^ 0x8408;
                } else {
                    crc = crc >>> 1;
                }
            }
        }

        crc = ~crc;
        crc = ((crc & 0xFF) << 8) | ((crc >> 8) & 0xFF);

        return String.format("%04X", crc & 0xFFFF);
    }

    // Generate chuỗi VietQR chuẩn quốc gia
    public static String generateVietQRData(
            String bankBin,
            String accountNumber,
            String ownerName,
            long amount
    ) {

        String f00 = tlv("00", "01");
        String f01 = tlv("01", "12"); // dynamic QR

        String f2600 = tlv("00", "A000000727");        // GUID
        String f2601 = tlv("01", bankBin);
        String f2602 = tlv("02", accountNumber);
        String f26 = tlv("26", f2600 + f2601 + f2602);

        String f52 = tlv("52", "0000");
        String f53 = tlv("53", "704");
        String f54 = tlv("54", String.valueOf(amount));
        String f58 = tlv("58", "VN");
        String f59 = tlv("59", ownerName.toUpperCase());

        String raw = f00 + f01 + f26 + f52 + f53 + f54 + f58 + f59 + "6304";

        String crc = crc16(raw);

        return raw + crc;
    }

    // Tạo ảnh QR Base64 từ chuỗi VietQR
    public static String generateQRBase64(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);

        BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    // Hàm tổng hợp: VietQR + QR Image
    public static String createVietQRBase64(
            String bankBin,
            String accountNumber,
            String ownerName,
            long amount
    ) throws Exception {

        String vietQRData = generateVietQRData(bankBin, accountNumber, ownerName, amount);
        return generateQRBase64(vietQRData);
    }
}