package com.assets.data;

import com.assets.ByteReader;
import com.tool.BitConverter;

public class MeshHelper {
	public enum VertexChannelFormat {
		kChannelFormatFloat, kChannelFormatFloat16, kChannelFormatColor, kChannelFormatByte, kChannelFormatUInt32
	}

	public enum VertexFormat2017 {
		kVertexFormatFloat, kVertexFormatFloat16, kVertexFormatColor, kVertexFormatUNorm8, kVertexFormatSNorm8,
		kVertexFormatUNorm16, kVertexFormatSNorm16, kVertexFormatUInt8, kVertexFormatSInt8, kVertexFormatUInt16,
		kVertexFormatSInt16, kVertexFormatUInt32, kVertexFormatSInt32
	}

	public enum VertexFormat {
		kVertexFormatFloat, kVertexFormatFloat16, kVertexFormatUNorm8, kVertexFormatSNorm8, kVertexFormatUNorm16,
		kVertexFormatSNorm16, kVertexFormatUInt8, kVertexFormatSInt8, kVertexFormatUInt16, kVertexFormatSInt16,
		kVertexFormatUInt32, kVertexFormatSInt32
	}

	public static VertexFormat ToVertexFormat(int format, int[] version) {
		if (version[0] < 2017) {
			VertexChannelFormat vertexChannelFormat = VertexChannelFormat.values()[format];
			switch (vertexChannelFormat) {
			case kChannelFormatFloat:
				return VertexFormat.kVertexFormatFloat;
			case kChannelFormatFloat16:
				return VertexFormat.kVertexFormatFloat16;
			case kChannelFormatColor: // in 4.x is size 4
				return VertexFormat.kVertexFormatUNorm8;
			case kChannelFormatByte:
				return VertexFormat.kVertexFormatUInt8;
			case kChannelFormatUInt32: // in 5.x
				return VertexFormat.kVertexFormatUInt32;
			default:
				throw new IndexOutOfBoundsException("format");
			}
		} else if (version[0] < 2019) {
			VertexFormat2017 vertexFormat2017 = VertexFormat2017.values()[format];
			switch (vertexFormat2017) {
			case kVertexFormatFloat:
				return VertexFormat.kVertexFormatFloat;
			case kVertexFormatFloat16:
				return VertexFormat.kVertexFormatFloat16;
			case kVertexFormatColor:
			case kVertexFormatUNorm8:
				return VertexFormat.kVertexFormatUNorm8;
			case kVertexFormatSNorm8:
				return VertexFormat.kVertexFormatSNorm8;
			case kVertexFormatUNorm16:
				return VertexFormat.kVertexFormatUNorm16;
			case kVertexFormatSNorm16:
				return VertexFormat.kVertexFormatSNorm16;
			case kVertexFormatUInt8:
				return VertexFormat.kVertexFormatUInt8;
			case kVertexFormatSInt8:
				return VertexFormat.kVertexFormatSInt8;
			case kVertexFormatUInt16:
				return VertexFormat.kVertexFormatUInt16;
			case kVertexFormatSInt16:
				return VertexFormat.kVertexFormatSInt16;
			case kVertexFormatUInt32:
				return VertexFormat.kVertexFormatUInt32;
			case kVertexFormatSInt32:
				return VertexFormat.kVertexFormatSInt32;
			default:
				throw new IndexOutOfBoundsException("format");
			}
		} else {
			return VertexFormat.values()[format];
		}
	}

	public static long GetFormatSize(VertexFormat format)
    {
        switch (format)
        {
            case kVertexFormatFloat:
            case kVertexFormatUInt32:
            case kVertexFormatSInt32:
                return 4L;
            case kVertexFormatFloat16:
            case kVertexFormatUNorm16:
            case kVertexFormatSNorm16:
            case kVertexFormatUInt16:
            case kVertexFormatSInt16:
                return 2L;
            case kVertexFormatUNorm8:
            case kVertexFormatSNorm8:
            case kVertexFormatUInt8:
            case kVertexFormatSInt8:
                return 1L;
            default:
				throw new IndexOutOfBoundsException("format");
        }
    }

	public static boolean IsIntFormat(VertexFormat format) {
		return format.ordinal() >= VertexFormat.kVertexFormatUInt8.ordinal();
	}

	public static float[] BytesToFloatArray(byte[] inputBytes, VertexFormat format) {
		long size = GetFormatSize(format);
		long len = inputBytes.length / size;
		float[] result = new float[(int) len];
		for (int i = 0; i < len; i++) {
			switch (format) {
			case kVertexFormatFloat:
                result[i] = BitConverter.toFloat(inputBytes, i * 4);
				break;
			/*case kVertexFormatFloat16:
				result[i] = Half.ToHalf(inputBytes, i * 2);
				break;
			case kVertexFormatUNorm8:
				result[i] = inputBytes[i] / 255f;
				break;
			case kVertexFormatSNorm8:
				result[i] = Math.Max((sbyte) inputBytes[i] / 127f, -1f);
				break;
			case kVertexFormatUNorm16:
				result[i] = BitConverter.ToUInt16(inputBytes, i * 2) / 65535f;
				break;
			case kVertexFormatSNorm16:
				result[i] = Math.Max(BitConverter.ToInt16(inputBytes, i * 2) / 32767f, -1f);
				break;*/
			default:
					
			}
		}
		return result;
	}

	public static int[] BytesToIntArray(byte[] inputBytes, VertexFormat format) {
		long size = GetFormatSize(format);
		long len = inputBytes.length / size;
		int[] result = new int[(int) len];
		for (int i = 0; i < len; i++) {
			switch (format) {
			case kVertexFormatUInt8:
			case kVertexFormatSInt8:
				result[i] = inputBytes[i];
				break;
			case kVertexFormatUInt16:
			case kVertexFormatSInt16:
				result[i] = BitConverter.toShort(inputBytes, i * 2);
				break;
			case kVertexFormatUInt32:
			case kVertexFormatSInt32:
				result[i] = BitConverter.toInt(inputBytes, i * 4);
				break;
			default:
				
			}
		}
		return result;
	}
}
