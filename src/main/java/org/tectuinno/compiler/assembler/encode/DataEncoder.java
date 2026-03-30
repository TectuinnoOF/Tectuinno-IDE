package org.tectuinno.compiler.assembler.encode;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.tectuinno.compiler.assembler.utils.AsmSyntaxDictionary;
import org.tectuinno.compiler.assembler.utils.Token;
import org.tectuinno.compiler.assembler.utils.TokenType;

/**
 * Encodes assembler data directives into raw bytes.
 *
 * <p>
 * Supported directives:
 * </p>
 * <ul>
 *     <li>{@code word}</li>
 *     <li>{@code half}</li>
 *     <li>{@code byte}</li>
 *     <li>{@code asciz}</li>
 * </ul>
 */
public final class DataEncoder {

	private DataEncoder() {}

    /**
     * Encodes a data-producing directive into raw bytes.
     *
     * @param mnemonic directive mnemonic
     * @param args normalized directive arguments
     * @return encoded byte array
     */
    public static byte[] encode(String mnemonic, List<Token> args) {
        return switch (mnemonic) {
            case AsmSyntaxDictionary.WORD -> encodeWord(args);
            case AsmSyntaxDictionary.HALF -> encodeHalf(args);
            case AsmSyntaxDictionary.BYTE -> encodeByte(args);
            case AsmSyntaxDictionary.ASCIZ -> encodeAsciz(args);
            default -> new byte[0];
        };
    }

    /**
     * Converts a byte array to a displayable hexadecimal string.
     *
     * @param bytes byte array
     * @return uppercase hexadecimal string separated by spaces
     */
    public static String toHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int i = 0; i < bytes.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(String.format("%02X", bytes[i] & 0xFF));
        }
        return sb.toString();
    }

    private static byte[] encodeWord(List<Token> args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (Token token : valueTokens(args)) {
            long value = scalarValue(token);

            if (value < Integer.MIN_VALUE || value > 0xFFFFFFFFL) {
                throw new IllegalArgumentException("Value out of range for .word: " + token.getValue());
            }

            writeLittleEndian(out, value, 4);
        }

        return out.toByteArray();
    }

    private static byte[] encodeHalf(List<Token> args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (Token token : valueTokens(args)) {
            long value = scalarValue(token);

            if (value < Short.MIN_VALUE || value > 0xFFFFL) {
                throw new IllegalArgumentException("Value out of range for .half: " + token.getValue());
            }

            writeLittleEndian(out, value, 2);
        }

        return out.toByteArray();
    }

    private static byte[] encodeByte(List<Token> args) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (Token token : valueTokens(args)) {
            long value = scalarValue(token);

            if (value < Byte.MIN_VALUE || value > 0xFFL) {
                throw new IllegalArgumentException("Value out of range for .byte: " + token.getValue());
            }

            writeLittleEndian(out, value, 1);
        }

        return out.toByteArray();
    }

    private static byte[] encodeAsciz(List<Token> args) {
        if (args.isEmpty()) {
            return new byte[] { 0 };
        }

        Token first = args.get(0);

        if (first.getType() != TokenType.STRING) {
            throw new IllegalArgumentException("Expected STRING argument for .asciz");
        }

        byte[] text = first.getValue().getBytes(StandardCharsets.US_ASCII);
        byte[] out = new byte[text.length + 1];

        System.arraycopy(text, 0, out, 0, text.length);
        out[out.length - 1] = 0;

        return out;
    }

    private static List<Token> valueTokens(List<Token> args) {
        List<Token> values = new ArrayList<>();

        for (Token token : args) {
            if (token.getType() == TokenType.IMMEDIATE || token.getType() == TokenType.CHAR) {
                values.add(token);
            }
        }

        return values;
    }

    private static long scalarValue(Token token) {
        if (token.getType() == TokenType.CHAR) {
            if (token.getValue().isEmpty()) {
                throw new IllegalArgumentException("Empty character literal");
            }
            return token.getValue().charAt(0);
        }

        if (token.getType() == TokenType.IMMEDIATE) {
            return parseImmediate(token.getValue());
        }

        throw new IllegalArgumentException("Unsupported token for scalar value: " + token.getType());
    }

    private static long parseImmediate(String value) {
        if (value.startsWith("-0x") || value.startsWith("-0X")) {
            return -Long.parseUnsignedLong(value.substring(3), 16);
        }

        if (value.startsWith("0x") || value.startsWith("0X")) {
            return Long.parseUnsignedLong(value.substring(2), 16);
        }

        return Long.parseLong(value);
    }

    private static void writeLittleEndian(ByteArrayOutputStream out, long value, int byteCount) {
        for (int i = 0; i < byteCount; i++) {
            out.write((int) ((value >> (8 * i)) & 0xFF));
        }
    }
	
}
